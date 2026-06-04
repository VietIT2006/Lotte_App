const express = require('express');
const purchasingController = require('./purchasing.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

// Lấy danh sách nhà cung cấp
router.get('/admin/suppliers', authMiddleware(['admin', 'superAdmin']), purchasingController.getSuppliers);

// Lấy danh sách phiếu nhập
router.get('/admin/import-orders', authMiddleware(['admin', 'superAdmin']), purchasingController.getImportOrders);

// Tạo phiếu nhập mới
router.post('/admin/import-orders', authMiddleware(['admin', 'superAdmin']), purchasingController.createImportOrder);

// Nhận hàng (duyệt phiếu nhập)
router.put('/admin/import-orders/:id/receive', authMiddleware(['admin', 'superAdmin']), purchasingController.receiveImportOrder);

module.exports = router;