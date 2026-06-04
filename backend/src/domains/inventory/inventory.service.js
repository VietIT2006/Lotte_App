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

    // --- BATCHES ---
    async getAdminBatches() {
        const batches = await db.collection('inventorybatches')
            .find({})
            .sort({ received_date: -1, created_at: -1 })
            .toArray();
        
        // Populate product names if needed, but we might only have product_id. Let's do it.
        const productIds = batches.map(b => db.toId(b.product_id)).filter(id => id);
        const products = await db.collection('products').find({ _id: { $in: productIds } }).toArray();
        const productMap = {};
        products.forEach(p => productMap[p._id.toString()] = p.name);

        return batches.map(b => {
            return {
                id: b._id ? b._id.toString() : "",
                batch_code: b.batch_code || "",
                product_id: b.product_id ? b.product_id.toString() : "",
                product_name: b.product_id && productMap[b.product_id.toString()] ? productMap[b.product_id.toString()] : "Sản phẩm",
                supplier_name: b.supplier_name || "",
                quantity: Number(b.quantity) || 0,
                original_quantity: Number(b.original_quantity) || 0,
                cost_price: Number(b.cost_price) || 0,
                received_date: b.received_date || b.created_at || new Date(),
                exp_date: b.exp_date || null
            };
        });
    }
}

module.exports = new InventoryService();