const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../../core/db');

class AuthService {
    getDefaultUserFields() {
        return {
            avatar: null,
            role_id: 2,
            role_key: "customer",
            permissions: [],
            branch_id: null,
            lotte_points: 0,
            membership_level: "Bạc",
            signup_method: "email",
            login_provider: "local",
            authProviders: ["local"],
            googleId: null,
            facebookId: null,
            facebook_id: null,
            social_links: { facebook: null, google: null },
            status: "ACTIVE",
            is_active: true,
            profile_completed: false,
            wallet_balance: 0,
            default_payment_method: null,
            email_verified: false,
            email_verification_code: null,
            email_verification_expires_at: null,
            email_verification_attempts: 0,
            email_otp_last_sent_at: null,
            dob: null,
            gender: null,
            address: null,
            bio: null,
            note: "",
            tags: [],
            preferences: {
                newsletter: true,
                sms_alerts: true,
                language: "vi",
                receive_promotions: true,
                eco_prefer: false,
                favorite_categories: [],
                preferred_store: null,
                notification_email_promo: true,
                notification_sms_order: true,
                notification_push_order: true,
                notification_promo: true,
                notification_system: true
            },
            password_changed_at: null,
            security: {
                two_factor_enabled: false,
                two_factor_method: null,
                totp_secret: null,
                backup_codes: [],
                last_login_device: "",
                last_login_ip: "",
                last_login_at: null
            },
            settings: {
                language: "vi",
                dark_mode: false,
                privacy_profile_visible: true,
                marketing_opt_in: true,
                sms_opt_in: true
            },
            last_login_at: null,
            refresh_token: null,
            is_deleted: false,
            force_password_change: false,
            employee_info: {
                employee_code: null,
                department: null,
                work_type: "FULL_TIME",
                notes: ""
            },
            social_providers: []
        };
    }

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
            ...this.getDefaultUserFields(),
            username,
            email,
            phone: phone || "",
            password_hash: passwordHash,
            full_name,
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

    async socialLogin(socialData) {
        const { email, full_name, avatar } = socialData;

        // Tìm user theo email (trường đã có sẵn)
        let user = await db.collection('users').findOne({ email: email });

        if (!user) {
            // Nếu chưa có user, tạo mới dựa trên các trường hiện có
            const newUser = {
                ...this.getDefaultUserFields(),
                username: email.split('@')[0] + '_' + Math.floor(Math.random() * 1000),
                email,
                phone: "",
                password_hash: await bcrypt.hash(Math.random().toString(36), 10),
                full_name,
                avatar: avatar || null,
                signup_method: "social",
                login_provider: "social",
                authProviders: ["social"],
                social_providers: ["social"],
                created_at: new Date(),
                updated_at: new Date()
            };

            const result = await db.collection('users').insertOne(newUser);
            user = { ...newUser, _id: result.insertedId };
        } else {
            // Nếu đã có user, chỉ cập nhật avatar nếu họ chưa có
            if (!user.avatar && avatar) {
                await db.collection('users').updateOne(
                    { _id: user._id },
                    { $set: { avatar: avatar, updated_at: new Date() } }
                );
                user.avatar = avatar;
            }
        }

        const token = jwt.sign(
            { id: user._id, username: user.username, role: user.role_id },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN }
        );

        return { user: this.transformUser(user), token };
    }

    async forgotPassword(email) {
        const user = await db.collection('users').findOne({ email: email });
        if (!user) {
            const err = new Error('Không tìm thấy tài khoản với email này');
            err.statusCode = 404;
            throw err;
        }

        // Generate 6-digit OTP
        const otp = Math.floor(100000 + Math.random() * 900000).toString();
        const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutes

        // Save OTP to DB
        await db.collection('users').updateOne(
            { _id: user._id },
            { 
                $set: { 
                    email_verification_code: otp,
                    email_verification_expires_at: expiresAt,
                    updated_at: new Date()
                } 
            }
        );

        // Send email
        const nodemailer = require('nodemailer');
        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: process.env.SMTP_EMAIL,
                pass: process.env.SMTP_PASSWORD
            }
        });

        const mailOptions = {
            from: `"Lotte Mart App" <${process.env.SMTP_EMAIL}>`,
            to: email,
            subject: 'Lotte Mart - Mã xác nhận quên mật khẩu',
            html: `
                <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                    <h2>Xin chào ${user.full_name || 'bạn'},</h2>
                    <p>Bạn đã yêu cầu khôi phục mật khẩu. Dưới đây là mã xác nhận (OTP) 6 số của bạn:</p>
                    <h1 style="color: #D32F2F; letter-spacing: 5px;">${otp}</h1>
                    <p>Mã này sẽ hết hạn trong vòng 5 phút.</p>
                    <p>Nếu bạn không yêu cầu thay đổi mật khẩu, vui lòng bỏ qua email này.</p>
                    <hr/>
                    <p style="font-size: 12px; color: #777;">Đội ngũ Lotte Mart</p>
                </div>
            `
        };

        try {
            await transporter.sendMail(mailOptions);
        } catch (error) {
            console.error('Error sending email:', error);
            const err = new Error('Lỗi khi gửi email. Vui lòng kiểm tra lại cấu hình SMTP.');
            err.statusCode = 500;
            throw err;
        }

        return { message: 'OTP sent successfully' };
    }

    async resetPassword(email, otp, newPassword) {
        const user = await db.collection('users').findOne({ email: email });
        if (!user) {
            const err = new Error('Không tìm thấy tài khoản');
            err.statusCode = 404;
            throw err;
        }

        if (user.email_verification_code !== otp) {
            const err = new Error('Mã OTP không chính xác');
            err.statusCode = 400;
            throw err;
        }

        if (new Date() > user.email_verification_expires_at) {
            const err = new Error('Mã OTP đã hết hạn');
            err.statusCode = 400;
            throw err;
        }

        // Update password
        const passwordHash = await bcrypt.hash(newPassword, 10);
        await db.collection('users').updateOne(
            { _id: user._id },
            { 
                $set: { 
                    password_hash: passwordHash,
                    password_changed_at: new Date(),
                    updated_at: new Date(),
                    email_verification_code: null, // Clear OTP
                    email_verification_expires_at: null
                } 
            }
        );

        return { message: 'Password reset successfully' };
    }
}

module.exports = new AuthService();
