package com.example.doanqldiem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import api.RetrofitClient;
import api.cauhinhapi;
import api.thoikhoabieuapi;
import model.KhoaBieuModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class thoikhoabieuActivity extends AppCompatActivity {

    private static final String TAG = "TKB_LOG";
    private LinearLayout containerSchedule;
    private GridLayout gridWeeks;
    private TextView txtWeekInfo, tvSemesterInfo;
    private ProgressBar progressBar;
    private Button btnViewWeek, btnViewFull;

    private int selectedWeekNumber = 1;
    private String studentId; // Đã bỏ gán cứng
    private String currentYear = "";
    private String currentSemesterCode = "";
    private List<KhoaBieuModel.AcademicWeek> semesterWeeks = new ArrayList<>();
    private List<KhoaBieuModel> fullSemesterSchedule = new ArrayList<>();
    private boolean isFullSemesterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.thoikhoabieu);

        // Lấy StudentID từ SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = userPrefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupInsets();
        setupToolbar();

        // 1. Tự động đồng bộ và tải dữ liệu
        autoSyncAndLoad();

        setupListeners();
    }

    private void initViews() {
        containerSchedule = findViewById(R.id.container_schedule);
        gridWeeks = findViewById(R.id.grid_weeks);
        txtWeekInfo = findViewById(R.id.txt_week_info);
        tvSemesterInfo = findViewById(R.id.tv_semester_info);
        progressBar = findViewById(R.id.loading_progress);
        btnViewWeek = findViewById(R.id.btn_view_week);
        btnViewFull = findViewById(R.id.btn_view_full);
    }

    private void setupListeners() {
        if (btnViewWeek != null) {
            btnViewWeek.setOnClickListener(v -> {
                isFullSemesterMode = false;
                updateTabUI();
                renderSchedule(selectedWeekNumber);
            });
        }

        if (btnViewFull != null) {
            btnViewFull.setOnClickListener(v -> {
                isFullSemesterMode = true;
                updateTabUI();
                renderFullSemesterSchedule();
            });
        }
    }

    private void setupInsets() {
        View main = findViewById(R.id.main_thoikhoabieu_layout);
        if (main != null) {
            ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
            });
        }
    }

    private void autoSyncAndLoad() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        currentYear = prefs.getString("selected_year", "2023-2024");
        String semesterDisplay = prefs.getString("selected_semester", "Học kỳ 1");
        
        // Hiện thông tin cấu hình lên Header ngay lập tức
        if (tvSemesterInfo != null) {
            tvSemesterInfo.setText(String.format("Năm học: %s | %s", currentYear, semesterDisplay));
        }

        // Chuẩn hóa sang mã code (HK1/HK2) để server nhận diện
        currentSemesterCode = (semesterDisplay != null && semesterDisplay.contains("2")) ? "HK2" : "HK1";

        cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
        api.saveConfig(studentId, currentYear, currentSemesterCode).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                fetchInitialData();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fetchInitialData();
            }
        });
    }

    private void fetchInitialData() {
        thoikhoabieuapi api = RetrofitClient.getClient().create(thoikhoabieuapi.class);
        api.getIndex(studentId, currentSemesterCode).enqueue(new Callback<KhoaBieuModel.IndexResponse>() {
            @Override
            public void onResponse(@NonNull Call<KhoaBieuModel.IndexResponse> call, @NonNull Response<KhoaBieuModel.IndexResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    KhoaBieuModel.IndexResponse data = response.body();
                    
                    if (data.studentInfo != null && tvSemesterInfo != null) {
                        tvSemesterInfo.setText(String.format("%s | Lớp: %s", data.studentInfo.semesterName, data.studentInfo.className));
                    }
                    if (data.weeksInSemester != null) semesterWeeks = data.weeksInSemester;
                    if (data.schedule != null) fullSemesterSchedule = data.schedule;
                    
                    if (data.currentWeek != null) selectedWeekNumber = data.currentWeek.weekNumber;
                    else if (!semesterWeeks.isEmpty()) selectedWeekNumber = semesterWeeks.get(0).weekNumber;

                    updateTabUI();
                    setupWeekGrid();
                    renderSchedule(selectedWeekNumber);
                }
            }
            @Override
            public void onFailure(@NonNull Call<KhoaBieuModel.IndexResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(thoikhoabieuActivity.this, "Lỗi kết nối lịch học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupWeekGrid() {
        if (semesterWeeks == null || semesterWeeks.isEmpty() || gridWeeks == null) return;
        gridWeeks.removeAllViews();
        for (KhoaBieuModel.AcademicWeek w : semesterWeeks) {
            Button btn = new Button(this, null, com.google.android.material.R.attr.materialButtonStyle);
            btn.setText(String.valueOf(w.weekNumber));
            btn.setPadding(0, 0, 0, 0);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; params.height = 110;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4);
            btn.setLayoutParams(params);

            if (w.weekNumber == selectedWeekNumber) {
                btn.setBackgroundTintList(ColorStateList.valueOf(0xFFEA580C));
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            } else if (w.isCurrentWeek) {
                btn.setBackgroundTintList(ColorStateList.valueOf(0xFFFFEDD5));
                btn.setTextColor(ColorStateList.valueOf(0xFFEA580C));
            } else {
                btn.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
                btn.setTextColor(ColorStateList.valueOf(0xFF374151));
            }

            btn.setOnClickListener(v -> {
                selectedWeekNumber = w.weekNumber;
                isFullSemesterMode = false;
                setupWeekGrid();
                updateTabUI();
                renderSchedule(selectedWeekNumber);
            });
            gridWeeks.addView(btn);
        }
    }

    private void renderSchedule(int weekNumber) {
        if (containerSchedule == null) return;
        containerSchedule.removeAllViews();
        KhoaBieuModel.AcademicWeek weekInfo = null;
        for (KhoaBieuModel.AcademicWeek w : semesterWeeks) {
            if (w.weekNumber == weekNumber) { weekInfo = w; break; }
        }
        if (weekInfo != null && txtWeekInfo != null) {
            txtWeekInfo.setText(String.format("Tuần %d (%s - %s)", weekNumber, formatDateSimple(weekInfo.startDate), formatDateSimple(weekInfo.endDate)));
        }

        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();
        if (fullSemesterSchedule != null) {
            for (KhoaBieuModel item : fullSemesterSchedule) {
                if (item != null && item.getWeekNumber() != null && item.getWeekNumber() == weekNumber) {
                    int day = item.getDayOfWeek();
                    if (!dayMap.containsKey(day)) dayMap.put(day, new ArrayList<>());
                    dayMap.get(day).add(item);
                }
            }
        }
        renderDays(dayMap, weekInfo);
    }

    private void renderFullSemesterSchedule() {
        if (containerSchedule == null) return;
        containerSchedule.removeAllViews();
        if (txtWeekInfo != null) txtWeekInfo.setText("Toàn bộ lịch học trong kỳ");
        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();
        if (fullSemesterSchedule != null) {
            for (KhoaBieuModel item : fullSemesterSchedule) {
                int day = item.getDayOfWeek();
                if (!dayMap.containsKey(day)) dayMap.put(day, new ArrayList<>());
                boolean exists = false;
                for (KhoaBieuModel m : dayMap.get(day)) {
                    if (m.getSubjectName() != null && m.getSubjectName().equals(item.getSubjectName()) && m.getPeriod().equals(item.getPeriod())) {
                        exists = true; break;
                    }
                }
                if (!exists) dayMap.get(day).add(item);
            }
        }
        renderDays(dayMap, null);
    }

    private void renderDays(Map<Integer, List<KhoaBieuModel>> dayMap, KhoaBieuModel.AcademicWeek weekInfo) {
        int[] daysOrder = {2, 3, 4, 5, 6, 7, 1};
        for (int day : daysOrder) {
            View dayView = LayoutInflater.from(this).inflate(R.layout.item_thoikhoabieu_day, containerSchedule, false);
            TextView tvHeader = dayView.findViewById(R.id.tv_day_header);
            LinearLayout morningContainer = dayView.findViewById(R.id.layout_morning_periods);
            LinearLayout afternoonContainer = dayView.findViewById(R.id.layout_afternoon_periods);

            if (tvHeader != null) tvHeader.setText(calculateDayHeader(day, weekInfo));

            Map<Integer, KhoaBieuModel> periodMap = new HashMap<>();
            if (dayMap.containsKey(day)) {
                for (KhoaBieuModel m : dayMap.get(day)) {
                    periodMap.put(m.getPeriod(), m);
                }
            }

            for (int p = 1; p <= 5; p++) addPeriodRow(morningContainer, p, periodMap.get(p));
            for (int p = 6; p <= 10; p++) addPeriodRow(afternoonContainer, p, periodMap.get(p));

            if (dayMap.containsKey(day) || weekInfo != null) containerSchedule.addView(dayView);
        }
    }

    private void addPeriodRow(LinearLayout container, int periodNum, KhoaBieuModel model) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_timetable_row, container, false);
        TextView tvNum = row.findViewById(R.id.tv_period_num);
        TextView tvInfo = row.findViewById(R.id.tv_subject_info);

        if (tvNum != null) tvNum.setText(String.valueOf(periodNum));
        if (tvInfo != null) {
            if (model != null) tvInfo.setText(model.getSubjectName() + " - " + model.getTeacherName());
            else tvInfo.setText("");
        }
        container.addView(row);
    }

    private void updateTabUI() {
        if (btnViewWeek == null || btnViewFull == null) return;
        if (!isFullSemesterMode) {
            btnViewWeek.setBackgroundTintList(ColorStateList.valueOf(0xFF1E40AF));
            btnViewWeek.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnViewFull.setBackgroundTintList(ColorStateList.valueOf(0xFFE5E7EB));
            btnViewFull.setTextColor(ColorStateList.valueOf(0xFF4B5563));
        } else {
            btnViewFull.setBackgroundTintList(ColorStateList.valueOf(0xFF1E40AF));
            btnViewFull.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnViewWeek.setBackgroundTintList(ColorStateList.valueOf(0xFFE5E7EB));
            btnViewWeek.setTextColor(ColorStateList.valueOf(0xFF4B5563));
        }
    }

    private String calculateDayHeader(int dayOfWeek, KhoaBieuModel.AcademicWeek week) {
        String name = (dayOfWeek == 1) ? "Chủ nhật" : "Thứ " + dayOfWeek;
        if (week == null || week.startDate == null) return name;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(week.startDate.split("T")[0]));
            int offset = (dayOfWeek == 1) ? 6 : (dayOfWeek - 2);
            cal.add(Calendar.DATE, offset);
            return name + " (" + new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime()) + ")";
        } catch (Exception e) { return name; }
    }

    private String formatDateSimple(String dateStr) {
        if (dateStr == null) return "";
        try { return dateStr.split("T")[0].split("-")[2] + "/" + dateStr.split("T")[0].split("-")[1]; } catch (Exception e) { return dateStr; }
    }
}