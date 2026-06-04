import os
import glob
import re

directory = r'C:\LapTrinhHuongDichVu\Lotte_App\app\src\main\java\com\ptithcm\lottemart\features\admin'

title_map = {
    'AdminUserManagementActivity.java': 'Quản lý Người dùng',
    'AdminOrdersActivity.java': 'Quản lý Đơn hàng',
    'AdminProductManagementActivity.java': 'Quản lý Sản phẩm',
    'AdminCategoryManagementActivity.java': 'Quản lý Danh mục',
    'AdminBranchManagementActivity.java': 'Quản lý Chi nhánh',
    'AdminPromotionsActivity.java': 'Quản lý Khuyến mãi',
    'AdminSuppliersActivity.java': 'Quản lý Nhà cung cấp',
    'AdminImportOrdersActivity.java': 'Quản lý Đơn nhập',
    'AdminReceiptsActivity.java': 'Quản lý Phiếu nhập',
    'AdminBatchesActivity.java': 'Quản lý Lô hàng',
    'AdminTransfersActivity.java': 'Quản lý Chuyển kho',
    'AdminRolesActivity.java': 'Quản lý Phân quyền',
    'AdminReviewsActivity.java': 'Quản lý Đánh giá',
    'AdminCategoryFormActivity.java': 'Biểu mẫu Danh mục',
    'AdminProductFormActivity.java': 'Biểu mẫu Sản phẩm'
}

for filepath in glob.glob(os.path.join(directory, 'Admin*Activity.java')):
    filename = os.path.basename(filepath)
    if filename == 'AdminDashboardActivity.java':
        continue
        
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace extends AppCompatActivity with BaseAdminActivity
    content = content.replace('extends AppCompatActivity', 'extends BaseAdminActivity')
    content = content.replace('import androidx.appcompat.app.AppCompatActivity;', '')
    
    # Remove back button logic specifically
    content = re.sub(r'ImageView\s+btnBack\s*=\s*findViewById\(R\.id\.btnBack\);\s*btnBack\.setOnClickListener\([^;]+;\s*', '', content)
    content = re.sub(r'findViewById\(R\.id\.btnBack\)\.setOnClickListener\([^;]+;\s*', '', content)
    
    # Remove toolbar logic specifically
    content = re.sub(r'Toolbar\s+toolbar\s*=\s*findViewById\([^;]+\);\s*setSupportActionBar\(toolbar\);\s*', '', content)
    content = re.sub(r'if\s*\(getSupportActionBar\(\)\s*!=\s*null\)\s*\{[\s\S]*?\}\s*', '', content)
    
    # Inject setHeaderTitle right after setContentView
    title = title_map.get(filename, 'Quản lý')
    
    match = re.search(r'(setContentView\(R\.layout\.[^;]+;\s*)', content)
    if match:
        replacement = match.group(1) + 'setHeaderTitle("' + title + '");\n        '
        content = content.replace(match.group(1), replacement, 1)
    
    with open(filepath, 'w', encoding='utf-8', newline='') as f:
        f.write(content)

print('Transformation complete.')
