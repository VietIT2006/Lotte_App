const crypto = require('crypto');
const axios = require('axios');
const db = require('../../core/db');
const { ObjectId } = require('mongodb');

class PaymentsService {
    toId(id) {
        try { return new ObjectId(id); } catch(e) { return id; }
    }

    getSignature(data, checksumKey) {
        const sortedData = Object.keys(data)
            .sort()
            .reduce((acc, key) => {
                acc[key] = data[key];
                return acc;
            }, {});
        
        const queryString = Object.keys(sortedData)
            .map(key => `${key}=${sortedData[key]}`)
            .join('&');
            
        return crypto.createHmac('sha256', checksumKey)
            .update(queryString)
            .digest('hex');
    }

    async createPayosLink({ orderId, amount, description, type, userId }) {
        const orderCode = Math.floor(Date.now() % 100000000) * 10 + Math.floor(Math.random() * 10);
        
        const returnUrl = "https://lotte-app.local/payment-success";
        const cancelUrl = "https://lotte-app.local/payment-failure";

        const data = {
            orderCode: orderCode,
            amount: Number(amount),
            description: description.substring(0, 25),
            cancelUrl: cancelUrl,
            returnUrl: returnUrl
        };

        const checksumKey = '173bbbcb0a50af57bf7fa7b550a2cb643cc37baa6011374ea074214de829e333';
        const signature = this.getSignature(data, checksumKey);
        data.signature = signature;

        const response = await axios.post('https://api-merchant.payos.vn/v2/payment-requests', data, {
            headers: {
                'x-client-id': 'a187b55f-6c8a-499b-886b-722fcc0c3039',
                'x-api-key': 'b8a86931-3e77-4787-8acd-322dceb91f70',
                'Content-Type': 'application/json'
            }
        });

        if (response.data && response.data.code === '00') {
            const checkoutUrl = response.data.data.checkoutUrl;

            await db.collection('payos_transactions').insertOne({
                order_code: orderCode,
                order_id: orderId ? this.toId(orderId) : null,
                user_id: this.toId(userId),
                amount: Number(amount),
                type: type, // 'order' or 'wallet_topup'
                status: 'pending',
                created_at: new Date()
            });

            return { checkoutUrl, orderCode };
        } else {
            console.error("PayOS API error response:", response.data);
            throw new Error(response.data ? response.data.desc || response.data.message : 'Lỗi kết nối PayOS');
        }
    }

    async confirmPayment(orderCode) {
        const tx = await db.collection('payos_transactions').findOne({ order_code: Number(orderCode) });
        if (!tx) {
            return { success: false, message: 'Giao dịch không tồn tại hoặc đã xử lý' };
        }

        if (tx.status === 'success') {
            return { success: true };
        }

        // Call PayOS API to verify real status
        try {
            const response = await axios.get(`https://api-merchant.payos.vn/v2/payment-requests/${orderCode}`, {
                headers: {
                    'x-client-id': 'a187b55f-6c8a-499b-886b-722fcc0c3039',
                    'x-api-key': 'b8a86931-3e77-4787-8acd-322dceb91f70',
                    'Content-Type': 'application/json'
                }
            });

            if (response.data && response.data.code === '00') {
                const payosStatus = response.data.data.status; // 'PAID', 'PENDING', 'CANCELLED', etc.
                if (payosStatus === 'PAID') {
                    // Update transaction status
                    await db.collection('payos_transactions').updateOne(
                        { _id: tx._id },
                        { $set: { status: 'success', paid_at: new Date() } }
                    );

                    if (tx.type === 'order') {
                        // Update order status to paid
                        await db.collection('orders').updateOne(
                            { _id: tx.order_id },
                            { 
                                $set: { 
                                    status: 'CONFIRMED',
                                    'payment.status': 'PAID',
                                    updated_at: new Date()
                                } 
                            }
                        );
                    } else if (tx.type === 'wallet_topup') {
                        // Update shipper wallet balance
                        await db.collection('users').updateOne(
                            { _id: tx.user_id },
                            { $inc: { wallet_balance: Number(tx.amount) } }
                        );

                        // Insert wallet transaction history
                        await db.collection('wallet_transactions').insertOne({
                            user_id: tx.user_id,
                            amount: Number(tx.amount),
                            type: 'credit',
                            description: 'Nạp tiền ví qua PayOS',
                            created_at: new Date()
                        });
                    }

                    return { success: true };
                } else if (payosStatus === 'CANCELLED') {
                    await db.collection('payos_transactions').updateOne(
                        { _id: tx._id },
                        { $set: { status: 'cancelled', cancelled_at: new Date() } }
                    );
                    return { success: false, message: 'Giao dịch đã bị hủy trên cổng thanh toán' };
                } else {
                    return { success: false, message: `Giao dịch chưa được thanh toán (Trạng thái: ${payosStatus})` };
                }
            } else {
                return { success: false, message: response.data ? response.data.desc || response.data.message : 'Lỗi kết nối PayOS' };
            }
        } catch (error) {
            console.error("Error verifying payment with PayOS:", error.message);
            return { success: false, message: 'Lỗi kiểm tra trạng thái giao dịch với PayOS' };
        }
    }
}

module.exports = new PaymentsService();