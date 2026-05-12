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
}

module.exports = new LoyaltyService();