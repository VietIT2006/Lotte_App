const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../../core/db');

class AuthService {
    async register(userData) {
        const { username, email, phone, password, full_name } = userData;

        // Check if user exists
        const checkUser = await db.query(
            'SELECT * FROM users WHERE email = $1 OR username = $2',
            [email, username]
        );

        if (checkUser.rows.length > 0) {
            const err = new Error('Username or email already exists');
            err.statusCode = 400;
            throw err;
        }

        // Hash password
        const passwordHash = await bcrypt.hash(password, 10);

        // Insert new user
        const result = await db.query(
            `INSERT INTO users (username, email, phone, password_hash, full_name) 
             VALUES ($1, $2, $3, $4, $5) RETURNING id, username, email, full_name, role_key`,
            [username, email, phone, passwordHash, full_name]
        );

        return result.rows[0];
    }

    async login(loginData) {
        const { email, password } = loginData;

        // Find user
        const result = await db.query(
            'SELECT * FROM users WHERE email = $1',
            [email]
        );

        const user = result.rows[0];
        if (!user) {
            const err = new Error('Invalid email or password');
            err.statusCode = 401;
            throw err;
        }

        // Check password
        const isMatch = await bcrypt.compare(password, user.password_hash);
        if (!isMatch) {
            const err = new Error('Invalid email or password');
            err.statusCode = 401;
            throw err;
        }

        // Generate JWT
        const token = jwt.sign(
            { id: user.id, username: user.username, role: user.role_key },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN }
        );

        // Remove password hash from response
        delete user.password_hash;

        return { user, token };
    }
}

module.exports = new AuthService();
