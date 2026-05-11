const { MongoClient } = require('mongodb');
require('dotenv').config();

const uri = process.env.MONGODB_URI;
const client = new MongoClient(uri);

let dbInstance = null;

async function connectDB() {
    if (dbInstance) return dbInstance;
    
    try {
        await client.connect();
        console.log('✅ Connected to MongoDB');
        dbInstance = client.db();
        return dbInstance;
    } catch (error) {
        console.error('❌ MongoDB Connection Error:', error);
        process.exit(1);
    }
}

module.exports = {
    connectDB,
    getDb: () => dbInstance,
    collection: (name) => dbInstance.collection(name),
    // Helper để tương thích ngược (nếu cần)
    query: async (text, params) => {
        console.warn('⚠️ Cảnh báo: Bạn đang gọi hàm query (SQL) trên MongoDB. Vui lòng cập nhật code Service.');
        return { rows: [] };
    }
};