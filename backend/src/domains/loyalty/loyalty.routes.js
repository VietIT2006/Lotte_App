const express = require('express');
const loyaltyController = require('./loyalty.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

router.use(authMiddleware);

router.get('/balance', loyaltyController.getPointsBalance);
router.post('/redeem', loyaltyController.redeemPoints);

module.exports = router;