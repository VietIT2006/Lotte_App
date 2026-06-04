require('dotenv').config();
const { MongoClient } = require('mongodb');

async function check() {
    const uri = process.env.MONGODB_URI;
    const client = new MongoClient(uri);
    try {
        await client.connect();
        const db = client.db();
        const product = await db.collection('products').findOne({});
        console.log("PRODUCT FIELDS:", Object.keys(product));
        console.log("product.thumbnail:", product.thumbnail);
        console.log("product.image:", product.image);
        console.log("product.images:", product.images);
        
        const branchProduct = await db.collection('branchproducts').findOne({});
        if (branchProduct) {
            console.log("BRANCH PRODUCT FIELDS:", Object.keys(branchProduct));
        }
    } catch(e) {
        console.error(e);
    } finally {
        await client.close();
    }
}
check();
