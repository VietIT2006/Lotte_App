package com.ptithcm.lottemart.utils;

import android.util.Patterns;
import java.util.regex.Pattern;

public class Validator {

    // Regex cho số điện thoại Việt Nam (10 số, bắt đầu bằng 0)
    private static final String PHONE_PATTERN = "^0[0-9]{9}$";

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isEmailOrPhone(String input) {
        return isValidEmail(input) || isValidPhone(input);
    }
}
