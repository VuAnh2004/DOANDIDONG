package com.example.doanqldiem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;

import com.example.doanqldiem.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_security, container, false);

        // Khởi tạo API service
        apiService = RetrofitClient.getClient().create(profileapi.class);

        // Khởi tạo SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
        studentId = sharedPreferences.getString("StudentID", "");

        // Ánh xạ view
        edtEmail = view.findViewById(R.id.edt_email);
        edtOldPassword = view.findViewById(R.id.edt_old_password);
        edtNewPassword = view.findViewById(R.id.edt_new_password);
        btnSaveSecurity = view.findViewById(R.id.btn_save_security);
        switchPasskey = view.findViewById(R.id.switch_passkey);

        // Load dữ liệu
        loadUserData();
        loadPasskeyStatus();

        // Thiết lập sự kiện
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

    private void loadUserData() {
        String email = sharedPreferences.getString("Email", "");
        edtEmail.setText(email);
    }

    private void loadPasskeyStatus() {
        Call<ResponseBody> call = apiService.getPasskeyStatus(studentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Không hiển thị lỗi khi load status
            }
        });
    }

    private void updateSecurity() {
        String email = edtEmail.getText().toString().trim();
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();

        // Validate
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

        // Gọi API update
        Call<ResponseBody> call = apiService.updateSecurity(studentId, oldPassword, newPassword, email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật bảo mật thành công", Toast.LENGTH_SHORT).show();
                    edtOldPassword.setText("");
                    edtNewPassword.setText("");

                    // Cập nhật SharedPreferences
                    if (!email.isEmpty()) {
                        sharedPreferences.edit().putString("Email", email).apply();
                    }
                } else {
                    try {
                        String error = response.errorBody().string();
                        if (error.contains("Mật khẩu cũ không chính xác")) {
                            Toast.makeText(getContext(), "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                        } else if (error.contains("Email đã tồn tại")) {
                            Toast.makeText(getContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ====== PASSKEY - ĐĂNG KÝ ======
    private void enablePasskey() {
        // Kiểm tra thiết bị hỗ trợ sinh trắc học
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        switch (canAuthenticate) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Thiết bị hỗ trợ, hiển thị dialog xác thực
                showBiometricPromptForRegistration();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getContext(), "❌ Thiết bị không hỗ trợ vân tay/Face ID", Toast.LENGTH_LONG).show();
                isProcessing = true;
                switchPasskey.setChecked(false);
                isProcessing = false;
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getContext(), "❌ Tính năng sinh trắc học hiện không khả dụng", Toast.LENGTH_LONG).show();
                isProcessing = true;
                switchPasskey.setChecked(false);
                isProcessing = false;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(getContext(), "⚠️ Vui lòng đăng ký vân tay/Face ID trong cài đặt thiết bị", Toast.LENGTH_LONG).show();
                isProcessing = true;
                switchPasskey.setChecked(false);
                isProcessing = false;
                break;
            default:
                Toast.makeText(getContext(), "❌ Không xác định được trạng thái sinh trắc học", Toast.LENGTH_LONG).show();
                isProcessing = true;
                switchPasskey.setChecked(false);
                isProcessing = false;
                break;
        }
    }

    private void showBiometricPromptForRegistration() {
        Executor executor = Executors.newSingleThreadExecutor();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // Xác thực thành công -> đăng ký Passkey lên server
                        requireActivity().runOnUiThread(() -> {
                            callRegisterPasskeyApi();
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "❌ Xác thực thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            isProcessing = true;
                            switchPasskey.setChecked(false);
                            isProcessing = false;
                        });
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "❌ Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                            isProcessing = true;
                            switchPasskey.setChecked(false);
                            isProcessing = false;
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

    private void callRegisterPasskeyApi() {
        // Gọi API đăng ký Passkey
        // Tạm thời mô phỏng thành công
        Toast.makeText(getContext(), "✅ Đăng ký Passkey thành công!", Toast.LENGTH_LONG).show();

        // Lưu trạng thái đã đăng ký
        sharedPreferences.edit().putBoolean("hasPasskey", true).apply();
        isProcessing = true;
        switchPasskey.setChecked(true);
        isProcessing = false;
    }

    // ====== PASSKEY - HỦY ĐĂNG KÝ ======
    private void disablePasskey() {
        // Hiển thị dialog xác nhận hủy Passkey
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận tắt Passkey")
                .setMessage("Bạn có chắc muốn tắt đăng nhập bằng vân tay/Face ID?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    callRemovePasskeyApi();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    isProcessing = true;
                    switchPasskey.setChecked(true);
                    isProcessing = false;
                })
                .show();
    }

    private void callRemovePasskeyApi() {
        Call<ResponseBody> call = apiService.removePasskey(studentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), " Đã tắt Passkey", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean("hasPasskey", false).apply();
                    isProcessing = true;
                    switchPasskey.setChecked(false);
                    isProcessing = false;
                } else {
                    Toast.makeText(getContext(), " Lỗi tắt Passkey", Toast.LENGTH_SHORT).show();
                    isProcessing = true;
                    switchPasskey.setChecked(true);
                    isProcessing = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), " Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isProcessing = true;
                switchPasskey.setChecked(true);
                isProcessing = false;
            }
        });
    }
}