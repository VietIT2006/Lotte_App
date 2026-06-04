require('dotenv').config();
const { MongoClient } = require('mongodb');

async function listDbs() {
    const uri = process.env.MONGODB_URI;
    const client = new MongoClient(uri);
    try {
        await client.connect();
        const adminDb = client.db().admin();
        const dbs = await adminDb.listDatabases();
        console.log("Databases in cluster:");
        dbs.databases.forEach(db => console.log(` - ${db.name}`));
    } catch(e) {
        console.error(e);
    } finally {
        await client.close();
    }
}
listDbs();
