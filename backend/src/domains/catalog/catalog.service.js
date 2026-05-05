const db = require('../../core/db');

class CatalogService {
    async getCategories() {
        const query = 'SELECT * FROM categories WHERE is_active = true ORDER BY sort_order ASC';
        const { rows } = await db.query(query);
        return rows;
    }

    async getProducts(categoryId) {
        let query = 'SELECT * FROM products WHERE is_active = true';
        const params = [];
        if (categoryId) {
            query += ' AND category_id = $1';
            params.push(categoryId);
        }
        query += ' ORDER BY created_at DESC';
        const { rows } = await db.query(query, params);
        return rows;
    }

    async getFeaturedProducts() {
        const query = 'SELECT * FROM products WHERE is_active = true AND is_featured = true ORDER BY created_at DESC';
        const { rows } = await db.query(query);
        return rows;
    }

    async getProductById(id) {
        const query = 'SELECT * FROM products WHERE id = $1';
        const { rows } = await db.query(query, [id]);
        return rows[0];
    }

    async searchProducts(q, sortBy) {
        let query = 'SELECT * FROM products WHERE is_active = true AND name ILIKE $1';
        const params = [`%${q}%`];
        
        if (sortBy === 'price_asc') {
            query += ' ORDER BY price ASC';
        } else if (sortBy === 'price_desc') {
            query += ' ORDER BY price DESC';
        } else {
            query += ' ORDER BY created_at DESC';
        }
        
        const { rows } = await db.query(query, params);
        return rows;
    }
}

module.exports = new CatalogService();