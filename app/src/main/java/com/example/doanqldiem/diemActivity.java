package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import api.RetrofitClient;
import api.diemapi;
import model.DiemModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class diemActivity extends BaseActivity {

    private AutoCompleteTextView spinYear, spinSemester, spinnerSubject;
    private LinearLayout containerItems;
    private ProgressBar loadingProgress;
    private TextView txtTotalSubjects;
    private LinearLayout emptyState;
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

        // Các nút từ toolbar
        View imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        View thongbao = findViewById(R.id.btn_bell);
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

        TextInputLayout layoutYear = findViewById(R.id.layout_year);
        TextInputLayout layoutSemester = findViewById(R.id.layout_semester);
        TextInputLayout layoutSubject = findViewById(R.id.layout_subject);

        containerItems = findViewById(R.id.container_items);
        loadingProgress = findViewById(R.id.loading_progress);

        emptyState = findViewById(R.id.empty_state);

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
        Log.d("DIEM_ACTIVITY", "StudentID: " + studentId);
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
                } else {
                    Log.e("API_ERROR", "Metadata response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<diemapi.MetadataResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Metadata failed: " + t.getMessage());
            }
        });
    }

    private <T> void setupDropdown(AutoCompleteTextView view, List<T> items) {
        if (view != null && items != null && !items.isEmpty()) {
            ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            view.setAdapter(adapter);
        }
    }

    // ===== LOAD DỮ LIỆU ĐIỂM =====
    private void loadDiemData() {
        if (loadingProgress != null) loadingProgress.setVisibility(View.VISIBLE);

        if (txtTotalSubjects != null) txtTotalSubjects.setText("0 môn");
        if (emptyState != null) emptyState.setVisibility(View.GONE);
        if (containerItems != null) containerItems.removeAllViews();

        // Xóa các container tổng kết cũ
        LinearLayout containerSummary = findViewById(R.id.container_summary);
        if (containerSummary != null) containerSummary.removeAllViews();

        LinearLayout containerYearSummary = findViewById(R.id.container_year_summary);
        if (containerYearSummary != null) containerYearSummary.removeAllViews();

        String year = spinYear.getText().toString().trim();
        String sem = spinSemester.getText().toString().trim();
        String subName = spinnerSubject.getText().toString().trim();

        Log.d("DIEM_ACTIVITY", "Searching: Year=" + year + ", Semester=" + sem + ", Subject=" + subName);

        // ===== KIỂM TRA CÓ ĐANG LỌC THEO MÔN KHÔNG =====
        boolean isFilteringBySubject = !subName.isEmpty() && !subName.equals("Tất cả môn") && !subName.equals("Tất cả");
        // =============================================

        Integer subId = null;
        if (listSubjects != null && !subName.isEmpty() && !subName.equals("Tất cả môn") && !subName.equals("Tất cả")) {
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
                                List<DiemModel> data = response.body();

                                // Log dữ liệu nhận được
                                for (DiemModel m : data) {
                                    Log.d("DIEM_ACTIVITY", "Data: Subject=" + m.getSubjectName() +
                                            ", Semester=" + m.getSemesterName() +
                                            ", SemesterCode=" + m.getSemesterCode() +
                                            ", Year=" + m.getYear() +
                                            ", Avg=" + m.getAverageScore());
                                }

                                // Cập nhật số môn
                                if (txtTotalSubjects != null) {
                                    txtTotalSubjects.setText(data.size() + " môn");
                                }

                                // Hiển thị danh sách môn
                                int stt = 1;
                                for (DiemModel m : data) {
                                    View row = LayoutInflater.from(diemActivity.this)
                                            .inflate(R.layout.diem_item_row, containerItems, false);
                                    populateRow(row, m, stt++);
                                    containerItems.addView(row);
                                }

                                // ===== KIỂM TRA HIỂN THỊ TỔNG KẾT =====
                                // Chỉ hiển thị tổng kết khi KHÔNG lọc theo môn cụ thể
                                if (!isFilteringBySubject) {
                                    // Tính tổng kết theo từng học kỳ
                                    calculateSemesterSummaryBySemester(data);
                                    // Tính tổng kết cả năm
                                    calculateYearSummary(data);
                                } else {
                                    // Ẩn các card tổng kết khi đang lọc theo môn
                                    LinearLayout containerSummary = findViewById(R.id.container_summary);
                                    if (containerSummary != null) containerSummary.removeAllViews();

                                    LinearLayout containerYearSummary = findViewById(R.id.container_year_summary);
                                    if (containerYearSummary != null) containerYearSummary.removeAllViews();
                                }
                                // ====================================

                            } else {
                                if (txtTotalSubjects != null) txtTotalSubjects.setText("0 môn");
                                if (emptyState != null) emptyState.setVisibility(View.VISIBLE);

                                LinearLayout containerSummary = findViewById(R.id.container_summary);
                                if (containerSummary != null) containerSummary.removeAllViews();

                                LinearLayout containerYearSummary = findViewById(R.id.container_year_summary);
                                if (containerYearSummary != null) containerYearSummary.removeAllViews();

                                Toast.makeText(diemActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<DiemModel>> call, @NonNull Throwable t) {
                        if (loadingProgress != null) loadingProgress.setVisibility(View.GONE);
                        Log.e("API_ERROR", "Get diem failed: " + t.getMessage());
                        Toast.makeText(diemActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        if (txtTotalSubjects != null) txtTotalSubjects.setText("0 môn");
                        if (emptyState != null) emptyState.setVisibility(View.VISIBLE);

                        LinearLayout containerSummary = findViewById(R.id.container_summary);
                        if (containerSummary != null) containerSummary.removeAllViews();

                        LinearLayout containerYearSummary = findViewById(R.id.container_year_summary);
                        if (containerYearSummary != null) containerYearSummary.removeAllViews();
                    }
                });
    }

    // ===== TÍNH TỔNG KẾT THEO TỪNG HỌC KỲ =====
    private void calculateSemesterSummaryBySemester(List<DiemModel> data) {
        if (data == null || data.isEmpty()) {
            LinearLayout containerSummary = findViewById(R.id.container_summary);
            if (containerSummary != null) containerSummary.removeAllViews();
            return;
        }

        // Nhóm dữ liệu theo học kỳ
        Map<String, List<DiemModel>> semesterMap = new HashMap<>();
        String currentYear = "";

        for (DiemModel m : data) {
            String semester = m.getSemesterName();
            if (semester == null || semester.isEmpty()) {
                semester = m.getSemesterCode();
            }
            if (semester == null || semester.isEmpty()) {
                semester = "HK1";
            }

            if (currentYear.isEmpty()) {
                currentYear = m.getYear() != null ? m.getYear() : "2025-2026";
            }

            if (!semesterMap.containsKey(semester)) {
                semesterMap.put(semester, new ArrayList<>());
            }
            semesterMap.get(semester).add(m);
        }

        // Sắp xếp học kỳ theo thứ tự (HK1, HK2, ...)
        List<String> sortedSemesters = new ArrayList<>(semesterMap.keySet());
        Collections.sort(sortedSemesters, (s1, s2) -> {
            try {
                int num1 = Integer.parseInt(s1.replaceAll("[^0-9]", ""));
                int num2 = Integer.parseInt(s2.replaceAll("[^0-9]", ""));
                return Integer.compare(num1, num2);
            } catch (Exception e) {
                return s1.compareTo(s2);
            }
        });

        LinearLayout containerSummary = findViewById(R.id.container_summary);
        if (containerSummary == null) {
            Log.e("DIEM_ACTIVITY", "container_summary not found in layout!");
            return;
        }

        // Xóa các view cũ
        containerSummary.removeAllViews();

        // Hiển thị tổng kết cho từng học kỳ
        for (String semester : sortedSemesters) {
            List<DiemModel> semesterData = semesterMap.get(semester);
            View summaryView = createSemesterSummaryView(semester, currentYear, semesterData);
            containerSummary.addView(summaryView);
        }
    }

    // ===== TẠO VIEW TỔNG KẾT CHO TỪNG HỌC KỲ =====
    private View createSemesterSummaryView(String semesterName, String year, List<DiemModel> data) {
        View summaryView = LayoutInflater.from(this)
                .inflate(R.layout.diem_trung_binh, null, false);

        int totalSubjects = data.size();
        int passed = 0;
        int failed = 0;
        double totalAvg = 0;

        for (DiemModel m : data) {
            Double avg = m.getAverageScore();
            if (avg != null) {
                totalAvg += avg;
                if (avg >= 5.0) {
                    passed++;
                } else {
                    failed++;
                }
            }
        }

        double semesterAvg = totalSubjects > 0 ? totalAvg / totalSubjects : 0;

        // Cập nhật UI
        TextView txtSemesterSummary = summaryView.findViewById(R.id.txt_semester_summary);
        TextView txtTotalSubjectsSummary = summaryView.findViewById(R.id.txt_total_subjects_summary);
        TextView txtPassedSubjects = summaryView.findViewById(R.id.txt_passed_subjects);
        TextView txtFailedSubjects = summaryView.findViewById(R.id.txt_failed_subjects);
        TextView txtRankSummary = summaryView.findViewById(R.id.txt_rank_summary);
        TextView txtAvgSemester = summaryView.findViewById(R.id.txt_avg_semester);
        TextView txtRankLetter = summaryView.findViewById(R.id.txt_rank_letter);

        if (txtSemesterSummary != null) {
            txtSemesterSummary.setText(semesterName + " - " + year);
        }

        if (txtTotalSubjectsSummary != null) {
            txtTotalSubjectsSummary.setText(String.valueOf(totalSubjects));
        }

        if (txtPassedSubjects != null) {
            txtPassedSubjects.setText(String.valueOf(passed));
        }

        if (txtFailedSubjects != null) {
            txtFailedSubjects.setText(String.valueOf(failed));
        }

        String rank = getRankText(semesterAvg);
        if (txtRankSummary != null) txtRankSummary.setText(rank);
        if (txtRankLetter != null) txtRankLetter.setText("(" + rank + ")");

        if (txtAvgSemester != null) {
            txtAvgSemester.setText(String.format("%.1f", semesterAvg));
        }

        return summaryView;
    }

    // ===== TÍNH TỔNG KẾT CẢ NĂM HỌC =====
    private void calculateYearSummary(List<DiemModel> data) {
        if (data == null || data.isEmpty()) {
            LinearLayout containerYearSummary = findViewById(R.id.container_year_summary);
            if (containerYearSummary != null) containerYearSummary.removeAllViews();
            return;
        }

        LinearLayout containerYearSummary = findViewById(R.id.container_year_summary);
        if (containerYearSummary == null) {
            Log.e("DIEM_ACTIVITY", "container_year_summary not found in layout!");
            return;
        }

        // Xóa view cũ
        containerYearSummary.removeAllViews();

        // Tính tổng kết cả năm
        int totalSubjects = data.size();
        int totalPassed = 0;
        int totalFailed = 0;
        double totalAvgAll = 0;
        String currentYear = "";

        // Đếm số học kỳ
        Map<String, Boolean> semesterCount = new HashMap<>();

        for (DiemModel m : data) {
            Double avg = m.getAverageScore();
            if (avg != null) {
                totalAvgAll += avg;
                if (avg >= 5.0) {
                    totalPassed++;
                } else {
                    totalFailed++;
                }
            }

            if (currentYear.isEmpty()) {
                currentYear = m.getYear() != null ? m.getYear() : "2025-2026";
            }

            String semester = m.getSemesterName();
            if (semester == null || semester.isEmpty()) {
                semester = m.getSemesterCode();
            }
            if (semester != null && !semester.isEmpty()) {
                semesterCount.put(semester, true);
            }
        }

        double yearAvg = totalSubjects > 0 ? totalAvgAll / totalSubjects : 0;
        int totalSemesters = semesterCount.size();

        // Tạo view tổng kết cả năm
        View yearSummaryView = LayoutInflater.from(this)
                .inflate(R.layout.diem_tong_ket_theo_nam_hoc, null, false);

        // Cập nhật UI
        TextView txtYearSummary = yearSummaryView.findViewById(R.id.txt_year_summary);
        TextView txtAvgYearTotal = yearSummaryView.findViewById(R.id.txt_avg_year_total);
        TextView txtRankYearLetter = yearSummaryView.findViewById(R.id.txt_rank_year_letter);

        if (txtYearSummary != null) {
            txtYearSummary.setText(currentYear);
        }



        String rank = getRankText(yearAvg);
        if (txtRankYearLetter != null) txtRankYearLetter.setText("(" + rank + ")");

        if (txtAvgYearTotal != null) {
            txtAvgYearTotal.setText(String.format("%.1f", yearAvg));
        }

        // Thêm vào container
        containerYearSummary.addView(yearSummaryView);
    }

    // ===== PHƯƠNG THỨC XẾP LOẠI =====
    private String getRankText(double avg) {
        if (avg >= 9.0) return "Xuất sắc";
        if (avg >= 8.0) return "Giỏi";
        if (avg >= 6.5) return "Khá";
        if (avg >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // ===== HIỂN THỊ DỮ LIỆU TRÊN ROW =====
    private void populateRow(View v, DiemModel m, int stt) {
        try {
            TextView txtStt = v.findViewById(R.id.txt_stt);
            if (txtStt != null) txtStt.setText(String.valueOf(stt));

            TextView txtYearItem = v.findViewById(R.id.txt_year_item);
            if (txtYearItem != null) {
                String year = m.getYear() != null ? m.getYear() : "2025-2026";
                txtYearItem.setText(year);
            }

            TextView txtSemesterItem = v.findViewById(R.id.txt_semester_item);
            if (txtSemesterItem != null) {
                String semester = m.getSemesterName();
                if (semester == null || semester.isEmpty()) {
                    semester = m.getSemesterCode();
                }
                txtSemesterItem.setText(semester != null ? semester : "HK1");
            }

            TextView txtSubjectName = v.findViewById(R.id.txt_subject_name);
            if (txtSubjectName != null) txtSubjectName.setText(m.getSubjectName());

            TextView tvAvg = v.findViewById(R.id.txt_avg_year);
            double avg = m.getAverageScore() != null ? m.getAverageScore() : 0.0;
            if (tvAvg != null) {
                tvAvg.setText(String.format(Locale.US, "%.1f", avg));
                tvAvg.setTextColor(0xFFFFFFFF);

                int colorRes;
                if (avg >= 8.0) {
                    colorRes = R.color.success_green;
                } else if (avg >= 5.0) {
                    colorRes = R.color.warning_orange;
                } else {
                    colorRes = R.color.danger_red;
                }
                tvAvg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, colorRes)));
            }

            TextView txtOral = v.findViewById(R.id.txt_score_oral);
            if (txtOral != null) txtOral.setText(m.getOralScoresString());

            TextView txt15p = v.findViewById(R.id.txt_score_15m);
            if (txt15p != null) txt15p.setText(m.getQuizzesString());

            TextView txtMid = v.findViewById(R.id.txt_score_mid);
            if (txtMid != null) txtMid.setText(formatScore(m.getMidtermScore()));

            TextView txtFinal = v.findViewById(R.id.txt_score_final);
            if (txtFinal != null) txtFinal.setText(formatScore(m.getFinal_score()));

            TextView txtSemAvg = v.findViewById(R.id.txt_score_sem_avg);
            if (txtSemAvg != null) txtSemAvg.setText(formatScore(m.getAverageScore()));


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
            Log.e("DIEM_ACTIVITY", "Error populating row", e);
        }
    }

    // ===== COLLAPSE ITEM =====
    private void collapseItem(View itemView) {
        if (itemView == null) return;
        LinearLayout detail = itemView.findViewById(R.id.layout_subject_detail);
        ImageView icon = itemView.findViewById(R.id.btn_expand_icon);

        if (detail != null) detail.setVisibility(View.GONE);
        if (icon != null) icon.setRotation(0);

        if (currentlyExpanded == itemView) currentlyExpanded = null;
    }

    // ===== FORMAT ĐIỂM =====
    private String formatScore(Double d) {
        return (d != null && d >= 0) ? String.format(Locale.US, "%.1f", d) : "---";
    }
}