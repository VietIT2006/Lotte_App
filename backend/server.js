require('dotenv').config();
const http = require('http');
const app = require('./src/app');

const PORT = process.env.PORT || 3000;
const { connectDB } = require('./src/core/db');

async function startServer() {
    await connectDB();
    const server = http.createServer(app);
    server.listen(PORT, '0.0.0.0', () => {
        console.log(`✅ Server is running on http://0.0.0.0:${PORT}`);
        console.log(`🚀 Thử truy cập từ thiết bị khác bằng IP máy tính của bạn.`);
    });
}

startServer();

process.on('unhandledRejection', (err) => {
    console.error('Unhandled Rejection! Shutting down...', err);
    server.close(() => {
        process.exit(1);
    });
});
