const { MongoClient } = require('mongodb');
require('dotenv').config();

async function run() {
    const client = new MongoClient(process.env.MONGODB_URI);
    try {
        await client.connect();
        const db = client.db(); 
        
        const missingFields = {
            role_key: "admin",
            permissions: [],
            branch_id: null,
            lotte_points: 0,
            membership_level: "Kim Cương",
            signup_method: "email",
            login_provider: "local",
            authProviders: ["local"],
            googleId: null,
            facebookId: null,
            facebook_id: null,
            social_links: { "facebook": null, "google": null },
            profile_completed: true,
            wallet_balance: 0,
            default_payment_method: null,
            email_verified: true,
            email_verification_code: null,
            email_verification_expires_at: null,
            email_verification_attempts: 0,
            email_otp_last_sent_at: null,
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
                receive_promotions: true,
                eco_prefer: false,
                favorite_categories: [],
                preferred_store: null,
                notification_email_promo: true,
                notification_sms_order: true,
                notification_push_order: true,
                notification_promo: true,
                notification_system: true
            },
            password_changed_at: null,
            security: {
                two_factor_enabled: false,
                two_factor_method: null,
                totp_secret: null,
                backup_codes: [],
                last_login_device: "",
                last_login_ip: "",
                last_login_at: null
            },
            settings: {
                language: "vi",
                dark_mode: false,
                privacy_profile_visible: true,
                marketing_opt_in: true,
                sms_opt_in: true
            },
            last_login_at: null,
            refresh_token: null,
            is_deleted: false,
            force_password_change: false,
            employee_info: {
                employee_code: null,
                department: null,
                work_type: "FULL_TIME",
                notes: ""
            },
            social_providers: [],
            __v: 0
        };
        
        const result = await db.collection('users').updateOne(
            { email: 'admin2@lotte.com' },
            { $set: missingFields }
        );
        console.log('Update result:', result);
    } catch (e) {
        console.error(e);
    } finally {
        await client.close();
    }
}

run();
