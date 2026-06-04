const db = require('../../core/db');

class PromotionsService {
    transformPromotion(item) {
        if (!item) return null;
        return {
            id: item._id ? item._id.toString() : "",
            title: item.title || "",
            description: item.description || "",
            banner_image: item.banner_image || item.image || "",
            type: item.type || "percent"
        };
    }

    transformCoupon(item) {
        if (!item) return null;
        return {
            id: item._id ? item._id.toString() : "",
            code: item.code || "",
            title: item.title || "",
            discount_value: Number(item.discount_value) || 0,
            image: item.image || ""
        };
    }

    async getActivePromotions() {
        const promotions = await db.collection('promotions')
            .find({ is_active: true })
            .sort({ created_at: -1 })
            .toArray();
        return promotions.map(p => this.transformPromotion(p));
    }

    async getActiveCoupons() {
        const coupons = await db.collection('coupons')
            .find({ is_active: true })
            .sort({ created_at: -1 })
            .toArray();
        return coupons.map(c => this.transformCoupon(c));
    }

    // --- ADMIN PROMOTIONS ---
    async getAdminPromotions() {
        const promotions = await db.collection('promotions')
            .find({})
            .sort({ created_at: -1 })
            .toArray();
        return promotions.map(p => {
            const transformed = this.transformPromotion(p);
            transformed.is_active = p.is_active !== false; // Default to true if not set
            return transformed;
        });
    }

    async createPromotion(data) {
        const newPromo = {
            title: data.title || "Khuyến mãi mới",
            description: data.description || "",
            banner_image: data.banner_image || data.image || "https://via.placeholder.com/800x400",
            type: data.type || "percent",
            is_active: data.is_active !== false,
            created_at: new Date(),
            updated_at: new Date()
        };
        const result = await db.collection('promotions').insertOne(newPromo);
        return { ...this.transformPromotion(newPromo), id: result.insertedId.toString(), is_active: newPromo.is_active };
    }

    async updatePromotion(id, data) {
        const query = { _id: this.toId(id) };
        const updateData = {
            ...data,
            updated_at: new Date()
        };
        // Remove id if it's there
        delete updateData.id;
        delete updateData._id;

        const result = await db.collection('promotions').findOneAndUpdate(
            query,
            { $set: updateData },
            { returnDocument: 'after' }
        );
        if (!result.value && !result) return null;
        const updated = result.value || result;
        return { ...this.transformPromotion(updated), is_active: updated.is_active };
    }

    async deletePromotion(id) {
        const query = { _id: this.toId(id) };
        await db.collection('promotions').deleteOne(query);
        return true;
    }

    // --- ADMIN COUPONS ---
    async getAdminCoupons() {
        const coupons = await db.collection('coupons')
            .find({})
            .sort({ created_at: -1 })
            .toArray();
        return coupons.map(c => {
            const transformed = this.transformCoupon(c);
            transformed.is_active = c.is_active !== false;
            return transformed;
        });
    }

    async createCoupon(data) {
        const newCoupon = {
            code: data.code || "",
            title: data.title || "Coupon",
            discount_value: Number(data.discount_value) || 0,
            image: data.image || "https://via.placeholder.com/400x200",
            is_active: data.is_active !== false,
            created_at: new Date(),
            updated_at: new Date()
        };
        const result = await db.collection('coupons').insertOne(newCoupon);
        return { ...this.transformCoupon(newCoupon), id: result.insertedId.toString(), is_active: newCoupon.is_active };
    }

    async updateCoupon(id, data) {
        const query = { _id: this.toId(id) };
        const updateData = {
            ...data,
            updated_at: new Date()
        };
        delete updateData.id;
        delete updateData._id;

        const result = await db.collection('coupons').findOneAndUpdate(
            query,
            { $set: updateData },
            { returnDocument: 'after' }
        );
        if (!result.value && !result) return null;
        const updated = result.value || result;
        return { ...this.transformCoupon(updated), is_active: updated.is_active };
    }

    async deleteCoupon(id) {
        const query = { _id: this.toId(id) };
        await db.collection('coupons').deleteOne(query);
        return true;
    }

    toId(id) {
        try {
            const { ObjectId } = require('mongodb');
            return new ObjectId(id);
        } catch (e) {
            return id;
        }
    }
}

module.exports = new PromotionsService();