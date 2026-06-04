import os

directory = r'C:\LapTrinhHuongDichVu\Lotte_App\app\src\main\java\com\ptithcm\lottemart\features\admin'

fixes = {
    'AdminBatchesActivity.java': ('adapter.setBatches', 'adapter.setbatches'),
    'AdminImportOrdersActivity.java': ('adapter.setImportOrders', 'adapter.setorders'),
    'AdminOrdersActivity.java': ('adapter.setOrders', 'adapter.setorders'),
    'AdminSuppliersActivity.java': ('adapter.setSuppliers', 'adapter.setsuppliers')
}

for filename, (old_call, new_call) in fixes.items():
    filepath = os.path.join(directory, filename)
    if os.path.exists(filepath):
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        content = content.replace(old_call, new_call)
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)

print('Method calls fixed.')
