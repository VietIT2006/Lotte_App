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
}

module.exports = new UsersController();