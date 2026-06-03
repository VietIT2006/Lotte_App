const { MongoClient } = require('mongodb');
require('dotenv').config();

async function run() {
    const client = new MongoClient(process.env.MONGODB_URI);
    try {
        await client.connect();
        const db = client.db(); 
        
        const newAdmin = {
            username: "admin2",
            full_name: "Admin 2",
            email: "admin2@lotte.com",
            phone: "0900000002",
            password_hash: "$2b$10$nQZlZ681cFM4NFO2YUgrQeto1QzsXF47IO6R5HO3Tjcyn3fMHJlkm", // Hash cho mật khẩu 123456
            avatar: "https://via.placeholder.com/150",
            role_id: 1, // 1 là role admin
            status: "ACTIVE",
            is_active: true,
            created_at: new Date(),
            updated_at: new Date()
        };
        
        const result = await db.collection('users').insertOne(newAdmin);
        console.log('Thêm tài khoản admin thành công!', result.insertedId);
    } catch (e) {
        console.error(e);
    } finally {
        await client.close();
    }
}

run();
