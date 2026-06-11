const promotionsService = require('./promotions.service');

class PromotionsController {
    async getPromotions(req, res, next) {
        try {
            const promotions = await promotionsService.getActivePromotions();
            res.status(200).json({
                success: true,
                data: promotions
            });
        } catch (error) {
            next(error);
        }
    }

    async getCoupons(req, res, next) {
        try {
            const coupons = await promotionsService.getActiveCoupons();
            res.status(200).json({
                success: true,
                data: coupons
            });
        } catch (error) {
            next(error);
        }
    }

    // --- SPIN EVENTS ---
    async getActiveSpinEvent(req, res, next) {
        try {
            const event = await promotionsService.getActiveSpinEvent();
            if (!event) {
                return res.status(404).json({ success: false, message: 'Không có sự kiện vòng quay nào đang diễn ra' });
            }
            res.status(200).json({ success: true, data: event });
        } catch (error) { next(error); }
    }

    async playSpinEvent(req, res, next) {
        try {
            const userId = req.user ? req.user.id : null;
            if (!userId) {
                return res.status(401).json({ success: false, message: 'Vui lòng đăng nhập' });
            }
            const result = await promotionsService.playSpinEvent(userId);
            if (!result.success) {
                return res.status(400).json({ success: false, message: result.message });
            }
            res.status(200).json({ success: true, data: result.data });
        } catch (error) { next(error); }
    }

    // --- ADMIN ---
    async getAdminPromotions(req, res, next) {
        try {
            const promotions = await promotionsService.getAdminPromotions();
            res.status(200).json({ success: true, data: promotions });
        } catch (error) { next(error); }
    }

    async createPromotion(req, res, next) {
        try {
            const newPromo = await promotionsService.createPromotion(req.body);
            res.status(201).json({ success: true, data: newPromo });
        } catch (error) { next(error); }
    }

    async updatePromotion(req, res, next) {
        try {
            const updatedPromo = await promotionsService.updatePromotion(req.params.id, req.body);
            if (!updatedPromo) {
                return res.status(404).json({ success: false, message: 'Not found' });
            }
            res.status(200).json({ success: true, data: updatedPromo });
        } catch (error) { next(error); }
    }

    async deletePromotion(req, res, next) {
        try {
            await promotionsService.deletePromotion(req.params.id);
            res.status(200).json({ success: true, message: 'Deleted successfully' });
        } catch (error) { next(error); }
    }

    // --- ADMIN COUPONS ---
    async getAdminCoupons(req, res, next) {
        try {
            const coupons = await promotionsService.getAdminCoupons();
            res.status(200).json({ success: true, data: coupons });
        } catch (error) { next(error); }
    }

    async createCoupon(req, res, next) {
        try {
            const newCoupon = await promotionsService.createCoupon(req.body);
            res.status(201).json({ success: true, data: newCoupon });
        } catch (error) { next(error); }
    }

    async updateCoupon(req, res, next) {
        try {
            const updatedCoupon = await promotionsService.updateCoupon(req.params.id, req.body);
            if (!updatedCoupon) {
                return res.status(404).json({ success: false, message: 'Not found' });
            }
            res.status(200).json({ success: true, data: updatedCoupon });
        } catch (error) { next(error); }
    }

    async deleteCoupon(req, res, next) {
        try {
            await promotionsService.deleteCoupon(req.params.id);
            res.status(200).json({ success: true, message: 'Deleted successfully' });
        } catch (error) { next(error); }
    }
}

module.exports = new PromotionsController();