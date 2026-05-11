require('dotenv').config();
const http = require('http');
const app = require('./src/app');

const { connectDB } = require('./src/core/db');

const PORT = process.env.PORT || 3000;

async function startServer() {
    try {
        await connectDB();
        const server = http.createServer(app);
        server.listen(PORT, () => {
            console.log(`✅ Server is running on port ${PORT}`);
        });
    } catch (error) {
        console.error('❌ Failed to start server:', error);
        process.exit(1);
    }
}

startServer();


process.on('unhandledRejection', (err) => {
    console.error('Unhandled Rejection! Shutting down...', err);
    server.close(() => {
        process.exit(1);
    });
});
