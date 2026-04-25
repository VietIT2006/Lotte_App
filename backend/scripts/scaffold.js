const fs = require('fs');
const path = require('path');

const domains = [
    'users', 'catalog', 'inventory', 'ordering', 
    'payments', 'promotions', 'loyalty', 'purchasing', 
    'customer_service', 'notifications'
];

const srcDir = path.join(__dirname, '..', 'src', 'domains');

// Utility function to convert snake_case to PascalCase
const toPascalCase = (str) => {
    return str.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join('');
};

// Utility function to convert snake_case to camelCase
const toCamelCase = (str) => {
    return str.replace(/_([a-z])/g, function (g) { return g[1].toUpperCase(); });
};

domains.forEach(domain => {
    const dir = path.join(srcDir, domain);
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }

    const routeFile = path.join(dir, `${domain}.routes.js`);
    const controllerFile = path.join(dir, `${domain}.controller.js`);
    const serviceFile = path.join(dir, `${domain}.service.js`);

    const className = toPascalCase(domain);
    const varName = toCamelCase(domain);

    if (!fs.existsSync(controllerFile)) {
        fs.writeFileSync(controllerFile, `class ${className}Controller {\n    // TODO: Implement handler methods\n}\n\nmodule.exports = new ${className}Controller();`);
    }

    if (!fs.existsSync(serviceFile)) {
        fs.writeFileSync(serviceFile, `const db = require('../../core/db');\n\nclass ${className}Service {\n    // TODO: Implement database logic\n}\n\nmodule.exports = new ${className}Service();`);
    }

    if (!fs.existsSync(routeFile)) {
        fs.writeFileSync(routeFile, `const express = require('express');\nconst ${varName}Controller = require('./${domain}.controller');\n\nconst router = express.Router();\n\n// Example: router.get('/', ${varName}Controller.getAll);\n\nmodule.exports = router;`);
    }
});

console.log('Domain scaffolding complete.');
