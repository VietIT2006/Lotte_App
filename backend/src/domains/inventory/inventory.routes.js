const express = require('express');
const inventoryController = require('./inventory.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

router.get('/admin/batches', authMiddleware(['admin', 'superAdmin']), inventoryController.getAdminBatches);

module.exports = router;