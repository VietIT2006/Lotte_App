# Dự án LOTTE Mart - Hệ thống mua sắm và quản lý siêu thị

Dự án này là hệ thống thương mại điện tử tích hợp quản trị nội bộ cho siêu thị **LOTTE Mart**, bao gồm Frontend (React + TypeScript) và Backend (Node.js + Express + MongoDB).

Dưới đây là thống kê danh sách các trang, phân loại theo vai trò người dùng (Role), kèm theo chức năng chi tiết của từng trang.

---

## TỔNG QUAN HỆ THỐNG
Hệ thống được phân chia làm 3 nhóm vai trò chính:
1. **Khách vãng lai / Khách hàng chưa đăng nhập (Guest / Public)**: Xem thông tin sản phẩm, sự kiện, công thức nấu ăn, và tìm kiếm.
2. **Khách hàng đã đăng nhập (Authenticated Customer)**: Thực hiện mua sắm, quản lý giỏ hàng chung, đặt hàng, thanh toán và quản lý thông tin cá nhân.
3. **Quản trị viên & Nhân viên siêu thị (Admin / Staff)**: Quản lý sản phẩm, đơn hàng, kho bãi, chương trình khuyến mãi, phân quyền và giám sát hoạt động.

---

## 1. VAI TRÒ: KHÁCH VÃNG LAI (GUEST / PUBLIC)
*Các trang này ai cũng có thể truy cập mà không cần đăng nhập.*

| STT | Trang | Đường dẫn (Path) | Chức năng chi tiết |
|---|---|---|---|
| 1 | **Trang chủ** | `/`, `/home` | Hiển thị các banner quảng cáo, danh mục sản phẩm nổi bật, sản phẩm bán chạy, sản phẩm khuyến mãi và các sự kiện đang diễn ra tại siêu thị. |
| 2 | **Danh sách sản phẩm** | `/products` | Hiển thị danh sách toàn bộ sản phẩm. Hỗ trợ bộ lọc nâng cao (theo danh mục, giá cả, nhãn sinh thái - eco label), tính năng tìm kiếm và sắp xếp. |
| 3 | **Chi tiết sản phẩm** | `/products/:id`<br>`/:locale/product/:id`<br>`/home/product/:id` | Hiển thị hình ảnh chi tiết, mô tả sản phẩm, thông số kỹ thuật, đánh giá từ khách hàng, hướng dẫn sử dụng/bảo quản và gợi ý sản phẩm liên quan. Tích hợp nút so sánh và nút theo dõi giá. |
| 4 | **So sánh sản phẩm** | `/compare` | So sánh thông số, giá cả, đánh giá của các sản phẩm đã chọn trong bảng đối chiếu trực quan. Sử dụng **AI (Gemini)** để tóm tắt điểm khác biệt và tư vấn lựa chọn. |
| 5 | **Đi chợ hộ / Mua hộ** | `/shop-at-home` | Giới thiệu dịch vụ đi chợ hộ tại nhà và hướng dẫn khách hàng mua sắm trực tuyến. |
| 6 | **Khuyến mãi Hot** | `/hot-deals/product/:id` | Xem chi tiết các sản phẩm đang có ưu đãi giảm giá sâu đặc biệt. |
| 7 | **Giới thiệu** | `/about` | Giới thiệu về lịch sử hình thành, tầm nhìn, sứ mệnh và giá trị cốt lõi của LOTTE Mart. |
| 8 | **Kết quả tìm kiếm** | `/search` | Hiển thị danh sách sản phẩm tìm được theo từ khóa người dùng nhập vào. |
| 9 | **Sự kiện nổi bật** | `/featured-events` | Danh sách các sự kiện, chương trình lễ hội mua sắm của siêu thị. |
| 10 | **Chi tiết sự kiện** | `/events/:id` | Xem nội dung chi tiết về một chương trình sự kiện cụ thể. |
| 11 | **Công thức nấu ăn** | `/recipes`<br>`/recipes/:name` | Xem chi tiết công thức nấu ăn. Có tính năng **"Recipe-to-Cart"** tự động khớp nguyên liệu trong công thức với sản phẩm trong siêu thị để cho vào giỏ hàng. Tích hợp AI tạo công thức nấu ăn khi không tìm thấy trong cơ sở dữ liệu. |
| 12 | **Theo dõi đơn hàng nhanh** | `/order/track`<br>`/order/track/:orderId` | Hỗ trợ khách hàng tra cứu tiến độ vận chuyển đơn hàng bằng mã đơn mà không bắt buộc phải đăng nhập. |
| 13 | **Góc tương tác 3D** | `/carrot-scene` | Hoạt cảnh đồ họa 3D tương tác (Gamification) để tăng tính thú vị cho ứng dụng. |
| 14 | **Mua sắm thông minh** | `/smart-shopping` | Đề xuất sản phẩm cá nhân hóa thông minh (sản phẩm khuyên dùng, mua lại, xu hướng) với nút gạt bật/tắt chế độ Smart Mode. |
| 15 | **Đăng nhập** | `/login` | Đăng nhập tài khoản bằng Email/Mật khẩu, Google OAuth, Facebook OAuth hoặc mã OTP. |
| 16 | **Đăng ký** | `/register` | Đăng ký tài khoản khách hàng mới, xác thực thông qua mã OTP gửi về Email. |
| 17 | **Đăng nhập thành công** | `/login-success` | Trang trung gian xử lý chuyển hướng sau khi xác thực thành công qua Google/Facebook. |

---

## 2. VAI TRÒ: KHÁCH HÀNG ĐÃ ĐĂNG NHẬP (CUSTOMER)
*Yêu cầu đăng nhập tài khoản khách hàng để truy cập. Được bảo vệ bởi bộ lọc xác thực (AuthGuard).*

| STT | Trang | Đường dẫn (Path) | Chức năng chi tiết |
|---|---|---|---|
| 1 | **Giỏ hàng** | `/cart` | Xem và điều chỉnh số lượng các sản phẩm đã chọn, chọn chi nhánh siêu thị phục vụ, tính toán giá trị tạm tính. |
| 2 | **Giỏ hàng gia đình** | `/family-cart` | Giỏ hàng dùng chung giữa các thành viên. Đồng bộ hóa hoạt động thêm/xóa sản phẩm theo thời gian thực (Realtime via Socket.IO). |
| 3 | **Khu vui chơi giải trí** | `/lotte-fun-zone` | Tham gia các trò chơi tương tác tích điểm thưởng thành viên hoặc nhận voucher quà tặng. |
| 4 | **Thanh toán (Checkout)** | `/checkout/*` | Chọn địa chỉ giao hàng, phương thức vận chuyển và nhập mã giảm giá (coupon) để tính tổng tiền đơn hàng. |
| 5 | **Cổng thanh toán** | `/payment` | Tiến hành thanh toán trực tuyến (quét mã QR, thanh toán thẻ). Ràng buộc xác thực an toàn (đã xác minh email và số điện thoại). |
| 6 | **Thanh toán thành công** | `/payment/success` | Thông báo đơn hàng đã thanh toán và đặt hàng thành công. |
| 7 | **Thanh toán thất bại** | `/payment/fail` | Thông báo thanh toán không thành công và hướng dẫn thử lại. |
| 8 | **Hồ sơ cá nhân** | `/account` | Xem và thay đổi thông tin cá nhân (Họ tên, ảnh đại diện, số điện thoại, ngày sinh...). |
| 9 | **Đơn hàng của tôi** | `/account/orders` | Danh sách toàn bộ các đơn hàng người dùng đã đặt và trạng thái xử lý đơn hàng. |
| 10 | **Chi tiết đơn hàng** | `/account/orders/:orderId` | Xem chi tiết từng món hàng trong đơn, theo dõi lịch sử trạng thái, xuất hóa đơn điện tử hoặc yêu cầu đổi trả hàng. |
| 11 | **Sổ địa chỉ** | `/account/addresses` | Quản lý danh sách các địa chỉ giao hàng của người dùng (thêm mới, chỉnh sửa, xóa và thiết lập mặc định). |
| 12 | **Ví Coupon** | `/account/coupons` | Lưu trữ và hiển thị các mã giảm giá (voucher) mà người dùng đã sưu tầm hoặc được tặng. |
| 13 | **Phương thức thanh toán** | `/account/payments` | Quản lý các thẻ ngân hàng hoặc tài khoản ví điện tử đã liên kết. |
| 14 | **Điểm tích lũy & Hạng** | `/account/loyalty` | Xem hạng thành viên (L.POINT), tổng điểm tích lũy và lịch sử giao dịch tích lũy/sử dụng điểm. |
| 15 | **Quản lý đánh giá** | `/account/reviews` | Xem danh sách các sản phẩm chưa đánh giá và viết đánh giá, xem lại các đánh giá đã gửi. |
| 16 | **Trung tâm hỗ trợ** | `/account/support` | Gửi yêu cầu trợ giúp và tham gia phòng chat trực tuyến thời gian thực với nhân viên hỗ trợ. |
| 17 | **Cài đặt tài khoản** | `/account/settings` | Thiết lập quyền riêng tư, cấu hình nhận thông báo và đổi mật khẩu tài khoản. |
| 18 | **Thông báo** | `/account/notifications` | Hộp thư lưu trữ các thông báo hệ thống, thông báo trạng thái đơn hàng và tin tức khuyến mãi cá nhân. |
| 19 | **Sản phẩm yêu thích** | `/account/wishlist` | Lưu trữ danh sách các sản phẩm người dùng yêu thích để tiện theo dõi và mua sắm sau này. |
| 20 | **Lịch sử đã xem** | `/account/viewed-history` | Xem lại các sản phẩm đã xem gần đây (đồng bộ giữa local storage và tài khoản). |
| 21 | **Yêu cầu đổi trả** | `/account/returns` | Tạo và quản lý các yêu cầu đổi trả hàng hoặc hoàn tiền đối với sản phẩm bị lỗi. |

---

## 3. VAI TRÒ: QUẢN TRỊ VIÊN (ADMIN)
*Yêu cầu tài khoản Admin. Nằm trong phân hệ `/admin` và được bảo vệ bởi AdminGuard và AdminPermissionGuard (phân quyền chi tiết theo chức năng).*

| STT | Trang | Đường dẫn (Path) | Chức năng chi tiết (Quyền yêu cầu) |
|---|---|---|---|
| 1 | **Đăng nhập Admin** | `/admin/login` | Đăng nhập tài khoản quản trị/nhân viên siêu thị. |
| 2 | **Bảng điều khiển** | `/admin/dashboard` | Trang thống kê tổng hợp số liệu doanh thu, sản lượng bán ra, số đơn hàng, khách hàng mới qua biểu đồ. |
| 3 | **Quản lý sản phẩm** | `/admin/products` | Danh sách và quản lý thông tin sản phẩm (thêm, sửa, xóa, điều chỉnh giá bán và ảnh minh họa). |
| 4 | **Quản lý danh mục** | `/admin/categories` | Quản lý các danh mục ngành hàng của siêu thị *(Yêu cầu quyền: `products.read`)*. |
| 5 | **Quản lý khách hàng** | `/admin/customers` | Quản lý danh sách khách hàng đăng ký hệ thống, xem thông tin chi tiết và lịch sử mua sắm. |
| 6 | **Quản lý khuyến mãi** | `/admin/coupons` | Tạo mới và quản lý các chương trình ưu đãi, mã giảm giá, thiết lập điều kiện áp dụng. |
| 7 | **Quản lý Gamification** | `/admin/gamification` | Cấu hình các trò chơi, thiết lập giải thưởng và phần quà cho khách hàng tại Lotte Fun Zone. |
| 8 | **Quản lý sự kiện** | `/admin/events` | Quản lý tin tức sự kiện, bài viết quảng cáo hiển thị trên trang chủ siêu thị. |
| 9 | **Thiết lập hệ thống** | `/admin/settings` | Cài đặt thông tin thương hiệu (Tên hệ thống, logo, favicon), cấu hình thông số kỹ thuật và kích hoạt chế độ bảo trì toàn hệ thống. |
| 10 | **Quản lý đơn hàng** | `/admin/orders` | Tiếp nhận đơn hàng mới, duyệt đơn, phân phối đơn hàng cho Shipper, theo dõi tiến độ giao hàng. |
| 11 | **Quản lý đánh giá** | `/admin/reviews` | Giám sát các bình luận, phản hồi và đánh giá sản phẩm của người dùng. |
| 12 | **Quản lý hỏi đáp** | `/admin/questions` | Phản hồi các thắc mắc của khách hàng về sản phẩm trên website. |
| 13 | **Yêu cầu hỗ trợ** | `/admin/support` | Tiếp nhận và trả lời các yêu cầu hỗ trợ (Support Ticket) của khách hàng qua khung chat trực tiếp. |
| 14 | **Quản lý đổi trả** | `/admin/returns` | Phê duyệt hoặc từ chối các yêu cầu hoàn tiền, đổi trả sản phẩm bị lỗi từ phía khách hàng. |
| 15 | **Quản lý nhà cung cấp** | `/admin/suppliers` | Quản lý thông tin liên hệ và hợp đồng với các nhà cung cấp hàng hóa cho siêu thị *(Yêu cầu quyền: `suppliers.read`)*. |
| 16 | **Đơn đặt hàng nhập** | `/admin/import-orders` | Tạo và quản lý các đơn đặt hàng nhập kho gửi đến nhà cung cấp *(Yêu cầu quyền: `imports.read`)*. |
| 17 | **Phiếu nhập kho** | `/admin/import-receipts` | Quản lý các phiếu xác nhận đã nhận hàng thực tế tại kho siêu thị *(Yêu cầu quyền: `imports.read`)*. |
| 18 | **Quản lý lô hàng** | `/admin/inventory-batches` | Theo dõi tồn kho chi tiết theo lô (hạn sử dụng, mã lô, vị trí lưu trữ tại kho) *(Yêu cầu quyền: `inventory.read`)*. |
| 19 | **Biến động kho hàng** | `/admin/stock-movements` | Thống kê và theo dõi lịch sử xuất, nhập, chuyển kho hoặc điều chỉnh hao hụt hàng hóa *(Yêu cầu quyền: `inventory.read`)*. |
| 20 | **Phân quyền vai trò** | `/admin/roles` | Phân quyền chi tiết cho nhân viên siêu thị (ví dụ: nhân viên kho, nhân viên CSKH, thủ quỹ) *(Chỉ Super Admin mới truy cập được)*. |
| 21 | **Nhật ký hoạt động** | `/admin/audit-logs` | Ghi nhận toàn bộ thao tác thay đổi dữ liệu của nhân viên trên hệ thống quản trị để phục vụ giám sát *(Yêu cầu quyền: `audit.read`)*. |
| 22 | **Quản lý chi nhánh** | `/admin/branch-locations` | Thêm, sửa, xóa các cơ sở siêu thị LOTTE Mart, thiết lập tọa độ địa lý phục vụ và vẽ bản đồ bán kính giao hàng *(Yêu cầu quyền: `settings.read`)*. |

---

## KẾ HOẠCH BỔ SUNG & PHÁT TRIỂN MOBILE APP (NEXT STEPS)

Dựa trên đối chiếu chức năng với hệ thống Web, dưới đây là lộ trình các bước tiếp theo cần bổ sung cho ứng dụng Mobile nhằm đồng bộ hóa trải nghiệm người dùng:

### Phase 1: Hoàn thiện Luồng Mua hàng & Thanh toán (Trọng tâm)
*   [ ] **CheckoutActivity**: Trang hiển thị thông tin đặt hàng, chọn địa chỉ giao hàng, phương thức vận chuyển và áp dụng mã giảm giá.
*   [ ] **PaymentActivity & PaymentFailureActivity**: Tích hợp cổng thanh toán trực tuyến (Momo, VNPAY, QR Code) và màn hình xử lý khi thanh toán thất bại.
*   [ ] **AddressBookActivity**: Quản lý danh sách sổ địa chỉ nhận hàng (thêm mới, chỉnh sửa, chọn làm mặc định).
*   [ ] **CouponWalletActivity**: Trang hiển thị ví coupon/voucher mà khách hàng đang sở hữu.

### Phase 2: Nâng cấp Trải nghiệm Khách hàng & Tương tác cá nhân
*   [ ] **SupportChatActivity**: Tích hợp chat hỗ trợ trực tuyến thời gian thực (Socket.IO) kết nối người dùng với bộ phận CSKH.
*   [ ] **ReturnRequestActivity**: Trang tạo và quản lý các yêu cầu đổi trả hàng/hoàn tiền đối với sản phẩm bị lỗi.
*   [ ] **FamilyCartActivity**: Đồng bộ hóa giỏ hàng gia đình dùng chung theo thời gian thực (Realtime via Socket.IO).
*   [ ] **Wishlist & ViewedHistory**: Lưu trữ danh sách sản phẩm yêu thích và lịch sử sản phẩm đã xem gần đây.

### Phase 3: Tính năng Thông minh & Giải trí (Gamification)
*   [ ] **CompareProductActivity**: Giao diện so sánh các sản phẩm và tích hợp AI (Gemini) tư vấn lựa chọn.
*   [ ] **RecipeActivity**: Danh sách công thức nấu ăn tích hợp nút "Recipe-to-Cart" tự động thêm nguyên liệu vào giỏ hàng.
*   [ ] **LotteFunZoneActivity & CarrotSceneActivity**: Các hoạt cảnh/trò chơi tương tác (3D Gamification) để tích lũy điểm thưởng L.POINT.

### Phase 4: Bổ sung Quản trị & Giám sát dành cho Admin Mobile
*   [ ] **AdminGamificationActivity & AdminEventsActivity**: Quản lý các cấu hình giải thưởng mini-game và bài viết sự kiện siêu thị.
*   [ ] **AdminQuestionsActivity**: Tiếp nhận và phản hồi các thắc mắc (Q&A) từ người dùng.
*   [ ] **AdminAuditLogsActivity**: Nhật ký hoạt động ghi nhận thao tác của nhân viên trên hệ thống di động.

