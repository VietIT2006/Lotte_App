package com.ptithcm.lottemart.data.remote;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetworkConfig {
    private static final String TAG = "NetworkConfig";
    
    // Mặc định ban đầu dùng 10.0.2.2 cho máy ảo hoặc localhost cho máy thật kết nối adb reverse
    public static String BASE_URL = isEmulator() 
            ? "http://10.0.2.2:3000/api/v1/" 
            : "http://127.0.0.1:3000/api/v1/";

    // Timeout (ms)
    public static final int CONNECT_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 30000;

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    /**
     * Tự động quét tìm kiếm máy tính chạy backend Node.js trong mạng Wi-Fi nội bộ
     */
    public static void discoverBackendIP(Context context, Runnable onComplete) {
        if (isEmulator()) {
            // Máy ảo luôn dùng 10.0.2.2, không cần quét
            BASE_URL = "http://10.0.2.2:3000/api/v1/";
            if (onComplete != null) onComplete.run();
            return;
        }

        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null || !wifiManager.isWifiEnabled()) {
                Log.d(TAG, "Wifi is not enabled, fallback to default loopback IP");
                if (onComplete != null) onComplete.run();
                return;
            }

            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (dhcpInfo == null || dhcpInfo.gateway == 0) {
                Log.d(TAG, "Cannot get DHCP info, fallback to default loopback IP");
                if (onComplete != null) onComplete.run();
                return;
            }

            // Tìm subnet IP (Ví dụ: 192.168.1)
            int ipAddress = dhcpInfo.ipAddress;
            String ipString = String.format("%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff));

            Log.d(TAG, "My IP prefix: " + ipString + ".x");

            // Quét đồng thời toàn bộ dải IP 1-254 bằng ThreadPool nhanh chóng (khoảng 1-2 giây)
            ExecutorService executor = Executors.newFixedThreadPool(50);
            final String[] discoveredIp = {null};

            for (int i = 1; i <= 254; i++) {
                final String host = ipString + "." + i;
                executor.submit(() -> {
                    try {
                        Socket socket = new Socket();
                        // Kết nối thử đến cổng 3000 với timeout cực ngắn (250ms)
                        socket.connect(new InetSocketAddress(host, 3000), 250);
                        socket.close();
                        
                        // Nếu kết nối thành công, đây chính là máy chủ đang chạy Backend
                        synchronized (discoveredIp) {
                            discoveredIp[0] = host;
                            Log.d(TAG, "Found backend server at: " + host);
                        }
                    } catch (IOException ignored) {
                    }
                });
            }

            executor.shutdown();
            new Thread(() -> {
                try {
                    // Đợi tối đa 1.5 giây để kết thúc quét dải IP
                    executor.awaitTermination(1500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Scan interrupted", e);
                }

                synchronized (discoveredIp) {
                    if (discoveredIp[0] != null) {
                        BASE_URL = "http://" + discoveredIp[0] + ":3000/api/v1/";
                        Log.i(TAG, "Auto-discovered backend server BASE_URL: " + BASE_URL);
                    } else {
                        Log.w(TAG, "No backend server found on local network, using default fallback IP");
                    }
                }
                if (onComplete != null) onComplete.run();
            }).start();
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: Missing ACCESS_WIFI_STATE permission", e);
            if (onComplete != null) onComplete.run();
        }
    }
}
