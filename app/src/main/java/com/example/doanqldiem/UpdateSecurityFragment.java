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
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import api.RetrofitClient;
import api.profileapi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateSecurityFragment extends Fragment {

    private TextInputEditText edtEmail, edtOldPassword, edtNewPassword;
    private String studentId; // Đã bỏ gán cứng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_security, container, false);

        // Lấy StudentID từ SharedPreferences
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
            studentId = prefs.getString("StudentID", "");
        }

        edtEmail = view.findViewById(R.id.edt_email);
        edtOldPassword = view.findViewById(R.id.edt_old_password);
        edtNewPassword = view.findViewById(R.id.edt_new_password);

        view.findViewById(R.id.btn_save_security).setOnClickListener(v -> updateSecurity());

        return view;
    }

    private void updateSecurity() {
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = edtEmail.getText().toString().trim();
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();

        if (email.isEmpty() && newPass.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập thông tin cần thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.updateSecurity(studentId, oldPass, newPass, email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật bảo mật thành công", Toast.LENGTH_SHORT).show();
                    edtOldPassword.setText("");
                    edtNewPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}