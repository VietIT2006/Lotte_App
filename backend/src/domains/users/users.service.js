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

    async getAllUsers(requestRole) {
        let query = {};
        if (requestRole === 'admin') {
            // Admin thường chỉ thấy khách hàng
            query = { role_key: 'customer' }; 
        }
        // SuperAdmin (hoặc các role khác có quyền) sẽ lấy hết vì query rỗng
        
        const users = await db.collection('users').find(query).toArray();
        return users.map(user => this.transformUser(user));
    }

    async updateUserRole(userId, newRole) {
        const query = { $or: [{ _id: userId }, { _id: this.toId(userId) }] };
        const updateFields = { 
            role_key: newRole,
            updated_at: new Date() 
        };

        // Also update role_id for backward compatibility
        if (newRole === 'superAdmin') updateFields.role_id = 1;
        else if (newRole === 'admin') updateFields.role_id = 1;
        else if (newRole === 'shipper') updateFields.role_id = 3;
        else updateFields.role_id = 2; // customer

        await db.collection('users').updateOne(
            query,
            { $set: updateFields }
        );

        return this.getProfile(userId);
    }
}

module.exports = new UsersService();