package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
    private String currentUsername = "";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable hideErrorRunnable;
    private boolean isFirstLaunch = true;

    private boolean isBiometricSupported = false;
    private String biometricError = "";

    // ⭐ DEVICE ID
    private String deviceId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ⭐ LẤY DEVICE ID
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        initViews();
        setupInputWatchers();
        setupBiometricPrompt();
        checkBiometricSupport();

        checkRememberedUser();

        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSwitchAccount.setOnClickListener(v -> switchToNewAccount());

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnFaceId.setOnClickListener(v -> handlePasskeyLogin());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstLaunch) {
            checkSessionAndAutoLogin();
        }
        isFirstLaunch = false;
    }

    private void checkSessionAndAutoLogin() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        long lastLoginTime = prefs.getLong("lastLoginTime", 0);
        long currentTime = System.currentTimeMillis();
        long sessionTimeout = 3 * 60 * 1000;

        if (isLoggedIn && (currentTime - lastLoginTime < sessionTimeout)) {
            prefs.edit().putLong("lastLoginTime", currentTime).apply();
            navigateToMain();
        } else if (isLoggedIn) {
            prefs.edit()
                    .putBoolean("isLoggedIn", false)
                    .remove("lastLoginTime")
                    .apply();
            Toast.makeText(this, "⚠️ Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
        }
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

        btnFaceId.setVisibility(View.VISIBLE);
    }

    private void setupInputWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
    }

    private void checkBiometricSupport() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
        );

        switch (canAuthenticate) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                isBiometricSupported = true;
                biometricError = "";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                isBiometricSupported = false;
                biometricError = "📱 Thiết bị không hỗ trợ vân tay/Face ID";
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                isBiometricSupported = false;
                biometricError = "⏳ Tính năng sinh trắc học hiện không khả dụng";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                isBiometricSupported = false;
                biometricError = "⚠️ Vui lòng đăng ký vân tay/Face ID trong cài đặt thiết bị";
                break;
            default:
                isBiometricSupported = false;
                biometricError = "❌ Thiết bị không hỗ trợ vân tay/FaceID";
                break;
        }

        if (isEmulator()) {
            isBiometricSupported = false;
            biometricError = "📱 Máy ảo không hỗ trợ vân tay, vui lòng đăng nhập bằng mật khẩu";
        }

        if (!isBiometricSupported && !biometricError.isEmpty()) {
            showTemporaryMessage(biometricError, getColor(android.R.color.darker_gray));
        }
    }

    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private void setupBiometricPrompt() {
        Executor executor = Executors.newSingleThreadExecutor();

        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "✅ Xác thực thành công!", Toast.LENGTH_SHORT).show();
                            // ⭐ GỌI LOGIN PASSKEY LÊN SERVER
                            loginWithPasskey(currentUsername);
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
                .setTitle("🔐 Xác thực vân tay/Face ID")
                .setSubtitle("Đăng nhập bằng sinh trắc học")
                .setDescription("Sử dụng vân tay hoặc Face ID của bạn để đăng nhập")
                .setNegativeButtonText("Hủy")
                .build();
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
        savedFullName = prefs.getString("FullName", "");
        hasPasskey = prefs.getBoolean("hasPasskey", false);

        if (!savedUsername.isEmpty()) {
            layoutUsernameInput.setVisibility(View.GONE);
            layoutSavedUser.setVisibility(View.VISIBLE);
            tvGreeting.setText(getGreetingMessage());

            String displayName = savedFullName.isEmpty() ? savedUsername : savedFullName;
            tvDisplayUsername.setText(displayName);

            tvSwitchAccount.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
            currentUsername = savedUsername;

            if (hasPasskey && isBiometricSupported) {
                showTemporaryMessage("🔐 Sử dụng vân tay/Face ID để đăng nhập nhanh hoặc nhập mật khẩu",
                        getColor(android.R.color.holo_blue_dark));
            } else {
                showTemporaryMessage("👋 Nhập mật khẩu để đăng nhập",
                        getColor(android.R.color.darker_gray));
            }

            btnFaceId.setVisibility(View.VISIBLE);

        } else {
            layoutUsernameInput.setVisibility(View.VISIBLE);
            layoutSavedUser.setVisibility(View.GONE);
            tvSwitchAccount.setVisibility(View.GONE);
            btnFaceId.setVisibility(View.VISIBLE);
            hideError();
        }
    }

    private void showTemporaryMessage(String message, int color) {
        tvError.setText(message);
        tvError.setTextColor(color);
        tvError.setVisibility(View.VISIBLE);

        if (hideErrorRunnable != null) {
            handler.removeCallbacks(hideErrorRunnable);
        }

        hideErrorRunnable = () -> {
            tvError.setVisibility(View.GONE);
            hideErrorRunnable = null;
        };
        handler.postDelayed(hideErrorRunnable, 4000);
    }

    private void hideError() {
        if (hideErrorRunnable != null) {
            handler.removeCallbacks(hideErrorRunnable);
            hideErrorRunnable = null;
        }
        tvError.setVisibility(View.GONE);
    }

    private void switchToNewAccount() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit()
                .remove("Username")
                .remove("FullName")
                .remove("hasPasskey")
                .remove("StudentID")
                .remove("Email")
                .remove("AuthToken")
                .remove("isLoggedIn")
                .remove("lastLoginTime")
                .apply();

        savedUsername = "";
        savedFullName = "";
        hasPasskey = false;
        currentUsername = "";

        layoutUsernameInput.setVisibility(View.VISIBLE);
        layoutSavedUser.setVisibility(View.GONE);
        tvSwitchAccount.setVisibility(View.GONE);
        btnFaceId.setVisibility(View.VISIBLE);
        etUsername.setText("");
        etPassword.setText("");
        hideError();

        Toast.makeText(this, "Nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
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

    // ==================== LOGIN WITH PASSWORD ====================

    private void handleLogin() {
        String username = (layoutUsernameInput.getVisibility() == View.VISIBLE)
                ? etUsername.getText().toString().trim()
                : savedUsername;

        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập");
            return;
        }

        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu");
            return;
        }

        hideError();
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
                        // ⭐ KIỂM TRA PASSKEY SAU KHI LOGIN
                        checkPasskeyAndNavigate(loginResponse.getUser().getUsername());
                    } else {
                        String msg = loginResponse.getMessage();
                        if (msg == null || msg.isEmpty()) {
                            msg = "Đăng nhập thất bại";
                        }
                        showError(msg);
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

                    if (errorMsg.isEmpty()) {
                        errorMsg = "Mật khẩu không chính xác.";
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                showError("Lỗi kết nối mạng. Vui lòng thử lại.");
            }
        });
    }

    // ==================== PASSKEY LOGIN ====================

    private void handlePasskeyLogin() {
        String username;
        if (layoutSavedUser.getVisibility() == View.VISIBLE) {
            username = savedUsername;
        } else {
            username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                showError("Vui lòng nhập tên đăng nhập trước");
                return;
            }
        }
        currentUsername = username;

        if (!isBiometricSupported) {
            if (biometricError.isEmpty()) {
                biometricError = "❌ Thiết bị không hỗ trợ đăng nhập bằng vân tay/Face ID";
            }
            showError(biometricError);

            if (biometricError.contains("đăng ký")) {
                showBiometricEnrollmentDialog();
            }
            return;
        }

        // ⭐ KIỂM TRA USER TỒN TẠI
        checkUserExists(username);
    }

    private void showBiometricEnrollmentDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("⚠️ Chưa đăng ký vân tay/Face ID")
                .setMessage("Vui lòng đăng ký vân tay hoặc Face ID trong cài đặt thiết bị của bạn.\n\n" +
                        "Sau khi đăng ký, quay lại ứng dụng để sử dụng tính năng này.\n\n" +
                        "Bạn có thể đăng nhập bằng mật khẩu ngay bây giờ.")
                .setPositiveButton("Đăng nhập bằng mật khẩu", (dialog, which) -> {
                    etPassword.requestFocus();
                })
                .setNegativeButton("Mở cài đặt", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS);
                    startActivity(intent);
                })
                .setNeutralButton("Đóng", null)
                .show();
    }

    private void checkUserExists(String username) {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getProfile(username).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getProfile() != null) {
                    runOnUiThread(() -> {
                        biometricPrompt.authenticate(promptInfo);
                    });
                } else {
                    runOnUiThread(() -> {
                        showError("❌ Tài khoản không tồn tại");
                    });
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    showError("❌ Lỗi kết nối, vui lòng thử lại");
                });
            }
        });
    }

    // ⭐⭐ LOGIN BẰNG PASSKEY ⭐⭐
    private void loginWithPasskey(String username) {
        btnLogin.setEnabled(false);
        showTemporaryMessage("⏳ Đang xác thực Passkey...", getColor(android.R.color.holo_blue_dark));

        loginapi api = RetrofitClient.getClient().create(loginapi.class);
        Call<LoginResponse> call = api.loginWithPasskey(username, deviceId);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                hideError();

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        Toast.makeText(LoginActivity.this, "✅ Đăng nhập Passkey thành công!", Toast.LENGTH_SHORT).show();

                        // ⭐ LƯU USER DATA + HAS PASSKEY = TRUE
                        saveUserData(loginResponse);
                        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                        prefs.edit().putBoolean("hasPasskey", true).apply();

                        // ⭐ LẤY FULLNAME TỪ API PROFILE
                        fetchFullNameAndNavigate(loginResponse.getUser().getUsername());
                    } else {
                        String msg = loginResponse.getMessage();
                        if (msg == null || msg.isEmpty()) {
                            msg = "❌ Passkey không hợp lệ hoặc đã bị xóa.\nVui lòng đăng nhập bằng mật khẩu.";
                        }
                        showError(msg);
                        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                        prefs.edit().putBoolean("hasPasskey", false).apply();
                        hasPasskey = false;
                    }
                } else {
                    String errorMsg = "";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            try {
                                JSONObject jsonObject = new JSONObject(errorJson);
                                errorMsg = jsonObject.optString("message", jsonObject.optString("Message", ""));
                            } catch (Exception e) {
                                errorMsg = errorJson;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (errorMsg.isEmpty()) {
                        errorMsg = "❌ Đăng nhập Passkey thất bại.\nVui lòng đăng nhập bằng mật khẩu.";
                    }
                    showError(errorMsg);
                    SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                    prefs.edit().putBoolean("hasPasskey", false).apply();
                    hasPasskey = false;
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                showError("❌ Lỗi kết nối: " + t.getMessage() + "\nVui lòng đăng nhập bằng mật khẩu.");
            }
        });
    }

    // ⭐ LẤY FULLNAME SAU KHI LOGIN PASSKEY THÀNH CÔNG
    private void fetchFullNameAndNavigate(String studentId) {
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

    // ==================== KIỂM TRA PASSKEY SAU KHI LOGIN BẰNG MẬT KHẨU ====================

    private void checkPasskeyAndNavigate(String studentId) {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.checkPasskeyForDevice(studentId, deviceId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean hasPasskeyOnServer = false;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        hasPasskeyOnServer = obj.optBoolean("hasPasskey", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean finalHasPasskey = hasPasskeyOnServer;
                runOnUiThread(() -> {
                    SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                    prefs.edit().putBoolean("hasPasskey", finalHasPasskey).apply();
                    hasPasskey = finalHasPasskey;

                    // ⭐ LẤY FULLNAME
                    fetchFullNameAndNavigate(studentId);
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> {
                    navigateToMain();
                });
            }
        });
    }

    // ==================== NAVIGATION ====================

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // ==================== ERROR HANDLING ====================

    private void showError(String message) {
        tvError.setText(message);
        tvError.setTextColor(getColor(android.R.color.holo_red_dark));
        tvError.setVisibility(View.VISIBLE);

        if (hideErrorRunnable != null) {
            handler.removeCallbacks(hideErrorRunnable);
        }
        hideErrorRunnable = () -> {
            tvError.setVisibility(View.GONE);
            hideErrorRunnable = null;
        };
        handler.postDelayed(hideErrorRunnable, 6000);
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
                .putLong("lastLoginTime", System.currentTimeMillis())
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && hideErrorRunnable != null) {
            handler.removeCallbacks(hideErrorRunnable);
        }
    }
}