const { ObjectId } = require('mongodb');
const db = require('../../core/db');

class CustomerServiceService {
    async createComplaint(userId, subject, message) {
        const complaint = {
            user_id: new ObjectId(userId),
            subject,
            message,
            status: 'PENDING',
            created_at: new Date(),
            updated_at: new Date()
        };
        const result = await db.collection('complaints').insertOne(complaint);
        return { _id: result.insertedId, ...complaint };
    }

    async getComplaints() {
        // Lấy danh sách kèm theo thông tin user nếu cần
        const complaints = await db.collection('complaints').aggregate([
            {
                $lookup: {
                    from: 'users',
                    localField: 'user_id',
                    foreignField: '_id',
                    as: 'user'
                }
            },
            {
                $unwind: { path: '$user', preserveNullAndEmptyArrays: true }
            },
            {
                $sort: { created_at: -1 }
            }
        ]).toArray();
        
        return complaints.map(c => ({
            id: c._id.toString(),
            user_id: c.user_id.toString(),
            subject: c.subject,
            message: c.message,
            status: c.status,
            created_at: c.created_at,
            user: c.user ? {
                id: c.user._id.toString(),
                full_name: c.user.full_name,
                email: c.user.email,
                avatar: c.user.avatar,
                role: c.user.role_key || (c.user.role_id === 1 ? 'admin' : 'customer')
            } : null
        }));
    }
}

module.exports = new CustomerServiceService();