const { MongoClient } = require('mongodb');
require('dotenv').config();

async function run() {
    const client = new MongoClient(process.env.MONGODB_URI);
    try {
        await client.connect();
        const db = client.db(); 
        
        const hash = "$2b$10$nQZlZ681cFM4NFO2YUgrQeto1QzsXF47IO6R5HO3Tjcyn3fMHJlkm";
        
        const result = await db.collection('users').updateOne(
            { email: 'admin@lotte.com' },
            { $set: { password_hash: hash } }
        );
        
        console.log('Update result:', result);
    } catch (e) {
        console.error(e);
    } finally {
        await client.close();
    }
}

run();
