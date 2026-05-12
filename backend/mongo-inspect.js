const { MongoClient } = require('mongodb');
require('dotenv').config();

async function inspectMongoDB() {
    const uri = process.env.MONGODB_URI;

    if (!uri) {
        console.error('❌ LỖI: Chưa tìm thấy MONGODB_URI trong file .env');
        console.log('Vui lòng thêm dòng sau vào file .env:');
        console.log('MONGODB_URI=mongodb+srv://<user>:<password>@cluster.mongodb.net/dbname');
        return;
    }

    const client = new MongoClient(uri);

    try {
        console.log('⏳ Đang kết nối tới MongoDB...');
        await client.connect();
        console.log('✅ Kết nối thành công!');

        const db = client.db(); // Lấy db từ URI hoặc mặc định
        const collections = await db.listCollections().toArray();

        console.log(`\n--- DANH SÁCH COLLECTIONS TRONG DATABASE: ${db.databaseName} ---`);
        
        for (const colInfo of collections) {
            const collection = db.collection(colInfo.name);
            const count = await collection.countDocuments();
            const sample = await collection.findOne();

            console.log(`\n📦 Collection: ${colInfo.name}`);
            console.log(`   - Số lượng bản ghi: ${count}`);
            console.log(`   - Dữ liệu mẫu (1 bản ghi đầu tiên):`);
            console.log(JSON.stringify(sample, null, 2));
            console.log('   -----------------------------------');
        }

    } catch (error) {
        console.error('❌ Lỗi kết nối hoặc truy vấn:', error.message);
    } finally {
        await client.close();
    }
}

inspectMongoDB();
