package com.movieticketapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.movieticketapp.R;
import com.movieticketapp.firebase.AuthRepository;
import com.movieticketapp.notifications.NotificationHelper;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        NotificationHelper.createNotificationChannel(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AuthRepository authRepository = new AuthRepository();
            Intent intent = authRepository.getCurrentFirebaseUser() == null
                    ? new Intent(this, LoginActivity.class)
                    : new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1400);
    }
}
