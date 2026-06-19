package com.ptithcm.lottemart.features.categories;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.ProductApiService;
import com.ptithcm.lottemart.data.models.Category;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.ui.adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private ProductApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rvCategories = view.findViewById(R.id.rvCategoriesAll);
        adapter = new CategoryAdapter(getContext(), new ArrayList<>(), category -> {
            Intent intent = new Intent(getActivity(), CategoryProductsActivity.class);
            intent.putExtra("CATEGORY_ID", category.getId());
            intent.putExtra("CATEGORY_NAME", category.getName());
            startActivity(intent);
        });
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvCategories.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ProductApiService.class);
        fetchCategories();
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setCategories(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
