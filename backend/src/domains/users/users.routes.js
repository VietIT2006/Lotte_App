const express = require('express');
const usersController = require('./users.controller');
const authMiddleware = require('../../core/auth.middleware');

const router = express.Router();

router.use(authMiddleware);

router.get('/profile', usersController.getProfile);
router.put('/profile', usersController.updateProfile);
router.get('/', usersController.getUsers);

module.exports = router;