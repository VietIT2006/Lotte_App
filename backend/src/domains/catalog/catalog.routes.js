const express = require('express');
const catalogController = require('./catalog.controller');

const router = express.Router();

router.get('/categories', catalogController.getCategories);
router.get('/products', catalogController.getProducts);
router.get('/products/:id', catalogController.getProductDetail);

module.exports = router;