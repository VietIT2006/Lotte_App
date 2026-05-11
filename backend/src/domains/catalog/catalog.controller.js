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
}

module.exports = new CatalogController();