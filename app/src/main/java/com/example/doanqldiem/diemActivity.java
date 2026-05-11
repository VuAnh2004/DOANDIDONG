package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
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
        // 1. Kích hoạt EdgeToEdge để giao diện hiện đại
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diem);

        initViews();
        // 2. Xử lý khoảng cách thanh trạng thái (QUAN TRỌNG NHẤT)
        handleSystemInsets();

        loadStudentId();
        fetchMetadata();
        loadDiemData();

        // Nút Tìm kiếm
        Button btnSearch = findViewById(R.id.btn_search);
        if (btnSearch != null) btnSearch.setOnClickListener(v -> loadDiemData());

        // Nút Cấu hình
        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Nút Back (Logo)
        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        // Nút Thông báo
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                startActivity(new Intent(this, thongbaoActivity.class));
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

        setupSmartFilter(layoutYear, spinYear);
        setupSmartFilter(layoutSemester, spinSemester);
        setupSmartFilter(layoutSubject, spinnerSubject);
    }

    private void handleSystemInsets() {
        View mainLayout = findViewById(R.id.main_diem_layout);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // FIX: Thay số 0 bằng systemBars.top để nội dung không đè lên Status Bar
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupSmartFilter(TextInputLayout layout, AutoCompleteTextView spinner) {
        if (spinner == null || layout == null) return;
        spinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    layout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                } else {
                    layout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
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
            @Override public void onFailure(@NonNull Call<diemapi.MetadataResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Metadata failed: " + t.getMessage());
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
                        if (containerItems != null) {
                            containerItems.removeAllViews();
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                int stt = 1;
                                for (DiemModel m : response.body()) {
                                    View row = LayoutInflater.from(diemActivity.this).inflate(R.layout.diem_item_row, containerItems, false);
                                    populateRow(row, m, stt++);
                                    containerItems.addView(row);
                                }
                            } else {
                                Toast.makeText(diemActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override public void onFailure(@NonNull Call<List<DiemModel>> call, @NonNull Throwable t) {
                        if (loadingProgress != null) loadingProgress.setVisibility(View.GONE);
                    }
                });
    }

    private void populateRow(View v, DiemModel m, int stt) {
        try {
            ((TextView) v.findViewById(R.id.txt_stt)).setText(String.valueOf(stt));
            ((TextView) v.findViewById(R.id.txt_year_item)).setText(m.getSemesterCode());
            ((TextView) v.findViewById(R.id.txt_subject_name)).setText(m.getSubjectName());

            TextView tvAvg = v.findViewById(R.id.txt_avg_year);
            double avg = m.getAverageScore() != null ? m.getAverageScore() : 0.0;
            tvAvg.setText(String.valueOf(avg));
            tvAvg.setTextColor(0xFFFFFFFF); // Chữ trắng
            tvAvg.setBackgroundColor(avg >= 5.0 ? 0xFF10B981 : 0xFFEF4444);

            TextView tvStatus = v.findViewById(R.id.txt_status_icon);
            tvStatus.setText(avg >= 5.0 ? "✅" : "❌");

            View header = v.findViewById(R.id.layout_header_clickable);
            LinearLayout detail = v.findViewById(R.id.layout_subject_detail);
            TextView icon = v.findViewById(R.id.btn_expand_icon);

            header.setOnClickListener(view -> {
                boolean isVis = detail.getVisibility() == View.VISIBLE;
                detail.setVisibility(isVis ? View.GONE : View.VISIBLE);
                if (icon != null) icon.setRotation(isVis ? 0 : 180);
            });

            ((TextView) v.findViewById(R.id.txt_score_mid)).setText(formatScore(m.getMidtermScore()));
            ((TextView) v.findViewById(R.id.txt_score_final)).setText(formatScore(m.getFinal_score()));
            ((TextView) v.findViewById(R.id.txt_score_sem_avg)).setText(formatScore(m.getAverageScore()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String formatScore(Double d) {
        return (d != null && d >= 0) ? String.valueOf(d) : "---";
    }
}