const express = require('express');
const orderingController = require('./ordering.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

// Tất cả các route ordering đều cần đăng nhập
router.use(authMiddleware);

// Cart
router.get('/cart', orderingController.getCart);
router.post('/cart', orderingController.addToCart);
router.delete('/cart/:branch_product_id', orderingController.removeFromCart);

// Orders
router.get('/history', orderingController.getOrders);
router.get('/:id', orderingController.getOrderById);
router.post('/checkout', orderingController.createOrder);

module.exports = router;