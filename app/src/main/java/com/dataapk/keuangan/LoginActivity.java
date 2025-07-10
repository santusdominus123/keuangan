package com.dataapk.keuangan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp, textViewForgotPassword;
    private CheckBox checkBoxRememberMe;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide action bar for full screen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupSharedPreferences();
        checkRememberedLogin();
        setupClickListeners();
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
    }

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void checkRememberedLogin() {
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (isRemembered) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            editTextEmail.setText(savedEmail);
            editTextPassword.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);
        }

        // Check if user is already logged in
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            navigateToMainActivity();
        }
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> performLogin());

        textViewSignUp.setOnClickListener(v -> {
            // Navigate to SignUpActivity (you can create this later)
            Toast.makeText(this, "Fitur daftar akan segera hadir!", Toast.LENGTH_SHORT).show();
        });

        textViewForgotPassword.setOnClickListener(v -> {
            // Navigate to ForgotPasswordActivity (you can create this later)
            Toast.makeText(this, "Fitur lupa password akan segera hadir!", Toast.LENGTH_SHORT).show();
        });
    }

    private void performLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            // Simple validation for demo purposes
            // You can integrate with your database or API here
            if (isValidCredentials(email, password)) {
                saveLoginState(email, password);
                showSuccessMessage();
                navigateToMainActivity();
            } else {
                showErrorMessage("Email atau password salah!");
            }
        }
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email tidak boleh kosong");
            editTextEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Format email tidak valid");
            editTextEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password tidak boleh kosong");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password minimal 6 karakter");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidCredentials(String email, String password) {
        // Demo credentials - replace with your authentication logic
        return email.equals("user@example.com") && password.equals("123456") ||
                email.equals("santus@keuangan.com") && password.equals("santus123");
    }

    private void saveLoginState(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        if (checkBoxRememberMe.isChecked()) {
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
            editor.putBoolean(KEY_REMEMBER, false);
        }

        editor.apply();
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Login berhasil! Selamat datang 💕", Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Method to clear login state - dipanggil dari MainActivity
    public static void clearLoginState(AppCompatActivity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putBoolean(KEY_REMEMBER, false);
        editor.apply();
    }
}