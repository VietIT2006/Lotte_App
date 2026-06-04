const customerServiceService = require('./customer_service.service');

class CustomerServiceController {
    async createComplaint(req, res, next) {
        try {
            const userId = req.user.id;
            const { subject, message } = req.body;
            
            if (!subject || !message) {
                return res.status(400).json({ success: false, message: 'Vui lòng cung cấp tiêu đề và nội dung' });
            }
            
            const complaint = await customerServiceService.createComplaint(userId, subject, message);
            res.status(201).json({ success: true, data: complaint });
        } catch (error) {
            next(error);
        }
    }

    async getComplaints(req, res, next) {
        try {
            // Admin and superAdmin can view
            const role = req.user.role || (req.user.role_id === 1 ? 'admin' : 'customer');
            if (role !== 'admin' && role !== 'superAdmin') {
                return res.status(403).json({ success: false, message: 'Forbidden' });
            }

            const complaints = await customerServiceService.getComplaints();
            res.status(200).json({ success: true, data: complaints });
        } catch (error) {
            next(error);
        }
    }
}

module.exports = new CustomerServiceController();