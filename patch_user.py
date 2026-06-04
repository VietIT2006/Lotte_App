import os

filepath = r'C:\LapTrinhHuongDichVu\Lotte_App\app\src\main\java\com\ptithcm\lottemart\features\admin\AdminUserManagementActivity.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

replacement = '} else if (response.code() == 403 || response.code() == 401) {\n                    android.widget.Toast.makeText(AdminUserManagementActivity.this, "Bạn không có quyền xem mục này", android.widget.Toast.LENGTH_LONG).show();\n                } else {'
content = content.replace('} else {', replacement, 1)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print('User management 403 patched')
