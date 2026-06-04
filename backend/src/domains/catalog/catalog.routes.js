const express = require('express');
const catalogController = require('./catalog.controller');

const router = express.Router();

router.get('/categories', catalogController.getCategories);
router.get('/products', catalogController.getProducts);
router.get('/products/featured', catalogController.getFeaturedProducts);
router.get('/products/search', catalogController.searchProducts);
router.get('/products/:id', catalogController.getProductById);
router.get('/products/:id/reviews', catalogController.getProductReviews);
router.get('/branches', catalogController.getBranches);

// --- ADMIN ROUTES ---
router.post('/products', catalogController.createProduct);
router.put('/products/:id', catalogController.updateProduct);
router.delete('/products/:id', catalogController.deleteProduct);
router.post('/upload', catalogController.uploadImage);

router.post('/categories', catalogController.createCategory);
router.put('/categories/:id', catalogController.updateCategory);
router.delete('/categories/:id', catalogController.deleteCategory);

router.post('/branches', catalogController.createBranch);
router.put('/branches/:id', catalogController.updateBranch);
router.delete('/branches/:id', catalogController.deleteBranch);

router.get('/admin/reviews', catalogController.getAllReviews);
router.delete('/admin/reviews/:id', catalogController.deleteReview);

module.exports = router;