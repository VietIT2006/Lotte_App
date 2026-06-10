package com.ptithcm.lottemart.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
    private ImageView ivHeroBanner;
    private ProductApiService apiService;
    private android.widget.TextView tvLocationLabel;
    private android.widget.TextView tvLocation;
    private com.ptithcm.lottemart.data.local.SessionManager sessionManager;
    private List<com.ptithcm.lottemart.data.models.Branch> availableBranches = new ArrayList<>();
    
    private androidx.viewpager2.widget.ViewPager2 vpBanner;
    private android.os.Handler sliderHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable sliderRunnable;

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
        fetchBranchInfo();
        fetchPromotions();
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rvCategories);
        rvFeatured = view.findViewById(R.id.rvFeatured);
        tvLocationLabel = view.findViewById(R.id.tvLocationLabel);
        tvLocation = view.findViewById(R.id.tvLocation);
        View searchContainer = view.findViewById(R.id.searchContainer);
        
        sessionManager = new com.ptithcm.lottemart.data.local.SessionManager(getContext());
        
        View.OnClickListener locationClickListener = v -> showBranchSelectorDialog();
        if (tvLocationLabel != null) tvLocationLabel.setOnClickListener(locationClickListener);
        if (tvLocation != null) tvLocation.setOnClickListener(locationClickListener);
        
        setupBannerCarousel(view);
        View btnNotification = view.findViewById(R.id.btnNotification);

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.notifications.NotificationActivity.class);
                startActivity(intent);
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

    private void fetchBranchInfo() {
        apiService.getBranches().enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Branch>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Branch>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Branch>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    availableBranches = response.body().getData();
                    
                    String savedId = sessionManager.getSelectedBranchId();
                    com.ptithcm.lottemart.data.models.Branch selectedBranch = null;
                    
                    if (savedId != null) {
                        for (com.ptithcm.lottemart.data.models.Branch b : availableBranches) {
                            if (b.getId().equals(savedId)) {
                                selectedBranch = b;
                                break;
                            }
                        }
                    }
                    
                    if (selectedBranch == null) {
                        selectedBranch = availableBranches.get(0);
                        sessionManager.saveSelectedBranch(selectedBranch.getId(), selectedBranch.getName(), selectedBranch.getAddress());
                    }
                    
                    updateLocationUI(selectedBranch.getName(), selectedBranch.getAddress());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Branch>>> call, Throwable t) {
                Log.e(TAG, "Error fetching branches", t);
            }
        });
    }

    private void fetchPromotions() {
        apiService.getPromotions().enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.api.ProductApiService.Promotion>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.api.ProductApiService.Promotion>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.api.ProductApiService.Promotion>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    List<com.ptithcm.lottemart.data.api.ProductApiService.Promotion> promos = response.body().getData();
                    List<com.ptithcm.lottemart.data.models.Banner> banners = new ArrayList<>();
                    for (com.ptithcm.lottemart.data.api.ProductApiService.Promotion p : promos) {
                        banners.add(new com.ptithcm.lottemart.data.models.Banner(p.getId(), p.getTitle(), p.getDescription(), p.getBannerImage()));
                    }
                    if (vpBanner != null) {
                        com.ptithcm.lottemart.ui.adapters.BannerAdapter adapter = new com.ptithcm.lottemart.ui.adapters.BannerAdapter(getContext(), banners);
                        vpBanner.setAdapter(adapter);
                        
                        View view = getView();
                        if (view != null) {
                            android.widget.LinearLayout layoutDots = view.findViewById(R.id.layoutDots);
                            if (layoutDots != null) {
                                setupIndicator(layoutDots, banners.size());
                                setCurrentIndicator(layoutDots, 0);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.api.ProductApiService.Promotion>>> call, Throwable t) {
                Log.e(TAG, "Error fetching promotions banner", t);
            }
        });
    }

    private void updateLocationUI(String name, String address) {
        if (tvLocationLabel != null) {
            tvLocationLabel.setText("Giao đến: " + name);
        }
        if (tvLocation != null) {
            tvLocation.setText(address + " ▾");
        }
    }

    private void showBranchSelectorDialog() {
        if (availableBranches.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải danh sách chi nhánh...", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] branchNames = new String[availableBranches.size()];
        for (int i = 0; i < availableBranches.size(); i++) {
            com.ptithcm.lottemart.data.models.Branch b = availableBranches.get(i);
            branchNames[i] = b.getName() + " - " + b.getAddress();
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn chi nhánh giao hàng")
                .setItems(branchNames, (dialog, which) -> {
                    com.ptithcm.lottemart.data.models.Branch selected = availableBranches.get(which);
                    sessionManager.saveSelectedBranch(selected.getId(), selected.getName(), selected.getAddress());
                    updateLocationUI(selected.getName(), selected.getAddress());
                })
                .show();
    }

    private void setupBannerCarousel(View view) {
        vpBanner = view.findViewById(R.id.vpBanner);
        android.widget.LinearLayout layoutDots = view.findViewById(R.id.layoutDots);

        if (vpBanner == null || layoutDots == null) return;

        List<com.ptithcm.lottemart.data.models.Banner> banners = new ArrayList<>();
        banners.add(new com.ptithcm.lottemart.data.models.Banner("1", "Đêm nhạc Lotte Harmony 2024", "Đại nhạc hội tri ân khách hàng thân thiết Lotte Mart với dàn sao cực đỉnh.", "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?q=80&w=1200"));
        banners.add(new com.ptithcm.lottemart.data.models.Banner("2", "Lễ hội Trái cây Nhiệt đới", "Thưởng thức trái cây tươi ngon mỗi ngày với giá cực sốc.", "https://images.unsplash.com/photo-1610832958506-aa56368176cf?q=80&w=1200"));
        banners.add(new com.ptithcm.lottemart.data.models.Banner("3", "Thịt Tươi Giảm Sâu", "Mua nhiều giảm sâu, nhập khẩu 100% chuẩn châu Âu.", "https://images.unsplash.com/photo-1607006411021-d70c4103cd31?q=80&w=1200"));

        com.ptithcm.lottemart.ui.adapters.BannerAdapter adapter = new com.ptithcm.lottemart.ui.adapters.BannerAdapter(getContext(), banners);
        vpBanner.setAdapter(adapter);

        setupIndicator(layoutDots, banners.size());
        setCurrentIndicator(layoutDots, 0);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpBanner != null && vpBanner.getAdapter() != null) {
                    int currentItem = vpBanner.getCurrentItem();
                    int totalItems = vpBanner.getAdapter().getItemCount();
                    if (currentItem < totalItems - 1) {
                        vpBanner.setCurrentItem(currentItem + 1);
                    } else {
                        vpBanner.setCurrentItem(0); // loop back to first
                    }
                }
                sliderHandler.postDelayed(this, 3000); // 3 seconds interval
            }
        };

        vpBanner.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(layoutDots, position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    private void setupIndicator(android.widget.LinearLayout layoutDots, int count) {
        layoutDots.removeAllViews();
        android.widget.ImageView[] dots = new android.widget.ImageView[count];
        for (int i = 0; i < count; i++) {
            dots[i] = new android.widget.ImageView(getContext());
            dots[i].setImageDrawable(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.presence_invisible));
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            layoutDots.addView(dots[i], params);
        }
    }

    private void setCurrentIndicator(android.widget.LinearLayout layoutDots, int index) {
        int childCount = layoutDots.getChildCount();
        for (int i = 0; i < childCount; i++) {
            android.widget.ImageView imageView = (android.widget.ImageView) layoutDots.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.presence_online));
            } else {
                imageView.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.presence_invisible));
            }
        }
    }
 }
