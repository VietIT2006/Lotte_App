const db = require('../../core/db'); // Assuming db module exists here, standard pattern
const { ApiError } = require('../../core/error.handler');

class DeliveryService {
    async getShipperOrders(shipperId, status) {
        let query = `
            SELECT o.*, b.address as branch_address, b.coordinates as branch_coordinates
            FROM orders o
            LEFT JOIN branches b ON o.branch_id = b.id
            WHERE o.shipper_id = $1
        `;
        const params = [shipperId];
        
        if (status) {
            query += ` AND o.status = $2`;
            params.push(status);
        }
        
        query += ` ORDER BY o.created_at DESC`;
        
        const { rows } = await db.query(query, params);
        return rows;
    }

    async updateOrderStatus(orderId, shipperId, status, note, location) {
        // First verify the order belongs to this shipper
        const checkQuery = `SELECT * FROM orders WHERE id = $1 AND shipper_id = $2`;
        const { rows: orderRows } = await db.query(checkQuery, [orderId, shipperId]);
        
        if (orderRows.length === 0) {
            throw new ApiError(404, 'Không tìm thấy đơn hàng hoặc đơn hàng không thuộc về bạn');
        }

        // Update the order status
        let updateQuery = `UPDATE orders SET status = $1`;
        const params = [status];
        let paramCount = 2;

        if (status === 'picked_up') {
            updateQuery += `, pickup_time = NOW()`;
        } else if (status === 'delivered' || status === 'delivery_failed') {
            updateQuery += `, delivered_time = NOW()`;
        }

        if (note) {
            updateQuery += `, delivery_notes = $${paramCount}`;
            params.push(note);
            paramCount++;
        }

        updateQuery += ` WHERE id = $${paramCount} RETURNING *`;
        params.push(orderId);

        const { rows: updatedOrder } = await db.query(updateQuery, params);

        // Record history
        const historyQuery = `
            INSERT INTO delivery_histories (order_id, shipper_id, status, location, note)
            VALUES ($1, $2, $3, $4, $5)
        `;
        await db.query(historyQuery, [orderId, shipperId, status, location || null, note || null]);

        return updatedOrder[0];
    }

    async uploadEvidence(orderId, shipperId, evidenceUrl) {
        const checkQuery = `SELECT * FROM orders WHERE id = $1 AND shipper_id = $2`;
        const { rows: orderRows } = await db.query(checkQuery, [orderId, shipperId]);
        
        if (orderRows.length === 0) {
            throw new ApiError(404, 'Không tìm thấy đơn hàng hoặc đơn hàng không thuộc về bạn');
        }

        const updateQuery = `
            UPDATE orders 
            SET delivery_evidence = array_append(delivery_evidence, $1)
            WHERE id = $2 RETURNING *
        `;
        const { rows } = await db.query(updateQuery, [evidenceUrl, orderId]);
        return rows[0];
    }

    async updateLocation(shipperId, location) {
        const updateQuery = `
            INSERT INTO shipper_profiles (user_id, current_location, status)
            VALUES ($1, $2, 'online')
            ON CONFLICT (user_id) 
            DO UPDATE SET current_location = EXCLUDED.current_location, status = 'online', updated_at = NOW()
        `;
        await db.query(updateQuery, [shipperId, location]);
        return true;
    }

    async getShippers(filters) {
        // Basic query for shippers
        const query = `
            SELECT u.id, u.full_name, u.phone, sp.vehicle_type, sp.license_plate, sp.status, sp.current_location
            FROM users u
            JOIN roles r ON u.role_id = r.id
            LEFT JOIN shipper_profiles sp ON u.id = sp.user_id
            WHERE r.key = 'shipper'
        `;
        const { rows } = await db.query(query);
        return rows;
    }

    async assignOrder(orderId, shipperId) {
        const checkQuery = `SELECT * FROM orders WHERE id = $1`;
        const { rows: orderRows } = await db.query(checkQuery, [orderId]);
        
        if (orderRows.length === 0) {
            throw new ApiError(404, 'Không tìm thấy đơn hàng');
        }

        const updateQuery = `
            UPDATE orders 
            SET shipper_id = $1, status = 'assigned'
            WHERE id = $2 RETURNING *
        `;
        const { rows } = await db.query(updateQuery, [shipperId, orderId]);

        // Record history
        const historyQuery = `
            INSERT INTO delivery_histories (order_id, shipper_id, status, note)
            VALUES ($1, $2, 'assigned', 'Đơn hàng được gán bởi hệ thống/admin')
        `;
        await db.query(historyQuery, [orderId, shipperId]);

        return rows[0];
    }
}

module.exports = new DeliveryService();
