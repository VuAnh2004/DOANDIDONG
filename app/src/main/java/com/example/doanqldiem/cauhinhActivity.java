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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
    private String currentUsername;

    // Khai báo SharedPreferences
    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_YEAR = "selected_year";
    private static final String KEY_SEMESTER = "selected_semester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cauhinh);

        // Lấy username từ SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        currentUsername = userPrefs.getString("Username", "");

        if (currentUsername.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                // Đang ở cài đặt rồi
                Toast.makeText(this, "Bạn đang ở trang cài đặt", Toast.LENGTH_SHORT).show();
            });
        }

        // Click Icon thông báo
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(cauhinhActivity.this, thongbaoActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Logo trường click về home
        ImageView logoTruong = findViewById(R.id.logotruong);
        if (logoTruong != null) {
            logoTruong.setOnClickListener(v -> {
                Intent intent = new Intent(cauhinhActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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

        if (!year.isEmpty()) {
            autoNamHoc.setText(year, false);
        }
        if (!semester.isEmpty()) {
            autoHocKy.setText(semester, false);
        }
    }

    private void saveLocalConfig(String year, String semester) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_YEAR, year);
        editor.putString(KEY_SEMESTER, semester);
        editor.apply();
    }

    private void fetchInitialData() {
        cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
        api.getInitialData(currentUsername).enqueue(new Callback<CauHinhResponse>() {
            @Override
            public void onResponse(@NonNull Call<CauHinhResponse> call, @NonNull Response<CauHinhResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CauHinhResponse data = response.body();

                    if (data.getOptions() != null) {
                        // Set adapter cho năm học
                        if (data.getOptions().getYears() != null && !data.getOptions().getYears().isEmpty()) {
                            ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(cauhinhActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, data.getOptions().getYears());
                            autoNamHoc.setAdapter(yearAdapter);
                        }

                        // Set adapter cho học kỳ
                        if (data.getOptions().getSemesters() != null && !data.getOptions().getSemesters().isEmpty()) {
                            ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(cauhinhActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, data.getOptions().getSemesters());
                            autoHocKy.setAdapter(semesterAdapter);
                        }
                    }
                } else {
                    // Nếu không có dữ liệu từ server, dùng dữ liệu mặc định
                    setDefaultOptions();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CauHinhResponse> call, @NonNull Throwable t) {
                // Nếu lỗi mạng, dùng dữ liệu mặc định
                setDefaultOptions();
                Toast.makeText(cauhinhActivity.this, "Không thể kết nối server, dùng dữ liệu mặc định", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultOptions() {
        // Dữ liệu mặc định
        String[] defaultYears = {"2023-2024", "2024-2025", "2025-2026"};
        String[] defaultSemesters = {"Học kỳ 1", "Học kỳ 2"};

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, defaultYears);
        autoNamHoc.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, defaultSemesters);
        autoHocKy.setAdapter(semesterAdapter);
    }

    private void handleConfiguration() {
        String year = autoNamHoc.getText().toString().trim();
        String semester = autoHocKy.getText().toString().trim();

        if (year.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn năm học!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (semester.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn học kỳ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi tên học kỳ sang mã code nếu cần
        String semesterCode = semester.contains("2") ? "HK2" : "HK1";

        // A. LƯU TẠI MÁY
        saveLocalConfig(year, semester);

        // B. GỬI BROADCAST THÔNG BÁO CHO CÁC ACTIVITY KHÁC
        sendConfigChangedBroadcast(year, semester, semesterCode);

        // C. ĐỒNG BỘ LÊN SERVER
        cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
        api.saveConfig(currentUsername, year, semesterCode).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(cauhinhActivity.this, "Cấu hình đã được lưu thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(cauhinhActivity.this, "Đã lưu tại máy (Server lỗi)", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Dù server lỗi vẫn báo thành công vì đã lưu tại máy
                Toast.makeText(cauhinhActivity.this, "Đã lưu cấu hình tại máy", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Gửi broadcast thông báo cấu hình đã thay đổi
     * Giúp các Activity khác (như thoikhoabieuActivity) reload dữ liệu
     */
    private void sendConfigChangedBroadcast(String year, String semester, String semesterCode) {
        Intent intent = new Intent("CONFIG_CHANGED");
        intent.putExtra("year", year);
        intent.putExtra("semester", semester);
        intent.putExtra("semester_code", semesterCode);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        // Log để debug
        android.util.Log.d("CAUHINH", "===== BROADCAST SENT =====");
        android.util.Log.d("CAUHINH", "Year: " + year);
        android.util.Log.d("CAUHINH", "Semester: " + semester);
        android.util.Log.d("CAUHINH", "Semester Code: " + semesterCode);
        android.util.Log.d("CAUHINH", "=========================");
    }
}