package com.ptithcm.lottemart.utils;

import com.ptithcm.lottemart.BuildConfig;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class PayOSUtils {

    // Đọc từ local.properties thông qua BuildConfig
    public static final String PAYOS_CLIENT_ID = BuildConfig.PAYOS_CLIENT_ID;
    public static final String PAYOS_API_KEY = BuildConfig.PAYOS_API_KEY;
    public static final String PAYOS_CHECKSUM_KEY = BuildConfig.PAYOS_CHECKSUM_KEY;

    public static String createSignature(long orderCode, int amount, String description, String cancelUrl, String returnUrl, String checksumKey) {
        String dataStr = "amount=" + amount 
                + "&cancelUrl=" + cancelUrl 
                + "&description=" + description 
                + "&orderCode=" + orderCode 
                + "&returnUrl=" + returnUrl;
        
        return hmacSHA256(dataStr, checksumKey);
    }

    private static String hmacSHA256(String data, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
