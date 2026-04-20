package com.ptithcm.lottemart.data.remote;

public class NetworkConfig {
    // Địa chỉ máy chủ chính (Base URL)
    public static final String BASE_URL = "https://api.lottemart.vn/";

    // Các Endpoint (Đường dẫn phụ)
    public static final String ENDPOINT_LOGIN = BASE_URL + "auth/login";
    public static final String ENDPOINT_REGISTER = BASE_URL + "auth/register";
    public static final String ENDPOINT_FORGOT_PASSWORD = BASE_URL + "auth/forgot-password";
    
    // Endpoint cho Sản phẩm
    public static final String ENDPOINT_PRODUCTS = BASE_URL + "products";
    public static final String ENDPOINT_PRODUCT_CATEGORIES = BASE_URL + "categories";
}
