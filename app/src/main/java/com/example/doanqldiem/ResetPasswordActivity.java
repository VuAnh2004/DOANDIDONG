package com.example.doanqldiem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import api.RetrofitClient;
import api.loginapi;
import model.ResetPasswordRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etToken, etNewPassword, etConfirmPassword;
    private Button btnReset;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();

        btnReset.setOnClickListener(v -> handleResetPassword());
    }

    private void initViews() {
        etToken = findViewById(R.id.et_token);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnReset = findViewById(R.id.btn_reset);
        progressBar = findViewById(R.id.loading_progress);
    }

    private void handleResetPassword() {
        String token = etToken.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnReset.setEnabled(false);

        ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword);
        loginapi api = RetrofitClient.getClient().create(loginapi.class);

        api.resetPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnReset.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                    finish(); // Quay lại màn hình đăng nhập
                } else {
                    String error = "Lỗi xác thực hoặc Token hết hạn";
                    Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnReset.setEnabled(true);
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}