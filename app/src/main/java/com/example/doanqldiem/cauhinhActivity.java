package com.example.doanqldiem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import api.RetrofitClient;
import api.cauhinhapi;
import model.CauHinhResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.button.MaterialButton;

public class cauhinhActivity extends AppCompatActivity {

    private AutoCompleteTextView autoHocKy, autoNamHoc;
    private MaterialButton btnConfirm;
    private String currentUsername = "admin";

    // Khai báo SharedPreferences
    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_YEAR = "selected_year";
    private static final String KEY_SEMESTER = "selected_semester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cauhinh);

        initViews();

        // 1. TẢI DỮ LIỆU TỪ MÁY TRƯỚC (HIỆN LÊN LUÔN)
        loadLocalConfig();

        // 2. TẢI OPTIONS TỪ SERVER (ĐỂ CHỌN)
        fetchInitialData();

        btnConfirm.setOnClickListener(v -> handleConfiguration());

        // Nút thoát
        TextView btnExit = findViewById(R.id.btnExitFA);
        if (btnExit != null) btnExit.setOnClickListener(v -> finish());

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> finish());
        //  Click Icon thong bao
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(cauhinhActivity.this, thongbaoActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void initViews() {
        autoNamHoc = findViewById(R.id.autoCompleteNamHoc);
        autoHocKy = findViewById(R.id.autoCompleteHocKy);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void loadLocalConfig() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String year = prefs.getString(KEY_YEAR, "");
        String semester = prefs.getString(KEY_SEMESTER, "");

        if (!year.isEmpty()) autoNamHoc.setText(year, false);
        if (!semester.isEmpty()) autoHocKy.setText(semester, false);
    }

    private void saveLocalConfig(String year, String semester) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_YEAR, year);
        editor.putString(KEY_SEMESTER, semester);
        editor.apply(); // Lưu vào máy
    }

    private void fetchInitialData() {
        cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
        api.getInitialData(currentUsername).enqueue(new Callback<CauHinhResponse>() {
            @Override
            public void onResponse(@NonNull Call<CauHinhResponse> call, @NonNull Response<CauHinhResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CauHinhResponse data = response.body();

                    if (data.getOptions() != null) {
                        if (data.getOptions().getYears() != null) {
                            autoNamHoc.setAdapter(new ArrayAdapter<>(cauhinhActivity.this,
                                    android.R.layout.simple_list_item_1, data.getOptions().getYears()));
                        }
                        if (data.getOptions().getSemesters() != null) {
                            autoHocKy.setAdapter(new ArrayAdapter<>(cauhinhActivity.this,
                                    android.R.layout.simple_list_item_1, data.getOptions().getSemesters()));
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<CauHinhResponse> call, @NonNull Throwable t) {}
        });
    }

    private void handleConfiguration() {
        String year = autoNamHoc.getText().toString();
        String semester = autoHocKy.getText().toString();

        if (year.isEmpty() || semester.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // A. LƯU TẠI MÁY (CHUẨN HÓA)
        saveLocalConfig(year, semester);

        // B. ĐỒNG BỘ LÊN SERVER
        cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
        api.saveConfig(currentUsername, year, semester).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Toast.makeText(cauhinhActivity.this, "Cấu hình đã được lưu tại máy và máy chủ!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Dù server lỗi vẫn báo thành công vì đã lưu tại máy
                Toast.makeText(cauhinhActivity.this, "Đã lưu tại máy (Máy chủ đang bận)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}