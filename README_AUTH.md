# Cơ chế Xác thực (Authentication) - Lotte Mart App

Tài liệu này giải thích cấu trúc và cách vận hành của hệ thống đăng nhập, đăng ký và quản lý phiên làm việc trong dự án Lotte Mart Android.

## 1. Kiến trúc tổng quan
Hệ thống xác thực được xây dựng dựa trên kiến trúc **Activity-Fragment** với việc quản lý trạng thái tập trung thông qua **SessionManager**. Toàn bộ mã nguồn tuân thủ quy tắc đặt tên: `vai_trò + _ + loại_màn_hình + _ + tên_tính_năng`.

## 2. Các thành phần chính

### 🔐 Quản lý Phiên (Local Data)
- **Class**: `com.ptithcm.lottemart.data.local.SessionManager`
- **Công nghệ**: `SharedPreferences`
- **Chức năng**:
    - Lưu trữ JWT Token (hiện tại là mock token).
    - Lưu thông tin cơ bản người dùng (Tên, Email).
    - Quản lý trạng thái `isLoggedIn` (Đã đăng nhập hay chưa).

### 🌐 Cấu hình Mạng (Remote Config)
- **Class**: `com.ptithcm.lottemart.data.remote.NetworkConfig`
- **Chức năng**: 
    - Lưu trữ tập trung `BASE_URL` và các `Endpoint` (Đường dẫn API).
    - Giúp việc thay đổi địa chỉ Server trở nên dễ dàng (chỉ cần sửa 1 nơi).

### ✅ Kiểm tra dữ liệu (Validation)
- **Class**: `com.ptithcm.lottemart.utils.Validator`
- **Chức năng**: Kiểm tra định dạng Email, Số điện thoại Việt Nam (10 số, bắt đầu bằng 0) và độ dài mật khẩu.

## 3. Các luồng dữ liệu quan trọng

### A. Luồng Đăng nhập (Login Flow)
1. Người dùng nhập Email/SĐT và Mật khẩu.
2. `Validator` kiểm tra định dạng dữ liệu đầu vào.
3. Nếu hợp lệ, hệ thống kiểm tra với dữ liệu mẫu (Mock Data):
    - **Tài khoản**: `admin@lottemart.com`
    - **Mật khẩu**: `123456`
4. Nếu đúng, `SessionManager` lưu thông tin vào máy và chuyển hướng sang `MainActivity`.

### B. Tự động đăng nhập (Auto-login)
1. Khi mở App, `LoginActivity` sẽ gọi `sessionManager.isLoggedIn()`.
2. Nếu kết quả là `true`, App sẽ tự động bỏ qua màn hình đăng nhập và đi thẳng vào Trang chủ.

### C. Đăng xuất (Logout)
1. Tại tab **Cá nhân** (`ProfileFragment`), người dùng nhấn nút Đăng xuất.
2. Hệ thống gọi `sessionManager.logout()` để xóa sạch bộ nhớ cache/pref.
3. Chuyển hướng về `LoginActivity` và xóa sạch lịch sử màn hình (Clear Task) để đảm bảo bảo mật.

## 4. Quy tắc đặt tên Layout (res/layout)
Vui lòng tuân thủ công thức sau khi tạo mới:
`user_activity_[tên_tính_năng].xml` hoặc `user_fragment_[tên_tính_năng].xml`

---
*Tài liệu này được soạn thảo để hỗ trợ đội ngũ phát triển Lotte Mart.*
