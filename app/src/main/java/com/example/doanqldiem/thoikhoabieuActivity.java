package com.example.doanqldiem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
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

    private LinearLayout containerSchedule;
    private GridLayout gridWeeks;
    private TextView txtWeekInfo, tvSemesterInfo;
    private ProgressBar progressBar;
    private Button btnViewWeek, btnViewFull;

    private int selectedWeekNumber = 1;
    private final String studentId = "24290001"; // Mã HS test
    private String currentSemesterCode = "HK1";
    private List<KhoaBieuModel.AcademicWeek> semesterWeeks = new ArrayList<>();
    private List<KhoaBieuModel> fullSemesterSchedule = new ArrayList<>();
    private boolean isFullSemesterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thoikhoabieu);

        initViews();
        setupInsets();
        setupToolbar();

        autoSyncAndLoad();

        btnViewWeek.setOnClickListener(v -> {
            isFullSemesterMode = false;
            updateTabUI();
            renderSchedule(selectedWeekNumber);
        });

        btnViewFull.setOnClickListener(v -> {
            isFullSemesterMode = true;
            updateTabUI();
            renderFullSemesterSchedule();
        });
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
        String year = prefs.getString("selected_year", "2023-2024");
        String semester = prefs.getString("selected_semester", "HK1");
        currentSemesterCode = semester.contains("2") ? "HK2" : "HK1";

        cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
        api.saveConfig(studentId, year, currentSemesterCode).enqueue(new Callback<ResponseBody>() {
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
                    if (data.studentInfo != null) {
                        tvSemesterInfo.setText(String.format("%s | Lớp: %s", data.studentInfo.semesterName, data.studentInfo.className));
                    }
                    semesterWeeks = data.weeksInSemester;
                    fullSemesterSchedule = data.schedule;
                    if (data.currentWeek != null) selectedWeekNumber = data.currentWeek.weekNumber;

                    updateTabUI();
                    setupWeekGrid();
                    renderSchedule(selectedWeekNumber);
                }
            }
            @Override
            public void onFailure(@NonNull Call<KhoaBieuModel.IndexResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(thoikhoabieuActivity.this, "Lỗi tải lịch học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupWeekGrid() {
        if (semesterWeeks == null || semesterWeeks.isEmpty()) return;
        gridWeeks.removeAllViews();
        for (KhoaBieuModel.AcademicWeek w : semesterWeeks) {
            Button btn = new Button(this, null, 0, com.google.android.material.R.style.Widget_Material3_Button_ElevatedButton);
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

    private void loadScheduleByWeek(int week) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        int semId = currentSemesterCode.equals("HK2") ? 2 : 1;

        thoikhoabieuapi api = RetrofitClient.getClient().create(thoikhoabieuapi.class);
        api.getThoiKB(week, semId, studentId).enqueue(new Callback<List<KhoaBieuModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<KhoaBieuModel>> call, @NonNull Response<List<KhoaBieuModel>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    renderScheduleData(response.body(), week);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<KhoaBieuModel>> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void renderSchedule(int weekNumber) {
        // Lọc từ dữ liệu đã load sẵn nếu có, hoặc gọi API
        renderScheduleData(null, weekNumber);
    }

    private void renderScheduleData(List<KhoaBieuModel> newData, int weekNumber) {
        containerSchedule.removeAllViews();
        KhoaBieuModel.AcademicWeek weekInfo = null;
        for (KhoaBieuModel.AcademicWeek w : semesterWeeks) {
            if (w.weekNumber == weekNumber) { weekInfo = w; break; }
        }
        if (weekInfo != null) {
            txtWeekInfo.setText(String.format("Tuần %d (%s - %s)", weekNumber, formatDateSimple(weekInfo.startDate), formatDateSimple(weekInfo.endDate)));
        }

        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();
        List<KhoaBieuModel> targetList = (newData != null) ? newData : fullSemesterSchedule;

        for (KhoaBieuModel item : targetList) {
            if (item.getWeekNumber() != null && item.getWeekNumber() == weekNumber) {
                int day = item.getDayOfWeek();
                if (!dayMap.containsKey(day)) dayMap.put(day, new ArrayList<>());
                dayMap.get(day).add(item);
            }
        }
        renderDays(dayMap, weekInfo);
    }

    private void renderFullSemesterSchedule() {
        containerSchedule.removeAllViews();
        txtWeekInfo.setText("Toàn bộ lịch học trong kỳ");
        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();
        for (KhoaBieuModel item : fullSemesterSchedule) {
            int day = item.getDayOfWeek();
            if (!dayMap.containsKey(day)) dayMap.put(day, new ArrayList<>());
            boolean exists = false;
            for (KhoaBieuModel m : dayMap.get(day)) {
                if (m.getSubjectName().equals(item.getSubjectName()) && m.getPeriod().equals(item.getPeriod())) {
                    exists = true; break;
                }
            }
            if (!exists) dayMap.get(day).add(item);
        }
        renderDays(dayMap, null);
    }

    private void renderDays(Map<Integer, List<KhoaBieuModel>> dayMap, KhoaBieuModel.AcademicWeek weekInfo) {
        int[] daysOrder = {2, 3, 4, 5, 6, 7, 1};
        for (int day : daysOrder) {
            View dayView = LayoutInflater.from(this).inflate(R.layout.item_thoikhoabieu_day, containerSchedule, false);
            TextView tvHeader = dayView.findViewById(R.id.tv_day_header);
            LinearLayout subContainer = dayView.findViewById(R.id.container_subjects);
            tvHeader.setText(calculateDayHeader(day, weekInfo));
            if (dayMap.containsKey(day)) {
                for (KhoaBieuModel m : dayMap.get(day)) {
                    View cell = LayoutInflater.from(this).inflate(R.layout.item_timetable_cell, subContainer, false);
                    ((TextView) cell.findViewById(R.id.tvSubject)).setText(m.getSubjectName());
                    ((TextView) cell.findViewById(R.id.tvTeacher)).setText(m.getTeacherName() + " | Tiết " + m.getPeriod());
                    subContainer.addView(cell);
                }
            } else if (weekInfo != null) {
                TextView tvEmpty = new TextView(this); tvEmpty.setText("Nghỉ học"); tvEmpty.setPadding(40, 20, 0, 20); subContainer.addView(tvEmpty);
            }
            if (dayMap.containsKey(day) || weekInfo != null) containerSchedule.addView(dayView);
        }
    }

    private void updateTabUI() {
        btnViewWeek.setBackgroundTintList(isFullSemesterMode ? null : ColorStateList.valueOf(0xFF1E40AF));
        btnViewWeek.setTextColor(isFullSemesterMode ? ContextCompat.getColor(this, android.R.color.darker_gray) : ContextCompat.getColor(this, android.R.color.white));
        btnViewFull.setBackgroundTintList(isFullSemesterMode ? ColorStateList.valueOf(0xFF1E40AF) : null);
        btnViewFull.setTextColor(isFullSemesterMode ? ContextCompat.getColor(this, android.R.color.white) : ContextCompat.getColor(this, android.R.color.darker_gray));
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
