package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import api.RetrofitClient;
import api.diemapi;
import model.DiemModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class diemActivity extends AppCompatActivity {

    private AutoCompleteTextView spinYear, spinSemester, spinnerSubject;
    private TextInputLayout layoutYear, layoutSemester, layoutSubject;
    private LinearLayout containerItems;
    private ProgressBar loadingProgress;
    private String studentId;
    private List<DiemModel.SubjectOption> listSubjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diem);

        initViews();
        handleSystemInsets();
        loadStudentId();

        // 1. Tải danh sách cho các Dropdown
        fetchMetadata();

        // 2. Thiết lập logic nút X và Mũi tên sổ xuống
        setupSmartFilter(layoutYear, spinYear);
        setupSmartFilter(layoutSemester, spinSemester);
        setupSmartFilter(layoutSubject, spinnerSubject);

        // 3. Tải dữ liệu mặc định khi vào trang
        loadDiemData();

        // 4. Sự kiện nút Tìm kiếm
        findViewById(R.id.btn_search).setOnClickListener(v -> loadDiemData());

        // 5. Nút Cấu hình
        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // 6. Nút Back (Logo)
        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        //  Click Icon thong bao
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(diemActivity.this, thongbaoActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void initViews() {
        spinYear = findViewById(R.id.spinner_year);
        spinSemester = findViewById(R.id.spinner_semester);
        spinnerSubject = findViewById(R.id.spinner_subject);

        layoutYear = findViewById(R.id.layout_year);
        layoutSemester = findViewById(R.id.layout_semester);
        layoutSubject = findViewById(R.id.layout_subject);

        containerItems = findViewById(R.id.container_items);
        loadingProgress = findViewById(R.id.loading_progress);
    }

    private void setupSmartFilter(TextInputLayout layout, AutoCompleteTextView spinner) {
        spinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Có dữ liệu: Hiện nút X để xóa
                    layout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                    layout.setEndIconOnClickListener(v -> {
                        spinner.setText(null, false);
                        layout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                        loadDiemData(); // Tải lại toàn bộ dữ liệu khi xóa bộ lọc
                    });
                } else {
                    // Trống: Hiện lại mũi tên sổ xuống
                    layout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Tự động tìm kiếm ngay khi chọn item trong danh sách sổ xuống
        spinner.setOnItemClickListener((parent, view, position, id) -> loadDiemData());
    }

    private void loadStudentId() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "24290001");
    }

    private void fetchMetadata() {
        diemapi api = RetrofitClient.getClient().create(diemapi.class);
        api.getMetadata().enqueue(new Callback<diemapi.MetadataResponse>() {
            @Override
            public void onResponse(@NonNull Call<diemapi.MetadataResponse> call, @NonNull Response<diemapi.MetadataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupDropdown(spinYear, response.body().years);
                    setupDropdown(spinSemester, response.body().semesters);
                    listSubjects = response.body().subjects;
                    setupDropdown(spinnerSubject, listSubjects);
                }
            }
            @Override
            public void onFailure(@NonNull Call<diemapi.MetadataResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi tải metadata: " + t.getMessage());
            }
        });
    }

    private <T> void setupDropdown(AutoCompleteTextView view, List<T> items) {
        if (view != null && items != null) {
            ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            view.setAdapter(adapter);
        }
    }

    private void loadDiemData() {
        if (loadingProgress != null) loadingProgress.setVisibility(View.VISIBLE);

        String year = spinYear.getText().toString().trim();
        String sem = spinSemester.getText().toString().trim();
        String subName = spinnerSubject.getText().toString().trim();

        Integer subId = null;
        if (listSubjects != null) {
            for (DiemModel.SubjectOption s : listSubjects) {
                if (s.SubjectName != null && s.SubjectName.equals(subName)) {
                    subId = s.SubjectID;
                    break;
                }
            }
        }

        diemapi api = RetrofitClient.getClient().create(diemapi.class);
        api.getDiem(studentId, year.isEmpty() ? null : year, sem.isEmpty() ? null : sem, subId)
                .enqueue(new Callback<List<DiemModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<DiemModel>> call, @NonNull Response<List<DiemModel>> response) {
                        if (loadingProgress != null) loadingProgress.setVisibility(View.GONE);
                        containerItems.removeAllViews();

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            int stt = 1;
                            for (DiemModel m : response.body()) {
                                View row = LayoutInflater.from(diemActivity.this).inflate(R.layout.diem_item_row, containerItems, false);
                                populateRow(row, m, stt++);
                                containerItems.addView(row);
                            }
                        } else {
                            Toast.makeText(diemActivity.this, "Không tìm thấy kết quả phù hợp", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<DiemModel>> call, @NonNull Throwable t) {
                        if (loadingProgress != null) loadingProgress.setVisibility(View.GONE);
                        Log.e("API_ERROR", "Lỗi tải điểm: " + t.getMessage());
                    }
                });
    }

    private void populateRow(View v, DiemModel m, int stt) {
        try {
            ((TextView) v.findViewById(R.id.txt_stt)).setText(String.valueOf(stt));
            ((TextView) v.findViewById(R.id.txt_year_item)).setText(m.getSemesterCode() != null ? m.getSemesterCode() : "N/A");
            ((TextView) v.findViewById(R.id.txt_subject_name)).setText(m.getSubjectName());

            TextView tvAvg = v.findViewById(R.id.txt_avg_year);
            double avg = m.getAverageScore() != null ? m.getAverageScore() : 0.0;
            tvAvg.setText(String.valueOf(avg));

            // Màu sắc dựa trên điểm số
            tvAvg.setBackgroundColor(avg >= 5.0 ? 0xFF10B981 : 0xFFEF4444);

            TextView tvStatus = v.findViewById(R.id.txt_status_icon);
            tvStatus.setText(avg >= 5.0 ? "✅" : "❌");

            // Phần mở rộng chi tiết
            final LinearLayout detail = v.findViewById(R.id.layout_subject_detail);
            final TextView icon = v.findViewById(R.id.btn_expand_icon);
            v.findViewById(R.id.layout_header_clickable).setOnClickListener(view -> {
                boolean isVisible = detail.getVisibility() == View.VISIBLE;
                detail.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                if (icon != null) icon.setRotation(isVisible ? 0 : 180);
            });

            // Đổ các loại điểm thành phần
            ((TextView) v.findViewById(R.id.txt_score_mid)).setText(formatScore(m.getMidtermScore()));
            ((TextView) v.findViewById(R.id.txt_score_final)).setText(formatScore(m.getFinal_score()));
            ((TextView) v.findViewById(R.id.txt_score_sem_avg)).setText(formatScore(m.getAverageScore()));

        } catch (Exception e) {
            Log.e("UI_ERROR", "Lỗi đổ dữ liệu dòng: " + e.getMessage());
        }
    }

    private String formatScore(Double d) {
        return (d != null && d >= 0) ? String.valueOf(d) : "---";
    }

    private void handleSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_diem_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}