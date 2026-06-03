const express = require('express');
const customerServiceController = require('./customer_service.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

router.use(authMiddleware);

router.post('/complaints', customerServiceController.createComplaint);
router.get('/complaints', customerServiceController.getComplaints);

module.exports = router;