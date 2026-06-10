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
        let notifications = await db.collection('notifications')
            .find({ user_id: userId })
            .sort({ created_at: -1 })
            .toArray();

        if (notifications.length === 0) {
            try {
                const defaultNotifications = [
                    {
                        user_id: userId,
                        title: "Khuyến mãi chào hè - Giảm đến 50%",
                        message: "Đại tiệc siêu thương hiệu Lotte Mart giảm sâu 50% các mặt hàng tươi sống và đồ gia dụng từ ngày 10/06 đến 20/06.",
                        type: "PROMO",
                        is_read: false,
                        created_at: new Date(),
                        updated_at: new Date()
                    },
                    {
                        user_id: userId,
                        title: "Đơn hàng đang giao đến bạn",
                        message: "Đơn hàng Lotte Mart số #LM-982761 của bạn đã được đóng gói và đang được tài xế vận chuyển giao đến địa chỉ của bạn.",
                        type: "ORDER",
                        is_read: false,
                        created_at: new Date(Date.now() - 30 * 60 * 1000),
                        updated_at: new Date(Date.now() - 30 * 60 * 1000)
                    },
                    {
                        user_id: userId,
                        title: "Tích lũy L-Point thành công",
                        message: "Chúc mừng! Bạn đã nhận được +1,200 L-Point từ hóa đơn mua sắm siêu thị ngày hôm qua. Hãy tiếp tục tích lũy để đổi quà nhé.",
                        type: "SYSTEM",
                        is_read: true,
                        created_at: new Date(Date.now() - 24 * 60 * 60 * 1000),
                        updated_at: new Date(Date.now() - 24 * 60 * 60 * 1000)
                    },
                    {
                        user_id: userId,
                        title: "Chào mừng thành viên mới!",
                        message: "Chào mừng bạn đến với ứng dụng đi chợ online của Lotte Mart. Nhập mã LOTTENEW để được giảm ngay 50.000đ cho đơn hàng đầu tiên.",
                        type: "SYSTEM",
                        is_read: true,
                        created_at: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
                        updated_at: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)
                    }
                ];
                await db.collection('notifications').insertMany(defaultNotifications);
                notifications = await db.collection('notifications')
                    .find({ user_id: userId })
                    .sort({ created_at: -1 })
                    .toArray();
            } catch (err) {
                console.error("Error seeding default notifications: ", err);
            }
        }
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