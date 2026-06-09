const express = require('express');
const usersController = require('./users.controller');
const authMiddleware = require('../../core/auth.middleware');

const multer = require('multer');
const path = require('path');

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, path.join(__dirname, '../../../public/uploads/avatars'));
    },
    filename: function (req, file, cb) {
        cb(null, 'avatar-' + Date.now() + path.extname(file.originalname));
    }
});
const upload = multer({ storage: storage });

const router = express.Router();

router.use(authMiddleware);

router.get('/profile', usersController.getProfile);
router.put('/profile', usersController.updateProfile);
router.post('/profile/avatar', upload.single('avatar'), usersController.uploadAvatar);
router.get('/', usersController.getUsers);

// --- ADMIN ---
router.put('/admin/:id/role', usersController.updateUserRole);

module.exports = router;