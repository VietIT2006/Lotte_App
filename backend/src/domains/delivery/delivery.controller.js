const deliveryService = require('./delivery.service');
const { ApiError } = require('../../core/error.handler');

class DeliveryController {
    async getShipperOrders(req, res, next) {
        try {
            const { status } = req.query;
            const shipperId = req.user.id;
            const orders = await deliveryService.getShipperOrders(shipperId, status);
            res.json({ success: true, data: orders });
        } catch (error) {
            next(error);
        }
    }

    async updateOrderStatus(req, res, next) {
        try {
            const { id } = req.params;
            const { status, note, location } = req.body;
            const shipperId = req.user.id;
            const result = await deliveryService.updateOrderStatus(id, shipperId, status, note, location);
            res.json({ success: true, data: result, message: 'Cập nhật trạng thái thành công' });
        } catch (error) {
            next(error);
        }
    }

    async uploadEvidence(req, res, next) {
        try {
            const { id } = req.params;
            const { evidenceUrl } = req.body; // In real app, this might use multer
            const shipperId = req.user.id;
            const result = await deliveryService.uploadEvidence(id, shipperId, evidenceUrl);
            res.json({ success: true, data: result, message: 'Tải lên bằng chứng thành công' });
        } catch (error) {
            next(error);
        }
    }

    async updateLocation(req, res, next) {
        try {
            const { lat, lng } = req.body;
            const shipperId = req.user.id;
            await deliveryService.updateLocation(shipperId, { lat, lng });
            res.json({ success: true, message: 'Cập nhật vị trí thành công' });
        } catch (error) {
            next(error);
        }
    }

    async getWalletInfo(req, res, next) {
        try {
            const shipperId = req.user.id;
            const result = await deliveryService.getWalletInfo(shipperId);
            res.json({ success: true, data: result });
        } catch (error) {
            next(error);
        }
    }

    async updateShipperStatus(req, res, next) {
        try {
            const { status } = req.body;
            const shipperId = req.user.id;
            const result = await deliveryService.updateShipperStatus(shipperId, status);
            res.json({ success: true, data: result, message: 'Cập nhật trạng thái thành công' });
        } catch (error) {
            next(error);
        }
    }

    async topupWallet(req, res, next) {
        try {
            const { amount } = req.body;
            const shipperId = req.user.id;
            const result = await deliveryService.topupWallet(shipperId, amount);
            res.json({ success: true, data: result, message: 'Nạp tiền vào ví thành công' });
        } catch (error) {
            next(error);
        }
    }

    async getShippers(req, res, next) {
        try {
            // Require admin role check here in real app
            const shippers = await deliveryService.getShippers(req.query);
            res.json({ success: true, data: shippers });
        } catch (error) {
            next(error);
        }
    }

    async assignOrder(req, res, next) {
        try {
            const { id } = req.params;
            const { shipperId } = req.body;
            const result = await deliveryService.assignOrder(id, shipperId);
            res.json({ success: true, data: result, message: 'Gán đơn hàng thành công' });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new DeliveryController();
