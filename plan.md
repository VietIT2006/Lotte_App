# 📅 Lotte Mart Implementation Plan - Day 2 (COMPLETED)

Mục tiêu chính: Kết nối ứng dụng Android với cơ sở dữ liệu thật trên Supabase thông qua Backend Node.js.

## 🏁 Trạng thái hiện tại
- **Database**: Đã có 100+ sản phẩm và 13 danh mục trên Supabase.
- **Backend**: Đã hoàn thiện API Catalog và Authentication kết nối DB.
- **Android**: Đã tích hợp Retrofit, Glide và kết nối dữ liệu thật cho Home, Login, Register và Detail.

---

## 🛠️ Bước 1: Hoàn thiện Backend API (DONE)
*Tập trung vào việc tạo ra các đầu cổng (Endpoints) để Android gọi vào.*

- [x] **Authentication API**:
    - `POST /api/v1/auth/login`: Kiểm tra username/password và trả về JWT.
    - `POST /api/v1/auth/register`: Tạo user mới trong bảng `users`.
- [x] **Content API**:
    - `GET /api/v1/catalog/categories`: Lấy danh sách 13 danh mục để hiện menu.
    - `GET /api/v1/catalog/products`: Lấy danh sách sản phẩm.
    - `GET /api/v1/catalog/products/featured`: Lấy các sản phẩm có flag `is_featured=true`.

## 📱 Bước 2: Android Networking (DONE)
*Tạo đường truyền để lấy dữ liệu từ Backend về điện thoại.*

- [x] **Library Setup**: Thêm `Retrofit`, `Gson`, `OkHttp` và `Glide` vào `build.gradle`.
- [x] **API Service**: 
    - Tạo `AuthApiService.java` (Login/Register).
    - Tạo `ProductApiService.java` (Get Categories/Products).
- [x] **Model Update**: 
    - Cập nhật `Product.java` và `Category.java` thêm các annotation `@SerializedName` để khớp với database.

## 🚀 Bước 3: Tích hợp và Chạy thực tế (DONE)
*Thay thế toàn bộ dữ liệu giả bằng dữ liệu thật.*

- [x] **Home Screen**:
    - Gọi API lấy Categories -> Đổ vào `rvCategories`.
    - Gọi API lấy Featured Products -> Đổ vào `rvFeatured`.
- [x] **Login Flow**:
    - Thực hiện gọi API login khi bấm nút.
    - Lưu `access_token` vào `SharedPreferences` thông qua `SessionManager`.
- [x] **Product Detail**:
    - Khi bấm vào 1 sản phẩm, truyền ID thật và gọi API lấy chi tiết sản phẩm từ Database.

## 🧪 Bước 4: Kiểm tra & Sửa lỗi (IN PROGRESS)
- [ ] Kiểm tra hiển thị hình ảnh từ URL Supabase trên Android. (Cần build app thật)
- [ ] Kiểm tra chức năng Đăng ký tài khoản mới xem có lưu vào bảng `users` thật không. (Cần build app thật)
- [x] Xử lý lỗi khi mất kết nối mạng hoặc server sập (Đã thêm Log và Toast).

---
*Ghi chú: Luôn chạy lệnh `node test-db.js` trước khi bắt đầu để đảm bảo kết nối Cloud vẫn thông suốt.*
