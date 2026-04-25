package com.ptithcm.lottemart.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LotteMartSession";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Lưu Token đăng nhập và trạng thái
     */
    public void saveAuthToken(String token, String name, String email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Lấy Token đăng nhập
     */
    public String getAuthToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    /**
     * Lấy tên người dùng
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "Khách");
    }

    /**
     * Kiểm tra trạng thái đăng nhập
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Đăng xuất - Xóa sạch dữ liệu phiên (nhưng giữ lại danh sách tài khoản mock)
     */
    public void logout() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * Giả lập việc đăng ký người dùng mới (Lưu vào máy)
     */
    public void registerMockUser(String email, String password, String name) {
        editor.putString("mock_pass_" + email, password);
        editor.putString("mock_name_" + email, name);
        editor.apply();
    }

    /**
     * Kiểm tra đăng nhập với danh sách giả lập
     */
    public boolean validateMockLogin(String email, String password) {
        // 1. Tài khoản Admin cố định
        if (email.equals("admin@lottemart.com") && password.equals("123456")) {
            return true;
        }

        // 2. Tài khoản vừa đăng ký
        String storedPass = pref.getString("mock_pass_" + email, null);
        return storedPass != null && storedPass.equals(password);
    }

    /**
     * Lấy tên của tài khoản mock vừa đăng nhập
     */
    public String getMockName(String email) {
        if (email.equals("admin@lottemart.com")) return "Admin Lotte";
        return pref.getString("mock_name_" + email, "Người dùng");
    }
}
