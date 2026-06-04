const db = require('../../core/db');

class PurchasingService {
    // --- SUPPLIERS ---
    async getSuppliers() {
        const suppliers = await db.collection('suppliers').find({}).toArray();
        return suppliers.map(s => this.transformSupplier(s));
    }

    transformSupplier(item) {
        if (!item) return null;
        return {
            id: item._id ? item._id.toString() : "",
            code: item.code || "",
            name: item.name || "",
            contact_name: item.contact_name || "",
            phone: item.phone || "",
            email: item.email || "",
            address: item.address || "",
            is_active: item.is_active !== false
        };
    }

    // --- IMPORT ORDERS ---
    async getImportOrders() {
        const orders = await db.collection('importorders')
            .find({})
            .sort({ created_at: -1 })
            .toArray();
        
        // Populate supplier names
        const supplierIds = orders.map(o => db.toId(o.supplier_id)).filter(id => id);
        const suppliers = await db.collection('suppliers').find({ _id: { $in: supplierIds } }).toArray();
        const supplierMap = {};
        suppliers.forEach(s => supplierMap[s._id.toString()] = s.name);

        return orders.map(o => {
            const transformed = this.transformImportOrder(o);
            if (o.supplier_id && supplierMap[o.supplier_id.toString()]) {
                transformed.supplier_name = supplierMap[o.supplier_id.toString()];
            } else {
                transformed.supplier_name = "Nhà cung cấp";
            }
            return transformed;
        });
    }

    transformImportOrder(item) {
        if (!item) return null;
        return {
            id: item._id ? item._id.toString() : "",
            order_code: item.order_code || "",
            supplier_id: item.supplier_id ? item.supplier_id.toString() : "",
            branch_id: item.branch_id ? item.branch_id.toString() : "",
            status: item.status || "pending",
            total_amount: Number(item.total_amount) || 0,
            note: item.note || "",
            created_at: item.created_at || new Date(),
            items: (item.items || []).map(i => ({
                product_id: i.product_id ? i.product_id.toString() : "",
                product_name: i.product_name || "",
                quantity_ordered: Number(i.quantity_ordered) || 0,
                unit_cost: Number(i.unit_cost) || 0,
                subtotal: Number(i.subtotal) || 0
            }))
        };
    }

    async createImportOrder(data, userId) {
        const orderCode = "PO-" + Math.random().toString(36).substring(2, 9).toUpperCase();
        
        let totalAmount = 0;
        const items = (data.items || []).map(i => {
            const qty = Number(i.quantity_ordered) || 0;
            const cost = Number(i.unit_cost) || 0;
            const sub = qty * cost;
            totalAmount += sub;
            return {
                _id: new db.ObjectId(),
                product_id: db.toId(i.product_id),
                product_name: i.product_name,
                quantity_ordered: qty,
                quantity_received: 0,
                unit_cost: cost,
                subtotal: sub,
                note: ""
            };
        });

        const newOrder = {
            order_code: orderCode,
            supplier_id: db.toId(data.supplier_id),
            branch_id: db.toId(data.branch_id || "000000000000000000000001"), // Default branch if not provided
            status: "pending",
            items: items,
            total_amount: totalAmount,
            total_received_amount: 0,
            note: data.note || "",
            created_by: db.toId(userId),
            created_at: new Date(),
            updated_at: new Date()
        };

        const result = await db.collection('importorders').insertOne(newOrder);
        return { ...this.transformImportOrder(newOrder), id: result.insertedId.toString() };
    }

    async receiveImportOrder(id, userId) {
        const orderId = db.toId(id);
        const order = await db.collection('importorders').findOne({ _id: orderId });
        if (!order) throw new Error("Order not found");
        if (order.status === "received") throw new Error("Order already received");

        // Prepare updates
        const now = new Date();
        const batches = [];
        const inventoryUpdates = []; // for bulkWrite to branchproducts

        // Fetch supplier info for batch
        const supplier = await db.collection('suppliers').findOne({ _id: order.supplier_id });
        const supplierName = supplier ? supplier.name : "Unknown Supplier";

        order.items.forEach((item, index) => {
            // Generate batch
            const batchCode = `BATCH-${order.order_code}-${index+1}`;
            
            // Assume expiry is 1 year from now if not provided
            const expDate = new Date();
            expDate.setFullYear(expDate.getFullYear() + 1);

            batches.push({
                batch_code: batchCode,
                branch_product_id: item.product_id, // We use product_id mapping for simplicity in schema
                product_id: item.product_id, // ensure we know the product
                quantity: item.quantity_ordered,
                original_quantity: item.quantity_ordered,
                cost_price: item.unit_cost,
                supplier_id: order.supplier_id,
                supplier_name: supplierName,
                purchase_order_id: order._id,
                received_date: now,
                exp_date: expDate,
                manufacture_date: now,
                created_at: now,
                updated_at: now
            });

            // Prepare inventory update (branchproducts collection based on schema)
            inventoryUpdates.push({
                updateOne: {
                    filter: { product_id: item.product_id, branch_id: order.branch_id },
                    update: { 
                        $inc: { stock: item.quantity_ordered },
                        $set: { updated_at: now }
                    },
                    upsert: true
                }
            });

            // Update item in order
            item.quantity_received = item.quantity_ordered;
            item.batch_code = batchCode;
        });

        // 1. Update Import Order
        await db.collection('importorders').updateOne(
            { _id: orderId },
            { 
                $set: { 
                    status: "received", 
                    received_date: now,
                    updated_at: now,
                    total_received_amount: order.total_amount,
                    items: order.items
                } 
            }
        );

        // 2. Insert Batches
        if (batches.length > 0) {
            await db.collection('inventorybatches').insertMany(batches);
        }

        // 3. Update Inventory (branchproducts)
        if (inventoryUpdates.length > 0) {
            await db.collection('branchproducts').bulkWrite(inventoryUpdates);
        }

        return true;
    }
}

module.exports = new PurchasingService();