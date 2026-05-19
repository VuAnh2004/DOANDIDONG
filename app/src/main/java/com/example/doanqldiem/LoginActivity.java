package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

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

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvError, tvForgotPassword, tvDisplayUsername, tvSwitchAccount, tvGreeting;
    private LinearLayout layoutSavedUser, layoutUsernameInput;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    
    private String savedUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        checkRememberedUser();
        setupInputWatchers();

        btnLogin.setOnClickListener(v -> handleLogin());
        
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSwitchAccount.setOnClickListener(v -> switchToNewAccount());

        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            etPassword.setSelection(etPassword.getText().length());
        });
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
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
    }

    private void setupInputWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvError.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
    }

    private String getGreetingMessage() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 10) return "Chào buổi sáng,";
        if (hour < 13) return "Chào buổi trưa,";
        if (hour < 18) return "Chào buổi chiều,";
        return "Chào buổi tối,";
    }

    private void checkRememberedUser() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        savedUsername = prefs.getString("Username", "");
        String fullName = prefs.getString("FullName", "");
        
        if (!savedUsername.isEmpty()) {
            layoutUsernameInput.setVisibility(View.GONE);
            layoutSavedUser.setVisibility(View.VISIBLE);
            tvGreeting.setText(getGreetingMessage());
            tvDisplayUsername.setText(fullName.isEmpty() ? savedUsername : fullName);
            tvSwitchAccount.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
        } else {
            layoutUsernameInput.setVisibility(View.VISIBLE);
            layoutSavedUser.setVisibility(View.GONE);
            tvSwitchAccount.setVisibility(View.GONE);
        }
    }

    private void switchToNewAccount() {
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
        String username = (layoutUsernameInput.getVisibility() == View.VISIBLE) 
                ? etUsername.getText().toString().trim() 
                : savedUsername;
        
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin");
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
                        processLoginError(loginResponse.getMessage(), response.code());
                    }
                } else {
                    String errorMsg = "";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            try {
                                JSONObject jsonObject = new JSONObject(errorJson);
                                errorMsg = jsonObject.optString("message", 
                                           jsonObject.optString("Message", 
                                           jsonObject.optString("error", "")));
                            } catch (Exception e) {
                                errorMsg = errorJson;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    processLoginError(errorMsg, response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                showError("Lỗi kết nối mạng. Vui lòng thử lại.");
            }
        });
    }

    private void processLoginError(String message, int statusCode) {
        if (message == null) message = "";
        String cleanMsg = message.trim().toLowerCase();

        boolean hasUserKeyword = cleanMsg.contains("tên đăng nhập") || cleanMsg.contains("username") || cleanMsg.contains("tài khoản");
        boolean hasPassKeyword = cleanMsg.contains("mật khẩu") || cleanMsg.contains("password");

        // 1. Trường hợp sai cả hai hoặc lỗi đăng nhập chung (Mã 401 không kèm thông tin chi tiết hoặc có cả 2 từ khóa)
        if ((hasUserKeyword && hasPassKeyword) ||
            cleanMsg.contains("credentials") ||
            (statusCode == 401 && !hasUserKeyword && !hasPassKeyword) ||
            cleanMsg.isEmpty()) {
            
            if (layoutSavedUser.getVisibility() == View.VISIBLE) {
                showError("Mật khẩu không chính xác. Vui lòng nhập lại");
            } else {
                showError("Tên đăng nhập hoặc mật khẩu không chính xác vui lòng nhập lại");
            }
            return;
        }

        // 2. Trường hợp chỉ sai mật khẩu
        if (hasPassKeyword) {
            showError("Mật khẩu không chính xác. Vui lòng nhập lại");
            return;
        }

        // 3. Trường hợp chỉ sai tên đăng nhập
        if (hasUserKeyword) {
            showError("Tên đăng nhập không tồn tại");
            return;
        }

        showError("Đăng nhập thất bại. Vui lòng thử lại.");
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
