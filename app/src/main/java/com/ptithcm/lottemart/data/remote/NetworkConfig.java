package com.ptithcm.lottemart.data.remote;

public class NetworkConfig {
    // Địa chỉ IP đặc biệt 10.0.2.2 trỏ về localhost của máy tính từ Emulator
    // Port 3000 là cổng mặc định trong server.js của bạn
    public static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";

    // Authentication Endpoints
    public static final String ENDPOINT_LOGIN = BASE_URL + "auth/login";
    public static final String ENDPOINT_REGISTER = BASE_URL + "auth/register";
    public static final String ENDPOINT_FORGOT_PASSWORD = BASE_URL + "auth/forgot-password";

    // Catalog & Search Endpoints (Dựa trên kế hoạch Day 2)
    public static final String ENDPOINT_PRODUCTS = BASE_URL + "products";
    public static final String ENDPOINT_PRODUCT_CATEGORIES = BASE_URL + "categories";
    public static final String ENDPOINT_SEARCH = BASE_URL + "catalog/search";
}