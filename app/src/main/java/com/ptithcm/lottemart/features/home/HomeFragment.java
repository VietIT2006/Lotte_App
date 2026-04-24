package com.ptithcm.lottemart.features.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.lottemart.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Khởi tạo các thành phần giao diện và dữ liệu ở đây
        setupMockData();
    }

    private void setupMockData() {
        // Setup Categories
        androidx.recyclerview.widget.RecyclerView rvCategories = getView().findViewById(R.id.rvCategories);
        if (rvCategories != null) {
            java.util.List<com.ptithcm.lottemart.data.models.Category> categories = new java.util.ArrayList<>();
            categories.add(new com.ptithcm.lottemart.data.models.Category("1", "Rau Rủ", ""));
            categories.add(new com.ptithcm.lottemart.data.models.Category("2", "Thịt cá", ""));
            categories.add(new com.ptithcm.lottemart.data.models.Category("3", "Sữa", ""));
            categories.add(new com.ptithcm.lottemart.data.models.Category("4", "Trái cây", ""));
            
            com.ptithcm.lottemart.ui.adapters.CategoryAdapter categoryAdapter = new com.ptithcm.lottemart.ui.adapters.CategoryAdapter(getContext(), categories);
            rvCategories.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            rvCategories.setAdapter(categoryAdapter);
        }

        // Setup Featured Products
        androidx.recyclerview.widget.RecyclerView rvFeatured = getView().findViewById(R.id.rvFeatured);
        if (rvFeatured != null) {
            java.util.List<com.ptithcm.lottemart.data.models.Product> products = new java.util.ArrayList<>();
            products.add(new com.ptithcm.lottemart.data.models.Product("p1", "Thịt bò Kobe (1kg)", 1500000, 2000000, ""));
            products.add(new com.ptithcm.lottemart.data.models.Product("p2", "Sữa tươi Đà Lạt Latte", 45000, 50000, ""));
            products.add(new com.ptithcm.lottemart.data.models.Product("p3", "Dưa lưới giòn", 75000, 85000, ""));
            products.add(new com.ptithcm.lottemart.data.models.Product("p4", "Cá hồi tươi", 350000, 420000, ""));

            com.ptithcm.lottemart.ui.adapters.ProductAdapter productAdapter = new com.ptithcm.lottemart.ui.adapters.ProductAdapter(getContext(), products, product -> {
                // Navigate to Product Detail
                android.content.Intent intent = new android.content.Intent(getActivity(), com.ptithcm.lottemart.features.shopping.ProductDetailActivity.class);
                startActivity(intent);
            });
            rvFeatured.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
            rvFeatured.setAdapter(productAdapter);
        }
    }
}
