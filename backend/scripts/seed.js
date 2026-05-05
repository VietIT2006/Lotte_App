require('dotenv').config();
const { Pool } = require('pg');

const pool = new Pool({
    connectionString: process.env.DATABASE_URL,
    ssl: { rejectUnauthorized: false }
});

async function seed() {
    try {
        console.log('--- Đang nạp dữ liệu mẫu với hình ảnh thực tế ---');

        await pool.query('DELETE FROM branch_products');
        await pool.query('DELETE FROM products');
        await pool.query('DELETE FROM categories');

        const categories = [
            ['Thực phẩm tươi sống', 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', true, 1],
            ['Sản phẩm từ sữa', 'https://images.unsplash.com/photo-1550583724-125581f778d3?w=200', true, 2],
            ['Nước giải khát', 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=200', true, 3],
            ['Đồ ăn vặt', 'https://images.unsplash.com/photo-1599490659213-e2b9527bb087?w=200', true, 4],
            ['Hóa mỹ phẩm', 'https://images.unsplash.com/photo-1585232351009-aa87416fca90?w=200', true, 5]
        ];

        const catIds = [];
        for (const cat of categories) {
            const res = await pool.query(
                'INSERT INTO categories (name, image, is_active, sort_order) VALUES ($1, $2, $3, $4) RETURNING id',
                cat
            );
            catIds.push(res.rows[0].id);
        }

        const products = [
            ['Dâu tây Đà Lạt (250g)', catIds[0], 45000, 'Dâu tây tươi ngon từ trang trại Đà Lạt.', 'https://images.unsplash.com/photo-1464965224031-937d74b2d7bc?w=500', true, true],
            ['Sữa tươi TH True Milk (1L)', catIds[1], 32000, 'Sữa tươi tiệt trùng nguyên chất.', 'https://images.unsplash.com/photo-1563636619-e910009355dc?w=500', true, true],
            ['Coca-Cola lon 330ml', catIds[2], 10000, 'Nước giải khát có gas phổ biến.', 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500', true, true],
            ['Khoai tây chiên Lays (95g)', catIds[3], 18000, 'Vị khoai tây tự nhiên giòn tan.', 'https://images.unsplash.com/photo-1566478433399-66380629b352?w=500', true, true],
            ['Nước rửa chén Sunlight (750ml)', catIds[4], 25000, 'Đánh bay dầu mỡ hiệu quả.', 'https://images.unsplash.com/photo-1584622781564-1d987f7333c1?w=500', true, true],
            ['Táo Envy Mỹ', catIds[0], 89000, 'Táo giòn, ngọt và thơm.', 'https://images.unsplash.com/photo-1560806887-1e4cd0b6bcd6?w=500', true, false],
            ['Bánh mì Sandwich', catIds[3], 15000, 'Bánh mì gối tươi mỗi ngày.', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500', true, false],
            ['Trứng gà (10 quả)', catIds[0], 35000, 'Trứng gà sạch từ trang trại.', 'https://images.unsplash.com/photo-1582722872445-44c50e70488e?w=500', true, false],
            ['Cam sành (1kg)', catIds[0], 25000, 'Cam sành mọng nước, nhiều vitamin C.', 'https://images.unsplash.com/photo-1557800636-894a64c1696f?w=500', true, true],
            ['Sữa chua Vinamilk (lốc 4)', catIds[1], 24000, 'Sữa chua có đường ngon mịn.', 'https://images.unsplash.com/photo-1571212474847-79a6064d8816?w=500', true, false],
            ['Nước suối Aquafina (500ml)', catIds[2], 5000, 'Nước tinh khiết tuyệt đối.', 'https://images.unsplash.com/photo-1562184647-703373752e2f?w=500', true, false],
            ['Xúc xích Đức (500g)', catIds[3], 75000, 'Xúc xích xông khói đậm đà.', 'https://images.unsplash.com/photo-1534127395081-30a1396a3c5a?w=500', true, false]
        ];

        for (const prod of products) {
            await pool.query(
                'INSERT INTO products (name, category_id, price, description, thumbnail, is_active, is_featured) VALUES ($1, $2, $3, $4, $5, $6, $7)',
                prod
            );
        }

        console.log('✅ Đã nạp dữ liệu mẫu thành công!');
    } catch (err) {
        console.error('❌ Lỗi khi nạp dữ liệu:', err);
    } finally {
        await pool.end();
    }
}

seed();
