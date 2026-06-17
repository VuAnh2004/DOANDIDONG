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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import api.RetrofitClient;
import api.loginapi;
import api.profileapi;
import model.LoginRequest;
import model.LoginResponse;
import model.ProfileResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Views
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvError, tvForgotPassword, tvDisplayUsername, tvSwitchAccount, tvGreeting;
    private LinearLayout layoutSavedUser, layoutUsernameInput;
    private ImageView ivTogglePassword;
    private CardView btnFaceId;

    // Variables
    private boolean isPasswordVisible = false;
    private String savedUsername = "";
    private String savedFullName = "";
    private boolean hasPasskey = false;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra đã đăng nhập chưa
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupInputWatchers();
        setupBiometricPrompt();

        // Kiểm tra tài khoản đã lưu
        checkRememberedUser();

        // Sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());

        // Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Chuyển tài khoản khác
        tvSwitchAccount.setOnClickListener(v -> switchToNewAccount());

        // Hiển thị/Ẩn mật khẩu
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Đăng nhập bằng Passkey (Vân tay/Face ID)
        btnFaceId.setOnClickListener(v -> handlePasskeyLogin());
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
        btnFaceId = findViewById(R.id.btn_faceid);

        // LUÔN HIỂN THỊ NÚT VÂN TAY
        btnFaceId.setVisibility(View.VISIBLE);
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

    private void setupBiometricPrompt() {
        Executor executor = Executors.newSingleThreadExecutor();

        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, " Xác thực thành công!", Toast.LENGTH_SHORT).show();
                            loginWithPasskey(savedUsername);
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "❌ Xác thực thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        runOnUiThread(() -> {
                            if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                Toast.makeText(LoginActivity.this, "❌ Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("🔐 Đăng nhập bằng vân tay/Face ID")
                .setSubtitle("Xác thực để đăng nhập nhanh")
                .setDescription("Sử dụng vân tay hoặc Face ID của bạn")
                .setNegativeButtonText("Hủy")
                .build();
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
        savedFullName = prefs.getString("FullName", "");
        hasPasskey = prefs.getBoolean("hasPasskey", false);

        android.util.Log.d("LoginActivity", "savedUsername: " + savedUsername);
        android.util.Log.d("LoginActivity", "savedFullName: " + savedFullName);
        android.util.Log.d("LoginActivity", "hasPasskey: " + hasPasskey);

        if (!savedUsername.isEmpty()) {
            // Hiển thị tài khoản đã lưu
            layoutUsernameInput.setVisibility(View.GONE);
            layoutSavedUser.setVisibility(View.VISIBLE);
            tvGreeting.setText(getGreetingMessage());

            // Hiển thị tên đầy đủ, nếu không có thì hiển thị username
            String displayName = savedFullName.isEmpty() ? savedUsername : savedFullName;
            tvDisplayUsername.setText(displayName);

            tvSwitchAccount.setVisibility(View.VISIBLE);
            etPassword.requestFocus();

            // LUÔN HIỂN THỊ NÚT VÂN TAY - KHÔNG PHỤ THUỘC hasPasskey
            btnFaceId.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);

        } else {
            // Chưa có tài khoản lưu
            layoutUsernameInput.setVisibility(View.VISIBLE);
            layoutSavedUser.setVisibility(View.GONE);
            tvSwitchAccount.setVisibility(View.GONE);
            // LUÔN HIỂN THỊ NÚT VÂN TAY
            btnFaceId.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
        }
    }

    private void switchToNewAccount() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit().clear().apply();

        savedUsername = "";
        savedFullName = "";
        hasPasskey = false;
        layoutUsernameInput.setVisibility(View.VISIBLE);
        layoutSavedUser.setVisibility(View.GONE);
        tvSwitchAccount.setVisibility(View.GONE);
        // LUÔN HIỂN THỊ NÚT VÂN TAY
        btnFaceId.setVisibility(View.VISIBLE);
        etUsername.setText("");
        etPassword.setText("");
        tvError.setVisibility(View.GONE);
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye);
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    // ==================== LOGIN METHODS ====================

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
                        fetchProfileAndCheckPasskey(loginResponse.getUser().getUsername());
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

    // Đăng nhập bằng Passkey - XỬ LÝ CẢ 2 TRƯỜNG HỢP
    private void handlePasskeyLogin() {
        // Kiểm tra thiết bị hỗ trợ sinh trắc học
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            // Thiết bị không hỗ trợ sinh trắc học
            String errorMsg = "";
            switch (canAuthenticate) {
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    errorMsg = " Thiết bị không hỗ trợ vân tay/Face ID";
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    errorMsg = " Tính năng sinh trắc học hiện không khả dụng";
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    errorMsg = "Vui lòng đăng ký vân tay/Face ID trong cài đặt thiết bị";
                    break;
                default:
                    errorMsg = " Không xác định được trạng thái sinh trắc học";
                    break;
            }
            showError(errorMsg);
            return;
        }

        // Kiểm tra nếu chưa đăng ký Passkey trên server
        if (!hasPasskey && !savedUsername.isEmpty()) {
            // Chưa đăng ký -> Hiển thị thông báo và chuyển đến đăng ký
            showError("🔐 Bạn chưa đăng ký vân tay đăng nhập. Hãy đăng ký để đăng nhập nhanh hơn!");

            // Chuyển đến Profile để đăng ký Passkey
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("openProfileFragment", true);
            intent.putExtra("openSecurityTab", true);
            intent.putExtra("studentId", savedUsername);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Nếu chưa có tài khoản lưu (người dùng đang nhập username mới)
        if (savedUsername.isEmpty()) {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                showError("Vui lòng nhập tên đăng nhập trước");
                return;
            }
            // Lưu tạm username để đăng nhập
            savedUsername = username;
        }

        // Đã đăng ký Passkey -> Hiển thị dialog xác thực
        biometricPrompt.authenticate(promptInfo);
    }

    private void loginWithPasskey(String username) {
        // Gọi API login bằng Passkey
        Toast.makeText(this, " Đăng nhập thành công với Passkey", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit()
                .putString("Username", username)
                .putBoolean("isLoggedIn", true)
                .apply();

        navigateToMain();
    }

    // ==================== FETCH PROFILE & CHECK PASSKEY ====================

    private void fetchProfileAndCheckPasskey(String studentId) {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);

        api.getProfile(studentId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getProfile() != null) {
                    String fullName = response.body().getProfile().getFullName();
                    SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                    prefs.edit().putString("FullName", fullName).apply();
                }
                checkPasskeyAndNavigate(studentId);
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                checkPasskeyAndNavigate(studentId);
            }
        });
    }

    private void checkPasskeyAndNavigate(String studentId) {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getPasskeyStatus(studentId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean hasPasskey = false;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        hasPasskey = obj.optBoolean("hasPasskey", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                prefs.edit().putBoolean("hasPasskey", hasPasskey).apply();

                if (hasPasskey) {
                    navigateToMain();
                } else {
                    navigateToProfileFragment(studentId);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                navigateToMain();
            }
        });
    }

    // ==================== NAVIGATION ====================

    private void navigateToProfileFragment(String studentId) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("openProfileFragment", true);
        intent.putExtra("openSecurityTab", true);
        intent.putExtra("studentId", studentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // ==================== ERROR HANDLING ====================

    private void processLoginError(String message, int statusCode) {
        if (message == null) message = "";
        String cleanMsg = message.trim().toLowerCase();

        boolean hasUserKeyword = cleanMsg.contains("tên đăng nhập") || cleanMsg.contains("username") || cleanMsg.contains("tài khoản");
        boolean hasPassKeyword = cleanMsg.contains("mật khẩu") || cleanMsg.contains("password");

        if (layoutSavedUser.getVisibility() == View.VISIBLE) {
            showError("Mật khẩu không chính xác. Vui lòng nhập lại");
            return;
        }

        if ((hasUserKeyword && hasPassKeyword) || (statusCode == 401 && !hasUserKeyword && !hasPassKeyword) || cleanMsg.isEmpty()) {
            showError("Sai mật khẩu và tên đăng nhập. Vui lòng kiểm tra lại");
            return;
        }

        if (hasPassKeyword) {
            showError("Mật khẩu không chính xác. Vui lòng nhập lại");
            return;
        }

        if (hasUserKeyword) {
            showError("Tên đăng nhập không chính xác hoặc không tồn tại");
            return;
        }

        showError("Đăng nhập thất bại. Vui lòng thử lại.");
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    // ==================== SAVE USER DATA ====================

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
}