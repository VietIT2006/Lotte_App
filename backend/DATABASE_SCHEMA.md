# 📊 MongoDB Database Schema Reference

File này chứa cấu trúc chi tiết của các bảng trong MongoDB để đối chiếu API.

## 📦 Collection: comparesummaries
- **Số lượng bản ghi:** 2
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69f83522f216e4284606d48c",
  "product_ids": [
    "000000000000000000000010",
    "000000000000000000000011"
  ],
  "hash": "000000000000000000000010-000000000000000000000011-vi",
  "locale": "vi",
  "summary": {
    "summary": "So sánh dựa trên dữ liệu hiện tại.",
    "best_choice": {
      "product_name": "",
      "reason": ""
    },
    "price_analysis": "",
    "quality_analysis": "",
    "value_analysis": "",
    "pros_cons": [],
    "recommendation": "Hãy lựa chọn theo nhu cầu."
  },
  "access_count": 3,
  "last_accessed_at": "2026-05-04T06:06:12.535Z",
  "createdAt": "2026-05-04T05:56:50.489Z",
  "updatedAt": "2026-05-04T06:06:12.536Z",
  "__v": 0
}
`

---

## 📦 Collection: viewedhistories
- **Số lượng bản ghi:** 29
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d70f9b7b9edb43e17c3d27",
  "product_id": "000000000000000000000010",
  "branch_product_id": null,
  "__v": 0,
  "created_at": "2026-04-09T02:31:55.732Z",
  "updated_at": "2026-04-09T02:31:55.732Z",
  "view_count": 1,
  "viewed_at": "2026-04-09T02:31:55.731Z"
}
`

---

## 📦 Collection: notifications
- **Số lượng bản ghi:** 9
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d1d843fcf7709cbcdf07b2",
  "user_id": "69c9d1daf68c224b03959259",
  "type": "coupon",
  "title": "⚠️ Xả hàng sắp hết hạn: bbbbbb",
  "message": "COUPON MỚI: Xả hàng sắp hết hạn bbbbbb. SKU: SKU-MNGTSD2N. Nhập từ: Nhà Cung Cấp Tổng Hợp (Default). Lô: BATCH-MNJT3GYX. HSD: 5/4/2026. Còn 0 ngày. Tồn hiện tại: 0.",
  "icon": "sell",
  "link": "/my-coupons",
  "is_read": false,
  "metadata": {
    "campaign_type": "coupon",
    "campaign_id": "69d1d843fcf7709cbcdf07aa",
    "created_by": "69c9d1daf68c224b03959259",
    "audience": "all_active_users"
  },
  "__v": 0,
  "created_at": "2026-04-05T03:34:27.466Z",
  "updated_at": "2026-04-05T03:34:27.466Z"
}
`

---

## 📦 Collection: adminsettings
- **Số lượng bản ghi:** 6
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d053da68d77df32fe0f2",
  "key": "default_shipping_fee",
  "value": "25000",
  "label": "",
  "group": "general",
  "__v": 0,
  "createdAt": "2026-03-30T01:22:27.498Z",
  "updatedAt": "2026-05-05T09:10:10.130Z"
}
`

---

## 📦 Collection: promotionclaims
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: event_posts
- **Số lượng bản ghi:** 12
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c7630a5c9a4bdbcd49b135",
  "title": "ƯU ĐÃI ĐỘC QUYỀN THÁNG 3 TỪ CÁC GIAN HÀNG TẠI LOTTE MART",
  "slug": "uu-dai-doc-quyen-thang-3",
  "category_id": 6,
  "thumbnail": "https://example.com/images/event_banner_march.jpg",
  "thumbnail_alt": "Banner ưu đãi tháng 3 Lotte Mart",
  "excerpt": "Chương trình quy tụ hơn 45+ thương hiệu đối tác với ưu đãi giảm từ 20-50% cùng nhiều phần quà tặng hấp dẫn.",
  "author_name": "Lotte Marketing Team",
  "author_avatar": "https://i.pravatar.cc/100?img=12",
  "published_at": "2026-02-28T09:00:00.000Z",
  "read_time": 5,
  "views": 18506,
  "likes": 820,
  "tags": [
    "khuyến mãi",
    "ưu đãi",
    "gian hàng"
  ],
  "start_date": "2026-03-01T00:00:00.000Z",
  "end_date": "2026-03-31T00:00:00.000Z",
  "is_featured": true,
  "is_published": true,
  "related_post_ids": [
    101,
    104
  ],
  "content_blocks": [
    {
      "type": "title",
      "text": "Ưu đãi độc quyền tháng 3 tại Lotte Mart"
    },
    {
      "type": "paragraph",
      "text": "Tháng 3 này, Lotte Mart mang đến chương trình khuyến mãi lớn nhất từ trước đến nay với sự tham gia của hơn 45 thương hiệu đối tác."
    },
    {
      "type": "section_title",
      "text": "Các ưu đãi nổi bật"
    },
    {
      "type": "list",
      "items": [
        "Giảm 20-50% toàn bộ gian hàng thời trang và mỹ phẩm",
        "Mua 1 tặng 1 cho nhiều sản phẩm thực phẩm",
        "Voucher giảm giá 100.000đ khi mua sắm từ 500.000đ"
      ]
    },
    {
      "type": "image",
      "url": "https://example.com/images/event_march_1.jpg",
      "alt": "Gian hàng ưu đãi"
    },
    {
      "type": "paragraph",
      "text": "Ưu đãi áp dụng tại tất cả chi nhánh Lotte Mart trên toàn quốc từ ngày 1/3 đến 31/3/2026."
    },
    {
      "type": "cta",
      "text": "Xem chi tiết chương trình",
      "url": "/promotions"
    }
  ],
  "status": "published",
  "created_by": 5,
  "created_at": "2026-02-28T09:00:00.000Z",
  "updated_at": "2026-05-04T04:06:18.121Z",
  "__v": 0
}
`

---

## 📦 Collection: promotions
- **Số lượng bản ghi:** 5
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d053da68d77df32fe0d8",
  "title": "Đại tiệc Trái cây Nhập khẩu Tươi ngon",
  "description": "Ưu đãi đặc biệt cho Táo Envy, Nho mẫu đơn và Cam Úc. Đảm bảo độ tươi ngon mỗi ngày.",
  "type": "percent",
  "discount_value": 0,
  "min_order_value": 0,
  "max_discount": 0,
  "image": "",
  "banner_image": "https://lottemart.vn/media/catalog/category/new_category/Bia_Th_t_Nh_p_Kh_u.png",
  "start_date": null,
  "end_date": null,
  "is_active": true,
  "applicable_categories": [],
  "applicable_products": [],
  "usage_limit": 0,
  "used_count": 0,
  "terms": "",
  "__v": 1,
  "created_at": "2026-03-30T01:22:27.357Z",
  "updated_at": "2026-04-06T06:18:41.159Z",
  "auto_hide_after_expired": true,
  "badge_text": "",
  "banner_url": "",
  "claim_campaign": true,
  "claimed_count": 0,
  "excluded_category_ids": [],
  "excluded_product_ids": [],
  "gift_quantity": 0,
  "hide_after_expired_hours": 24,
  "is_auto_generated": false,
  "min_order_amount": 0,
  "min_quantity": 0,
  "notification_sent": false,
  "points_multiplier": 1,
  "priority": 0,
  "scope": "all",
  "source": "manual",
  "stackable": false,
  "status": "active",
  "suggested_by_system": false,
  "target_branch_ids": [],
  "target_category_ids": [],
  "target_product_ids": [],
  "usage_count": 0,
  "usage_per_user": 1,
  "total_quantity": 100,
  "voucher_type": "product"
}
`

---

## 📦 Collection: paymentmethods
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: loyaltyrules
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: inventorybatches
- **Số lượng bản ghi:** 13
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d08d4044d138ca1777adf8",
  "branch_product_id": "000000000000000000000101",
  "batch_code": "BATCH-INITIAL",
  "quantity": 118,
  "exp_date": "2026-04-15T00:00:00.000Z",
  "manufacture_date": "2026-02-04T04:02:08.722Z",
  "cost_price": 20300,
  "supplier_id": "69d08d3f44d138ca1777adda",
  "supplier_name": "Nhà Cung Cấp Tổng Hợp (Default)",
  "note": "Auto-generated backfill",
  "purchase_order_id": "69d08d4044d138ca1777adf2",
  "import_receipt_id": "69d08d4044d138ca1777adf5",
  "received_date": "2026-04-04T04:02:08.882Z",
  "created_at": "2026-04-04T04:02:08.882Z",
  "updated_at": "2026-04-04T04:02:08.882Z",
  "__v": 0
}
`

---

## 📦 Collection: reviews
- **Số lượng bản ghi:** 2
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d0a704ae8266db37a2b59d",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "user_name": "Cường Lê",
  "user_avatar": null,
  "product_id": "000000000000000000000001",
  "product_name": "",
  "branch_id": null,
  "branch_name": "",
  "order_id": null,
  "rating": 5,
  "title": "",
  "content": "",
  "images": [],
  "status": "published",
  "is_verified_purchase": false,
  "helpful_count": 0,
  "reported_count": 0,
  "is_featured": false,
  "is_hidden": false,
  "is_deleted": false,
  "admin_notes": "",
  "moderation_reason": "",
  "reply": {
    "content": null,
    "admin_id": null,
    "admin_name": null,
    "replied_at": null
  },
  "created_at": "2026-04-04T05:52:04.790Z",
  "updated_at": "2026-04-04T06:37:05.045Z",
  "__v": 0
}
`

---

## 📦 Collection: importorders
- **Số lượng bản ghi:** 13
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d08d4044d138ca1777adf2",
  "order_code": "PO-MNJT3EHK",
  "supplier_id": "69d08d3f44d138ca1777adda",
  "branch_id": "000000000000000000000001",
  "status": "received",
  "expected_date": "2026-04-04T04:02:08.792Z",
  "ordered_date": "2026-04-04T04:02:08.792Z",
  "received_date": "2026-04-04T04:02:08.792Z",
  "currency": "VND",
  "items": [
    {
      "product_id": "000000000000000000000001",
      "branch_product_id": "000000000000000000000101",
      "sku": "MILK001",
      "product_name": "Sữa tươi Vinamilk 1L",
      "quantity_ordered": 118,
      "quantity_received": 118,
      "unit_cost": 20300,
      "subtotal": 2395400,
      "batch_code": "BATCH-INITIAL",
      "expiry_date": "2026-04-15T00:00:00.000Z",
      "note": "",
      "_id": "69d08d4044d138ca1777adf3"
    }
  ],
  "total_amount": 2395400,
  "total_received_amount": 2395400,
  "note": "Auto-generated backfill",
  "created_by": null,
  "updated_by": null,
  "timeline": [],
  "created_at": "2026-04-04T04:02:08.797Z",
  "updated_at": "2026-04-04T04:02:08.797Z",
  "__v": 0
}
`

---

## 📦 Collection: promotionusages
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: coupons
- **Số lượng bản ghi:** 5
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d0bf08222f26796c4911be",
  "code": "FREESHIP10",
  "title": "Miễn phí vận chuyển 30k",
  "description": "",
  "image": "",
  "type": "free_shipping",
  "discount_value": 30000,
  "min_order_amount": 100000,
  "min_quantity": 0,
  "max_discount_amount": null,
  "total_quantity": 100,
  "remaining_quantity": 2000,
  "claimed_count": 1,
  "hide_after_expired_hours": 24,
  "auto_hide_after_expired": true,
  "start_date": null,
  "end_date": null,
  "usage_limit": 2000,
  "usage_per_user": 1,
  "used_count": 0,
  "is_active": true,
  "status": "active",
  "claim_campaign": true,
  "badge_text": "",
  "banner_image": "",
  "scope": "all",
  "target_product_ids": [],
  "target_category_ids": [],
  "target_branch_ids": [],
  "excluded_product_ids": [],
  "excluded_category_ids": [],
  "__v": 0,
  "created_at": "2026-04-04T07:34:32.084Z",
  "updated_at": "2026-04-06T06:18:40.491Z",
  "voucher_type": "shipping"
}
`

---

## 📦 Collection: auditlogs
- **Số lượng bản ghi:** 8
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d0902e1f17dc86477f5abe",
  "user_id": "69c9d1daf68c224b03959259",
  "user_name": "Admin Lotte",
  "action": "CREATE",
  "entity": "supplier",
  "entity_id": "69d0902e1f17dc86477f5abc",
  "details": {
    "new_data": {
      "code": "HTP901",
      "name": "hưng thịnh phát",
      "contact_name": "trưởng phòng A",
      "email": "phongA@gmail.com",
      "phone": "0908070605",
      "address": "quận 5 tpHCM",
      "tax_code": "82662881552",
      "payment_terms": "COD",
      "note": "",
      "total_debt": 0,
      "is_active": true,
      "_id": "69d0902e1f17dc86477f5abc",
      "created_at": "2026-04-04T04:14:38.738Z",
      "updated_at": "2026-04-04T04:14:38.738Z",
      "__v": 0
    }
  },
  "ip": "::1",
  "created_at": "2026-04-04T04:14:38.782Z",
  "updatedAt": "2026-04-04T04:14:38.782Z",
  "__v": 0
}
`

---

## 📦 Collection: events
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: eventcomments
- **Số lượng bản ghi:** 2
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d4c22aa6bff2cb6d99767b",
  "event_id": "69c7630a5c9a4bdbcd49b13f",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "user_name": "Cường Lê",
  "user_avatar": null,
  "content": "hi",
  "status": "active",
  "created_at": "2026-04-07T08:36:58.243Z",
  "updatedAt": "2026-04-07T08:36:58.243Z",
  "__v": 0
}
`

---

## 📦 Collection: notificationtemplates
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: paymentproviders
- **Số lượng bản ghi:** 4
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d053da68d77df32fe0ed",
  "name": "MoMo",
  "code": "momo",
  "icon": "",
  "is_active": true,
  "__v": 0,
  "createdAt": "2026-03-30T01:22:27.462Z",
  "updatedAt": "2026-03-30T01:22:27.462Z"
}
`

---

## 📦 Collection: permissions
- **Số lượng bản ghi:** 19
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69cceed0768d7ed95a6226b0",
  "key": "products.read",
  "__v": 0,
  "created_at": "2026-04-01T10:09:20.259Z",
  "description": "",
  "group": "products",
  "is_active": true,
  "label": "Read products",
  "updated_at": "2026-05-11T00:34:06.697Z"
}
`

---

## 📦 Collection: branches
- **Số lượng bản ghi:** 5
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "000000000000000000000001",
  "name": "Lotte Mart Quận 7",
  "address": "469 Nguyễn Hữu Thọ, Tân Hưng, Quận 7",
  "city": "Hồ Chí Minh",
  "phone": "028 5411 5555",
  "manager": "",
  "is_active": true,
  "operating_hours": "08:00 - 22:00",
  "__v": 0,
  "createdAt": "2026-03-30T01:22:27.286Z",
  "updatedAt": "2026-05-04T03:19:51.305Z",
  "coordinates": {
    "lat": 10.74164,
    "lng": 106.701706
  }
}
`

---

## 📦 Collection: expressRateRecords
- **Số lượng bản ghi:** 1
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "::ffff:127.0.0.1-Mozilla/5.0(WindowsN-guest",
  "counter": 1,
  "expirationDate": "2026-05-11T00:49:12.531Z"
}
`

---

## 📦 Collection: wishlistitems
- **Số lượng bản ghi:** 2
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d70fb5c4f730c4a1ed2d73",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "product_id": "000000000000000000000010",
  "branch_product_id": "000000000000000000000113",
  "created_at": "2026-04-09T02:32:21.339Z",
  "updated_at": "2026-04-09T02:32:21.339Z",
  "__v": 0
}
`

---

## 📦 Collection: branchproducts
- **Số lượng bản ghi:** 15
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "000000000000000000000101",
  "product_id": "000000000000000000000001",
  "branch_id": "000000000000000000000001",
  "price": 29000,
  "original_price": 40000,
  "discount_percent": 28,
  "stock": 107,
  "min_stock": 0,
  "max_purchase_limit": 0,
  "is_available": true,
  "promotion_tag": "",
  "promotion_end_date": null,
  "__v": 0,
  "created_at": "2026-03-30T01:22:27.322Z",
  "updated_at": "2026-04-11T06:55:12.071Z",
  "sold_count": 12,
  "batch_code": "BATCH-INITIAL",
  "category_name": "Đồ uống",
  "import_price": 0,
  "is_expired": false,
  "is_expiring_soon": false,
  "master_id": "MAS-00000001",
  "sku": "MILK001",
  "supplier_name": "Nhà Cung Cấp Tổng Hợp (Default)",
  "expiry_date": "2026-04-15T00:00:00.000Z",
  "manufacture_date": "2026-02-04T04:02:08.722Z",
  "category_id": "000000000000000000000002",
  "supplier_id": "69d08d3f44d138ca1777adda"
}
`

---

## 📦 Collection: supporttickets
- **Số lượng bản ghi:** 1
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d0a685ae8266db37a2b56c",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "user_name": "",
  "user_email": "",
  "user_avatar": null,
  "branch_id": null,
  "branch_name": "",
  "order_id": null,
  "category": "general",
  "priority": "medium",
  "status": "resolved",
  "subject": "thiếu đơn hàng",
  "message": "",
  "attachments": [],
  "messages": [
    {
      "sender": "user",
      "sender_name": "Cường Lê",
      "content": "hi chào bạn",
      "attachments": [],
      "_id": "69d0acb8df5b771c055723f4",
      "created_at": "2026-04-04T06:16:24.206Z"
    },
    {
      "sender": "agent",
      "sender_name": "Cường Lê",
      "content": "bạn cần mình giúp gì nào\n",
      "attachments": [],
      "_id": "69d0aceddf5b771c0557244e",
      "created_at": "2026-04-04T06:17:17.122Z"
    },
    {
      "sender": "user",
      "sender_name": "Cường Lê",
      "content": "hi",
      "attachments": [],
      "_id": "69d0b142563c7607e763eba8",
      "created_at": "2026-04-04T06:35:46.715Z"
    },
    {
      "sender": "agent",
      "sender_name": "Cường Lê",
      "content": "ha",
      "attachments": [],
      "_id": "69d0b14d563c7607e763ebc5",
      "created_at": "2026-04-04T06:35:57.199Z"
    }
  ],
  "assigned_agent_id": "69c9d1daf68c224b03959259",
  "assigned_agent_name": "Cường Lê",
  "assigned_to": "69c9d1daf68c224b03959259",
  "sla_due_at": null,
  "first_response_at": null,
  "resolved_at": "2026-04-04T06:16:53.622Z",
  "closed_at": null,
  "thread": [
    {
      "sender_type": "user",
      "sender_id": "69c9d847ea3c026a0831bf1e",
      "sender_name": "Cường Lê",
      "content": "hi chào bạn",
      "attachments": [],
      "_id": "69d0acb8df5b771c055723f3",
      "created_at": "2026-04-04T06:16:24.205Z"
    },
    {
      "sender_type": "agent",
      "sender_id": "69c9d1daf68c224b03959259",
      "sender_name": "Cường Lê",
      "content": "bạn cần mình giúp gì nào\n",
      "attachments": [],
      "_id": "69d0aceddf5b771c0557244d",
      "created_at": "2026-04-04T06:17:17.121Z"
    },
    {
      "sender_type": "user",
      "sender_id": "69c9d847ea3c026a0831bf1e",
      "sender_name": "Cường Lê",
      "content": "hi",
      "attachments": [],
      "_id": "69d0b142563c7607e763eba7",
      "created_at": "2026-04-04T06:35:46.713Z"
    },
    {
      "sender_type": "agent",
      "sender_id": "69c9d1daf68c224b03959259",
      "sender_name": "Cường Lê",
      "content": "ha",
      "attachments": [],
      "_id": "69d0b14d563c7607e763ebc4",
      "created_at": "2026-04-04T06:35:57.199Z"
    }
  ],
  "internal_notes": [],
  "created_at": "2026-04-04T05:49:57.128Z",
  "updated_at": "2026-04-04T06:35:57.201Z",
  "__v": 4
}
`

---

## 📦 Collection: users
- **Số lượng bản ghi:** 12
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "000000000000000000000001",
  "username": "superadmin",
  "full_name": "superadmin",
  "email": "admin@lotte.com",
  "phone": "0900000001",
  "password_hash": "$2a$10$5x.DSxW26vLR7ZC0fAFrvuP3t8kX7Womibjv.mvQzmbd8whOsfy/C",
  "avatar": "https://via.placeholder.com/150",
  "role_id": 1,
  "branch_id": null,
  "lotte_points": 5000,
  "membership_level": "Kim Cương",
  "signup_method": "email",
  "googleId": null,
  "facebookId": null,
  "social_links": {
    "facebook": null,
    "google": null
  },
  "status": "ACTIVE",
  "is_active": true,
  "email_verified": false,
  "dob": null,
  "gender": null,
  "address": null,
  "bio": null,
  "note": "",
  "tags": [],
  "preferences": {
    "newsletter": true,
    "sms_alerts": true,
    "language": "vi",
    "receive_promotions": true
  },
  "last_login_at": null,
  "refresh_token": null,
  "social_providers": [],
  "__v": 0,
  "created_at": "2026-03-30T01:22:26.998Z",
  "updated_at": "2026-03-30T01:22:26.998Z"
}
`

---

## 📦 Collection: backupmetas
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: carts
- **Số lượng bản ghi:** 4
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69cb74c5c7c0ec81b5f69f44",
  "user_id": "69c9daead9fb80416235e662",
  "branch_id": "000000000000000000000002",
  "items": [
    {
      "branch_product_id": "000000000000000000000202",
      "quantity": 1,
      "price": 33000,
      "unit_price": 33000,
      "product_name": "Nước rửa chén Sunlight 750ml",
      "product_image": "https://cdn.lottemart.vn/images/sunlight.jpg",
      "_id": "69cb77b7756f56de3e2262e0"
    }
  ],
  "created_at": "2026-03-31T07:16:21.046Z",
  "updated_at": "2026-03-31T07:28:55.222Z",
  "__v": 6
}
`

---

## 📦 Collection: idempotencykeys
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: couponclaims
- **Số lượng bản ghi:** 4
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d348b03f9d12d01b166e91",
  "coupon_id": "69d0bf08222f26796c4911bd",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "status": "claimed",
  "used_order_id": null,
  "claimed_at": "2026-04-06T05:46:24.775Z",
  "created_at": "2026-04-06T05:46:24.775Z",
  "updated_at": "2026-04-06T05:46:24.775Z",
  "__v": 0
}
`

---

## 📦 Collection: featureflags
- **Số lượng bản ghi:** 8
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69f5bfa2e9087cbc275be3f4",
  "key": "enable_flash_deals",
  "enabled": true,
  "description": "Bật/tắt Flash Deals trên trang chủ",
  "percentage": 100,
  "allowed_roles": [],
  "updated_by": "",
  "created_at": "2026-05-02T09:10:58.034Z",
  "updated_at": "2026-05-02T09:10:58.034Z",
  "__v": 0
}
`

---

## 📦 Collection: banners
- **Số lượng bản ghi:** 3
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d053da68d77df32fe0e3",
  "title": "Khai trương Lotte Mart Quận 7",
  "image": "http://localhost:3001/uploads/promotions/promotion_1775373947178_t3dbwk.png",
  "link": "/promotions/promo_1",
  "position": "home",
  "sort_order": 0,
  "is_active": true,
  "start_date": null,
  "end_date": null,
  "__v": 0,
  "createdAt": "2026-03-30T01:22:27.393Z",
  "updatedAt": "2026-03-30T01:22:27.393Z",
  "alt_text": "",
  "image_url": "http://localhost:3001/uploads/promotions/promotion_1775373947178_t3dbwk.png",
  "link_type": "url",
  "mobile_image_url": "",
  "priority": 0,
  "subtitle": "",
  "updated_at": "2026-04-05T07:25:47.231Z",
  "overlay_color": "rgba(0,0,0,0.3)",
  "text_color": "#ffffff",
  "text_shadow": true
}
`

---

## 📦 Collection: recipes
- **Số lượng bản ghi:** 2
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69f84172b2bb2304decb4177",
  "title": "banh-cuon-nong",
  "normalized_name": "banh-cuon-nong",
  "description": "Công thức cơ bản cho món banh-cuon-nong. (Hệ thống AI hiện đang xử lý chậm, đây là hướng dẫn tiêu chuẩn)",
  "ingredients": [
    {
      "name": "Nguyên liệu chính",
      "quantity": "Tùy khẩu phần",
      "unit": ""
    },
    {
      "name": "Gia vị cơ bản (muối, đường, mắm, tiêu)",
      "quantity": "Vừa đủ",
      "unit": ""
    }
  ],
  "steps": [
    {
      "step": 1,
      "title": "Sơ chế",
      "description": "Rửa sạch và sơ chế cẩn thận các nguyên liệu đã chuẩn bị."
    },
    {
      "step": 2,
      "title": "Chế biến",
      "description": "Tiến hành nấu món banh-cuon-nong với lửa vừa, nêm nếm gia vị cho vừa miệng."
    },
    {
      "step": 3,
      "title": "Hoàn thiện",
      "description": "Trình bày ra đĩa đẹp mắt và thưởng thức khi còn nóng."
    }
  ],
  "prep_time": "15 phút",
  "cook_time": "30 phút",
  "servings": 2,
  "difficulty": "Trung bình",
  "tips": [
    "Nêm nếm gia vị từ từ để dễ điều chỉnh."
  ],
  "tags": [
    "cơ bản"
  ],
  "source_type": "ai_generated",
  "ai_generated": true,
  "status": "active",
  "access_count": 9,
  "last_accessed_at": "2026-05-08T03:05:28.555Z",
  "createdAt": "2026-05-04T06:49:22.106Z",
  "updatedAt": "2026-05-08T03:05:28.556Z",
  "__v": 0,
  "image_url": ""
}
`

---

## 📦 Collection: suppliers
- **Số lượng bản ghi:** 3
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d08d3f44d138ca1777adda",
  "code": "SUP-DEFAULT",
  "name": "Nhà Cung Cấp Tổng Hợp (Default)",
  "contact_name": "Admin",
  "email": "",
  "phone": "0901234567",
  "address": "",
  "tax_code": "",
  "payment_terms": "",
  "note": "",
  "total_debt": 0,
  "is_active": true,
  "created_at": "2026-04-04T04:02:07.951Z",
  "updated_at": "2026-04-04T04:02:07.951Z",
  "__v": 0
}
`

---

## 📦 Collection: addresses
- **Số lượng bản ghi:** 6
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69cb80eb48542cc379d1073d",
  "user_id": "69c9daead9fb80416235e662",
  "name": "D24TXCN01-N LE THANH CUONG",
  "phone": "0846183771",
  "street": "đường nghê",
  "ward": "phường nghe",
  "district": "quận 9",
  "city": "tp hcm",
  "full_address": "",
  "is_default": false,
  "label": "home",
  "created_at": "2026-03-31T08:08:11.784Z",
  "updated_at": "2026-04-01T10:45:51.855Z",
  "__v": 0
}
`

---

## 📦 Collection: hotdeals
- **Số lượng bản ghi:** 5
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d0bf08222f26796c4911c5",
  "title": "Táo Rockit Mỹ",
  "description": "",
  "image_url": "https://images.unsplash.com/photo-1560806887-1e4cd0b6faa6?q=80&w=1974",
  "badge_text": "",
  "product_id": "000000000000000000000004",
  "type": "percent",
  "discount_value": 30,
  "discount_percent": 30,
  "deal_price": 105000,
  "original_price": 150000,
  "target_product_ids": [],
  "target_category_ids": [],
  "target_branch_ids": [],
  "start_date": null,
  "end_date": null,
  "stock_limit": 100,
  "total_quantity": 100,
  "remaining_quantity": 100,
  "sold_count": 0,
  "is_active": true,
  "priority": 0,
  "created_by": null,
  "__v": 0,
  "created_at": "2026-04-04T07:34:32.164Z",
  "updated_at": "2026-04-08T07:05:31.048Z",
  "status": "draft"
}
`

---

## 📦 Collection: returnrequests
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: deliveryslots
- **Số lượng bản ghi:** 7
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d053da68d77df32fe0e5",
  "branch_id": null,
  "date": "2026-03-09",
  "time_start": "",
  "time_end": "",
  "capacity": 10,
  "booked": 0,
  "is_available": true,
  "__v": 0,
  "createdAt": "2026-03-30T01:22:27.427Z",
  "updatedAt": "2026-03-30T01:22:27.427Z"
}
`

---

## 📦 Collection: productquestions
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: roles
- **Số lượng bản ghi:** 5
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69cceed2768d7ed95a6226c3",
  "key": "super_admin",
  "__v": 0,
  "created_at": "2026-04-01T10:09:22.386Z",
  "description": "",
  "is_active": true,
  "is_system": true,
  "name": "Super Admin",
  "permissions": [
    "products.read",
    "products.write",
    "orders.read",
    "orders.write",
    "inventory.read",
    "inventory.write",
    "imports.read",
    "imports.write",
    "suppliers.read",
    "suppliers.write",
    "promotions.read",
    "promotions.write",
    "coupons.read",
    "coupons.write",
    "events.read",
    "events.write",
    "settings.read",
    "settings.write",
    "audit.read"
  ],
  "role_id": 1,
  "updated_at": "2026-05-11T00:34:10.638Z"
}
`

---

## 📦 Collection: refreshtokens
- **Số lượng bản ghi:** 21
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69f5bf2d374736baca8b584c",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5YzlkODQ3ZWEzYzAyNmEwODMxYmYxZSIsImlhdCI6MTc3NzcxMjk0MSwiZXhwIjoxNzgwMzA0OTQxfQ.ToCK4z_9muoWq1szJtNOKnWuZwItY1FuwFdhi6697mE",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "is_revoked": false,
  "createdAt": "2026-05-02T09:09:01.903Z",
  "__v": 0
}
`

---

## 📦 Collection: orders
- **Số lượng bản ghi:** 21
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d890ea3c026a0831bf36",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "items": [
    {
      "branch_product_id": "000000000000000000000101",
      "product_name": "Sản phẩm",
      "product_image": "https://via.placeholder.com/150",
      "quantity": 2,
      "price": 29000,
      "discount": 0,
      "_id": "69c9d890ea3c026a0831bf37"
    }
  ],
  "status": "PENDING",
  "subtotal": 5800000000,
  "shipping_fee": 25000,
  "discount_amount": 0,
  "total_amount": 5800025000,
  "coupon_code": null,
  "payment": {
    "method": "COD",
    "status": "PENDING",
    "transaction_id": null
  },
  "tracking": {
    "history": [
      {
        "timestamp": "2026-03-30T01:57:36.736Z",
        "status": "PENDING",
        "note": "Chờ thanh toán",
        "_id": "69c9d890ea3c026a0831bf38"
      }
    ],
    "tracking_number": null,
    "carrier": null,
    "estimated_delivery": null
  },
  "delivery_slot": null,
  "branch_id": 1,
  "note": "",
  "generated_invoice_url": null,
  "created_at": "2026-03-30T01:57:36.736Z",
  "updated_at": "2026-03-30T01:57:36.736Z",
  "__v": 0
}
`

---

## 📦 Collection: paymenttransactions
- **Số lượng bản ghi:** 21
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69c9d890ea3c026a0831bf3c",
  "order_id": "undefined",
  "provider": "COD",
  "transaction_id": "TXN-1774835856886",
  "amount": 5800025000,
  "status": "COMPLETED",
  "created_at": "2026-03-30T01:57:36.887Z",
  "updatedAt": "2026-03-30T01:57:36.887Z",
  "__v": 0
}
`

---

## 📦 Collection: importreceipts
- **Số lượng bản ghi:** 13
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d08d4044d138ca1777adf5",
  "receipt_code": "RC-MNJT3EIV",
  "import_order_id": "69d08d4044d138ca1777adf2",
  "supplier_id": "69d08d3f44d138ca1777adda",
  "branch_id": "000000000000000000000001",
  "status": "confirmed",
  "items": [
    {
      "import_order_item_id": "69d08d4044d138ca1777adf3",
      "product_id": "000000000000000000000001",
      "branch_product_id": "000000000000000000000101",
      "product_name": "Sữa tươi Vinamilk 1L",
      "quantity_received": 118,
      "unit_cost": 20300,
      "subtotal": 2395400,
      "batch_code": "BATCH-INITIAL",
      "expiry_date": "2026-04-15T00:00:00.000Z",
      "note": "",
      "_id": "69d08d4044d138ca1777adf6"
    }
  ],
  "total_amount": 2395400,
  "note": "",
  "created_by": null,
  "updated_by": null,
  "received_date": "2026-04-04T04:02:08.839Z",
  "created_at": "2026-04-04T04:02:08.840Z",
  "updated_at": "2026-04-04T04:02:08.840Z",
  "__v": 0
}
`

---

## 📦 Collection: categories
- **Số lượng bản ghi:** 8
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "000000000000000000000001",
  "name": "Thực phẩm",
  "slug": "thuc-pham",
  "icon": "restaurant",
  "image": "https://lottemart.vn/media/catalog/product/placeholder/default/20210219_logo_800x800.png",
  "parent_id": null,
  "sort_order": 0,
  "is_active": true,
  "product_count": 1200,
  "__v": 0,
  "createdAt": "2026-03-30T01:22:27.211Z",
  "updatedAt": "2026-04-04T04:25:17.919Z"
}
`

---

## 📦 Collection: loyaltytransactions
- **Số lượng bản ghi:** 7
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d4c1c4a6bff2cb6d99761c",
  "user_id": "69c9d847ea3c026a0831bf1e",
  "type": "earn",
  "points": 14,
  "source": "purchase",
  "description": "Tích điểm từ đơn hàng #69d4c1c1a6bff2cb6d997604 (144.000đ)",
  "order_id": "69d4c1c1a6bff2cb6d997604",
  "balance_after": 14,
  "created_at": "2026-04-07T08:35:16.961Z",
  "updatedAt": "2026-04-07T08:35:16.961Z",
  "__v": 0
}
`

---

## 📦 Collection: products
- **Số lượng bản ghi:** 14
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "000000000000000000000001",
  "name": "Sữa tươi Vinamilk 1L",
  "slug": "",
  "description": "Sữa tươi tiệt trùng 1L",
  "short_description": "Sữa tươi tiệt trùng, tươi ngon, giàu dinh dưỡng. Phù hợp cho mọi lứa tuổi.",
  "category_id": "000000000000000000000002",
  "brand": "Vinamilk",
  "origin": "",
  "unit": "1L",
  "weight": "1kg (approx)",
  "barcode": "",
  "sku": "MILK001",
  "price": 0,
  "original_price": 0,
  "discount_percent": 0,
  "images": [
    "https://cdn.lottemart.vn/images/milk.jpg",
    "https://example.com/images/milk_1.jpg",
    "https://example.com/images/milk_360.jpg"
  ],
  "thumbnail": "https://cdn.lottemart.vn/images/milk.jpg",
  "tags": [
    "LOTTE",
    "ECO"
  ],
  "is_active": true,
  "is_featured": false,
  "rating": 0,
  "review_count": 120,
  "sold_count": 12,
  "specifications": [
    {
      "label": "Xuất xứ",
      "value": "Việt Nam"
    },
    {
      "label": "Thương hiệu",
      "value": "Vinamilk"
    },
    {
      "label": "Dung tích",
      "value": "1L"
    },
    {
      "label": "Hạn sử dụng",
      "value": "90 ngày"
    },
    {
      "label": "Thành phần",
      "value": "Sữa tươi nguyên chất, chất điều chỉnh hương vị"
    }
  ],
  "nutrition_info": null,
  "storage_instructions": "",
  "__v": 1,
  "created_at": "2026-03-30T01:22:27.248Z",
  "updated_at": "2026-04-11T06:55:12.129Z",
  "batch_code": "BATCH-INITIAL",
  "category_name": "Đồ uống",
  "import_price": 0,
  "is_expired": false,
  "is_expiring_soon": false,
  "master_id": "MAS-00000001",
  "stock": 0,
  "supplier_name": "Nhà Cung Cấp Tổng Hợp (Default)",
  "is_best_seller": false,
  "is_new": false,
  "supplier_id": "69d08d3f44d138ca1777adda",
  "ar_model_url": "",
  "frequently_bought_together": [],
  "gallery": [],
  "highlights": [],
  "notes": "",
  "origin_country": "",
  "origin_flag": "",
  "product_details": [],
  "recipe_suggestions": [],
  "related_product_ids": [],
  "shipping_excluded": false,
  "storage_guide": "",
  "total_reviews": 0,
  "usage_guide": "",
  "vat_included": true
}
`

---

## 📦 Collection: couponusages
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

## 📦 Collection: stockmovements
- **Số lượng bản ghi:** 13
- **Cấu trúc trường dữ liệu:**

`json
{
  "_id": "69d08d4044d138ca1777adfa",
  "branch_id": "000000000000000000000001",
  "branch_name": "Lotte Mart Quận 7",
  "product_id": "000000000000000000000001",
  "product_name": "Sữa tươi Vinamilk 1L",
  "branch_product_id": "000000000000000000000101",
  "batch_code": "BATCH-INITIAL",
  "movement_type": "inbound",
  "quantity": 118,
  "before_stock": 0,
  "after_stock": 118,
  "reference_type": "import_receipt",
  "reference_id": "69d08d4044d138ca1777adf5",
  "created_by": null,
  "note": "Initial backfill seed",
  "created_at": "2026-04-04T04:02:08.919Z",
  "updated_at": "2026-04-04T04:02:08.919Z",
  "__v": 0
}
`

---

## 📦 Collection: featuredcollections
- **Số lượng bản ghi:** 0
- **Cấu trúc trường dữ liệu:**

`json
null
`

---

