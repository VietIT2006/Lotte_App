const { ObjectId } = require('mongodb');
const db = require('../../core/db');

class UsersService {
    toId(id) {
        try {
            return new ObjectId(id);
        } catch (e) {
            return id;
        }
    }

    transformUser(user) {
        if (!user) return null;
        return {
            id: user._id ? user._id.toString() : "",
            username: user.username || "",
            full_name: user.full_name || "",
            email: user.email || "",
            phone: user.phone || "",
            avatar: user.avatar || "",
            role_key: user.role_key || (user.role_id === 1 ? "admin" : "customer"),
            lotte_points: Number(user.lotte_points) || 0,
            membership_level: user.membership_level || "Thành viên",
            address: user.address || ""
        };
    }

    async getProfile(userId) {
        const query = { $or: [{ _id: userId }, { _id: this.toId(userId) }] };
        const user = await db.collection('users').findOne(query);
        return this.transformUser(user);
    }

    async updateProfile(userId, updateData) {
        const { full_name, phone, address, avatar } = updateData;
        const updateFields = { updated_at: new Date() };

        if (full_name) updateFields.full_name = full_name;
        if (phone) updateFields.phone = phone;
        if (address) updateFields.address = address;
        if (avatar) updateFields.avatar = avatar;

        const query = { $or: [{ _id: userId }, { _id: this.toId(userId) }] };
        await db.collection('users').updateOne(
            query,
            { $set: updateFields }
        );

        return this.getProfile(userId);
    }
}

module.exports = new UsersService();