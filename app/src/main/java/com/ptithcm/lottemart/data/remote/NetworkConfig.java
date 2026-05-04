package com.ptithcm.lottemart.data.remote;

public class NetworkConfig {
    // Địa chỉ máy chủ (Sử dụng 10.0.2.2 cho Android Emulator kết nối với localhost)
    public static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";

    // Timeout (ms)
    public static final int CONNECT_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 30000;
}
