const express = require('express');
const promotionsController = require('./promotions.controller');

const router = express.Router();

router.get('/', promotionsController.getPromotions);
router.get('/coupons', promotionsController.getCoupons);

module.exports = router;