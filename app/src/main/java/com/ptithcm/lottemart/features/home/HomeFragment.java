package com.ptithcm.lottemart.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.models.Product;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.shopping.ProductDetailActivity;
import com.ptithcm.lottemart.ui.adapters.CategoryAdapter;
import com.ptithcm.lottemart.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView rvCategories, rvFeatured;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private ProductApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        
        fetchCategories();
        fetchFeaturedProducts();
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rvCategories);
        rvFeatured = view.findViewById(R.id.rvFeatured);
        View searchContainer = view.findViewById(R.id.searchContainer);
        View btnNotification = view.findViewById(R.id.btnNotification);

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Tính năng thông báo đang được phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        searchContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.search.SearchActivity.class);
            startActivity(intent);
        });

        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>(), category -> {
            Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.categories.CategoryProductsActivity.class);
            intent.putExtra("CATEGORY_ID", category.getId());
            intent.putExtra("CATEGORY_NAME", category.getName());
            startActivity(intent);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), product -> {
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvFeatured.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFeatured.setAdapter(productAdapter);
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setCategories(response.body().getData());
                } else {
                    Log.e(TAG, "Failed to fetch categories: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>> call, Throwable t) {
                Log.e(TAG, "Error fetching categories", t);
                Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFeaturedProducts() {
        apiService.getFeaturedProducts().enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setProducts(response.body().getData());
                } else {
                    Log.e(TAG, "Failed to fetch featured products: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, Throwable t) {
                Log.e(TAG, "Error fetching featured products", t);
                Toast.makeText(getContext(), "Không thể tải sản phẩm nổi bật", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
