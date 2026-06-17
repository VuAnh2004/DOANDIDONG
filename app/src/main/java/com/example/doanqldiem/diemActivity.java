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

    private View currentlyExpanded = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diem);

        initViews();
        handleSystemInsets();
        loadStudentId();

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchMetadata();
        loadDiemData();

        Button btnSearch = findViewById(R.id.btn_search);
        if (btnSearch != null) btnSearch.setOnClickListener(v -> loadDiemData());

        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

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
        studentId = prefs.getString("StudentID", "");
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
                                    View row = LayoutInflater.from(diemActivity.this)
                                            .inflate(R.layout.diem_item_row, containerItems, false);
                                    populateRow(row, m, stt++);
                                    containerItems.addView(row);
                                }
                            } else {
                                Toast.makeText(diemActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<DiemModel>> call, @NonNull Throwable t) {
                        if (loadingProgress != null) loadingProgress.setVisibility(View.GONE);
                        Toast.makeText(diemActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateRow(View v, DiemModel m, int stt) {
        try {
            // STT
            ((TextView) v.findViewById(R.id.txt_stt)).setText(String.valueOf(stt));

            // Học kỳ
            TextView txtHocKy = v.findViewById(R.id.txt_year_item);
            if (txtHocKy != null) {
                String hocKy = m.getSemesterCode();
                if (hocKy == null || hocKy.isEmpty()) {
                    hocKy = m.getSemesterName();
                }
                txtHocKy.setText(hocKy != null ? hocKy : "HK1");
            }

            // Tên môn
            ((TextView) v.findViewById(R.id.txt_subject_name)).setText(m.getSubjectName());

            // Điểm TB
            TextView tvAvg = v.findViewById(R.id.txt_avg_year);
            double avg = m.getAverageScore() != null ? m.getAverageScore() : 0.0;
            tvAvg.setText(String.format("%.1f", avg));
            tvAvg.setTextColor(0xFFFFFFFF);
            tvAvg.setBackgroundColor(avg >= 5.0 ? 0xFF10B981 : 0xFFEF4444);

            // ================== ICON TÍCH ==================
            ImageView imgStatus = v.findViewById(R.id.img_status_icon);
            if (imgStatus != null) {
                if (avg >= 5.0) {
                    imgStatus.setImageResource(R.drawable.icontichxanh);
                    imgStatus.setColorFilter(null);        // giữ màu gốc
                } else {
                    imgStatus.setImageResource(R.drawable.icontichdo); // hoặc tạo ic_x_do
                    imgStatus.setColorFilter(0xFFEF4444);
                }
            }

            // Chi tiết
            TextView txtSemName = v.findViewById(R.id.txt_sem_name);
            if (txtSemName != null) {
                txtSemName.setText(m.getSemesterName() != null ? m.getSemesterName() : "---");
            }

            TextView txtOral = v.findViewById(R.id.txt_score_oral);
            if (txtOral != null) txtOral.setText(m.getOralScoresString());

            TextView txt15p = v.findViewById(R.id.txt_score_15m);
            if (txt15p != null) txt15p.setText(m.getQuizzesString());

            ((TextView) v.findViewById(R.id.txt_score_mid)).setText(formatScore(m.getMidtermScore()));
            ((TextView) v.findViewById(R.id.txt_score_final)).setText(formatScore(m.getFinal_score()));
            ((TextView) v.findViewById(R.id.txt_score_sem_avg)).setText(formatScore(m.getAverageScore()));

            // Expandable
            View header = v.findViewById(R.id.layout_header_clickable);
            LinearLayout detail = v.findViewById(R.id.layout_subject_detail);
            ImageView expandIcon = v.findViewById(R.id.btn_expand_icon);

            if (detail != null) detail.setVisibility(View.GONE);
            if (expandIcon != null) expandIcon.setRotation(0);

            if (header != null) {
                header.setOnClickListener(view -> {
                    boolean isExpanded = detail != null && detail.getVisibility() == View.VISIBLE;

                    if (currentlyExpanded != null && currentlyExpanded != v) {
                        collapseItem(currentlyExpanded);
                    }

                    if (isExpanded) {
                        collapseItem(v);
                    } else {
                        if (detail != null) detail.setVisibility(View.VISIBLE);
                        if (expandIcon != null) expandIcon.setRotation(180);
                        currentlyExpanded = v;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collapseItem(View itemView) {
        if (itemView == null) return;
        LinearLayout detail = itemView.findViewById(R.id.layout_subject_detail);
        ImageView icon = itemView.findViewById(R.id.btn_expand_icon);

        if (detail != null) detail.setVisibility(View.GONE);
        if (icon != null) icon.setRotation(0);

        if (currentlyExpanded == itemView) currentlyExpanded = null;
    }

    private String formatScore(Double d) {
        return (d != null && d >= 0) ? String.format("%.1f", d) : "---";
    }
}
