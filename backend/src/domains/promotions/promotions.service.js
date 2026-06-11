const db = require('../../core/db');

class PromotionsService {
    transformPromotion(item) {
        if (!item) return null;
        return {
            id: item._id ? item._id.toString() : "",
            title: item.title || "",
            description: item.description || item.subtitle || "",
            banner_image: item.banner_image || item.image_url || item.mobile_image_url || item.image || "",
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
        let promotions = await db.collection('promotions')
            .find({ is_active: true })
            .sort({ created_at: -1 })
            .toArray();
        
        // If there are no promotions or they contain localhost placeholder URLs, seed/refresh with the Cloudinary assets
        const hasLocalhostOrPlaceholder = promotions.some(p => {
            const url = p.banner_image || p.image || "";
            return url.includes("localhost") || url.includes("via.placeholder.com") || url.includes("placeholder");
        });

        if (promotions.length === 0 || hasLocalhostOrPlaceholder) {
            try {
                // Clear out localhost/placeholder promotions to avoid broken images on devices
                if (hasLocalhostOrPlaceholder) {
                    await db.collection('promotions').deleteMany({
                        $or: [
                            { banner_image: { $regex: /localhost|via\.placeholder\.com/ } },
                            { image: { $regex: /localhost|via\.placeholder\.com/ } }
                        ]
                    });
                }

                const defaultPromos = [
                    {
                        title: "Lotte Mart Banner 1",
                        description: "Khuyến mãi đặc biệt từ Lotte Mart",
                        banner_image: "https://res.cloudinary.com/dkzrqnahy/image/upload/v1780995391/OIP_1_q92slk.webp",
                        type: "percent",
                        is_active: true,
                        created_at: new Date(),
                        updated_at: new Date()
                    },
                    {
                        title: "Lotte Mart Banner 2",
                        description: "Mua sắm thả ga, nhận quà cực đã",
                        banner_image: "https://res.cloudinary.com/dkzrqnahy/image/upload/v1780995392/OIP_nji4cc.webp",
                        type: "percent",
                        is_active: true,
                        created_at: new Date(),
                        updated_at: new Date()
                    },
                    {
                        title: "Lotte Mart Banner 3",
                        description: "Ưu đãi ngập trạng mỗi ngày",
                        banner_image: "https://res.cloudinary.com/dkzrqnahy/image/upload/v1780995392/OIP_2_emj2gt.webp",
                        type: "percent",
                        is_active: true,
                        created_at: new Date(),
                        updated_at: new Date()
                    },
                    {
                        title: "Lotte Mart Logo",
                        description: "Lotte Mart siêu thị của mọi nhà",
                        banner_image: "https://res.cloudinary.com/dkzrqnahy/image/upload/v1780995392/logo-lotte-mart_kkhoso.jpg",
                        type: "percent",
                        is_active: true,
                        created_at: new Date(),
                        updated_at: new Date()
                    },
                    {
                        title: "Lotte Mart Biểu tượng",
                        description: "Biểu tượng Lotte Mart thương hiệu uy tín",
                        banner_image: "https://res.cloudinary.com/dkzrqnahy/image/upload/v1780995392/bieu-tuong-lotte_fhyde2.jpg",
                        type: "percent",
                        is_active: true,
                        created_at: new Date(),
                        updated_at: new Date()
                    }
                ];
                
                // Only insert if those images aren't already in the database
                for (const promo of defaultPromos) {
                    const existing = await db.collection('promotions').findOne({ banner_image: promo.banner_image });
                    if (!existing) {
                        await db.collection('promotions').insertOne(promo);
                    }
                }

                promotions = await db.collection('promotions')
                    .find({ is_active: true })
                    .sort({ created_at: -1 })
                    .toArray();
            } catch (err) {
                console.error("Error seeding promotions: ", err);
            }
        }
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