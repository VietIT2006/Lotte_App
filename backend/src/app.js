const express = require('express');
const cors = require('cors');
const { errorHandler } = require('./core/error.handler');
const authRoutes = require('./domains/auth/auth.routes');
const usersRoutes = require('./domains/users/users.routes');
const catalogRoutes = require('./domains/catalog/catalog.routes');
const inventoryRoutes = require('./domains/inventory/inventory.routes');
const orderingRoutes = require('./domains/ordering/ordering.routes');
const paymentsRoutes = require('./domains/payments/payments.routes');
const promotionsRoutes = require('./domains/promotions/promotions.routes');
const loyaltyRoutes = require('./domains/loyalty/loyalty.routes');
const purchasingRoutes = require('./domains/purchasing/purchasing.routes');
const customerServiceRoutes = require('./domains/customer_service/customer_service.routes');
const notificationsRoutes = require('./domains/notifications/notifications.routes');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Routing by Domains
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/users', usersRoutes);
app.use('/api/v1/catalog', catalogRoutes);
app.use('/api/v1/inventory', inventoryRoutes);
app.use('/api/v1/ordering', orderingRoutes);
app.use('/api/v1/payments', paymentsRoutes);
app.use('/api/v1/promotions', promotionsRoutes);
app.use('/api/v1/loyalty', loyaltyRoutes);
app.use('/api/v1/purchasing', purchasingRoutes);
app.use('/api/v1/customer-service', customerServiceRoutes);
app.use('/api/v1/notifications', notificationsRoutes);

// 404 Not Found Handle
app.use((req, res, next) => {
    res.status(404).json({
        success: false,
        message: 'Endpoint not found',
    });
});

// Global Error Handler
app.use(errorHandler);

module.exports = app;
