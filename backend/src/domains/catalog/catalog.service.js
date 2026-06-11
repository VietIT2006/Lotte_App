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
        let finalImage = item.thumbnail || item.image;
        
        // Nếu là ảnh thật nhưng chứa domain ảo (dummy data), thì lấy ảnh mock để tránh lỗi hiển thị trên app
        if (!finalImage || finalImage.includes('example.com') || finalImage.includes('lottemart.vn')) {
            finalImage = this.getSmartFallback(name, 'product');
        }

        // Lấy giá từ branch_info nếu có
        let price = Number(item.price) > 0 ? Number(item.price) : (Number(item.import_price) || 25000);
        let original_price = Number(item.original_price) || (Number(item.price) * 1.2) || 35000;
        
        if (item.branch_info && item.branch_info.length > 0) {
            if (item.branch_info[0].price) price = Number(item.branch_info[0].price);
            if (item.branch_info[0].original_price) original_price = Number(item.branch_info[0].original_price);
        }

        return {
            id: item._id ? item._id.toString() : "",
            name: name,
            price: price,
            original_price: original_price,
            thumbnail: finalImage, // LUÔN TRẢ VỀ ẢNH SỐNG
            description: item.description || `Sản phẩm chất lượng từ thương hiệu ${item.brand || 'Lotte Mart'}.`,
            category_id: item.category_id ? item.category_id.toString() : ""
        };
    }

    transformCategory(item) {
        if (!item) return null;
        const name = item.name || "Danh mục";
        let image = item.image || item.thumbnail;
        
        if (!image || image.includes('example.com') || image.includes('lottemart.vn')) {
            image = this.getSmartFallback(name, 'category');
        }

        return {
            id: item._id ? item._id.toString() : "",
            name: name,
            image: image
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

    async getProducts(categoryId, page = 1, limit = 20) {
        return await this.wrapAction(async () => {
            let filter = { is_active: true };
            if (categoryId) {
                filter.$or = [{ category_id: categoryId }, { category_id: this.toId(categoryId) }];
            }
            
            page = Math.max(1, Number(page) || 1);
            limit = Math.max(1, Number(limit) || 20);
            const skip = (page - 1) * limit;
            
            const total = await db.collection('products').countDocuments(filter);
            const total_pages = Math.ceil(total / limit);

            const products = await db.collection('products').aggregate([
                { $match: filter },
                { $sort: { created_at: -1 } },
                { $skip: skip },
                { $limit: limit },
                { $lookup: { from: 'branchproducts', localField: '_id', foreignField: 'product_id', as: 'branch_info' } }
            ]).toArray();
            
            return {
                list: products.map(p => this.transformProduct(p)),
                total,
                page,
                total_pages,
                limit
            };
        }) || { list: [], total: 0, page: 1, total_pages: 0, limit: 20 };
    }

    async getFeaturedProducts(page = 1, limit = 20) {
        return await this.wrapAction(async () => {
            page = Math.max(1, Number(page) || 1);
            limit = Math.max(1, Number(limit) || 20);
            const skip = (page - 1) * limit;
            
            const total = await db.collection('products').countDocuments({ is_active: true });
            const total_pages = Math.ceil(total / limit);

            const products = await db.collection('products').aggregate([
                { $match: { is_active: true } },
                { $sort: { created_at: -1 } },
                { $skip: skip },
                { $limit: limit },
                { $lookup: { from: 'branchproducts', localField: '_id', foreignField: 'product_id', as: 'branch_info' } }
            ]).toArray();
            return {
                list: products.map(p => this.transformProduct(p)),
                total,
                page,
                total_pages,
                limit
            };
        }) || { list: [], total: 0, page: 1, total_pages: 0, limit: 20 };
    }

    async getProductById(id) {
        return await this.wrapAction(async () => {
            const query = { $or: [{ _id: id }, { _id: this.toId(id) }] };
            const products = await db.collection('products').aggregate([
                { $match: query },
                { $lookup: { from: 'branchproducts', localField: '_id', foreignField: 'product_id', as: 'branch_info' } }
            ]).toArray();
            return products.length > 0 ? this.transformProduct(products[0]) : null;
        });
    }

    async searchProducts(q, sortBy, page = 1, limit = 20) {
        return await this.wrapAction(async () => {
            let filter = { 
                is_active: true, 
                $or: [{ name: { $regex: q, $options: 'i' } }, { brand: { $regex: q, $options: 'i' } }]
            };
            
            page = Math.max(1, Number(page) || 1);
            limit = Math.max(1, Number(limit) || 20);
            const skip = (page - 1) * limit;
            
            const total = await db.collection('products').countDocuments(filter);
            const total_pages = Math.ceil(total / limit);

            const products = await db.collection('products').aggregate([
                { $match: filter },
                { $skip: skip },
                { $limit: limit },
                { $lookup: { from: 'branchproducts', localField: '_id', foreignField: 'product_id', as: 'branch_info' } }
            ]).toArray();
            return {
                list: products.map(p => this.transformProduct(p)),
                total,
                page,
                total_pages,
                limit
            };
        }) || { list: [], total: 0, page: 1, total_pages: 0, limit: 20 };
    }

    async getBranches() {
        return await this.wrapAction(async () => {
            const branches = await db.collection('branches').find({ is_active: true }).toArray();
            return branches.map(b => ({
                ...b,
                id: b._id ? b._id.toString() : ""
            }));
        }) || [];
    }

    async getProductReviews(productId) {
        return await this.wrapAction(async () => {
            return await db.collection('reviews').find({ product_id: this.toId(productId) }).toArray();
        }) || [];
    }

    // --- ADMIN MODULE: PRODUCT CRUD ---

    async createProduct(productData) {
        return await this.wrapAction(async () => {
            productData.created_at = new Date();
            productData.is_active = productData.is_active !== undefined ? productData.is_active : true;
            if (productData.category_id) {
                productData.category_id = this.toId(productData.category_id);
            }
            const result = await db.collection('products').insertOne(productData);
            productData._id = result.insertedId;
            return this.transformProduct(productData);
        });
    }

    async updateProduct(id, updateData) {
        return await this.wrapAction(async () => {
            updateData.updated_at = new Date();
            if (updateData.category_id) {
                updateData.category_id = this.toId(updateData.category_id);
            }
            // Loại bỏ _id nếu có để tránh lỗi MongoDB
            delete updateData._id;

            const result = await db.collection('products').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: updateData },
                { returnDocument: 'after' }
            );
            return this.transformProduct(result.value || result);
        });
    }

    async deleteProduct(id) {
        return await this.wrapAction(async () => {
            // Soft delete
            const result = await db.collection('products').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: { is_active: false, updated_at: new Date() } },
                { returnDocument: 'after' }
            );
            return result.ok === 1 || result.lastErrorObject?.updatedExisting;
        });
    }

    async uploadImage(file) {
        return await this.wrapAction(async () => {
            // Mock upload - in a real app this would upload to S3/Cloudinary
            return {
                url: "https://images.unsplash.com/photo-1604719312566-8912e9227c6a?w=500&q=80"
            };
        });
    }

    // --- ADMIN MODULE: CATEGORY CRUD ---

    async createCategory(catData) {
        return await this.wrapAction(async () => {
            catData.is_active = catData.is_active !== undefined ? catData.is_active : true;
            catData.created_at = new Date();
            const result = await db.collection('categories').insertOne(catData);
            catData._id = result.insertedId;
            return this.transformCategory(catData);
        });
    }

    async updateCategory(id, updateData) {
        return await this.wrapAction(async () => {
            updateData.updated_at = new Date();
            delete updateData._id;
            const result = await db.collection('categories').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: updateData },
                { returnDocument: 'after' }
            );
            return this.transformCategory(result.value || result);
        });
    }

    async deleteCategory(id) {
        return await this.wrapAction(async () => {
            const result = await db.collection('categories').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: { is_active: false, updated_at: new Date() } },
                { returnDocument: 'after' }
            );
            return result.ok === 1 || result.lastErrorObject?.updatedExisting;
        });
    }

    // --- ADMIN MODULE: BRANCH CRUD ---

    async createBranch(branchData) {
        return await this.wrapAction(async () => {
            branchData.is_active = true;
            branchData.createdAt = new Date();
            const result = await db.collection('branches').insertOne(branchData);
            branchData._id = result.insertedId;
            return {
                ...branchData,
                id: branchData._id.toString()
            };
        });
    }

    async updateBranch(id, updateData) {
        return await this.wrapAction(async () => {
            updateData.updatedAt = new Date();
            delete updateData._id;
            const result = await db.collection('branches').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: updateData },
                { returnDocument: 'after' }
            );
            const branch = result.value || result;
            if(branch) {
                branch.id = branch._id ? branch._id.toString() : "";
            }
            return branch;
        });
    }

    async deleteBranch(id) {
        return await this.wrapAction(async () => {
            const result = await db.collection('branches').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: { is_active: false, updatedAt: new Date() } },
                { returnDocument: 'after' }
            );
            return result.ok === 1 || result.lastErrorObject?.updatedExisting;
        });
    }

    // --- ADMIN MODULE: REVIEWS CRUD ---
    async getAllReviews() {
        return await this.wrapAction(async () => {
            const reviews = await db.collection('reviews')
                .aggregate([
                    { $sort: { created_at: -1 } },
                    {
                        $lookup: {
                            from: 'users',
                            localField: 'user_id',
                            foreignField: '_id',
                            as: 'user'
                        }
                    },
                    {
                        $lookup: {
                            from: 'products',
                            localField: 'product_id',
                            foreignField: '_id',
                            as: 'product'
                        }
                    },
                    { $unwind: { path: '$user', preserveNullAndEmptyArrays: true } },
                    { $unwind: { path: '$product', preserveNullAndEmptyArrays: true } }
                ])
                .toArray();
            
            return reviews.map(r => ({
                id: r._id ? r._id.toString() : "",
                user_id: r.user_id ? r.user_id.toString() : "",
                user_name: r.user ? (r.user.full_name || r.user.username) : "Ẩn danh",
                product_id: r.product_id ? r.product_id.toString() : "",
                product_name: r.product ? r.product.name : "Sản phẩm không rõ",
                rating: Number(r.rating) || 5,
                comment: r.comment || "",
                created_at: r.created_at || new Date()
            }));
        }) || [];
    }

    async deleteReview(id) {
        return await this.wrapAction(async () => {
            await db.collection('reviews').deleteOne({ _id: this.toId(id) });
            return true;
        });
    }

    async getPendingProducts() {
        return await this.wrapAction(async () => {
            const products = await db.collection('products').find({ is_active: false }).toArray();
            return products.map(p => this.transformProduct(p));
        }) || [];
    }

    async getPendingCategories() {
        return await this.wrapAction(async () => {
            const categories = await db.collection('categories').find({ is_active: false }).toArray();
            return categories.map(c => this.transformCategory(c));
        }) || [];
    }

    async approveProduct(id) {
        return await this.wrapAction(async () => {
            const result = await db.collection('products').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: { is_active: true, updated_at: new Date() } },
                { returnDocument: 'after' }
            );
            return this.transformProduct(result.value || result);
        });
    }

    async approveCategory(id) {
        return await this.wrapAction(async () => {
            const result = await db.collection('categories').findOneAndUpdate(
                { _id: this.toId(id) },
                { $set: { is_active: true, updated_at: new Date() } },
                { returnDocument: 'after' }
            );
            return this.transformCategory(result.value || result);
        });
    }
}

module.exports = new CatalogService();