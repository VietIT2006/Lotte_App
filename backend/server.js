require('dotenv').config();
const http = require('http');
const app = require('./src/app');

const PORT = process.env.PORT || 3000;
const { connectDB } = require('./src/core/db');

async function startServer() {
    await connectDB();
    const server = http.createServer(app);
    server.listen(PORT, () => {
        console.log(`Server is running on port ${PORT}`);
    });
}

startServer();

process.on('unhandledRejection', (err) => {
    console.error('Unhandled Rejection! Shutting down...', err);
    server.close(() => {
        process.exit(1);
    });
});
