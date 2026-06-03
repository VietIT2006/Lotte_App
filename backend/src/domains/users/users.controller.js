const usersService = require('./users.service');

class UsersController {
    async getProfile(req, res, next) {
        try {
            const userId = req.user.id;
            const user = await usersService.getProfile(userId);
            res.status(200).json({
                success: true,
                data: user
            });
        } catch (error) {
            next(error);
        }
    }

    async updateProfile(req, res, next) {
        try {
            const userId = req.user.id;
            const user = await usersService.updateProfile(userId, req.body);
            res.status(200).json({
                success: true,
                message: 'Profile updated successfully',
                data: user
            });
        } catch (error) {
            next(error);
        }
    }

    async getUsers(req, res, next) {
        try {
            // Lấy role của user đang request (được gán từ middleware sau khi verify token)
            // Trong auth.service.js có lưu role_key trong token. Giả sử req.user có role
            const userRole = req.user.role || (req.user.role_id === 1 ? 'admin' : 'customer');
            
            // Nếu là admin hoặc superAdmin
            if (userRole === 'admin' || userRole === 'superAdmin') {
                const users = await usersService.getAllUsers(userRole);
                return res.status(200).json({
                    success: true,
                    data: users
                });
            } else {
                return res.status(403).json({
                    success: false,
                    message: "Forbidden: You don't have permission to access this resource"
                });
            }
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new UsersController();