const db = require('../../core/db');

class CatalogService {
    async getCategories() {
        const categoriesCollection = db.collection('categories');
        return await categoriesCollection.find({ is_active: true }).toArray();
    }

    async getProducts(filter = {}) {
        const productsCollection = db.collection('products');
        const query = { is_active: true };
        
        if (filter.category_id) {
            query.category_id = filter.category_id;
        }
        
        if (filter.search) {
            query.name = { $regex: filter.search, $options: 'i' };
        }

        return await productsCollection.find(query).toArray();
    }

    async getProductById(id) {
        const productsCollection = db.collection('products');
        // Vì ID trong MongoDB có thể là chuỗi hoặc ObjectId
        const { ObjectId } = require('mongodb');
        try {
            return await productsCollection.findOne({ _id: new ObjectId(id) });
        } catch (e) {
            return await productsCollection.findOne({ _id: id });
        }
    }
}

module.exports = new CatalogService();