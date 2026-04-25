# 📅 Lotte Mart Implementation Plan - Day 2

Mục tiêu chính: Kết nối ứng dụng Android với cơ sở dữ liệu thật trên Supabase thông qua Backend Node.js.

## 🏁 Trạng thái hiện tại
- **Database**: Đã có 100+ sản phẩm và 13 danh mục trên Supabase.
- **Backend**: Đã có khung (Scaffold) và kết nối DB thành công.
- **Android**: Đã có giao diện UI và các Adapter (hiện đang dùng Mock Data).

---

## 🛠️ Bước 1: Hoàn thiện Backend API (The Brain)
*Tập trung vào việc tạo ra các đầu cổng (Endpoints) để Android gọi vào.*

- [ ] **Authentication API**:
    - `POST /api/v1/auth/login`: Kiểm tra username/password và trả về JWT.
    - `POST /api/v1/auth/register`: Tạo user mới trong bảng `users`.
- [ ] **Content API**:
    - `GET /api/v1/categories`: Lấy danh sách 13 danh mục để hiện menu.
    - `GET /api/v1/products`: Lấy danh sách sản phẩm (có hỗ trợ filter theo category_id).
    - `GET /api/v1/products/featured`: Lấy các sản phẩm có flag `is_featured=true`.

## 📱 Bước 2: Android Networking (The Connector)
*Tạo đường truyền để lấy dữ liệu từ Backend về điện thoại.*

- [ ] **Library Setup**: Thêm `Retrofit`, `Gson`, `OkHttp` và `Glide` (để load ảnh) vào `build.gradle`.
- [ ] **API Service**: 
    - Tạo `AuthApiService.java` (Login/Register).
    - Tạo `ProductApiService.java` (Get Categories/Products).
- [ ] **Model Update**: 
    - Cập nhật `Product.java` và `Category.java` thêm các annotation `@SerializedName` để khớp với database.

## 🚀 Bước 3: Tích hợp và Chạy thực tế (The Launch)
*Thay thế toàn bộ dữ liệu giả bằng dữ liệu thật.*

- [ ] **Home Screen**:
    - Gọi API lấy Categories -> Đổ vào `rvCategories`.
    - Gọi API lấy Featured Products -> Đổ vào `rvFeatured`.
- [ ] **Login Flow**:
    - Thực hiện gọi API login khi bấm nút.
    - Lưu `access_token` vào `SharedPreferences` để duy trì phiên đăng nhập.
- [ ] **Product Detail**:
    - Khi bấm vào 1 sản phẩm, truyền ID thật và gọi API lấy chi tiết sản phẩm từ Database.

## 🧪 Bước 4: Kiểm tra & Sửa lỗi (Verification)
- [ ] Kiểm tra hiển thị hình ảnh từ URL Supabase trên Android.
- [ ] Kiểm tra chức năng Đăng ký tài khoản mới xem có lưu vào bảng `users` thật không.
- [ ] Xử lý lỗi khi mất kết nối mạng hoặc server sập.

---
*Ghi chú: Luôn chạy lệnh `node test-db.js` trước khi bắt đầu để đảm bảo kết nối Cloud vẫn thông suốt.*
