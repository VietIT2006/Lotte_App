const paymentsService = require('./payments.service');

class PaymentsController {
    async createPayosLink(req, res, next) {
        try {
            const { orderId, amount } = req.body;
            const userId = req.user.id;
            const result = await paymentsService.createPayosLink({
                orderId,
                amount,
                description: `Thanh toan don ${orderId.substring(orderId.length - 6)}`,
                type: 'order',
                userId
            });
            res.json({ success: true, data: result });
        } catch (error) {
            next(error);
        }
    }

    async createWalletTopupLink(req, res, next) {
        try {
            const { amount } = req.body;
            const userId = req.user.id;
            const result = await paymentsService.createPayosLink({
                amount,
                description: 'Nap vi tai xe Lotte',
                type: 'wallet_topup',
                userId
            });
            res.json({ success: true, data: result });
        } catch (error) {
            next(error);
        }
    }

    async confirmPayment(req, res, next) {
        try {
            const { orderCode } = req.params;
            const result = await paymentsService.confirmPayment(orderCode);
            if (result.success) {
                res.json({ success: true, message: 'Xác nhận thanh toán thành công' });
            } else {
                res.status(400).json({ success: false, message: result.message });
            }
        } catch (error) {
            next(error);
        }
    }

    async handleWebhook(req, res, next) {
        try {
            // Webhook payload from PayOS
            const { orderCode } = req.body.data || req.body;
            if (orderCode) {
                await paymentsService.confirmPayment(orderCode);
            }
            res.json({ success: true });
        } catch (error) {
            next(error);
        }
    }

    async savePaymentTransaction(req, res, next) {
        try {
            const result = await paymentsService.savePaymentTransaction(req.body);
            res.json({ success: true, data: result });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new PaymentsController();