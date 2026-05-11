const catalogService = require('./catalog.service');

class CatalogController {
    async getCategories(req, res, next) {
        try {
            const categories = await catalogService.getCategories();
            res.json({ success: true, data: categories });
        } catch (error) {
            next(error);
        }
    }

    async getProducts(req, res, next) {
        try {
            const { category_id, search } = req.query;
            const products = await catalogService.getProducts({ category_id, search });
            res.json({ success: true, data: products });
        } catch (error) {
            next(error);
        }
    }

    async getProductDetail(req, res, next) {
        try {
            const product = await catalogService.getProductById(req.params.id);
            if (!product) {
                return res.status(404).json({ success: false, message: 'Product not found' });
            }
            res.json({ success: true, data: product });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new CatalogController();