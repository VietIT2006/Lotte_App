const db = require('../../core/db');

class LoyaltyService {
    async getPointsBalance(userId) {
        const user = await db.collection('users').findOne({ _id: db.toId(userId) });
        return user ? (user.lotte_points || 0) : 0;
    }

    async getTransactionHistory(userId) {
        return await db.collection('loyalty_transactions')
            .find({ user_id: db.toId(userId) })
            .sort({ created_at: -1 })
            .toArray();
    }

    async addPoints(userId, amount, reason) {
        await db.collection('users').updateOne(
            { _id: db.toId(userId) },
            { $inc: { lotte_points: amount } }
        );

        return await db.collection('loyalty_transactions').insertOne({
            user_id: db.toId(userId),
            amount,
            type: 'EARNED',
            reason,
            created_at: new Date()
        });
    }

    async redeemPoints(userId, points, voucherCode, voucherTitle) {
        const currentPoints = await this.getPointsBalance(userId);
        if (currentPoints < points) {
            throw new Error('Không đủ điểm L.POINT để đổi voucher này!');
        }

        // 1. Trừ điểm người dùng
        await db.collection('users').updateOne(
            { _id: db.toId(userId) },
            { $inc: { lotte_points: -points } }
        );

        // 2. Ghi lịch sử giao dịch điểm
        await db.collection('loyalty_transactions').insertOne({
            user_id: db.toId(userId),
            amount: -points,
            type: 'REDEEMED',
            reason: `Đổi ${points} điểm lấy ${voucherTitle}`,
            created_at: new Date()
        });

        // 3. Tìm coupon hoặc tạo bản ghi claim coupon cho người dùng
        let coupon = await db.collection('coupons').findOne({ code: voucherCode });
        if (!coupon) {
            // Tạo mock coupon nếu chưa có trong DB để người dùng áp dụng được
            const discountVal = voucherCode.includes('20') ? 20000 : 10000;
            const insertResult = await db.collection('coupons').insertOne({
                code: voucherCode,
                title: voucherTitle,
                description: `Voucher đổi điểm: ${voucherTitle}`,
                type: 'discount',
                discount_value: discountVal,
                min_order_amount: 50000,
                is_active: true,
                created_at: new Date()
            });
            coupon = { _id: insertResult.insertedId, code: voucherCode };
        }

        // Tự động gán Voucher vào ví coupon của user (couponclaims)
        await db.collection('couponclaims').insertOne({
            coupon_id: coupon._id,
            user_id: db.toId(userId),
            status: 'claimed',
            claimed_at: new Date(),
            created_at: new Date()
        });

        const newPoints = currentPoints - points;
        return {
            newPoints,
            message: 'Đổi điểm thành công! Voucher đã được lưu vào ví cá nhân.'
        };
    }

    async transferPoints(senderId, recipientIdentifier, amount) {
        const sender = await db.collection('users').findOne({ _id: db.toId(senderId) });
        if (!sender) throw new Error('Không tìm thấy người gửi!');

        const senderPoints = sender.lotte_points || 0;
        if (senderPoints < amount) {
            throw new Error('Số dư điểm L.POINT không đủ!');
        }

        const recipient = await db.collection('users').findOne({
            $or: [
                { email: recipientIdentifier },
                { phone: recipientIdentifier },
                { username: recipientIdentifier }
            ]
        });

        if (!recipient) {
            throw new Error('Không tìm thấy người nhận!');
        }

        if (recipient._id.toString() === sender._id.toString()) {
            throw new Error('Không thể tự chuyển điểm cho chính mình!');
        }

        await db.collection('users').updateOne(
            { _id: sender._id },
            { $inc: { lotte_points: -amount } }
        );

        await db.collection('users').updateOne(
            { _id: recipient._id },
            { $inc: { lotte_points: amount } }
        );

        await db.collection('loyalty_transactions').insertOne({
            user_id: sender._id,
            amount: -amount,
            type: 'TRANSFERRED_OUT',
            reason: `Chuyển điểm đến ${recipient.full_name || recipient.email}`,
            created_at: new Date()
        });

        await db.collection('loyalty_transactions').insertOne({
            user_id: recipient._id,
            amount: amount,
            type: 'TRANSFERRED_IN',
            reason: `Nhận điểm từ ${sender.full_name || sender.email}`,
            created_at: new Date()
        });

        return senderPoints - amount;
    }
}

module.exports = new LoyaltyService();