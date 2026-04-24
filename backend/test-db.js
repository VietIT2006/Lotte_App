require('dotenv').config();
const { Pool } = require('pg');

async function testConnection() {
    console.log('--- Đang kiểm tra kết nối Database ---');
    console.log('URL:', process.env.DATABASE_URL ? 'Đã tìm thấy URL' : 'Không tìm thấy DATABASE_URL trong .env');

    const pool = new Pool({
        connectionString: process.env.DATABASE_URL,
        ssl: {
            rejectUnauthorized: false
        }
    });

    try {
        const start = Date.now();
        const res = await pool.query('SELECT NOW() as current_time, VERSION() as version');
        const duration = Date.now() - start;
        
        console.log('✅ Kết nối THÀNH CÔNG!');
        console.log('Thời gian phản hồi:', duration, 'ms');
        console.log('Server Time:', res.rows[0].current_time);
        
        // Kiểm tra xem bảng users có tồn tại không
        const tableCheck = await pool.query("SELECT count(*) FROM information_schema.tables WHERE table_name = 'users'");
        if (tableCheck.rows[0].count > 0) {
            console.log('✅ Bảng [users] đã tồn tại. Sẵn sàng cho việc Đăng ký/Đăng nhập.');
            
            // Đếm thử số lượng users
            const userCount = await pool.query('SELECT count(*) FROM users');
            console.log('📊 Số lượng người dùng hiện tại:', userCount.rows[0].count);
        } else {
            console.log('⚠️ Cảnh báo: Bảng [users] chưa được tạo. Hãy kiểm tra lại file db.sql.');
        }

    } catch (err) {
        console.error('❌ Kết nối THẤT BẠI!');
        console.error('Lỗi chi tiết:', err.message);
        if (err.message.includes('password authentication failed')) {
            console.log('👉 Gợi ý: Mật khẩu bạn nhập trong file .env có vẻ chưa đúng.');
        }
    } finally {
        await pool.end();
        console.log('--- Kết thúc kiểm tra ---');
    }
}

testConnection();
