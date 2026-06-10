const catalogService = require('./catalog.service');

class CatalogController {
    async getCategories(req, res, next) {
        try {
            const categories = await catalogService.getCategories();
            res.status(200).json({
                success: true,
                data: categories
            });
        } catch (error) {
            next(error);
        }
    }

    async getProducts(req, res, next) {
        try {
            const { category_id } = req.query;
            const products = await catalogService.getProducts(category_id);
            res.status(200).json({
                success: true,
                data: products
            });
        } catch (error) {
            next(error);
        }
    }

    async getFeaturedProducts(req, res, next) {
        try {
            const products = await catalogService.getFeaturedProducts();
            res.status(200).json({
                success: true,
                data: products
            });
        } catch (error) {
            next(error);
        }
    }

    async getProductById(req, res, next) {
        try {
            const { id } = req.params;
            const product = await catalogService.getProductById(id);
            if (!product) {
                return res.status(404).json({
                    success: false,
                    message: 'Product not found'
                });
            }
            res.status(200).json({
                success: true,
                data: product
            });
        } catch (error) {
            next(error);
        }
    }

    async searchProducts(req, res, next) {
        try {
            const { q, sort_by } = req.query;
            const products = await catalogService.searchProducts(q, sort_by);
            res.status(200).json({
                success: true,
                data: products
            });
        } catch (error) {
            next(error);
        }
    }

    async getBranches(req, res, next) {
        try {
            const branches = await catalogService.getBranches();
            res.status(200).json({
                success: true,
                data: branches
            });
        } catch (error) {
            next(error);
        }
    }

    async getProductReviews(req, res, next) {
        try {
            const { id } = req.params;
            const reviews = await catalogService.getProductReviews(id);
            res.status(200).json({
                success: true,
                data: reviews
            });
        } catch (error) {
            next(error);
        }
    }

    // --- ADMIN MODULE: PRODUCT CRUD ---

    async createProduct(req, res, next) {
        try {
            const productData = req.body;
            const newProduct = await catalogService.createProduct(productData);
            res.status(201).json({
                success: true,
                message: 'Product created successfully',
                data: newProduct
            });
        } catch (error) {
            next(error);
        }
    }

    async updateProduct(req, res, next) {
        try {
            const { id } = req.params;
            const updateData = req.body;
            const updatedProduct = await catalogService.updateProduct(id, updateData);
            res.status(200).json({
                success: true,
                message: 'Product updated successfully',
                data: updatedProduct
            });
        } catch (error) {
            next(error);
        }
    }

    async deleteProduct(req, res, next) {
        try {
            const { id } = req.params;
            await catalogService.deleteProduct(id);
            res.status(200).json({
                success: true,
                message: 'Product deleted successfully'
            });
        } catch (error) {
            next(error);
        }
    }

    async uploadImage(req, res, next) {
        try {
            // Mocking file upload
            const result = await catalogService.uploadImage();
            res.status(200).json({
                success: true,
                message: 'Image uploaded successfully',
                data: result
            });
        } catch (error) {
            next(error);
        }
    }

    // --- ADMIN MODULE: CATEGORY CRUD ---

    async createCategory(req, res, next) {
        try {
            const newCat = await catalogService.createCategory(req.body);
            res.status(201).json({ success: true, data: newCat });
        } catch (error) { next(error); }
    }

    async updateCategory(req, res, next) {
        try {
            const updatedCat = await catalogService.updateCategory(req.params.id, req.body);
            res.status(200).json({ success: true, data: updatedCat });
        } catch (error) { next(error); }
    }

    async deleteCategory(req, res, next) {
        try {
            await catalogService.deleteCategory(req.params.id);
            res.status(200).json({ success: true, message: 'Deleted' });
        } catch (error) { next(error); }
    }

    // --- ADMIN MODULE: BRANCH CRUD ---

    async createBranch(req, res, next) {
        try {
            const newBranch = await catalogService.createBranch(req.body);
            res.status(201).json({ success: true, data: newBranch });
        } catch (error) { next(error); }
    }

    async updateBranch(req, res, next) {
        try {
            const updatedBranch = await catalogService.updateBranch(req.params.id, req.body);
            res.status(200).json({ success: true, data: updatedBranch });
        } catch (error) { next(error); }
    }

    async deleteBranch(req, res, next) {
        try {
            await catalogService.deleteBranch(req.params.id);
            res.status(200).json({ success: true, message: 'Deleted successfully' });
        } catch (error) { next(error); }
    }

    // --- ADMIN MODULE: REVIEWS CRUD ---
    async getAllReviews(req, res, next) {
        try {
            const reviews = await catalogService.getAllReviews();
            res.status(200).json({ success: true, data: reviews });
        } catch (error) { next(error); }
    }

    async deleteReview(req, res, next) {
        try {
            await catalogService.deleteReview(req.params.id);
            res.status(200).json({ success: true, message: 'Review deleted successfully' });
        } catch (error) { next(error); }
    }

    async getPendingProducts(req, res, next) {
        try {
            const products = await catalogService.getPendingProducts();
            res.status(200).json({ success: true, data: products });
        } catch (error) { next(error); }
    }

    async getPendingCategories(req, res, next) {
        try {
            const categories = await catalogService.getPendingCategories();
            res.status(200).json({ success: true, data: categories });
        } catch (error) { next(error); }
    }

    async approveProduct(req, res, next) {
        try {
            const approved = await catalogService.approveProduct(req.params.id);
            res.status(200).json({ success: true, data: approved });
        } catch (error) { next(error); }
    }

    async approveCategory(req, res, next) {
        try {
            const approved = await catalogService.approveCategory(req.params.id);
            res.status(200).json({ success: true, data: approved });
        } catch (error) { next(error); }
    }
}

module.exports = new CatalogController();