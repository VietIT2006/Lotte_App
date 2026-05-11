const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../../core/db');

class AuthService {
    transformUser(user) {
        if (!user) return null;
        return {
            id: user._id ? user._id.toString() : "",
            username: user.username || "",
            email: user.email || "",
            full_name: user.full_name || "",
            phone: user.phone || "",
            avatar: user.avatar || "",
            role_key: user.role_key || (user.role_id === 1 ? "admin" : "customer"),
            lotte_points: Number(user.lotte_points) || 0,
            membership_level: user.membership_level || "Thành viên"
        };
    }

    async register(userData) {
        const { username, email, phone, password, full_name } = userData;

        const checkUser = await db.collection('users').findOne({
            $or: [{ email: email }, { username: username }]
        });

        if (checkUser) {
            const err = new Error('Username or email already exists');
            err.statusCode = 400;
            throw err;
        }

        const passwordHash = await bcrypt.hash(password, 10);

        const newUser = {
            username,
            email,
            phone,
            password_hash: passwordHash,
            full_name,
            role_id: 2,
            status: 'ACTIVE',
            is_active: true,
            lotte_points: 0,
            membership_level: 'Bạc',
            created_at: new Date(),
            updated_at: new Date()
        };

        const result = await db.collection('users').insertOne(newUser);
        return this.transformUser({ ...newUser, _id: result.insertedId });
    }

    async login(loginData) {
        const { email, password } = loginData;

        const user = await db.collection('users').findOne({ email: email });

        if (!user) {
            const err = new Error('Invalid email or password');
            err.statusCode = 401;
            throw err;
        }

        const isMatch = await bcrypt.compare(password, user.password_hash);
        if (!isMatch) {
            const err = new Error('Invalid email or password');
            err.statusCode = 401;
            throw err;
        }

        const token = jwt.sign(
            { id: user._id, username: user.username, role: user.role_id },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN }
        );

        return { user: this.transformUser(user), token };
    }
}

module.exports = new AuthService();
