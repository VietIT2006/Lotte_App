const fs = require('fs');
const path = require('path');
const FormData = require('form-data');
const axios = require('axios');
const jwt = require('jsonwebtoken');

require('dotenv').config();

const testUpload = async () => {
    // Tạo 1 file ảnh giả
    const testFilePath = path.join(__dirname, 'test_img.jpg');
    fs.writeFileSync(testFilePath, 'dummy image content');

    // Tạo JWT token giả cho user ID = 000000000000000000000002
    const token = jwt.sign(
        { id: '000000000000000000000002', username: 'manager_q7', role: 2 },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN }
    );

    const form = new FormData();
    form.append('avatar', fs.createReadStream(testFilePath));

    try {
        console.log('Sending request...');
        const response = await axios.post('http://localhost:3000/api/v1/users/profile/avatar', form, {
            headers: {
                ...form.getHeaders(),
                'Authorization': `Bearer ${token}`
            }
        });
        console.log('Upload successful!');
        console.log(response.data);
    } catch (err) {
        console.error('Upload failed!');
        console.error(err.message);
        if (err.response) {
            console.error(err.response.status, err.response.data);
        }
    }

    fs.unlinkSync(testFilePath);
};

testUpload();
