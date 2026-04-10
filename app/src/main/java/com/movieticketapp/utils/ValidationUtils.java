package com.movieticketapp.utils;

import android.text.TextUtils;
import android.util.Patterns;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.trim().length() >= 6;
    }

    public static boolean isNotBlank(String value) {
        return !TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim());
    }
}
