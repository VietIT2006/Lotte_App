const db = require('../../core/db');

class InventoryService {
    async getBranches() {
        return await db.collection('branches').find({ is_active: true }).toArray();
    }

    async getBranchById(id) {
        return await db.collection('branches').findOne({ _id: db.toId(id) });
    }

    async getProductStock(branchId, productId) {
        const stock = await db.collection('inventory').findOne({
            branch_id: db.toId(branchId),
            product_id: db.toId(productId)
        });
        return stock ? stock.quantity : 0;
    }

    async updateStock(branchId, productId, quantityDelta) {
        return await db.collection('inventory').updateOne(
            { 
                branch_id: db.toId(branchId),
                product_id: db.toId(productId)
            },
            { $inc: { quantity: quantityDelta }, $set: { updated_at: new Date() } },
            { upsert: true }
        );
    }
}

module.exports = new InventoryService();