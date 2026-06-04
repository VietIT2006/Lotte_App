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

    // --- ADMIN ---
    async getAdminOrders(req, res, next) {
        try {
            const userRole = req.user.roleStr || 'customer';
            if (userRole !== 'admin' && userRole !== 'superAdmin') {
                return res.status(403).json({ success: false, message: "Forbidden" });
            }
            const orders = await orderingService.getAdminOrders();
            res.status(200).json({
                success: true,
                data: orders
            });
        } catch (error) {
            next(error);
        }
    }

    async updateOrderStatus(req, res, next) {
        try {
            const userRole = req.user.roleStr || 'customer';
            if (userRole !== 'admin' && userRole !== 'superAdmin') {
                return res.status(403).json({ success: false, message: "Forbidden" });
            }
            
            const { id } = req.params;
            const { status } = req.body;
            
            if (!status) {
                return res.status(400).json({ success: false, message: 'Status is required' });
            }

            const updatedOrder = await orderingService.updateOrderStatus(id, status);
            res.status(200).json({
                success: true,
                message: 'Order status updated successfully',
                data: updatedOrder
            });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new OrderingController();