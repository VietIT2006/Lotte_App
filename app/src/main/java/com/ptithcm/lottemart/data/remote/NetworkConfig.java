package com.ptithcm.lottemart.data.remote;

public class NetworkConfig {
    // IP 10.0.2.2 là địa chỉ mặc định để Máy ảo (Emulator) kết nối với máy tính (Localhost)
    public static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";
    
    // Nếu dùng máy thật + cáp USB, hãy đổi 10.0.2.2 thành 127.0.0.1 và chạy file .bat
    // public static final String BASE_URL = "http://127.0.0.1:3000/api/v1/";

    // Timeout (ms)
    public static final int CONNECT_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 30000;
}
