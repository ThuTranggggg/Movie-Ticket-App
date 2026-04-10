package com.movieticketapp.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public final class UiUtils {

    private UiUtils() {
    }

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnack(View anchor, String message) {
        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(anchor.getContext(), com.movieticketapp.R.color.color_surface_alt));
        snackbar.setTextColor(ContextCompat.getColor(anchor.getContext(), com.movieticketapp.R.color.color_text_primary));
        snackbar.show();
    }

    public static void toggleView(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = ContextCompat.getSystemService(activity, InputMethodManager.class);
        View currentFocus = activity.getCurrentFocus();
        if (inputMethodManager != null && currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}
