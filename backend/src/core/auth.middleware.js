const jwt = require('jsonwebtoken');

const authenticate = (req, res, next) => {
    try {
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({
                success: false,
                message: 'Unauthorized: No token provided'
            });
        }

        const token = authHeader.split(' ')[1];
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        
        req.user = decoded;
        
        // Role mapping based on role_id
        const roleId = req.user.role;
        if (roleId === 1) req.user.roleStr = 'superAdmin';
        else if (roleId === 2) req.user.roleStr = 'admin';
        else if (roleId === 3) req.user.roleStr = 'customer';
        else if (roleId === 6) req.user.roleStr = 'shipper';

        next();
    } catch (error) {
        return res.status(401).json({
            success: false,
            message: 'Unauthorized: Invalid token'
        });
    }
};

const authMiddleware = (rolesOrReq, res, next) => {
    // If called with an array of roles, return a middleware function
    if (Array.isArray(rolesOrReq)) {
        const allowedRoles = rolesOrReq;
        return (req, res, next) => {
            authenticate(req, res, () => {
                if (allowedRoles.length > 0 && !allowedRoles.includes(req.user.roleStr) && !allowedRoles.includes(req.user.role)) {
                    return res.status(403).json({ success: false, message: 'Forbidden' });
                }
                next();
            });
        };
    }
    
    // Normal usage: authMiddleware(req, res, next)
    authenticate(rolesOrReq, res, next);
};

module.exports = authMiddleware;
