package com.ptithcm.lottemart.features.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.lottemart.R;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnBack, btnClearSearch;
    private android.view.View layoutSuggestions, layoutResults;
    private androidx.recyclerview.widget.RecyclerView rvResults, rvSuggestions;
    private com.ptithcm.lottemart.ui.adapters.ProductAdapter productAdapter;
    private com.ptithcm.lottemart.data.api.ProductApiService apiService;
    private String currentSort = "latest";
    private int currentPage = 1;
    private int limit = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentQuery = "";
    private android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_search);
        apiService = com.ptithcm.lottemart.data.remote.RetrofitClient.getClient().create(com.ptithcm.lottemart.data.api.ProductApiService.class);

        initViews();
        setupListeners();
        showSuggestions();
        
        findViewById(R.id.searchHeaderUnified).startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        layoutSuggestions = findViewById(R.id.layoutSuggestions);
        layoutResults = findViewById(R.id.layoutResults);
        rvResults = findViewById(R.id.rvResults);
        rvSuggestions = findViewById(R.id.rvSuggestions);

        productAdapter = new com.ptithcm.lottemart.ui.adapters.ProductAdapter(this, new java.util.ArrayList<>(), product -> {
            Intent intent = new Intent(this, com.ptithcm.lottemart.features.shopping.ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });

        rvResults.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        LayoutAnimationController animController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right);
        rvResults.setLayoutAnimation(animController);
        rvResults.setAdapter(productAdapter);
        
        rvResults.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    androidx.recyclerview.widget.GridLayoutManager layoutManager = (androidx.recyclerview.widget.GridLayoutManager) recyclerView.getLayoutManager();
                    if(layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == productAdapter.getItemCount() - 1) {
                        if(!isLoading && !isLastPage) {
                            currentPage++;
                            performSearch(currentQuery, false, false);
                        }
                    }
                }
            }
        });
        
        ChipGroup chipGroupHistory = findViewById(R.id.chipGroupHistory);
        String[] recentSearches = {"Thịt heo", "Sữa chua", "Rau củ", "Trái cây"};
        for (String s : recentSearches) {
            Chip chip = new Chip(this);
            chip.setText(s);
            chip.setCheckable(false);
            chip.setOnClickListener(v -> {
                etSearch.setText(s);
                performSearch(s, true, true);
            });
            chipGroupHistory.addView(chip);
        }
        
        etSearch.requestFocus();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (layoutResults.getVisibility() == android.view.View.VISIBLE) {
                showSuggestions();
            } else {
                finish();
            }
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            showSuggestions();
        });

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
                if (s.length() == 0) {
                    showSuggestions();
                } else {
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                    searchRunnable = () -> performSearch(s.toString().trim(), true, false);
                    searchHandler.postDelayed(searchRunnable, 500);
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString().trim(), true, true);
                return true;
            }
            return false;
        });

        findViewById(R.id.btnSortPrice).setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                    if (currentSort.equals("price_asc")) {
                        currentSort = "price_desc";
                        ((android.widget.TextView)v).setText("Sắp xếp: Giá cao đến thấp");
                    } else {
                        currentSort = "price_asc";
                        ((android.widget.TextView)v).setText("Sắp xếp: Giá thấp đến cao");
                    }
                    performSearch(etSearch.getText().toString().trim(), true, false);
                }).start();
            }).start();
        });

        findViewById(R.id.btnFilterPrice).setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Tính năng lọc giá đang được cập nhật", android.widget.Toast.LENGTH_SHORT).show();
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();
        });

        findViewById(R.id.btnFilterBrand).setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Tính năng lọc nhãn hiệu đang được cập nhật", android.widget.Toast.LENGTH_SHORT).show();
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();
        });
    }

    private void showSuggestions() {
        layoutSuggestions.setVisibility(android.view.View.VISIBLE);
        layoutResults.setVisibility(android.view.View.GONE);
    }

    private void performSearch(String query, boolean isNewSearch, boolean hideKeyboard) {
        if (query.isEmpty()) return;
        
        if (isNewSearch) {
            currentPage = 1;
            isLastPage = false;
            currentQuery = query;
        }

        if (hideKeyboard) {
            // Hide keyboard
            android.view.View view = this.getCurrentFocus();
            if (view != null) {
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        layoutSuggestions.setVisibility(android.view.View.GONE);
        layoutResults.setVisibility(android.view.View.VISIBLE);
        
        isLoading = true;

        apiService.searchProducts(query, currentSort, currentPage, limit).enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<com.ptithcm.lottemart.data.models.Product> products = response.body().getData();
                    com.ptithcm.lottemart.data.api.ApiResponse.PaginationMeta pagination = response.body().getPagination();
                    
                    if (currentPage == 1) {
                        productAdapter.setProducts(products);
                        rvResults.scheduleLayoutAnimation();
                    } else {
                        productAdapter.addProducts(products);
                    }
                    
                    if (pagination != null) {
                        isLastPage = currentPage >= pagination.getTotalPages();
                    } else {
                        isLastPage = products == null || products.isEmpty();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, Throwable t) {
                isLoading = false;
                android.widget.Toast.makeText(SearchActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
