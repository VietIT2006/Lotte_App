import os
import re

directory = r'C:\LapTrinhHuongDichVu\Lotte_App\app\src\main\java\com\ptithcm\lottemart\features\admin'

def process_file(filename, adapter_class, model_class, service_class, api_method, adapter_setter):
    filepath = os.path.join(directory, filename)
    if not os.path.exists(filepath): return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if api_method in content and filename != 'AdminUserManagementActivity.java':
        return

    imports = '''
import %s;
import com.ptithcm.lottemart.data.models.%s;
import com.ptithcm.lottemart.data.api.%s;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.data.local.SessionManager;
import java.util.List;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
''' % (adapter_class, model_class, service_class)

    content = re.sub(r'package[^;]+;', lambda m: m.group(0) + imports, content, 1)

    if 'AdminOrder' in adapter_class or 'AdminReview' in adapter_class or 'AdminImport' in adapter_class:
        adapter_init = '        %s adapter = new %s(this, new ArrayList<>(), item -> {});\n' % (adapter_class, adapter_class)
    elif 'AdminUser' in adapter_class:
        adapter_init = ''
    else:
        adapter_init = '        %s adapter = new %s(this, new ArrayList<>());\n' % (adapter_class, adapter_class)
    
    adapter_assign = '        rvList.setAdapter(adapter);\n' if adapter_init else ''

    classname = filename.replace('.java', '')
    api_logic = '''
        SessionManager sessionManager = new SessionManager(this);
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(%s.class).%s(token).enqueue(new Callback<ApiResponse<List<%s>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<%s>>> call, Response<ApiResponse<List<%s>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.%s(response.body().getData());
                } else if (response.code() == 403 || response.code() == 401) {
                    Toast.makeText(%s.this, "Bạn không có quyền xem mục này", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(%s.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<%s>>> call, Throwable t) {
                Toast.makeText(%s.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
''' % (service_class, api_method, model_class, model_class, model_class, adapter_setter, classname, classname, model_class, classname)
    
    if '// TODO: Create Adapter and set it to rvList' in content:
        content = content.replace('// TODO: Create Adapter and set it to rvList', adapter_init + adapter_assign + api_logic)
    elif '// TODO: Gọi API' in content:
        content = content.replace('// TODO: Gọi API', adapter_init + adapter_assign + api_logic)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)


process_file('AdminOrdersActivity.java', 'AdminOrderAdapter', 'Order', 'OrderApiService', 'getAdminOrders', 'setOrders')
process_file('AdminSuppliersActivity.java', 'AdminSupplierAdapter', 'Supplier', 'PurchasingApiService', 'getSuppliers', 'setSuppliers')
process_file('AdminImportOrdersActivity.java', 'AdminImportOrderAdapter', 'ImportOrder', 'PurchasingApiService', 'getImportOrders', 'setImportOrders')
process_file('AdminBatchesActivity.java', 'AdminBatchAdapter', 'InventoryBatch', 'InventoryApiService', 'getBatches', 'setBatches')
process_file('AdminReviewsActivity.java', 'AdminReviewAdapter', 'Review', 'ProductApiService', 'getAdminReviews', 'setReviews')

# For AdminTransfers and AdminReceipts, just add the Toast and hide RecyclerView since we have no API methods
for fake_file in ['AdminTransfersActivity.java', 'AdminReceiptsActivity.java']:
    fake_path = os.path.join(directory, fake_file)
    with open(fake_path, 'r', encoding='utf-8') as f:
        content = f.read()
    if '// TODO: Create Adapter' in content:
        content = content.replace('// TODO: Create Adapter and set it to rvList', 'android.widget.Toast.makeText(this, "Tính năng đang phát triển", android.widget.Toast.LENGTH_SHORT).show();')
        with open(fake_path, 'w', encoding='utf-8') as f:
            f.write(content)

print('APIs wired')
