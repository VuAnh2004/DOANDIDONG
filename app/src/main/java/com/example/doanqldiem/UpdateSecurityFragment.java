package com.example.doanqldiem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import api.RetrofitClient;
import api.profileapi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateSecurityFragment extends Fragment {

    private TextInputEditText edtEmail, edtOldPassword, edtNewPassword;
    private MaterialButton btnSaveSecurity;
    private SwitchMaterial switchPasskey;
    private SharedPreferences sharedPreferences;
    private String studentId;
    private profileapi apiService;
    private boolean isProcessing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_security, container, false);

        apiService = RetrofitClient.getClient().create(profileapi.class);
        sharedPreferences = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
        studentId = sharedPreferences.getString("StudentID", "");

        edtEmail        = view.findViewById(R.id.edt_email);
        edtOldPassword  = view.findViewById(R.id.edt_old_password);
        edtNewPassword  = view.findViewById(R.id.edt_new_password);
        btnSaveSecurity = view.findViewById(R.id.btn_save_security);
        switchPasskey   = view.findViewById(R.id.switch_passkey);

        loadUserData();
        loadPasskeyStatus();

        btnSaveSecurity.setOnClickListener(v -> updateSecurity());
        switchPasskey.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isProcessing) return;
            if (isChecked) {
                enablePasskey();
            } else {
                disablePasskey();
            }
        });

        return view;
    }

    // ================================================================
    // LOAD DỮ LIỆU
    // ================================================================

    private void loadUserData() {
        edtEmail.setText(sharedPreferences.getString("Email", ""));
    }

    private void loadPasskeyStatus() {
        apiService.getPasskeyStatus(studentId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        boolean hasPasskey = obj.optBoolean("hasPasskey", false);

                        isProcessing = true;
                        switchPasskey.setChecked(hasPasskey);
                        isProcessing = false;

                        sharedPreferences.edit().putBoolean("hasPasskey", hasPasskey).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    // ================================================================
    // CẬP NHẬT BẢO MẬT (email + mật khẩu)
    // ================================================================

    private void updateSecurity() {
        String email       = edtEmail.getText().toString().trim();
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();

        if (email.isEmpty() && oldPassword.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập thông tin cần cập nhật", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!oldPassword.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!oldPassword.isEmpty() && newPassword.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.updateSecurity(studentId, oldPassword, newPassword, email)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "✅ Cập nhật bảo mật thành công", Toast.LENGTH_SHORT).show();
                            edtOldPassword.setText("");
                            edtNewPassword.setText("");
                            if (!email.isEmpty()) {
                                sharedPreferences.edit().putString("Email", email).apply();
                            }
                        } else {
                            try {
                                String error = response.errorBody().string();
                                if (error.contains("Mật khẩu cũ không chính xác")) {
                                    Toast.makeText(getContext(), "❌ Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                                } else if (error.contains("Email đã tồn tại")) {
                                    Toast.makeText(getContext(), "❌ Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "❌ Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "❌ Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "❌ Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================================================================
    // ĐĂNG KÝ PASSKEY (bật toggle)
    // ================================================================

    private void enablePasskey() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPromptForRegistration();
        } else {
            String msg;
            switch (canAuthenticate) {
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    msg = "❌ Thiết bị không hỗ trợ vân tay/Face ID"; break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    msg = "❌ Tính năng sinh trắc học hiện không khả dụng"; break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    msg = "⚠️ Vui lòng đăng ký vân tay trong Cài đặt thiết bị trước"; break;
                default:
                    msg = "❌ Không xác định được trạng thái sinh trắc học"; break;
            }
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            setSwitch(false);
        }
    }

    private void showBiometricPromptForRegistration() {
        Executor executor = Executors.newSingleThreadExecutor();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // ✅ Xác thực vân tay thành công → gọi API lên server
                        requireActivity().runOnUiThread(() -> callRegisterPasskeyApi());
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(),
                                    "❌ Xác thực thất bại, vui lòng thử lại",
                                    Toast.LENGTH_SHORT).show();
                            setSwitch(false);
                        });
                    }

                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        requireActivity().runOnUiThread(() -> {
                            if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                Toast.makeText(getContext(),
                                        "❌ Lỗi: " + errString,
                                        Toast.LENGTH_SHORT).show();
                            }
                            setSwitch(false);
                        });
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("🔐 Đăng ký vân tay/Face ID")
                .setSubtitle("Xác thực để đăng ký đăng nhập nhanh")
                .setDescription("Sử dụng vân tay hoặc Face ID để xác thực")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    /**
     * ✅ FIX CHÍNH: Gọi API thật lên server để lưu passkey vào DB
     * Trước đây hàm này chỉ Toast báo thành công giả, không gọi API!
     */
    private void callRegisterPasskeyApi() {
        if (studentId.isEmpty()) {
            Toast.makeText(getContext(), "❌ Không tìm thấy StudentID", Toast.LENGTH_SHORT).show();
            setSwitch(false);
            return;
        }

        // Tạo credentialId và publicKey đơn giản (định danh thiết bị)
        // Vì backend không dùng WebAuthn thật, chỉ cần lưu flag hasPasskey = true
        String credentialId = "device_" + studentId + "_" + System.currentTimeMillis();
        String publicKey    = "android_biometric_" + UUID.randomUUID().toString();

        // Gọi API đăng ký passkey lên server
        apiService.registerPasskey(studentId, credentialId, publicKey)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // ✅ Server lưu thành công
                            Toast.makeText(getContext(),
                                    "✅ Đăng ký vân tay thành công!",
                                    Toast.LENGTH_LONG).show();
                            sharedPreferences.edit().putBoolean("hasPasskey", true).apply();
                            setSwitch(true);
                        } else {
                            // ❌ Server trả lỗi
                            try {
                                String errBody = response.errorBody() != null
                                        ? response.errorBody().string() : "Không rõ lỗi";
                                Toast.makeText(getContext(),
                                        "❌ Đăng ký thất bại: " + errBody,
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(),
                                        "❌ Đăng ký thất bại",
                                        Toast.LENGTH_SHORT).show();
                            }
                            setSwitch(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "❌ Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        setSwitch(false);
                    }
                });
    }

    // ================================================================
    // HỦY PASSKEY (tắt toggle)
    // ================================================================

    private void disablePasskey() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận tắt Passkey")
                .setMessage("Bạn có chắc muốn tắt đăng nhập bằng vân tay/Face ID?")
                .setPositiveButton("Đồng ý", (dialog, which) -> callRemovePasskeyApi())
                .setNegativeButton("Hủy", (dialog, which) -> setSwitch(true))
                .show();
    }

    private void callRemovePasskeyApi() {
        apiService.removePasskey(studentId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "✅ Đã tắt Passkey", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean("hasPasskey", false).apply();
                    setSwitch(false);
                } else {
                    Toast.makeText(getContext(), "❌ Lỗi tắt Passkey", Toast.LENGTH_SHORT).show();
                    setSwitch(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(),
                        "❌ Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                setSwitch(true);
            }
        });
    }

    // ================================================================
    // HELPER: set switch không trigger listener
    // ================================================================

    private void setSwitch(boolean checked) {
        isProcessing = true;
        switchPasskey.setChecked(checked);
        isProcessing = false;
    }
}