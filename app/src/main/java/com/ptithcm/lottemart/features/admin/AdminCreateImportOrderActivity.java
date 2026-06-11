package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.api.PurchasingApiService;
import com.ptithcm.lottemart.data.api.PurchasingApiService.CreateImportOrderItem;
import com.ptithcm.lottemart.data.api.PurchasingApiService.CreateImportOrderRequest;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Branch;
import com.ptithcm.lottemart.data.models.ImportOrder;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.models.Supplier;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateImportOrderActivity extends BaseAdminActivity {

    private Spinner spSupplier;
    private Spinner spProduct;
    private TextInputEditText etNote;
    private TextInputEditText etQuantity;
    private TextInputEditText etUnitCost;
    private Button btnAddItem;
    private Button btnCreateOrder;
    private RecyclerView rvAddedItems;

    private SessionManager sessionManager;
    private List<Supplier> suppliersList = new ArrayList<>();
    private List<Product> productsList = new ArrayList<>();
    private List<CreateImportOrderItem> draftItems = new ArrayList<>();
    private DraftItemsAdapter draftAdapter;
    private String selectedBranchId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_import_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        spSupplier = findViewById(R.id.spSupplier);
        spProduct = findViewById(R.id.spProduct);
        etNote = findViewById(R.id.etNote);
        etQuantity = findViewById(R.id.etQuantity);
        etUnitCost = findViewById(R.id.etUnitCost);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        rvAddedItems = findViewById(R.id.rvAddedItems);

        sessionManager = new SessionManager(this);

        // Setup Draft RecyclerView
        rvAddedItems.setLayoutManager(new LinearLayoutManager(this));
        draftAdapter = new DraftItemsAdapter();
        rvAddedItems.setAdapter(draftAdapter);

        // Load branch
        selectedBranchId = sessionManager.getSelectedBranchId();
        if (TextUtils.isEmpty(selectedBranchId)) {
            fetchBranchesAndDefault();
        }

        // Fetch suppliers and products
        fetchSuppliers();
        fetchProducts();

        // Add item button click
        btnAddItem.setOnClickListener(v -> addItemToDraft());

        // Create import order button click
        btnCreateOrder.setOnClickListener(v -> submitImportOrder());
    }

    private void fetchBranchesAndDefault() {
        RetrofitClient.getClient().create(ProductApiService.class).getBranches().enqueue(new Callback<ApiResponse<List<Branch>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Branch>>> call, Response<ApiResponse<List<Branch>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Branch> branches = response.body().getData();
                    if (branches != null && !branches.isEmpty()) {
                        selectedBranchId = branches.get(0).getId();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Branch>>> call, Throwable t) {
                // Ignore and hope backend can handle default branch or we retry later.
            }
        });
    }

    private void fetchSuppliers() {
        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(PurchasingApiService.class).getSuppliers(token).enqueue(new Callback<ApiResponse<List<Supplier>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Supplier>>> call, Response<ApiResponse<List<Supplier>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    suppliersList.clear();
                    if (response.body().getData() != null) {
                        suppliersList.addAll(response.body().getData());
                    }
                    ArrayAdapter<Supplier> adapter = new ArrayAdapter<>(AdminCreateImportOrderActivity.this,
                            android.R.layout.simple_spinner_item, suppliersList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSupplier.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminCreateImportOrderActivity.this, "Không thể tải danh sách nhà cung cấp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Supplier>>> call, Throwable t) {
                Toast.makeText(AdminCreateImportOrderActivity.this, "Lỗi kết nối khi tải nhà cung cấp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProducts() {
        RetrofitClient.getClient().create(ProductApiService.class).getProducts(null).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    productsList.clear();
                    if (response.body().getData() != null) {
                        productsList.addAll(response.body().getData());
                    }
                    
                    List<String> productNames = new ArrayList<>();
                    for (Product p : productsList) {
                        productNames.add(p.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminCreateImportOrderActivity.this,
                            android.R.layout.simple_spinner_item, productNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spProduct.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminCreateImportOrderActivity.this, "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Toast.makeText(AdminCreateImportOrderActivity.this, "Lỗi kết nối khi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToDraft() {
        if (spProduct.getSelectedItemPosition() == Spinner.INVALID_POSITION || productsList.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Product selectedProduct = productsList.get(spProduct.getSelectedItemPosition());

        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String costStr = etUnitCost.getText() != null ? etUnitCost.getText().toString().trim() : "";

        if (TextUtils.isEmpty(qtyStr)) {
            etQuantity.setError("Nhập số lượng");
            return;
        }

        if (TextUtils.isEmpty(costStr)) {
            etUnitCost.setError("Nhập đơn giá");
            return;
        }

        int qty;
        double cost;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                etQuantity.setError("Số lượng phải lớn hơn 0");
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Số lượng không hợp lệ");
            return;
        }

        try {
            cost = Double.parseDouble(costStr);
            if (cost <= 0) {
                etUnitCost.setError("Đơn giá phải lớn hơn 0");
                return;
            }
        } catch (NumberFormatException e) {
            etUnitCost.setError("Đơn giá không hợp lệ");
            return;
        }

        // Check if product is already in the draft list
        for (CreateImportOrderItem item : draftItems) {
            if (item.product_id.equals(selectedProduct.getId())) {
                item.quantity_ordered += qty;
                // Keep the latest unit cost or average it? We will update with the new unit cost.
                item.unit_cost = cost;
                draftAdapter.notifyDataSetChanged();
                clearInputs();
                return;
            }
        }

        CreateImportOrderItem newItem = new CreateImportOrderItem(
                selectedProduct.getId(),
                selectedProduct.getName(),
                qty,
                cost
        );
        draftItems.add(newItem);
        draftAdapter.notifyDataSetChanged();
        clearInputs();
    }

    private void clearInputs() {
        etQuantity.setText("");
        etUnitCost.setText("");
        etQuantity.setError(null);
        etUnitCost.setError(null);
    }

    private void submitImportOrder() {
        if (spSupplier.getSelectedItemPosition() == Spinner.INVALID_POSITION || suppliersList.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }

        Supplier selectedSupplier = suppliersList.get(spSupplier.getSelectedItemPosition());

        if (draftItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một mặt hàng vào đơn nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";
        String branchId = TextUtils.isEmpty(selectedBranchId) ? "65f0a1b2c3d4e5f678901234" : selectedBranchId; // Fallback branch ID if empty

        CreateImportOrderRequest request = new CreateImportOrderRequest(
                selectedSupplier.getId(),
                branchId,
                note,
                draftItems
        );

        String token = "Bearer " + sessionManager.getAuthToken();
        RetrofitClient.getClient().create(PurchasingApiService.class).createImportOrder(token, request).enqueue(new Callback<ApiResponse<ImportOrder>>() {
            @Override
            public void onResponse(Call<ApiResponse<ImportOrder>> call, Response<ApiResponse<ImportOrder>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminCreateImportOrderActivity.this, "Tạo đơn nhập kho thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AdminCreateImportOrderActivity.this, "Lỗi khi tạo đơn nhập: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ImportOrder>> call, Throwable t) {
                Toast.makeText(AdminCreateImportOrderActivity.this, "Lỗi kết nối khi gửi yêu cầu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class DraftItemsAdapter extends RecyclerView.Adapter<DraftItemsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AdminCreateImportOrderActivity.this).inflate(R.layout.item_draft_import_order_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CreateImportOrderItem item = draftItems.get(position);
            holder.tvDraftProductName.setText(item.product_name);

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            holder.tvDraftProductDetails.setText(String.format(Locale.getDefault(), "SL: %d x %s", item.quantity_ordered, format.format(item.unit_cost)));
            holder.tvDraftProductTotal.setText(format.format(item.quantity_ordered * item.unit_cost));

            holder.btnDeleteDraftItem.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    draftItems.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, draftItems.size());
                }
            });
        }

        @Override
        public int getItemCount() {
            return draftItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDraftProductName;
            TextView tvDraftProductDetails;
            TextView tvDraftProductTotal;
            ImageButton btnDeleteDraftItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDraftProductName = itemView.findViewById(R.id.tvDraftProductName);
                tvDraftProductDetails = itemView.findViewById(R.id.tvDraftProductDetails);
                tvDraftProductTotal = itemView.findViewById(R.id.tvDraftProductTotal);
                btnDeleteDraftItem = itemView.findViewById(R.id.btnDeleteDraftItem);
            }
        }
    }
}
