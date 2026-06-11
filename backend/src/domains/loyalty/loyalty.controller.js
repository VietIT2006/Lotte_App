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

    async transferPoints(req, res, next) {
        try {
            const senderId = req.user.id;
            const { recipient, amount } = req.body;

            if (!recipient || !amount || Number(amount) <= 0) {
                return res.status(400).json({
                    success: false,
                    message: 'Thông tin chuyển điểm không hợp lệ (cần recipient và amount > 0)'
                });
            }

            const newPoints = await loyaltyService.transferPoints(senderId, recipient, Number(amount));
            res.status(200).json({
                success: true,
                message: 'Chuyển điểm thành công!',
                data: { new_points: newPoints }
            });
        } catch (error) {
            res.status(400).json({
                success: false,
                message: error.message
            });
        }
    }

    async topupPoints(req, res, next) {
        try {
            const userId = req.user.id;
            const { amount } = req.body;

            if (!amount || Number(amount) <= 0) {
                return res.status(400).json({
                    success: false,
                    message: 'Số điểm nạp không hợp lệ (amount > 0)'
                });
            }

            await loyaltyService.addPoints(userId, Number(amount), 'Nạp điểm thành viên Lotte Mart');
            const newPoints = await loyaltyService.getPointsBalance(userId);
            res.status(200).json({
                success: true,
                message: 'Nạp điểm thành công!',
                data: { new_points: newPoints }
            });
        } catch (error) {
            next(error);
        }
    }

    async getTransactionHistory(req, res, next) {
        try {
            const userId = req.user.id;
            const history = await loyaltyService.getTransactionHistory(userId);
            res.status(200).json({
                success: true,
                data: history.map(item => ({
                    id: item._id ? item._id.toString() : "",
                    amount: item.amount,
                    type: item.type, // 'EARNED', 'REDEEMED', 'TRANSFERRED_IN', 'TRANSFERRED_OUT'
                    reason: item.reason,
                    created_at: item.created_at
                }))
            });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new LoyaltyController();