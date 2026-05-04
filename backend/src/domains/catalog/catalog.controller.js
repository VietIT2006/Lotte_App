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
}

module.exports = new CatalogController();