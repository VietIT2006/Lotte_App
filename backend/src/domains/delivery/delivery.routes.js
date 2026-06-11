const express = require('express');
const deliveryController = require('./delivery.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

// Tất cả các route delivery đều cần đăng nhập
router.use(authMiddleware);

// API cho Shipper
router.get('/shipper/orders', deliveryController.getShipperOrders);
router.patch('/shipper/orders/:id/status', deliveryController.updateOrderStatus);
router.post('/shipper/orders/:id/evidence', deliveryController.uploadEvidence);
router.patch('/shipper/profile/location', deliveryController.updateLocation);
router.get('/shipper/wallet', deliveryController.getWalletInfo);
router.patch('/shipper/status', deliveryController.updateShipperStatus);
router.post('/shipper/wallet/topup', deliveryController.topupWallet);

// API cho Admin (Quản lý Shipper, Phân bổ đơn)
router.get('/admin/shippers', deliveryController.getShippers);
router.post('/admin/orders/:id/assign', deliveryController.assignOrder);

module.exports = router;
