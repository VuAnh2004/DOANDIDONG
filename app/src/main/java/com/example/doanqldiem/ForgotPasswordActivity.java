package com.example.doanqldiem;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import api.RetrofitClient;
import api.loginapi;
import model.ForgotPasswordRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnSubmit, btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        btnSubmit.setOnClickListener(v -> handleForgotPassword());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.loading_progress);
    }

    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email đã đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // Gửi request chỉ với Email
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        loginapi api = RetrofitClient.getClient().create(loginapi.class);
        
        api.forgotPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Thành công! Vui lòng kiểm tra email để đặt lại mật khẩu.", 
                        Toast.LENGTH_LONG).show();
                    // Có thể chuyển người dùng về màn hình đăng nhập
                    finish();
                } else {
                    String errorMsg = "Lỗi " + response.code();
                    if (response.code() == 404) {
                        errorMsg = "Email không tồn tại trong hệ thống";
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
