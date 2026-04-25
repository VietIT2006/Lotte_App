1. products (Sản phẩm chính)
SQLCREATE TABLE products (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  slug TEXT UNIQUE,
  description TEXT,
  short_description TEXT,
  category_id UUID,
  category_name TEXT,                    -- denormalized
  supplier_id UUID,
  supplier_name TEXT,                    -- denormalized
  brand TEXT,
  origin TEXT,
  origin_country TEXT,
  unit TEXT,
  weight NUMERIC,
  barcode TEXT,
  sku TEXT UNIQUE,
  price NUMERIC NOT NULL,
  original_price NUMERIC,
  import_price NUMERIC,
  discount_percent NUMERIC,
  images TEXT[],                         -- array URL
  gallery TEXT[],
  thumbnail TEXT,
  tags TEXT[],
  eco_label TEXT,
  is_active BOOLEAN DEFAULT true,
  is_featured BOOLEAN DEFAULT false,
  is_best_seller BOOLEAN DEFAULT false,
  is_new BOOLEAN DEFAULT false,
  average_rating NUMERIC DEFAULT 0,
  review_count INTEGER DEFAULT 0,
  total_reviews INTEGER DEFAULT 0,
  sold_count INTEGER DEFAULT 0,
  stock INTEGER DEFAULT 0,               -- computed
  specifications JSONB,
  nutrition_info JSONB,
  storage_instructions TEXT,
  usage_guide TEXT,
  storage_guide TEXT,
  notes TEXT,
  recipe_suggestions TEXT[],
  highlights TEXT[],
  rating_breakdown JSONB,
  related_product_ids UUID[],
  frequently_bought_together UUID[],
  vat_included BOOLEAN DEFAULT true,
  shipping_excluded BOOLEAN DEFAULT false,
  master_id UUID,                        -- legacy
  manufacture_date DATE,
  expiry_date DATE,
  batch_code TEXT,
  is_expiring_soon BOOLEAN,
  is_expired BOOLEAN,
  created_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
2. users
SQLCREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username TEXT UNIQUE NOT NULL,
  full_name TEXT,
  email TEXT UNIQUE NOT NULL,
  phone TEXT,
  password_hash TEXT,
  avatar TEXT,
  role_id UUID,
  role_key TEXT,
  permissions TEXT[],
  branch_id UUID,
  lotte_points INTEGER DEFAULT 0,
  membership_level TEXT,
  signup_method TEXT,
  login_provider TEXT,
  google_id TEXT,
  facebook_id TEXT,
  social_providers JSONB,
  social_links JSONB,
  status TEXT DEFAULT 'active',
  is_active BOOLEAN DEFAULT true,
  profile_completed BOOLEAN DEFAULT false,
  wallet_balance NUMERIC DEFAULT 0,
  default_payment_method UUID,
  email_verified BOOLEAN DEFAULT false,
  email_verification_code TEXT,
  email_verification_expires_at TIMESTAMPTZ,
  email_verification_attempts INTEGER DEFAULT 0,
  email_otp_last_sent_at TIMESTAMPTZ,
  dob DATE,
  gender TEXT,
  address JSONB,
  bio TEXT,
  note TEXT,
  tags TEXT[],
  preferences JSONB,
  security JSONB,
  settings JSONB,
  last_login_at TIMESTAMPTZ,
  refresh_token TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
3. roles
SQLCREATE TABLE roles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  key TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  description TEXT,
  role_id TEXT,
  permissions TEXT[],
is_system BOOLEAN DEFAULT false,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
4. permissions
SQLCREATE TABLE permissions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  key TEXT UNIQUE NOT NULL,
  label TEXT,
  group TEXT,
  description TEXT,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
5. categories
SQLCREATE TABLE categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  slug TEXT UNIQUE,
  icon TEXT,
  image TEXT,
  banner TEXT,
  description TEXT,
  parent_id UUID,
  sort_order INTEGER,
  display_order INTEGER,
  is_active BOOLEAN DEFAULT true,
  product_count INTEGER DEFAULT 0,
  created_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
6. branches
SQLCREATE TABLE branches (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  address TEXT,
  city TEXT,
  phone TEXT,
  manager TEXT,
  is_active BOOLEAN DEFAULT true,
  operating_hours JSONB,
  coordinates JSONB,                     -- {lat, lng}
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
7. branch_products
SQLCREATE TABLE branch_products (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID NOT NULL REFERENCES products(id),
  master_id UUID,
  sku TEXT,
  category_id UUID,
  category_name TEXT,
  supplier_id UUID,
  supplier_name TEXT,
  branch_id UUID NOT NULL REFERENCES branches(id),
  price NUMERIC NOT NULL,
  original_price NUMERIC,
  import_price NUMERIC,
  discount_percent NUMERIC,
  stock INTEGER DEFAULT 0,
  min_stock INTEGER,
  max_purchase_limit INTEGER,
  is_available BOOLEAN DEFAULT true,
  sold_count INTEGER DEFAULT 0,
  manufacture_date DATE,
  expiry_date DATE,
  batch_code TEXT,
  is_expiring_soon BOOLEAN,
  is_expired BOOLEAN,
  promotion_tag TEXT,
  promotion_end_date TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
8. carts
SQLCREATE TABLE carts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  branch_id UUID NOT NULL REFERENCES branches(id),
  items JSONB NOT NULL,                  -- [{branch_product_id, quantity, price, ...}]
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, branch_id)
);
9. orders
SQLCREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  items JSONB NOT NULL,                  -- snapshot sản phẩm
  order_address JSONB,
  status TEXT,
  subtotal NUMERIC,
  shipping_fee NUMERIC,
  discount_amount NUMERIC,
  total_amount NUMERIC,
  coupon_code TEXT,
  points_earned INTEGER,
  payment JSONB,
  tracking JSONB,
  delivery_slot JSONB,
  branch_id UUID REFERENCES branches(id),
  branch_name TEXT,
  pricing_breakdown JSONB,
  applied_promotions JSONB[],
applied_coupon JSONB,
  gift_items JSONB[],
  note TEXT,
  generated_invoice_url TEXT,
  email_notification_status TEXT,
  email_notification_sent_at TIMESTAMPTZ,
  email_notification_error TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
10. payment_methods
SQLCREATE TABLE payment_methods (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  type TEXT NOT NULL,
  provider TEXT,
  brand TEXT,
  last4 TEXT,
  holder_name TEXT,
  card_number TEXT,
  card_holder TEXT,
  expiry TEXT,
  phone TEXT,
  is_default BOOLEAN DEFAULT false,
  icon TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
11. payment_transactions
SQLCREATE TABLE payment_transactions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID REFERENCES orders(id),
  user_id UUID REFERENCES users(id),
  provider TEXT,
  method_id UUID REFERENCES payment_methods(id),
  transaction_id TEXT,
  amount NUMERIC NOT NULL,
  currency TEXT DEFAULT 'VND',
  status TEXT,
  qr_data JSONB,
  paid_at TIMESTAMPTZ,
  expired_at TIMESTAMPTZ,
  metadata JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
12. payment_providers
SQLCREATE TABLE payment_providers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  code TEXT UNIQUE NOT NULL,
  icon TEXT,
  is_active BOOLEAN DEFAULT true,
  config JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
13. promotions
SQLCREATE TABLE promotions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title TEXT NOT NULL,
  description TEXT,
  type TEXT NOT NULL,
  voucher_type TEXT,
  status TEXT,
  start_date TIMESTAMPTZ,
  end_date TIMESTAMPTZ,
  is_active BOOLEAN DEFAULT true,
  priority INTEGER,
  scope TEXT,
  target_product_ids UUID[],
  target_category_ids UUID[],
  target_branch_ids UUID[],
  excluded_product_ids UUID[],
  excluded_category_ids UUID[],
  is_auto_generated BOOLEAN DEFAULT false,
  source TEXT,
  suggested_by_system BOOLEAN DEFAULT false,
  total_quantity INTEGER,
  remaining_quantity INTEGER,
  claimed_count INTEGER DEFAULT 0,
  hide_after_expired_hours INTEGER,
  auto_hide_after_expired BOOLEAN DEFAULT false,
  notification_sent BOOLEAN DEFAULT false,
  usage_limit INTEGER,
  max_redemptions INTEGER,
  usage_count INTEGER DEFAULT 0,
  usage_per_user INTEGER,
  min_order_amount NUMERIC,
  min_quantity INTEGER,
  gift_quantity INTEGER,
  discount_value NUMERIC,
  max_discount_amount NUMERIC,
  gift_product_id UUID,
  points_multiplier NUMERIC,
  badge_text TEXT,
  banner_image TEXT,
  image TEXT,
  banner_url TEXT,
  claim_campaign TEXT,
  stackable BOOLEAN DEFAULT false,
  created_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
14. coupons
SQLCREATE TABLE coupons (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code TEXT UNIQUE NOT NULL,
  title TEXT,
  description TEXT,
image TEXT,
  type TEXT,
  voucher_type TEXT,
  discount_value NUMERIC,
  min_order_amount NUMERIC,
  min_quantity INTEGER,
  max_discount_amount NUMERIC,
  total_quantity INTEGER,
  remaining_quantity INTEGER,
  claimed_count INTEGER DEFAULT 0,
  hide_after_expired_hours INTEGER,
  auto_hide_after_expired BOOLEAN DEFAULT false,
  start_date TIMESTAMPTZ,
  end_date TIMESTAMPTZ,
  usage_limit INTEGER,
  usage_per_user INTEGER,
  used_count INTEGER DEFAULT 0,
  is_active BOOLEAN DEFAULT true,
  status TEXT,
  claim_campaign TEXT,
  badge_text TEXT,
  banner_image TEXT,
  scope TEXT,
  target_product_ids UUID[],
  target_category_ids UUID[],
  target_branch_ids UUID[],
  excluded_product_ids UUID[],
  excluded_category_ids UUID[],
  created_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
15. coupon_usages
SQLCREATE TABLE coupon_usages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  coupon_id UUID NOT NULL REFERENCES coupons(id),
  user_id UUID NOT NULL REFERENCES users(id),
  order_id UUID REFERENCES orders(id),
  discount_amount NUMERIC,
  used_at TIMESTAMPTZ DEFAULT NOW()
);
16. coupon_claims
SQLCREATE TABLE coupon_claims (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  coupon_id UUID NOT NULL REFERENCES coupons(id),
  user_id UUID NOT NULL REFERENCES users(id),
  claimed_at TIMESTAMPTZ DEFAULT NOW(),
  status TEXT,
  used_order_id UUID REFERENCES orders(id)
);
17. promotion_claims
SQLCREATE TABLE promotion_claims (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  promotion_id UUID NOT NULL REFERENCES promotions(id),
  user_id UUID NOT NULL REFERENCES users(id),
  branch_id UUID REFERENCES branches(id),
  claimed_at TIMESTAMPTZ DEFAULT NOW(),
  status TEXT,
  used_order_id UUID REFERENCES orders(id)
);
18. promotion_usages
SQLCREATE TABLE promotion_usages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  promotion_id UUID NOT NULL REFERENCES promotions(id),
  user_id UUID REFERENCES users(id),
  order_id UUID NOT NULL REFERENCES orders(id),
  discount_amount NUMERIC,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
19. viewed_histories
SQLCREATE TABLE viewed_histories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  product_id UUID NOT NULL REFERENCES products(id),
  branch_product_id UUID,
  product_name TEXT,
  product_image TEXT,
  price NUMERIC,
  original_price NUMERIC,
  category TEXT,
  view_count INTEGER DEFAULT 1,
  viewed_at TIMESTAMPTZ DEFAULT NOW(),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, product_id, branch_product_id)
);
20. reviews
SQLCREATE TABLE reviews (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  user_name TEXT,
  user_avatar TEXT,
  product_id UUID NOT NULL REFERENCES products(id),
  product_name TEXT,
  branch_id UUID REFERENCES branches(id),
  branch_name TEXT,
  order_id UUID REFERENCES orders(id),
rating NUMERIC NOT NULL,
  title TEXT,
  content TEXT,
  images TEXT[],
  status TEXT,
  is_verified_purchase BOOLEAN DEFAULT false,
  helpful_count INTEGER DEFAULT 0,
  reported_count INTEGER DEFAULT 0,
  is_featured BOOLEAN DEFAULT false,
  is_hidden BOOLEAN DEFAULT false,
  is_deleted BOOLEAN DEFAULT false,
  admin_notes TEXT,
  moderation_reason TEXT,
  reply JSONB,                           -- {content, admin_id, admin_name, replied_at}
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
21. support_tickets
SQLCREATE TABLE support_tickets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_code TEXT UNIQUE NOT NULL,
  user_id UUID NOT NULL REFERENCES users(id),
  user_name TEXT,
  user_email TEXT,
  user_avatar TEXT,
  branch_id UUID REFERENCES branches(id),
  branch_name TEXT,
  order_id UUID REFERENCES orders(id),
  category TEXT,
  priority TEXT,
  status TEXT,
  subject TEXT NOT NULL,
  message TEXT,
  attachments TEXT[],
  thread JSONB,
  messages JSONB[],                      -- legacy
  internal_notes TEXT[],
  assigned_agent_id UUID,
  assigned_agent_name TEXT,
  assigned_to TEXT,                      -- legacy
  sla_due_at TIMESTAMPTZ,
  first_response_at TIMESTAMPTZ,
  resolved_at TIMESTAMPTZ,
  closed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
22. notifications
SQLCREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  type TEXT,
  title TEXT NOT NULL,
  message TEXT,
  icon TEXT,
  link TEXT,
  is_read BOOLEAN DEFAULT false,
  metadata JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
23. loyalty_transactions
SQLCREATE TABLE loyalty_transactions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  type TEXT NOT NULL,
  points INTEGER NOT NULL,
  source TEXT,
  description TEXT,
  order_id UUID REFERENCES orders(id),
  balance_after INTEGER,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
24. loyalty_rules
SQLCREATE TABLE loyalty_rules (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  description TEXT,
  type TEXT,
  points_per_unit NUMERIC,
  min_order_value NUMERIC,
  multiplier NUMERIC,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
25. return_requests
SQLCREATE TABLE return_requests (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  order_id UUID NOT NULL REFERENCES orders(id),
  branch_id UUID REFERENCES branches(id),
  status TEXT,
  reason TEXT NOT NULL,
  description TEXT,
  refund_method TEXT,
  contact_phone TEXT,
  amount_requested NUMERIC,
  evidence_images TEXT[],
  items JSONB,
  admin_note TEXT,
  resolved_by UUID,
  resolved_at TIMESTAMPTZ,
  timeline JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
26. inventory_batches
SQLCREATE TABLE inventory_batches (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  branch_product_id UUID NOT NULL,
  batch_code TEXT,
  quantity INTEGER NOT NULL,
  exp_date DATE,
  manufacture_date DATE,
  received_date DATE,
  cost_price NUMERIC,
  supplier_id UUID,
  supplier_name TEXT,
  note TEXT,
  purchase_order_id UUID,
  import_receipt_id UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
27. suppliers
SQLCREATE TABLE suppliers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code TEXT,
  name TEXT NOT NULL,
  contact_name TEXT,
  email TEXT,
  phone TEXT,
  address TEXT,
  tax_code TEXT,
  payment_terms TEXT,
  note TEXT,
  total_debt NUMERIC DEFAULT 0,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
28. import_orders
SQLCREATE TABLE import_orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_code TEXT UNIQUE NOT NULL,
  supplier_id UUID NOT NULL REFERENCES suppliers(id),
  branch_id UUID NOT NULL REFERENCES branches(id),
  status TEXT,
  expected_date DATE,
  ordered_date DATE,
  received_date DATE,
  currency TEXT DEFAULT 'VND',
  items JSONB,
  total_amount NUMERIC,
  total_received_amount NUMERIC,
  note TEXT,
  timeline JSONB,
  created_by UUID,
  updated_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
29. import_receipts
SQLCREATE TABLE import_receipts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  receipt_code TEXT UNIQUE NOT NULL,
  import_order_id UUID NOT NULL REFERENCES import_orders(id),
  supplier_id UUID NOT NULL REFERENCES suppliers(id),
  branch_id UUID NOT NULL REFERENCES branches(id),
  received_date DATE,
  status TEXT,
  items JSONB,
  total_amount NUMERIC,
  note TEXT,
  created_by UUID,
  updated_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);
30. stock_movements
SQLCREATE TABLE stock_movements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  branch_id UUID NOT NULL REFERENCES branches(id),
  branch_name TEXT,
  product_id UUID NOT NULL REFERENCES products(id),
  product_name TEXT,
  branch_product_id UUID NOT NULL,
  batch_code TEXT,
  movement_type TEXT NOT NULL,
  quantity INTEGER NOT NULL,
  before_stock INTEGER NOT NULL,
  after_stock INTEGER NOT NULL,
  reference_type TEXT,
  reference_id UUID,
  created_by UUID,
  note TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);