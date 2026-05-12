const { ObjectId } = require('mongodb');
const db = require('../../core/db');

class CatalogService {
    // 1. SÉT CỨNG ẢNH FALLBACK SIÊU ỔN ĐỊNH (GitHub/Unsplash)
    // Đảm bảo app luôn có hình ảnh đẹp, không bị icon lỗi
    getSmartFallback(name, type = 'product') {
        const lowerName = (name || "").toLowerCase();
        
        // Link ảnh thực phẩm sạch từ Unsplash (Cực kỳ ổn định)
        const images = {
            food: "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&q=80",
            drink: "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500&q=80",
            household: "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500&q=80",
            fashion: "https://images.unsplash.com/photo-1445205170230-053b830c6050?w=500&q=80",
            beauty: "https://images.unsplash.com/photo-1522335789183-b1500d79e059?w=500&q=80",
            default: "https://images.unsplash.com/photo-1604719312566-8912e9227c6a?w=500&q=80" // Ảnh siêu thị
        };

        if (type === 'category') {
            if (lowerName.includes('thực phẩm')) return images.food;
            if (lowerName.includes('uống')) return images.drink;
            if (lowerName.includes('gia dụng')) return images.household;
            if (lowerName.includes('mỹ phẩm')) return images.beauty;
            if (lowerName.includes('thời trang')) return images.fashion;
            return images.default;
        }

        // Với sản phẩm
        if (lowerName.includes('sữa') || lowerName.includes('nước')) return images.drink;
        if (lowerName.includes('mì') || lowerName.includes('thực phẩm')) return images.food;
        
        return images.default;
    }

    transformProduct(item) {
        if (!item) return null;
        
        const name = item.name || "Sản phẩm Lotte";
        const finalImage = this.getSmartFallback(name, 'product');

        return {
            id: item._id ? item._id.toString() : "",
            name: name,
            price: Number(item.price) > 0 ? Number(item.price) : (Number(item.import_price) || 25000),
            original_price: Number(item.original_price) || (Number(item.price) * 1.2) || 35000,
            thumbnail: finalImage, // LUÔN TRẢ VỀ ẢNH SỐNG
            description: item.description || `Sản phẩm chất lượng từ thương hiệu ${item.brand || 'Lotte Mart'}.`,
            category_id: item.category_id ? item.category_id.toString() : ""
        };
    }

    transformCategory(item) {
        if (!item) return null;
        const name = item.name || "Danh mục";
        return {
            id: item._id ? item._id.toString() : "",
            name: name,
            image: this.getSmartFallback(name, 'category') // LUÔN TRẢ VỀ ẢNH SỐNG
        };
    }

    // 2. CƠ CHẾ BẮT LỖI TẠI TẦNG DỮ LIỆU
    async wrapAction(action) {
        try {
            return await action();
        } catch (error) {
            console.error("Database Error:", error);
            return null; // Trả về null thay vì làm sập server
        }
    }

    toId(id) {
        try { return new ObjectId(id); } catch (e) { return id; }
    }

    async getCategories() {
        return await this.wrapAction(async () => {
            const categories = await db.collection('categories').find({ is_active: true }).toArray();
            const normalizedMap = new Map();
            for (const cat of categories) {
                let name = cat.name;
                if (name === "Đồ gia dụng") name = "Gia dụng";
                if (!normalizedMap.has(name)) {
                    normalizedMap.set(name, this.transformCategory({...cat, name}));
                }
            }
            return Array.from(normalizedMap.values());
        }) || [];
    }

    async getProducts(categoryId) {
        return await this.wrapAction(async () => {
            let filter = { is_active: true };
            if (categoryId) {
                filter.$or = [{ category_id: categoryId }, { category_id: this.toId(categoryId) }];
            }
            const products = await db.collection('products').find(filter).sort({ created_at: -1 }).limit(20).toArray();
            return products.map(p => this.transformProduct(p));
        }) || [];
    }

    async getFeaturedProducts() {
        return await this.wrapAction(async () => {
            const products = await db.collection('products').find({ is_active: true }).sort({ created_at: -1 }).limit(20).toArray();
            return products.map(p => this.transformProduct(p));
        }) || [];
    }

    async getProductById(id) {
        return await this.wrapAction(async () => {
            const query = { $or: [{ _id: id }, { _id: this.toId(id) }] };
            const product = await db.collection('products').findOne(query);
            return this.transformProduct(product);
        });
    }

    async searchProducts(q, sortBy) {
        return await this.wrapAction(async () => {
            let filter = { 
                is_active: true, 
                $or: [{ name: { $regex: q, $options: 'i' } }, { brand: { $regex: q, $options: 'i' } }]
            };
            const products = await db.collection('products').find(filter).limit(20).toArray();
            return products.map(p => this.transformProduct(p));
        }) || [];
    async getBranches() {
        return await this.wrapAction(async () => {
            return await db.collection('branches').find({ is_active: true }).toArray();
        }) || [];
    }

    async getProductReviews(productId) {
        return await this.wrapAction(async () => {
            return await db.collection('reviews').find({ product_id: this.toId(productId) }).toArray();
        }) || [];
    }
}

module.exports = new CatalogService();