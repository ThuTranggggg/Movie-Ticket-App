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

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout layoutFullName;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmPassword;
    private TextInputEditText edtFullName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPhone;
    private TextInputEditText edtPassword;
    private TextInputEditText edtConfirmPassword;
    private View loadingOverlay;
    private View btnRegister;
    private final AuthRepository authRepository = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        layoutFullName = findViewById(R.id.layoutFullName);
        layoutEmail = findViewById(R.id.layoutRegisterEmail);
        layoutPassword = findViewById(R.id.layoutRegisterPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtRegisterEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        btnRegister = findViewById(R.id.btnRegister);

        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.txtLogin).setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        layoutFullName.setError(null);
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);

        String fullName = String.valueOf(edtFullName.getText()).trim();
        String email = String.valueOf(edtEmail.getText()).trim();
        String phone = String.valueOf(edtPhone.getText()).trim();
        String password = String.valueOf(edtPassword.getText()).trim();
        String confirmPassword = String.valueOf(edtConfirmPassword.getText()).trim();

        if (!ValidationUtils.isNotBlank(fullName)) {
            layoutFullName.setError(getString(R.string.validation_name_required));
            return;
        }
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
        if (!ValidationUtils.isNotBlank(confirmPassword)) {
            layoutConfirmPassword.setError(getString(R.string.validation_confirm_password_required));
            return;
        }
        if (!password.equals(confirmPassword)) {
            layoutConfirmPassword.setError(getString(R.string.validation_confirm_password_mismatch));
            return;
        }

        setLoading(true);
        UiUtils.hideKeyboard(this);
        authRepository.register(fullName, email, phone, password, new com.movieticketapp.firebase.DataCallback<>() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser data) {
                setLoading(false);
                UiUtils.showToast(RegisterActivity.this, getString(R.string.register_success));
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
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
        btnRegister.setEnabled(!loading);
    }
}
