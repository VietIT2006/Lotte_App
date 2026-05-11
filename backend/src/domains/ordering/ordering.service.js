const { ObjectId } = require('mongodb');
const db = require('../../core/db');

class OrderingService {
    // Helper xử lý ID linh hoạt
    toId(id) {
        try {
            return new ObjectId(id);
        } catch (e) {
            return id;
        }
    }

    transformCart(cart) {
        if (!cart) return { items: [], total: 0 };
        return {
            id: cart._id ? cart._id.toString() : "",
            user_id: cart.user_id ? cart.user_id.toString() : "",
            items: (cart.items || []).map(item => ({
                id: item._id ? item._id.toString() : "",
                quantity: Number(item.quantity) || 0,
                product: {
                    id: item.branch_product_id || "",
                    name: item.product_name || "Sản phẩm",
                    price: Number(item.price) || 0,
                    original_price: Number(item.unit_price || item.price) || 0,
                    thumbnail: item.product_image || "",
                    description: ""
                }
            })),
            updated_at: cart.updated_at || new Date()
        };
    }

    transformOrder(order) {
        if (!order) return null;
        return {
            id: order._id ? order._id.toString() : "",
            status: order.status || "PENDING",
            total_amount: Number(order.total_amount) || 0,
            shipping_fee: Number(order.shipping_fee) || 0,
            created_at: order.created_at || new Date(),
            items: (order.items || []).map(item => ({
                product_name: item.product_name || "Sản phẩm",
                quantity: Number(item.quantity) || 0,
                price: Number(item.price) || 0
            })),
            payment_method: (order.payment && order.payment.method) || "COD"
        };
    }

    async getCart(userId) {
        const query = { $or: [{ user_id: userId }, { user_id: this.toId(userId) }] };
        const cart = await db.collection('carts').findOne(query);
        return this.transformCart(cart);
    }

    async addToCart(userId, item) {
        const query = { $or: [{ user_id: userId }, { user_id: this.toId(userId) }] };
        const cart = await db.collection('carts').findOne(query);

        if (!cart) {
            await db.collection('carts').insertOne({
                user_id: this.toId(userId),
                items: [{ ...item, _id: new ObjectId() }],
                created_at: new Date(),
                updated_at: new Date()
            });
        } else {
            const existingItemIndex = cart.items.findIndex(i => i.branch_product_id === item.branch_product_id);
            if (existingItemIndex > -1) {
                cart.items[existingItemIndex].quantity += Number(item.quantity);
            } else {
                cart.items.push({ ...item, _id: new ObjectId() });
            }
            await db.collection('carts').updateOne({ _id: cart._id }, {
                $set: { items: cart.items, updated_at: new Date() }
            });
        }
        return this.getCart(userId);
    }

    async removeFromCart(userId, branchProductId) {
        const query = { $or: [{ user_id: userId }, { user_id: this.toId(userId) }] };
        await db.collection('carts').updateOne(
            query,
            { 
                $pull: { items: { branch_product_id: branchProductId } },
                $set: { updated_at: new Date() }
            }
        );
        return this.getCart(userId);
    }

    async createOrder(userId, orderData) {
        const newOrder = {
            user_id: this.toId(userId),
            items: orderData.items || [],
            total_amount: Number(orderData.total_amount) || 0,
            shipping_fee: Number(orderData.shipping_fee) || 0,
            status: 'PENDING',
            payment: {
                method: orderData.payment_method || 'COD',
                status: 'PENDING'
            },
            created_at: new Date(),
            updated_at: new Date()
        };

        const result = await db.collection('orders').insertOne(newOrder);
        const query = { $or: [{ user_id: userId }, { user_id: this.toId(userId) }] };
        await db.collection('carts').deleteOne(query);

        return { ...newOrder, id: result.insertedId.toString() };
    }

    async getOrders(userId) {
        const query = { $or: [{ user_id: userId }, { user_id: this.toId(userId) }] };
        const orders = await db.collection('orders')
            .find(query)
            .sort({ created_at: -1 })
            .toArray();
        return orders.map(o => this.transformOrder(o));
    }

    async getOrderById(orderId) {
        const query = { $or: [{ _id: orderId }, { _id: this.toId(orderId) }] };
        const order = await db.collection('orders').findOne(query);
        return this.transformOrder(order);
    }
}

module.exports = new OrderingService();