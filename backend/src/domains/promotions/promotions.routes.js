const express = require('express');
const promotionsController = require('./promotions.controller');

const router = express.Router();

router.get('/', promotionsController.getPromotions);
router.get('/coupons', promotionsController.getCoupons);

// --- ADMIN ---
router.get('/admin/promotions', promotionsController.getAdminPromotions);
router.post('/admin/promotions', promotionsController.createPromotion);
router.put('/admin/promotions/:id', promotionsController.updatePromotion);
router.delete('/admin/promotions/:id', promotionsController.deletePromotion);

// --- ADMIN COUPONS ---
router.get('/admin/coupons', promotionsController.getAdminCoupons);
router.post('/admin/coupons', promotionsController.createCoupon);
router.put('/admin/coupons/:id', promotionsController.updateCoupon);
router.delete('/admin/coupons/:id', promotionsController.deleteCoupon);

module.exports = router;