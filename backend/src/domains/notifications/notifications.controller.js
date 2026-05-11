const notificationsService = require('./notifications.service');

class NotificationsController {
    async getNotifications(req, res, next) {
        try {
            const userId = req.user.id;
            const notifications = await notificationsService.getNotifications(userId);
            res.status(200).json({
                success: true,
                data: notifications
            });
        } catch (error) {
            next(error);
        }
    }

    async markAsRead(req, res, next) {
        try {
            const { id } = req.params;
            const result = await notificationsService.markAsRead(id);
            res.status(200).json({
                success: true,
                data: result
            });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new NotificationsController();