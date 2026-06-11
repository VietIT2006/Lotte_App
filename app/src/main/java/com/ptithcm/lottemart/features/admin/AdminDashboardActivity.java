package com.ptithcm.lottemart.features.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.OrderApiService;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.models.Order;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends BaseAdminActivity {
    private TextView tvKpi1Value, tvKpi2Value, tvKpi3Value, tvKpi4Value, tvChartTotalRevenue;
    
    // Lists & Adapters
    private RecyclerView rvPendingOrders, rvPendingProducts, rvPendingCategories;
    private PendingOrdersAdapter ordersAdapter;
    private PendingProductsAdapter productsAdapter;
    private PendingCategoriesAdapter categoriesAdapter;
    
    private List<Order> pendingOrders = new ArrayList<>();
    private List<Product> pendingProducts = new ArrayList<>();
    private List<Category> pendingCategories = new ArrayList<>();
    
    private LinearLayout llSuperAdminApprovals;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        setHeaderTitle("Bảng điều khiển");

        userRole = sessionManager.getUserRole();

        initViews();
        setupRecyclerViews();
        loadData();
    }

    private void initViews() {
        tvKpi1Value = findViewById(R.id.tvKpi1Value);
        tvKpi2Value = findViewById(R.id.tvKpi2Value);
        tvKpi3Value = findViewById(R.id.tvKpi3Value);
        tvKpi4Value = findViewById(R.id.tvKpi4Value);
        tvChartTotalRevenue = findViewById(R.id.tvChartTotalRevenue);
        
        rvPendingOrders = findViewById(R.id.rvPendingOrders);
        rvPendingProducts = findViewById(R.id.rvPendingProducts);
        rvPendingCategories = findViewById(R.id.rvPendingCategories);
        
        llSuperAdminApprovals = findViewById(R.id.llSuperAdminApprovals);

        if ("superAdmin".equalsIgnoreCase(userRole)) {
            llSuperAdminApprovals.setVisibility(View.VISIBLE);
        } else {
            llSuperAdminApprovals.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerViews() {
        rvPendingOrders.setLayoutManager(new LinearLayoutManager(this));
        ordersAdapter = new PendingOrdersAdapter();
        rvPendingOrders.setAdapter(ordersAdapter);

        if ("superAdmin".equalsIgnoreCase(userRole)) {
            rvPendingProducts.setLayoutManager(new LinearLayoutManager(this));
            productsAdapter = new PendingProductsAdapter();
            rvPendingProducts.setAdapter(productsAdapter);

            rvPendingCategories.setLayoutManager(new LinearLayoutManager(this));
            categoriesAdapter = new PendingCategoriesAdapter();
            rvPendingCategories.setAdapter(categoriesAdapter);
        }
    }

    private void loadData() {
        loadKpisAndOrders();
        if ("superAdmin".equalsIgnoreCase(userRole)) {
            loadPendingCatalog();
        }
    }

    private void loadKpisAndOrders() {
        OrderApiService orderApi = RetrofitClient.getClient().create(OrderApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        orderApi.getAdminOrders(token).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Order> allOrders = response.body().getData();
                    
                    // 1. Calculate KPI values
                    double totalRevenue = 0;
                    int orderCount = allOrders.size();
                    int pendingCount = 0;
                    pendingOrders.clear();

                    for (Order order : allOrders) {
                        if (order.getStatus() != null) {
                            if ("PENDING".equalsIgnoreCase(order.getStatus())) {
                                pendingCount++;
                                pendingOrders.add(order);
                            }
                            if (!"CANCELLED".equalsIgnoreCase(order.getStatus())) {
                                totalRevenue += (order.getTotalAmount() + order.getShippingFee());
                            }
                        }
                    }

                    tvKpi1Value.setText(String.format("%,.0f đ", totalRevenue));
                    tvChartTotalRevenue.setText(String.format("%,.0f đ", totalRevenue));
                    tvKpi2Value.setText(String.valueOf(orderCount));
                    tvKpi4Value.setText(String.valueOf(pendingCount));
                    
                    ordersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Lỗi kết nối máy chủ để lấy KPIs", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Products count for KPI 3
        ProductApiService productApi = RetrofitClient.getClient().create(ProductApiService.class);
        productApi.getProducts(null, 1, 1000).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    tvKpi3Value.setText(response.body().getData().size() + " mã");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                // Ignore failure quietly for secondary KPI
            }
        });
    }

    private void loadPendingCatalog() {
        ProductApiService productApi = RetrofitClient.getClient().create(ProductApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        productApi.getPendingProducts(token).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    pendingProducts.clear();
                    pendingProducts.addAll(response.body().getData());
                    productsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {}
        });

        productApi.getPendingCategories(token).enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    pendingCategories.clear();
                    pendingCategories.addAll(response.body().getData());
                    categoriesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {}
        });
    }

    // --- INNER ADAPTERS ---

    private class PendingOrdersAdapter extends RecyclerView.Adapter<PendingOrdersAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AdminDashboardActivity.this).inflate(R.layout.item_pending_order, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order order = pendingOrders.get(position);
            holder.tvOrderId.setText("Mã đơn: #" + order.getId());
            holder.tvOrderTotal.setText(String.format("%,.0f đ", order.getTotalAmount() + order.getShippingFee()));
            
            String date = order.getCreatedAt();
            if (date != null && date.contains("T")) {
                date = date.replace("T", " ").substring(0, 16);
            }
            holder.tvOrderDate.setText("Ngày đặt: " + (date != null ? date : "N/A"));

            holder.btnApprove.setOnClickListener(v -> updateOrderStatus(order.getId(), "ACCEPTED"));
            holder.btnDecline.setOnClickListener(v -> updateOrderStatus(order.getId(), "CANCELLED"));
        }

        @Override
        public int getItemCount() {
            return pendingOrders.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderId, tvOrderTotal, tvOrderDate;
            com.google.android.material.button.MaterialButton btnApprove, btnDecline;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
                tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnDecline = itemView.findViewById(R.id.btnDecline);
            }
        }
    }

    private void updateOrderStatus(String id, String status) {
        OrderApiService api = RetrofitClient.getClient().create(OrderApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        api.updateOrderStatus(token, id, new OrderApiService.UpdateOrderStatusRequest(status)).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Đã cập nhật trạng thái đơn hàng!", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Không thể duyệt đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {}
        });
    }

    private class PendingProductsAdapter extends RecyclerView.Adapter<PendingProductsAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AdminDashboardActivity.this).inflate(R.layout.item_pending_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product p = pendingProducts.get(position);
            holder.tvProductName.setText(p.getName());
            holder.tvProductPrice.setText(String.format("%,.0f đ", p.getPrice()));
            holder.tvProductCategory.setText("Hàng mới đang chờ Super Admin phê duyệt");

            holder.btnApprove.setOnClickListener(v -> approveProduct(p.getId()));
            holder.btnDecline.setOnClickListener(v -> declinePendingProduct(p.getId()));
        }

        @Override
        public int getItemCount() {
            return pendingProducts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName, tvProductPrice, tvProductCategory;
            com.google.android.material.button.MaterialButton btnApprove, btnDecline;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
                tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnDecline = itemView.findViewById(R.id.btnDecline);
            }
        }
    }

    private void approveProduct(String id) {
        ProductApiService api = RetrofitClient.getClient().create(ProductApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        api.approveProduct(token, id).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Đã duyệt sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {}
        });
    }

    private void declinePendingProduct(String id) {
        ProductApiService api = RetrofitClient.getClient().create(ProductApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        // Xóa sản phẩm chờ duyệt bằng hàm xóa mềm
        api.deleteBranch(token, id).enqueue(new Callback<ApiResponse<Void>>() { // Mượn hàm delete mapping chung của Retrofit hoặc update soft delete
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                Toast.makeText(AdminDashboardActivity.this, "Đã từ chối và xóa sản phẩm nháp!", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private class PendingCategoriesAdapter extends RecyclerView.Adapter<PendingCategoriesAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AdminDashboardActivity.this).inflate(R.layout.item_pending_category, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Category c = pendingCategories.get(position);
            holder.tvCategoryName.setText(c.getName());

            holder.btnApprove.setOnClickListener(v -> approveCategory(c.getId()));
            holder.btnDecline.setOnClickListener(v -> declinePendingCategory(c.getId()));
        }

        @Override
        public int getItemCount() {
            return pendingCategories.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCategoryName;
            com.google.android.material.button.MaterialButton btnApprove, btnDecline;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnDecline = itemView.findViewById(R.id.btnDecline);
            }
        }
    }

    private void approveCategory(String id) {
        ProductApiService api = RetrofitClient.getClient().create(ProductApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        api.approveCategory(token, id).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Đã duyệt danh mục thành công!", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {}
        });
    }

    private void declinePendingCategory(String id) {
        ProductApiService api = RetrofitClient.getClient().create(ProductApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();

        api.deleteBranch(token, id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                Toast.makeText(AdminDashboardActivity.this, "Đã từ chối danh mục nháp!", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }
}
