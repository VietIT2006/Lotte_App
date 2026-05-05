const express = require('express');
const catalogController = require('./catalog.controller');

const router = express.Router();

router.get('/categories', catalogController.getCategories);
router.get('/products', catalogController.getProducts);
router.get('/products/featured', catalogController.getFeaturedProducts);
router.get('/products/search', catalogController.searchProducts);
router.get('/products/:id', catalogController.getProductById);

module.exports = router;