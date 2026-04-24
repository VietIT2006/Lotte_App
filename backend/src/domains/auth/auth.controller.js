const authService = require('./auth.service');

class AuthController {
    async register(req, res, next) {
        try {
            const user = await authService.register(req.body);
            res.status(201).json({
                success: true,
                message: 'User registered successfully',
                data: user
            });
        } catch (error) {
            next(error);
        }
    }

    async login(req, res, next) {
        try {
            const { user, token } = await authService.login(req.body);
            res.status(200).json({
                success: true,
                message: 'Login successful',
                data: { user, token }
            });
        } catch (error) {
            next(error);
        }
    }

    async verifyOtp(req, res, next) {
        try {
            // Mock OTP verification since SMS is generally external
            const { otp } = req.body;
            if (otp !== '1234') {
                return res.status(400).json({ success: false, message: 'Invalid OTP' });
            }
            res.status(200).json({ success: true, message: 'OTP verified successfully' });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new AuthController();
