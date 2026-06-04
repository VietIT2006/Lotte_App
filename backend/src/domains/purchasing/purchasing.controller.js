const purchasingService = require('./purchasing.service');

class PurchasingController {
    async getSuppliers(req, res, next) {
        try {
            const suppliers = await purchasingService.getSuppliers();
            res.status(200).json({ success: true, data: suppliers });
        } catch (error) { next(error); }
    }

    async getImportOrders(req, res, next) {
        try {
            const orders = await purchasingService.getImportOrders();
            res.status(200).json({ success: true, data: orders });
        } catch (error) { next(error); }
    }

    async createImportOrder(req, res, next) {
        try {
            const userId = req.user ? req.user.id : null;
            const newOrder = await purchasingService.createImportOrder(req.body, userId);
            res.status(201).json({ success: true, data: newOrder });
        } catch (error) { next(error); }
    }

    async receiveImportOrder(req, res, next) {
        try {
            const userId = req.user ? req.user.id : null;
            await purchasingService.receiveImportOrder(req.params.id, userId);
            res.status(200).json({ success: true, message: 'Đã nhận hàng và tạo lô hàng thành công' });
        } catch (error) { next(error); }
    }
}

module.exports = new PurchasingController();