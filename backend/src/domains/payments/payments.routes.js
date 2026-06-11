const express = require('express');
const paymentsController = require('./payments.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

// Webhook from PayOS (no auth required)
router.post('/webhook', paymentsController.handleWebhook);

// Protected routes (requires auth)
router.use(authMiddleware);
router.post('/create-link', paymentsController.createPayosLink);
router.post('/wallet-link', paymentsController.createWalletTopupLink);
router.get('/confirm/:orderCode', paymentsController.confirmPayment);

module.exports = router;