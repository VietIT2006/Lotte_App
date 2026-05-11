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
}

module.exports = new PromotionsService();