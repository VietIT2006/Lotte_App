# Kế Hoạch và Kịch Bản Kiểm Thử (Test Cases) - Lotte Mart App

Tài liệu này bao gồm hướng dẫn kiểm tra trạng thái Backend và danh sách các Test Case chi tiết để kiểm thử các chức năng chính của ứng dụng Android Lotte Mart.

---

## PHẦN 1: HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN (QUICK START)

Dành cho thành viên mới hoặc sau khi `git pull` code mới về.

### 1. Cấu hình Backend (Node.js)
Mở Terminal tại thư mục `backend` và chạy các lệnh sau:
1.  **Cài đặt thư viện:** `npm install`
2.  **Khởi tạo môi trường (.env):** `npm run setup` 
    *(Lệnh này tự động tạo file .env từ cấu hình chuẩn đã có sẵn MongoDB Atlas)*
3.  **Chạy server:** `npm run dev`
    *(Kết quả mong đợi: `✅ Server is running...` và `✅ Connected to MongoDB`)*

### 2. Cấu hình Android (Lotte Mart App)
Mở Project bằng Android Studio. Cấu hình IP trong file `NetworkConfig.java` như sau:

*   **TRƯỜNG HỢP 1: Dùng Máy ảo (Emulator) - Mặc định**
    *   Giữ nguyên `BASE_URL = "http://10.0.2.2:3000/api/v1/"`.
    *   Nhấn Run và trải nghiệm.

*   **TRƯỜNG HỢP 2: Dùng Máy thật (Physical Device)**
    1.  Cắm cáp USB vào máy tính (Bật USB Debugging).
    2.  Trong `NetworkConfig.java`: Đổi `10.0.2.2` thành `127.0.0.1`.
    3.  Click đúp vào file **`chay_may_that.bat`** ở thư mục gốc.
    4.  Nhấn Run từ Android Studio.

### 3. Kiểm tra kết nối API (Dùng trình duyệt)
Sau khi chạy backend, hãy truy cập các link sau để đảm bảo dữ liệu đã thông:
- **API Danh mục:** `http://localhost:3000/api/v1/catalog/categories`
- **API Sản phẩm:** `http://localhost:3000/api/v1/catalog/products`

Nếu các đường dẫn trên trả về dữ liệu thành công, Backend đã sẵn sàng!

---

## PHẦN 2: CÁC TEST CASE CHỨC NĂNG TRÊN ANDROID

*Lưu ý: Chạy ứng dụng trên Android Studio (Emulator).*

### 1. Chức năng Đăng ký (Register)

| Mã TC | Mô tả Test Case | Các bước thực hiện | Kết quả mong đợi (Expected) | Trạng thái |
|---|---|---|---|---|
| TC_REG_01 | Bỏ trống thông tin | Để trống tất cả các trường và bấm "Đăng ký" | App báo lỗi "Họ tên không được để trống" | |
| TC_REG_02 | Email sai định dạng | Nhập Tên, nhập Email: `abc@`, Mật khẩu: `123456`, Xác nhận: `123456`, Bấm "Đăng ký" | App báo lỗi định dạng email tại ô Email | |
| TC_REG_03 | Mật khẩu quá ngắn | Nhập đầy đủ Tên, Email hợp lệ. Nhập Mật khẩu: `123`, Xác nhận: `123`, Bấm "Đăng ký" | App báo lỗi "Mật khẩu phải có ít nhất 6 ký tự" | |
| TC_REG_04 | Xác nhận MK không khớp | Nhập đầy đủ Tên, Email, Mật khẩu: `123456`, Xác nhận: `123457`, Bấm "Đăng ký" | App báo lỗi "Mật khẩu xác nhận không khớp" | |
| TC_REG_05 | Đăng ký thành công | Điền đầy đủ thông tin hợp lệ (Tên, Email mới, Mật khẩu >= 6 ký tự, Xác nhận đúng). Bấm "Đăng ký" | Hiện thông báo "Đăng ký thành công" và tự động chuyển về màn hình Đăng nhập. | |

### 2. Chức năng Đăng nhập (Login)

| Mã TC | Mô tả Test Case | Các bước thực hiện | Kết quả mong đợi (Expected) | Trạng thái |
|---|---|---|---|---|
| TC_LOG_01 | Bỏ trống thông tin | Để trống ô Email và Password, bấm "Đăng nhập" | App báo lỗi yêu cầu nhập Email / Mật khẩu | |
| TC_LOG_02 | Sai tài khoản/mật khẩu | Nhập Email chưa đăng ký hoặc nhập sai Mật khẩu. Bấm "Đăng nhập" | App báo lỗi "Đăng nhập thất bại" hoặc "Sai tài khoản/mật khẩu" | |
| TC_LOG_03 | Đăng nhập thành công | Nhập Email và Mật khẩu vừa tạo ở TC_REG_05. Bấm "Đăng nhập" | App chuyển thẳng vào màn hình Trang chủ (Home) | |

### 3. Chức năng Trang chủ (Home) & Hiển thị Dữ liệu

| Mã TC | Mô tả Test Case | Các bước thực hiện | Kết quả mong đợi (Expected) | Trạng thái |
|---|---|---|---|---|
| TC_HOM_01 | Tải danh sách Danh mục | Mở trang chủ, quan sát thanh Danh mục nằm ngang | Hiển thị đầy đủ hình ảnh và tên Danh mục (từ API). Vuốt ngang mượt mà. | |
| TC_HOM_02 | Tải Sản phẩm nổi bật | Cuộn xuống phần Sản phẩm nổi bật trên trang chủ | Hiển thị dạng lưới các sản phẩm (tên, giá, hình ảnh). Không bị lỗi ảnh. | |

### 4. Chức năng Chi tiết Sản phẩm (Product Detail)

| Mã TC | Mô tả Test Case | Các bước thực hiện | Kết quả mong đợi (Expected) | Trạng thái |
|---|---|---|---|---|
| TC_PRD_01 | Xem chi tiết sản phẩm | Tại Trang chủ, bấm vào 1 sản phẩm bất kỳ | Chuyển sang màn hình Chi tiết sản phẩm. Hiển thị đúng ảnh to, Tên SP, Giá tiền và Mô tả. | |
| TC_PRD_02 | Nút Quay lại (Back) | Tại màn hình Chi tiết SP, bấm biểu tượng nút Back (mũi tên) | Quay trở về Trang chủ. | |

### 5. Chức năng Giỏ hàng (Cart)

| Mã TC | Mô tả Test Case | Các bước thực hiện | Kết quả mong đợi (Expected) | Trạng thái |
|---|---|---|---|---|
| TC_CRT_01 | Truy cập Giỏ hàng | Từ màn hình Chi tiết sản phẩm, bấm nút "Thêm vào giỏ" (hoặc biểu tượng giỏ hàng) | Chuyển sang màn hình CartActivity thành công không bị crash. | |

### 6. Chức năng Đăng xuất & Tự động Đăng nhập

| Mã TC | Mô tả Test Case | Các bước thực hiện | Kết quả mong đợi (Expected) | Trạng thái |
|---|---|---|---|---|
| TC_SYS_01 | Tự động đăng nhập | Đang ở Trang chủ. Đóng hoàn toàn App. Mở lại App | App nhảy thẳng vào Trang chủ mà không yêu cầu Đăng nhập lại. | |
| TC_SYS_02 | Đăng xuất | Chuyển sang Tab Tài khoản (Profile). Bấm "Đăng xuất" | Trạng thái bị xóa, App đẩy về màn hình Đăng nhập. Thử mở lại App vẫn ở màn hình Đăng nhập. | |

## PHẦN 3: DANH SÁCH TÀI KHOẢN KIỂM THỬ

Tất cả các tài khoản dưới đây đều sử dụng chung một mật khẩu là: **`123456`**

### 1. Tài khoản Khách hàng (Customer)
Dùng để kiểm thử các tính năng ở góc độ người dùng thông thường như: đăng nhập, xem sản phẩm, thêm vào giỏ hàng, đặt hàng, xem điểm thành viên, v.v.
- **Email:** `customer@lotte.com`
- **Số điện thoại:** `0900000003`
- **Vai trò:** Customer (Khách hàng)
- **Mật khẩu:** `123456`

### 2. Tài khoản Giao hàng (Shipper)
Dùng để kiểm thử luồng giao nhận, xem danh sách đơn hàng được phân công, cập nhật trạng thái giao hàng, v.v.
- **Email:** `shipper@lotte.com`
- **Số điện thoại:** `0900000004`
- **Vai trò:** Shipper (Người giao hàng)
- **Mật khẩu:** `123456`

### 3. Tài khoản Quản trị viên (Admin)
Dùng để kiểm thử các chức năng dành cho ban quản trị, ví dụ: quản lý sản phẩm, đơn hàng, duyệt đánh giá.
- **Email:** `admin@lotte.com`
- **Số điện thoại:** `0900000002`
- **Vai trò:** Admin (Admin thường)
- **Mật khẩu:** `123456`

### 4. Tài khoản Siêu quản trị (Super Admin)
Dùng để kiểm thử các quyền cao nhất trong hệ thống, bao gồm cấu hình cửa hàng, quản lý nhân viên, phân quyền.
- **Email:** `superadmin@lotte.com`
- **Số điện thoại:** `0900000001`
- **Vai trò:** Super Admin (Quản trị viên cấp cao)
- **Mật khẩu:** `123456`

---
*Lưu ý (Chế độ Mock App):* Nếu ứng dụng Android đang chạy ở chế độ giao diện giả lập (chưa gọi API), có thể dùng tài khoản `admin@lottemart.com` / `123456` để đăng nhập.
