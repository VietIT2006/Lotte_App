const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../../core/db');

class AuthService {
    async register(userData) {
        const { username, email, phone, password, full_name } = userData;
        const usersCollection = db.collection('users');

        // Check if user exists
        const existingUser = await usersCollection.findOne({
            $or: [{ email }, { username }]
        });

        if (existingUser) {
            const err = new Error('Username or email already exists');
            err.statusCode = 400;
            throw err;
        }

        // Hash password
        const passwordHash = await bcrypt.hash(password, 10);

        // Insert new user
        const newUser = {
            username,
            email,
            phone,
            password_hash: passwordHash,
            full_name,
            role_key: 'user', // Default role
            created_at: new Date(),
            updated_at: new Date()
        };

        const result = await usersCollection.insertOne(newUser);
        
        // Trả về user (không kèm password_hash)
        const savedUser = { _id: result.insertedId, ...newUser };
        delete savedUser.password_hash;
        
        return savedUser;
    }

    async login(loginData) {
        const { email, password } = loginData;
        const usersCollection = db.collection('users');

        // Find user
        const user = await usersCollection.findOne({ email });

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
            { id: user._id, username: user.username, role: user.role_key },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN }
        );

        // Remove password hash from response
        const userResponse = { ...user };
        delete userResponse.password_hash;

        return { user: userResponse, token };
    }
}

module.exports = new AuthService();

