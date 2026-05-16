package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import api.RetrofitClient;
import api.loginapi;
import api.profileapi;
import model.LoginRequest;
import model.LoginResponse;
import model.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvError, tvForgotPassword, tvDisplayUsername, tvSwitchAccount, tvGreeting;
    private LinearLayout layoutSavedUser, layoutUsernameInput;
    
    private String savedUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Kiểm tra nếu đã đăng nhập thì vào thẳng Main
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        checkRememberedUser();

        btnLogin.setOnClickListener(v -> handleLogin());
        
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSwitchAccount.setOnClickListener(v -> switchToNewAccount());
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvError = findViewById(R.id.tv_error);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        
        tvGreeting = findViewById(R.id.tv_greeting);
        tvDisplayUsername = findViewById(R.id.tv_display_username);
        tvSwitchAccount = findViewById(R.id.tv_switch_account);
        layoutSavedUser = findViewById(R.id.layout_saved_user);
        layoutUsernameInput = findViewById(R.id.layout_username_input);
    }

    private String getGreetingMessage() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 10) return "Chào buổi sáng,";
        if (hour >= 10 && hour < 13) return "Chào buổi trưa,";
        if (hour >= 13 && hour < 18) return "Chào buổi chiều,";
        return "Chào buổi tối,";
    }

    private void checkRememberedUser() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        savedUsername = prefs.getString("Username", "");
        String fullName = prefs.getString("FullName", "");
        
        if (!savedUsername.isEmpty()) {
            // Hiển thị giao diện "Chào mừng quay trở lại" cho tài khoản này
            layoutUsernameInput.setVisibility(View.GONE);
            layoutSavedUser.setVisibility(View.VISIBLE);
            tvGreeting.setText(getGreetingMessage());
            tvDisplayUsername.setText(fullName.isEmpty() ? savedUsername : fullName);
            tvSwitchAccount.setVisibility(View.VISIBLE);
            
            // Focus vào ô mật khẩu để người dùng nhập luôn
            etPassword.requestFocus();
        } else {
            // Hiển thị giao diện nhập mới từ đầu
            layoutUsernameInput.setVisibility(View.VISIBLE);
            layoutSavedUser.setVisibility(View.GONE);
            tvSwitchAccount.setVisibility(View.GONE);
        }
    }

    private void switchToNewAccount() {
        // Xóa thông tin lưu cũ để đăng nhập tài khoản khác hoàn toàn
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        savedUsername = "";
        layoutUsernameInput.setVisibility(View.VISIBLE);
        layoutSavedUser.setVisibility(View.GONE);
        tvSwitchAccount.setVisibility(View.GONE);
        etUsername.setText("");
        etPassword.setText("");
    }

    private void handleLogin() {
        String username;
        if (layoutUsernameInput.getVisibility() == View.VISIBLE) {
            username = etUsername.getText().toString().trim();
        } else {
            username = savedUsername;
        }
        
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu");
            return;
        }

        tvError.setVisibility(View.GONE);
        btnLogin.setEnabled(false);

        loginapi api = RetrofitClient.getClient().create(loginapi.class);
        LoginRequest request = new LoginRequest(username, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        saveUserData(loginResponse);
                        fetchProfileAndNavigate(loginResponse.getUser().getUsername());
                    } else {
                        showError(loginResponse.getMessage());
                    }
                } else {
                    showError("Mật khẩu không chính xác.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                showError("Lỗi kết nối mạng");
            }
        });
    }

    private void fetchProfileAndNavigate(String studentId) {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getProfile(studentId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getProfile() != null) {
                    String fullName = response.body().getProfile().getFullName();
                    SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                    prefs.edit().putString("FullName", fullName).apply();
                }
                navigateToMain();
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                navigateToMain();
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void saveUserData(LoginResponse response) {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit()
            .putString("StudentID", response.getUser().getUsername())
            .putString("Username", response.getUser().getUsername())
            .putString("Email", response.getUser().getEmail())
            .putString("AuthToken", response.getToken())
            .putBoolean("isLoggedIn", true)
            .apply();
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
