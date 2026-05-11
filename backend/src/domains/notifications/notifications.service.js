const { ObjectId } = require('mongodb');
const db = require('../../core/db');

class NotificationsService {
    transform(item) {
        if (!item) return null;
        const result = { ...item, id: item._id };
        delete result._id;
        return result;
    }

    async getNotifications(userId) {
        const notifications = await db.collection('notifications')
            .find({ user_id: userId })
            .sort({ created_at: -1 })
            .toArray();
        return notifications.map(n => this.transform(n));
    }

    async markAsRead(notificationId) {
        await db.collection('notifications').updateOne(
            { _id: new ObjectId(notificationId) },
            { $set: { is_read: true, updated_at: new Date() } }
        );
        return { success: true };
    }
}

module.exports = new NotificationsService();