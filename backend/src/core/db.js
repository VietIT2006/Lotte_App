const mysql = require('mysql2/promise');
require('dotenv').config();

// Tạo kết nối Pool tới MySQL
const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'lotte_app',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// Xuất ra một đối tượng có hàm query tương tự như cũ để bạn không phải sửa quá nhiều ở các Service
module.exports = {
    /**
     * Thực thi truy vấn SQL
     * Lưu ý: MySQL dùng dấu "?" thay vì "$1, $2" cho tham số
     */
    query: async (sql, params) => {
        const [results] = await pool.execute(sql, params);
        return { rows: results }; // Trả về định dạng { rows: [] } để khớp với logic cũ của bạn
    },
    pool: pool
};