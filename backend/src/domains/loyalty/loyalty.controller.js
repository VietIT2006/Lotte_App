const loyaltyService = require('./loyalty.service');

class LoyaltyController {
    async getPointsBalance(req, res, next) {
        try {
            const userId = req.user.id;
            const points = await loyaltyService.getPointsBalance(userId);
            res.status(200).json({
                success: true,
                data: { points }
            });
        } catch (error) {
            next(error);
        }
    }

    async redeemPoints(req, res, next) {
        try {
            const userId = req.user.id;
            const { points, voucher_code, voucher_title } = req.body;
            
            if (!points || !voucher_code || !voucher_title) {
                return res.status(400).json({
                    success: false,
                    message: 'Thiếu thông tin đổi điểm (points, voucher_code, voucher_title)'
                });
            }

            const result = await loyaltyService.redeemPoints(userId, Number(points), voucher_code, voucher_title);
            res.status(200).json({
                success: true,
                message: result.message,
                data: {
                    new_points: result.newPoints
                }
            });
        } catch (error) {
            res.status(400).json({
                success: false,
                message: error.message
            });
        }
    }
}

module.exports = new LoyaltyController();