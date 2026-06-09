const { Server } = require("socket.io");

let io;

function initSocket(server) {
    io = new Server(server, {
        cors: {
            origin: "*",
            methods: ["GET", "POST"]
        }
    });

    io.on("connection", (socket) => {
        console.log("Client connected: " + socket.id);

        // Shipper hoặc Customer tham gia vào room của đơn hàng
        socket.on("join_order", (orderId) => {
            socket.join(`order_${orderId}`);
            console.log(`Socket ${socket.id} joined room order_${orderId}`);
        });

        // Rời khỏi room
        socket.on("leave_order", (orderId) => {
            socket.leave(`order_${orderId}`);
            console.log(`Socket ${socket.id} left room order_${orderId}`);
        });

        // Shipper gửi toạ độ mới
        // payload: { orderId: "123", lat: 10.762622, lng: 106.660172 }
        socket.on("update_location", (payload) => {
            const { orderId, lat, lng } = payload;
            // Phát lại toạ độ cho các client đang ở trong room đơn hàng (Customer)
            io.to(`order_${orderId}`).emit("location_updated", { lat, lng });
            console.log(`Location updated for order_${orderId}: ${lat}, ${lng}`);
        });

        socket.on("disconnect", () => {
            console.log("Client disconnected: " + socket.id);
        });
    });

    return io;
}

function getIo() {
    if (!io) {
        throw new Error("Socket.io not initialized!");
    }
    return io;
}

module.exports = {
    initSocket,
    getIo
};
