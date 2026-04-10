package com.movieticketapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.movieticketapp.R;
import com.movieticketapp.firebase.AuthRepository;
import com.movieticketapp.utils.UiUtils;
import com.movieticketapp.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private View loadingOverlay;
    private View btnLogin;
    private final AuthRepository authRepository = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        btnLogin = findViewById(R.id.btnLogin);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.txtRegister).setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        layoutEmail.setError(null);
        layoutPassword.setError(null);

        String email = String.valueOf(edtEmail.getText()).trim();
        String password = String.valueOf(edtPassword.getText()).trim();

        if (!ValidationUtils.isNotBlank(email)) {
            layoutEmail.setError(getString(R.string.validation_email_required));
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            layoutEmail.setError(getString(R.string.validation_email_invalid));
            return;
        }
        if (!ValidationUtils.isNotBlank(password)) {
            layoutPassword.setError(getString(R.string.validation_password_required));
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            layoutPassword.setError(getString(R.string.validation_password_length));
            return;
        }

        setLoading(true);
        UiUtils.hideKeyboard(this);
        authRepository.login(email, password, new com.movieticketapp.firebase.DataCallback<>() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser data) {
                setLoading(false);
                UiUtils.showToast(LoginActivity.this, getString(R.string.login_success));
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void setLoading(boolean loading) {
        loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}
