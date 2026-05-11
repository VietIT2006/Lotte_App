const express = require('express');
const notificationsController = require('./notifications.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

router.use(authMiddleware);

router.get('/', notificationsController.getNotifications);
router.patch('/:id/read', notificationsController.markAsRead);

module.exports = router;