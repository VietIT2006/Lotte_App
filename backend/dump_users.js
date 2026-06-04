const { MongoClient } = require('mongodb');
require('dotenv').config();
const fs = require('fs');

async function dumpUsers() {
    const uri = process.env.MONGODB_URI;
    const client = new MongoClient(uri);

    try {
        await client.connect();
        const db = client.db();
        const users = await db.collection('users').find({}).toArray();
        
        let markdown = `# Bảng Users (MongoDB)\n\n`;
        markdown += `Dưới đây là toàn bộ dữ liệu trong collection \`users\` hiện tại:\n\n`;
        markdown += `\`\`\`json\n`;
        markdown += JSON.stringify(users, null, 2);
        markdown += `\n\`\`\`\n`;
        
        fs.writeFileSync('DATABASE_SCHEMA.md', markdown);
        console.log('Successfully wrote to DATABASE_SCHEMA.md');
    } catch (error) {
        console.error(error);
    } finally {
        await client.close();
    }
}

dumpUsers();
