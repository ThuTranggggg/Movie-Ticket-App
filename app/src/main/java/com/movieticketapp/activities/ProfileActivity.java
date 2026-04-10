package com.movieticketapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.movieticketapp.R;
import com.movieticketapp.firebase.AuthRepository;
import com.movieticketapp.firebase.DataCallback;
import com.movieticketapp.firebase.FirestoreSeeder;
import com.movieticketapp.models.User;
import com.movieticketapp.utils.DateTimeUtils;
import com.movieticketapp.utils.UiUtils;

public class ProfileActivity extends AppCompatActivity {
    private final AuthRepository authRepository = new AuthRepository();
    private final FirestoreSeeder firestoreSeeder = new FirestoreSeeder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.btnMyTickets).setOnClickListener(v -> startActivity(new Intent(this, MyTicketsActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
        findViewById(R.id.btnSeedData).setOnClickListener(v -> seedData());

        loadProfile();
    }

    private void loadProfile() {
        authRepository.getCurrentUserProfile(new DataCallback<>() {
            @Override
            public void onSuccess(User data) {
                String name = data != null && data.getFullName() != null ? data.getFullName() : getString(R.string.anonymous_user);
                ((android.widget.TextView) findViewById(R.id.txtName)).setText(getString(R.string.greeting_user, name));
                ((android.widget.TextView) findViewById(R.id.txtEmail)).setText(data != null ? data.getEmail() : getString(R.string.unknown_value));
                ((android.widget.TextView) findViewById(R.id.txtPhone)).setText(getString(R.string.phone_label) + ": " + (data != null && data.getPhone() != null && !data.getPhone().trim().isEmpty() ? data.getPhone() : getString(R.string.unknown_value)));
                ((android.widget.TextView) findViewById(R.id.txtCreatedAt)).setText(getString(R.string.created_at_label) + ": " + (data != null ? DateTimeUtils.formatDate(data.getCreatedAt()) : getString(R.string.unknown_value)));
            }

            @Override
            public void onError(String message) {
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void seedData() {
        firestoreSeeder.seedDemoData(new DataCallback<>() {
            @Override
            public void onSuccess(String data) {
                UiUtils.showToast(ProfileActivity.this, data);
            }

            @Override
            public void onError(String message) {
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void logout() {
        authRepository.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
