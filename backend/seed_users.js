const { MongoClient } = require('mongodb');
const bcrypt = require('bcrypt');
require('dotenv').config();

async function run() {
    const client = new MongoClient(process.env.MONGODB_URI);
    try {
        await client.connect();
        const db = client.db();
        console.log("Connected to MongoDB.");

        // Helper to get default fields
        const getDefaultFields = () => ({
            avatar: "https://via.placeholder.com/150",
            permissions: [],
            branch_id: null,
            lotte_points: 0,
            membership_level: "Bạc",
            signup_method: "email",
            login_provider: "local",
            authProviders: ["local"],
            googleId: null,
            facebookId: null,
            facebook_id: null,
            social_links: { facebook: null, google: null },
            status: "ACTIVE",
            is_active: true,
            profile_completed: true,
            wallet_balance: 0,
            default_payment_method: null,
            email_verified: true,
            dob: null,
            gender: null,
            address: null,
            bio: null,
            note: "",
            tags: [],
            preferences: {
                newsletter: true,
                sms_alerts: true,
                language: "vi",
                receive_promotions: true
            },
            social_providers: []
        });

        const usersToInsert = [];
        const passwordHash = await bcrypt.hash("123456", 10);

        // 1. Super Admin
        usersToInsert.push({
            ...getDefaultFields(),
            username: "superadmin_demo",
            full_name: "Super Admin Demo",
            email: "superadmin@lotte.com",
            phone: "0900000001",
            password_hash: passwordHash,
            role_id: 1,
            role_key: "super_admin",
            membership_level: "Kim Cương",
            created_at: new Date(),
            updated_at: new Date()
        });

        // 2. Admin Thường
        usersToInsert.push({
            ...getDefaultFields(),
            username: "admin_demo",
            full_name: "Admin Demo",
            email: "admin@lotte.com",
            phone: "0900000002",
            password_hash: passwordHash,
            role_id: 2,
            role_key: "admin",
            created_at: new Date(),
            updated_at: new Date()
        });

        // 3. User (Customer)
        usersToInsert.push({
            ...getDefaultFields(),
            username: "customer_demo",
            full_name: "Customer Demo",
            email: "customer@lotte.com",
            phone: "0900000003",
            password_hash: passwordHash,
            role_id: 3,
            role_key: "customer",
            created_at: new Date(),
            updated_at: new Date()
        });

        // 4. Shipper
        usersToInsert.push({
            ...getDefaultFields(),
            username: "shipper_demo",
            full_name: "Shipper Demo",
            email: "shipper@lotte.com",
            phone: "0900000004",
            password_hash: passwordHash,
            role_id: 6,
            role_key: "shipper",
            created_at: new Date(),
            updated_at: new Date()
        });

        // Check and create Shipper role if not exists
        const shipperRole = await db.collection('roles').findOne({ key: 'shipper' });
        if (!shipperRole) {
            await db.collection('roles').insertOne({
                role_id: 6,
                key: "shipper",
                name: "Shipper",
                is_active: true,
                is_system: true,
                permissions: ["orders.read", "orders.update_status"],
                created_at: new Date()
            });
            console.log("Created missing role: Shipper");
        }

        // Insert Users
        for (const user of usersToInsert) {
            const existing = await db.collection('users').findOne({ email: user.email });
            if (!existing) {
                const result = await db.collection('users').insertOne(user);
                console.log(`Created user: ${user.email} (Role: ${user.role_key}) with ID: ${result.insertedId}`);
            } else {
                console.log(`User already exists: ${user.email}`);
            }
        }

        console.log("Database seeding completed.");

    } catch (e) {
        console.error("Error during seeding:", e);
    } finally {
        await client.close();
    }
}

run();
