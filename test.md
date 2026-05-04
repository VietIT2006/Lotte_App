# Kế Hoạch và Kịch Bản Kiểm Thử (Test Cases) - Lotte Mart App

Tài liệu này bao gồm hướng dẫn kiểm tra trạng thái Backend và danh sách các Test Case chi tiết để kiểm thử các chức năng chính của ứng dụng Android Lotte Mart.

---

## PHẦN 1: KIỂM TRA TRẠNG THÁI BACKEND (API)

Trước khi chạy ứng dụng Android, bạn cần đảm bảo Backend Node.js đang hoạt động bình thường.

### 1. Khởi động Backend
Mở Terminal / Command Prompt tại thư mục backend (`C:\LapTrinhHuongDichVu\lotte-mart-backend`) và chạy lệnh:
```bash
npm run dev
```
*(Yêu cầu: Màn hình hiển thị `Server is running on port 3000` và `Connected to Supabase successfully`)*

### 2. Kiểm tra nhanh API bằng Trình duyệt
Mở Google Chrome và truy cập lần lượt các đường dẫn sau:
- **Trạng thái Server:** `http://localhost:3000` -> (Phải hiện chữ "Welcome..." hoặc "API is running")
- **API Danh mục:** `http://localhost:3000/api/v1/products/categories` -> (Phải hiện danh sách JSON danh mục)
- **API Sản phẩm nổi bật:** `http://localhost:3000/api/v1/products/featured` -> (Phải hiện danh sách JSON sản phẩm)

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
