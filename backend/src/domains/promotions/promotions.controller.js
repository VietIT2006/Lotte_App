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
}

module.exports = new PromotionsController();