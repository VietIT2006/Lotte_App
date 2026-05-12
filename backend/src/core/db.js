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
    toId: (id) => {
        if (!id) return null;
        try { return new MongoClient.ObjectId(id); } catch (e) { 
            const { ObjectId } = require('mongodb');
            return new ObjectId(id);
        }
    },
    ObjectId: require('mongodb').ObjectId
};
