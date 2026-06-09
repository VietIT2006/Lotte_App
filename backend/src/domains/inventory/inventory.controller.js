const inventoryService = require('./inventory.service');

class InventoryController {
    async getAdminBatches(req, res, next) {
        try {
            const batches = await inventoryService.getAdminBatches();
            res.status(200).json({ success: true, data: batches });
        } catch (error) { next(error); }
    }

    async getAdminStockMovements(req, res, next) {
        try {
            const movements = await inventoryService.getAdminStockMovements();
            res.status(200).json({ success: true, data: movements });
        } catch (error) { next(error); }
    }
}

module.exports = new InventoryController();