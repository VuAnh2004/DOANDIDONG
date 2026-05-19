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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import api.RetrofitClient;
import api.thoikhoabieuapi;
import model.KhoaBieuModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class thoikhoabieuActivity extends AppCompatActivity {

    private LinearLayout containerSchedule;
    private GridLayout gridWeeks;
    private ProgressBar progressBar;
    private Button btnViewWeek, btnViewFull;

    private int selectedWeekNumber = 31; // Mặc định tuần 31 theo ảnh mẫu
    private String studentId = "24290001";
    private String currentSemesterCode = "HK1";
    private List<KhoaBieuModel.AcademicWeek> semesterWeeks = new ArrayList<>();
    private List<KhoaBieuModel> fullSemesterSchedule = new ArrayList<>();
    private boolean isFullSemesterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.thoikhoabieu);

        initViews();
        setupInsets();
        setupToolbar();
        loadUserConfig();
        fetchInitialData();
        setupListeners();
    }

    private void initViews() {
        containerSchedule = findViewById(R.id.container_schedule);
        gridWeeks = findViewById(R.id.grid_weeks);
        progressBar = findViewById(R.id.loading_progress);
        btnViewWeek = findViewById(R.id.btn_view_week);
        btnViewFull = findViewById(R.id.btn_view_full);
    }

    private void loadUserConfig() {
        SharedPreferences userPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = userPrefs.getString("StudentID", "24290001");

        SharedPreferences configPrefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        String semDisplay = configPrefs.getString("selected_semester", "Học kỳ 1");
        currentSemesterCode = (semDisplay != null && semDisplay.contains("2")) ? "HK2" : "HK1";
    }

    private void setupListeners() {
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

    private void setupInsets() {
        View main = findViewById(R.id.main_thoikhoabieu_layout);
        if (main != null) {
            ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupToolbar() {
        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        View btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> startActivity(new Intent(this, cauhinhActivity.class)));
    }

    private void fetchInitialData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        thoikhoabieuapi api = RetrofitClient.getClient().create(thoikhoabieuapi.class);
        
        api.getIndex(studentId, currentSemesterCode).enqueue(new Callback<KhoaBieuModel.IndexResponse>() {
            @Override
            public void onResponse(@NonNull Call<KhoaBieuModel.IndexResponse> call, @NonNull Response<KhoaBieuModel.IndexResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    semesterWeeks = response.body().weeksInSemester;
                    fullSemesterSchedule = response.body().schedule;
                    if (response.body().currentWeek != null) {
                        selectedWeekNumber = response.body().currentWeek.weekNumber;
                    }
                }
                setupWeekGrid();
                renderSchedule(selectedWeekNumber);
                updateTabUI();
            }

            @Override
            public void onFailure(@NonNull Call<KhoaBieuModel.IndexResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                setupWeekGrid();
                renderSchedule(selectedWeekNumber);
            }
        });
    }

    private void setupWeekGrid() {
        if (gridWeeks == null) return;
        gridWeeks.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        
        for (int i = 1; i <= 42; i++) {
            final int weekNum = i;
            View itemView = inflater.inflate(R.layout.item_week_number, gridWeeks, false);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            itemView.setLayoutParams(params);

            MaterialCardView card = (MaterialCardView) itemView;
            TextView tv = itemView.findViewById(R.id.tvWeekNumber);
            tv.setText(String.valueOf(weekNum));

            if (weekNum == selectedWeekNumber) {
                card.setCardBackgroundColor(ColorStateList.valueOf(0xFFEA580C)); // Cam đậm
                tv.setTextColor(0xFFFFFFFF);
            } else {
                card.setCardBackgroundColor(ColorStateList.valueOf(0xFFFFFFFF));
                tv.setTextColor(0xFF374151);
                if (semesterWeeks != null) {
                    for (KhoaBieuModel.AcademicWeek w : semesterWeeks) {
                        if (w.weekNumber == weekNum && w.isCurrentWeek) {
                            card.setCardBackgroundColor(ColorStateList.valueOf(0xFFFFEDD5)); // Cam nhạt
                            tv.setTextColor(0xFFEA580C);
                        }
                    }
                }
            }

            itemView.setOnClickListener(v -> {
                selectedWeekNumber = weekNum;
                isFullSemesterMode = false;
                setupWeekGrid();
                updateTabUI();
                renderSchedule(selectedWeekNumber);
            });
            gridWeeks.addView(itemView);
        }
    }

    private void renderSchedule(int weekNumber) {
        if (containerSchedule == null) return;
        containerSchedule.removeAllViews();
        
        KhoaBieuModel.AcademicWeek weekInfo = null;
        if (semesterWeeks != null) {
            for (KhoaBieuModel.AcademicWeek w : semesterWeeks) {
                if (w.weekNumber == weekNumber) { weekInfo = w; break; }
            }
        }

        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();
        if (fullSemesterSchedule != null) {
            for (KhoaBieuModel item : fullSemesterSchedule) {
                if (isWeekInValue(item.getWeekNumberString(), weekNumber)) {
                    int day = item.getDayOfWeek();
                    if (!dayMap.containsKey(day)) dayMap.put(day, new ArrayList<>());
                    dayMap.get(day).add(item);
                }
            }
        }
        renderDays(dayMap, weekInfo);
    }

    private boolean isWeekInValue(String weekValue, int targetWeek) {
        if (weekValue == null || weekValue.isEmpty()) return false;
        String[] parts = weekValue.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("-") || part.contains("->")) {
                String separator = part.contains("-") ? "-" : "->";
                String[] range = part.split(separator);
                if (range.length == 2) {
                    try {
                        int start = Integer.parseInt(range[0].trim());
                        int end = Integer.parseInt(range[1].trim());
                        if (targetWeek >= start && targetWeek <= end) return true;
                    } catch (Exception ignored) {}
                }
            } else {
                try {
                    if (Integer.parseInt(part) == targetWeek) return true;
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    private void renderFullSemesterSchedule() {
        if (containerSchedule == null) return;
        containerSchedule.removeAllViews();
        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();
        if (fullSemesterSchedule != null) {
            for (KhoaBieuModel item : fullSemesterSchedule) {
                int day = item.getDayOfWeek();
                if (!dayMap.containsKey(day)) dayMap.put(day, new ArrayList<>());
                dayMap.get(day).add(item);
            }
        }
        renderDays(dayMap, null);
    }

    private void renderDays(Map<Integer, List<KhoaBieuModel>> dayMap, KhoaBieuModel.AcademicWeek weekInfo) {
        int[] daysOrder = {2, 3, 4, 5, 6, 7, 1}; // Thứ 2 đến CN
        for (int day : daysOrder) {
            if (!dayMap.containsKey(day)) continue;

            View dayView = LayoutInflater.from(this).inflate(R.layout.item_thoikhoabieu_day, containerSchedule, false);
            TextView tvHeader = dayView.findViewById(R.id.tv_day_header);
            
            LinearLayout morningContainer = dayView.findViewById(R.id.layout_morning_container);
            LinearLayout afternoonContainer = dayView.findViewById(R.id.layout_afternoon_container);
            View morningSection = dayView.findViewById(R.id.layout_morning_section);
            View afternoonSection = dayView.findViewById(R.id.layout_afternoon_section);

            tvHeader.setText(calculateDayHeader(day, weekInfo));

            List<KhoaBieuModel> subjects = dayMap.get(day);
            Collections.sort(subjects, (o1, o2) -> o1.getPeriod().compareTo(o2.getPeriod()));
            
            boolean hasMorning = false;
            boolean hasAfternoon = false;

            for (KhoaBieuModel m : subjects) {
                // CHIA 2 BUỔI: 1-5 là Sáng, 6-12 là Chiều
                if (m.getPeriod() <= 5) {
                    addSubjectCard(morningContainer, m, day);
                    hasMorning = true;
                } else {
                    addSubjectCard(afternoonContainer, m, day);
                    hasAfternoon = true;
                }
            }
            
            if (hasMorning) morningSection.setVisibility(View.VISIBLE);
            if (hasAfternoon) afternoonSection.setVisibility(View.VISIBLE);
            
            containerSchedule.addView(dayView);
        }
    }

    private void addSubjectCard(LinearLayout container, KhoaBieuModel m, int day) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_schedule_card, container, false);
        
        ((TextView) cardView.findViewById(R.id.tv_subject_title)).setText(m.getSubjectName());
        int start = m.getPeriod();
        int end = start + m.getSoTiet() - 1;
        ((TextView) cardView.findViewById(R.id.tv_periods)).setText(start + " - " + end + " (" + m.getSoTiet() + " tiết)");
        ((TextView) cardView.findViewById(R.id.tv_location)).setText(m.getLocation());
        ((TextView) cardView.findViewById(R.id.tv_training_system)).setText(m.getTrainingSystem());
        ((TextView) cardView.findViewById(R.id.tv_course)).setText(m.getCourseCode());
        ((TextView) cardView.findViewById(R.id.tv_teacher)).setText(m.getTeacherName());
        ((TextView) cardView.findViewById(R.id.tv_weeks)).setText(m.getWeekNumberString());
        
        TextView tvWatermark = cardView.findViewById(R.id.tv_watermark);
        if (tvWatermark != null) {
            tvWatermark.setText(day == 1 ? "8" : String.valueOf(day));
        }

        container.addView(cardView);
    }

    private void updateTabUI() {
        btnViewWeek.setBackgroundTintList(ColorStateList.valueOf(isFullSemesterMode ? 0xFFE5E7EB : 0xFF3B82F6));
        btnViewWeek.setTextColor(isFullSemesterMode ? 0xFF4B5563 : 0xFFFFFFFF);
        btnViewFull.setBackgroundTintList(ColorStateList.valueOf(isFullSemesterMode ? 0xFF3B82F6 : 0xFFE5E7EB));
        btnViewFull.setTextColor(isFullSemesterMode ? 0xFFFFFFFF : 0xFF4B5563);
    }

    private String calculateDayHeader(int dayOfWeek, KhoaBieuModel.AcademicWeek week) {
        String name = (dayOfWeek == 1) ? "CN" : "Thứ " + dayOfWeek;
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
}