package com.ptithcm.lottemart.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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
    
    private static boolean hasShownPromoPopup = false;
    
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    
    private int apiCallsCompleted = 0;
    private final int TOTAL_API_CALLS = 3; // categories, featured products, promotions
    private android.widget.ProgressBar progressBar;
    private android.widget.ProgressBar progressBarLoadMore;
    private androidx.core.widget.NestedScrollView contentScrollView;
    
    // Pagination for featured products
    private int currentPage = 1;
    private int limit = 20;
    private boolean isLoadingMore = false;
    private boolean isLastPage = false;
    
    private void checkDataLoaded() {
        apiCallsCompleted++;
        if (apiCallsCompleted >= TOTAL_API_CALLS) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (contentScrollView != null) contentScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                requestLocationAndFindNearestBranch();
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể tự động chọn chi nhánh vì thiếu quyền vị trí", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        try {
            apiService = RetrofitClient.getClient().create(ProductApiService.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to init API service", e);
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        fetchCategories();
        fetchFeaturedProducts();
        fetchBranchInfo();
        fetchPromotions();
        startEntranceAnimations(view);
    }

    private void startEntranceAnimations(View view) {
        Animation slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
        
        View searchContainer = view.findViewById(R.id.searchContainer);
        if (searchContainer != null) {
            searchContainer.startAnimation(slideUp);
        }
        
        if (vpBanner != null) {
            vpBanner.startAnimation(fadeIn);
        }
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rvCategories);
        rvFeatured = view.findViewById(R.id.rvFeatured);
        tvLocationLabel = view.findViewById(R.id.tvLocationLabel);
        tvLocation = view.findViewById(R.id.tvLocation);
        View searchContainer = view.findViewById(R.id.searchContainer);
        progressBar = view.findViewById(R.id.progressBar);
        contentScrollView = view.findViewById(R.id.contentScrollView);
        progressBarLoadMore = view.findViewById(R.id.progressBarLoadMore);
        
        sessionManager = new com.ptithcm.lottemart.data.local.SessionManager(getContext());
        
        View.OnClickListener locationClickListener = v -> showBranchSelectorDialog();
        if (tvLocationLabel != null) tvLocationLabel.setOnClickListener(locationClickListener);
        if (tvLocation != null) tvLocation.setOnClickListener(locationClickListener);
        
        setupBannerCarousel(view);
        View btnNotification = view.findViewById(R.id.btnNotification);

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                        Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.notifications.NotificationActivity.class);
                        startActivity(intent);
                    }).start();
                }).start();
            });
        }

        searchContainer.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                    Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.search.SearchActivity.class);
                    startActivity(intent);
                }).start();
            }).start();
        });

        View fabSpinWheel = view.findViewById(R.id.fabSpinWheel);
        if (fabSpinWheel != null) {
            fabSpinWheel.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                        Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.promotions.SpinWheelActivity.class);
                        startActivity(intent);
                    }).start();
                }).start();
            });
        }

        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>(), category -> {
            Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.categories.CategoryProductsActivity.class);
            intent.putExtra("CATEGORY_ID", category.getId());
            intent.putExtra("CATEGORY_NAME", category.getName());
            startActivity(intent);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        LayoutAnimationController animController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_slide_right);
        rvCategories.setLayoutAnimation(animController);
        rvCategories.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), product -> {
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvFeatured.setLayoutManager(new GridLayoutManager(getContext(), 2));
        LayoutAnimationController animController2 = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_slide_right);
        rvFeatured.setLayoutAnimation(animController2);
        rvFeatured.setAdapter(productAdapter);
        rvFeatured.setNestedScrollingEnabled(false);
        
        // Infinite scroll via NestedScrollView
        if (contentScrollView != null) {
            contentScrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    // Check if scrolled to bottom
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight() - 200)) && scrollY > oldScrollY) {
                        if (!isLoadingMore && !isLastPage) {
                            currentPage++;
                            fetchFeaturedProducts();
                        }
                    }
                }
            });
        }
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>> response) {
                checkDataLoaded();
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setCategories(response.body().getData());
                } else {
                    Log.e(TAG, "Failed to fetch categories: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Category>>> call, Throwable t) {
                checkDataLoaded();
                Log.e(TAG, "Error fetching categories", t);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFeaturedProducts() {
        isLoadingMore = true;
        if (currentPage > 1 && progressBarLoadMore != null) {
            progressBarLoadMore.setVisibility(View.VISIBLE);
        }
        apiService.getFeaturedProducts(currentPage, limit).enqueue(new retrofit2.Callback<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, retrofit2.Response<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> response) {
                checkDataLoaded();
                isLoadingMore = false;
                if (progressBarLoadMore != null) progressBarLoadMore.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<com.ptithcm.lottemart.data.models.Product> newProducts = response.body().getData();
                    com.ptithcm.lottemart.data.api.ApiResponse.PaginationMeta pagination = response.body().getPagination();
                    
                    if (currentPage == 1) {
                        productAdapter.setProducts(newProducts);
                    } else {
                        productAdapter.addProducts(newProducts);
                    }
                    
                    if (pagination != null) {
                        isLastPage = currentPage >= pagination.getTotalPages();
                    } else {
                        isLastPage = newProducts == null || newProducts.isEmpty();
                    }
                } else {
                    Log.e(TAG, "Failed to fetch featured products: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.ptithcm.lottemart.data.api.ApiResponse<java.util.List<com.ptithcm.lottemart.data.models.Product>>> call, Throwable t) {
                checkDataLoaded();
                isLoadingMore = false;
                if (progressBarLoadMore != null) progressBarLoadMore.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching featured products", t);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Không thể tải sản phẩm nổi bật", Toast.LENGTH_SHORT).show();
                }
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
                    
                    // Auto-select nearest branch based on location (only once per app install/login)
                    if (!sessionManager.hasAskedLocationPermission()) {
                        checkLocationPermissionAndAutoSelectBranch();
                        sessionManager.setAskedLocationPermission(true);
                    }
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
                checkDataLoaded();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    List<com.ptithcm.lottemart.data.api.ProductApiService.Promotion> promos = response.body().getData();
                    List<com.ptithcm.lottemart.data.models.Banner> banners = new ArrayList<>();
                    
                    com.ptithcm.lottemart.data.api.ProductApiService.Promotion popupPromo = null;
                    
                    for (com.ptithcm.lottemart.data.api.ProductApiService.Promotion p : promos) {
                        banners.add(new com.ptithcm.lottemart.data.models.Banner(p.getId(), p.getTitle(), p.getDescription(), p.getBannerImage()));
                        if ("home".equalsIgnoreCase(p.getPosition()) && popupPromo == null) {
                            popupPromo = p;
                        }
                    }
                    
                    if (popupPromo == null && !promos.isEmpty()) {
                        popupPromo = promos.get(0);
                    }
                    
                    if (!hasShownPromoPopup && popupPromo != null) {
                        showPromotionPopup(popupPromo);
                        hasShownPromoPopup = true;
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
                checkDataLoaded();
                Log.e(TAG, "Error fetching promotions banner", t);
            }
        });
    }

    private void showPromotionPopup(com.ptithcm.lottemart.data.api.ProductApiService.Promotion promotion) {
        if (getContext() == null || getActivity() == null) return;
        
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        android.widget.RelativeLayout layout = new android.widget.RelativeLayout(getContext());
        layout.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.ImageView ivPromo = new android.widget.ImageView(getContext());
        ivPromo.setId(android.view.View.generateViewId());
        ivPromo.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        ivPromo.setAdjustViewBounds(true);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        ivPromo.setPadding(padding, padding, padding, padding);
        
        android.widget.RelativeLayout.LayoutParams ivParams = new android.widget.RelativeLayout.LayoutParams(
                (int) (320 * getResources().getDisplayMetrics().density),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        ivParams.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
        layout.addView(ivPromo, ivParams);

        String imageUrl = promotion.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = promotion.getBannerImage();
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(com.ptithcm.lottemart.data.remote.NetworkConfig.getFullImageUrl(imageUrl)).into(ivPromo);
        }

        android.widget.ImageButton btnClose = new android.widget.ImageButton(getContext());
        btnClose.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        btnClose.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnClose.setColorFilter(android.graphics.Color.LTGRAY);
        android.widget.RelativeLayout.LayoutParams btnParams = new android.widget.RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.addRule(android.widget.RelativeLayout.ALIGN_TOP, ivPromo.getId());
        btnParams.addRule(android.widget.RelativeLayout.ALIGN_END, ivPromo.getId());
        layout.addView(btnClose, btnParams);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        ivPromo.setOnClickListener(v -> {
            dialog.dismiss();
            if (promotion.getCategoryId() != null && !promotion.getCategoryId().isEmpty()) {
                Intent intent = new Intent(getActivity(), com.ptithcm.lottemart.features.categories.CategoryProductsActivity.class);
                intent.putExtra("CATEGORY_ID", promotion.getCategoryId());
                intent.putExtra("CATEGORY_NAME", promotion.getTitle());
                startActivity(intent);
            } else if (promotion.getLink() != null && !promotion.getLink().isEmpty()) {
                Toast.makeText(getContext(), "Chuyển đến: " + promotion.getLink(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setContentView(layout);
        dialog.show();
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

    private void checkLocationPermissionAndAutoSelectBranch() {
        if (getContext() == null) return;
        
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationAndFindNearestBranch();
        } else {
            locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void requestLocationAndFindNearestBranch() {
        if (getContext() == null || fusedLocationClient == null || availableBranches == null || availableBranches.isEmpty()) return;
        
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    findNearestBranch(location);
                } else {
                    // Fallback to mock user location if GPS is unavailable (e.g. on emulator)
                    Location mockUserLoc = new Location("mock");
                    mockUserLoc.setLatitude(10.776889); // Center of HCMC
                    mockUserLoc.setLongitude(106.700806);
                    findNearestBranch(mockUserLoc);
                }
            }).addOnFailureListener(e -> {
                Location mockUserLoc = new Location("mock");
                mockUserLoc.setLatitude(10.776889);
                mockUserLoc.setLongitude(106.700806);
                findNearestBranch(mockUserLoc);
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission missing", e);
        }
    }
    
    private void findNearestBranch(Location userLocation) {
        if (availableBranches == null || availableBranches.isEmpty()) return;
        
        com.ptithcm.lottemart.data.models.Branch nearestBranch = null;
        float minDistance = Float.MAX_VALUE;
        
        for (com.ptithcm.lottemart.data.models.Branch b : availableBranches) {
            Location branchLocation = getMockBranchLocation(b.getName());
            if (branchLocation != null) {
                float distance = userLocation.distanceTo(branchLocation);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestBranch = b;
                }
            }
        }
        
        if (nearestBranch != null) {
            sessionManager.saveSelectedBranch(nearestBranch.getId(), nearestBranch.getName(), nearestBranch.getAddress());
            updateLocationUI(nearestBranch.getName(), nearestBranch.getAddress());
            if (getContext() != null) {
                Toast.makeText(getContext(), "Đã tự động chọn chi nhánh gần bạn nhất: " + nearestBranch.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private Location getMockBranchLocation(String branchName) {
        Location loc = new Location("mock");
        String lowerName = branchName != null ? branchName.toLowerCase() : "";
        
        if (lowerName.contains("nam sài gòn") || lowerName.contains("quận 7")) {
            loc.setLatitude(10.735165);
            loc.setLongitude(106.700142);
        } else if (lowerName.contains("gò vấp")) {
            loc.setLatitude(10.835472);
            loc.setLongitude(106.666111);
        } else if (lowerName.contains("tân bình")) {
            loc.setLatitude(10.801648);
            loc.setLongitude(106.655823);
        } else if (lowerName.contains("hà nội") || lowerName.contains("ba đình")) {
            loc.setLatitude(21.031580);
            loc.setLongitude(105.812230);
        } else {
            // Default center of HCMC
            loc.setLatitude(10.776889);
            loc.setLongitude(106.700806);
        }
        return loc;
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
