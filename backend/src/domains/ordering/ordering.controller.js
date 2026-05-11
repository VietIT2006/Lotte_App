const orderingService = require('./ordering.service');

class OrderingController {
    // --- CART ---
    async getCart(req, res, next) {
        try {
            const userId = req.user.id; // Giả định đã có middleware auth
            const cart = await orderingService.getCart(userId);
            res.status(200).json({
                success: true,
                data: cart
            });
        } catch (error) {
            next(error);
        }
    }

    async addToCart(req, res, next) {
        try {
            const userId = req.user.id;
            const item = req.body;
            const cart = await orderingService.addToCart(userId, item);
            res.status(200).json({
                success: true,
                data: cart
            });
        } catch (error) {
            next(error);
        }
    }

    async removeFromCart(req, res, next) {
        try {
            const userId = req.user.id;
            const { branch_product_id } = req.params;
            const cart = await orderingService.removeFromCart(userId, branch_product_id);
            res.status(200).json({
                success: true,
                data: cart
            });
        } catch (error) {
            next(error);
        }
    }

    // --- ORDERS ---
    async createOrder(req, res, next) {
        try {
            const userId = req.user.id;
            const orderData = req.body;
            const order = await orderingService.createOrder(userId, orderData);
            res.status(201).json({
                success: true,
                data: order
            });
        } catch (error) {
            next(error);
        }
    }

    async getOrders(req, res, next) {
        try {
            const userId = req.user.id;
            const orders = await orderingService.getOrders(userId);
            res.status(200).json({
                success: true,
                data: orders
            });
        } catch (error) {
            next(error);
        }
    }

    async getOrderById(req, res, next) {
        try {
            const { id } = req.params;
            const order = await orderingService.getOrderById(id);
            if (!order) {
                return res.status(404).json({
                    success: false,
                    message: 'Order not found'
                });
            }
            res.status(200).json({
                success: true,
                data: order
            });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new OrderingController();