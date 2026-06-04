const { MongoClient } = require('mongodb');
const bcrypt = require('bcrypt');
require('dotenv').config();

async function run() {
    const client = new MongoClient(process.env.MONGODB_URI);
    try {
        await client.connect();
        const hash = await bcrypt.hash('123456', 10);
        await client.db().collection('users').updateMany({}, { $set: { password_hash: hash } });
        console.log('Updated all users to 123456');
    } catch (e) {
        console.error(e);
    } finally {
        await client.close();
    }
}
run();
