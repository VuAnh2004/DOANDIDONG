package com.example.doanqldiem;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import api.RetrofitClient;
import api.thoikhoabieuapi;
import model.KhoaBieuModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class thoikhoabieuActivity extends BaseActivity {

    private static final String TAG = "TKB_DEBUG";

    // UI Components
    private LinearLayout containerSchedule;
    private GridLayout gridWeeks;
    private TextView txtCurrentWeek, tvSemesterInfo, btnViewWeek, btnViewFull;
    private TextView txtCurrentWeekBadge;
    private ProgressBar progressBar;
    private ImageView btnPickWeek;
    private NestedScrollView nestedScrollView;
    private MaterialButton btnExportPdf, btnExportExcel;
    private MaterialCardView cardCurrentWeek, gridWeeksCard;

    // Data
    private int selectedWeekNumber = 1;
    private int currentWeekNumber = 1;
    private String studentId;
    private String currentYear = "";
    private String currentSemesterCode = "HK1";
    private List<KhoaBieuModel.AcademicWeek> allWeeks = new ArrayList<>();
    private List<KhoaBieuModel> fullSemesterSchedule = new ArrayList<>();
    private boolean isFullSemesterMode = false;

    // Cache
    private KhoaBieuModel.IndexResponse cachedData = null;
    private boolean isDataLoaded = false;

    // Broadcast Receiver
    private BroadcastReceiver configChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "===== CONFIG CHANGE BROADCAST RECEIVED =====");

            String newSemester = intent.getStringExtra("semester");
            String newYear = intent.getStringExtra("year");
            String newSemesterCode = intent.getStringExtra("semester_code");

            if (newSemesterCode != null && !newSemesterCode.isEmpty()) {
                currentSemesterCode = newSemesterCode;
            }
            if (newYear != null && !newYear.isEmpty()) {
                currentYear = newYear;
            }

            resetAllData();
            loadScheduleData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thoikhoabieu);

        setupCommonToolbar();
        initUserInfo();
        initViews();
        setupInsets();
        setupListeners();

        registerConfigChangeReceiver();
        loadConfigFromPreferences();
        loadScheduleData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndHandleConfigChange();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(configChangeReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering receiver: " + e.getMessage());
        }
    }

    /**
     * Reset toàn bộ dữ liệu cũ
     */
    private void resetAllData() {
        Log.d(TAG, "===== RESETTING ALL DATA =====");
        allWeeks.clear();
        fullSemesterSchedule.clear();
        selectedWeekNumber = 1;
        currentWeekNumber = 1;
        isFullSemesterMode = false;

        runOnUiThread(() -> {
            if (gridWeeks != null) {
                gridWeeks.removeAllViews();
                gridWeeks.setVisibility(View.GONE);
            }
            if (containerSchedule != null) {
                containerSchedule.removeAllViews();
                containerSchedule.setVisibility(View.GONE);
            }
            if (txtCurrentWeek != null) {
                txtCurrentWeek.setText("Đang tải...");
            }
            if (tvSemesterInfo != null) {
                tvSemesterInfo.setText("");
            }
            if (txtCurrentWeekBadge != null) {
                txtCurrentWeekBadge.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Đăng ký BroadcastReceiver
     */
    private void registerConfigChangeReceiver() {
        IntentFilter filter = new IntentFilter("CONFIG_CHANGED");
        LocalBroadcastManager.getInstance(this).registerReceiver(configChangeReceiver, filter);
        Log.d(TAG, "Config change receiver registered");
    }

    /**
     * Đọc cấu hình từ SharedPreferences
     */
    private void loadConfigFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

        currentYear = prefs.getString("selected_year", "2025-2026");

        String semesterDisplay = prefs.getString("selected_semester", "HK1");
        if (semesterDisplay == null || semesterDisplay.isEmpty()) {
            semesterDisplay = "HK1";
        }
        semesterDisplay = semesterDisplay.trim().toUpperCase();

        if (semesterDisplay.contains("HK2") || semesterDisplay.contains("2")) {
            currentSemesterCode = "HK2";
        } else {
            currentSemesterCode = "HK1";
        }

        Log.d(TAG, "===== CONFIG LOADED =====");
        Log.d(TAG, "Semester Code: " + currentSemesterCode);
    }

    /**
     * Lấy thông tin học kỳ từ SharedPreferences
     */
    private String getCurrentSemesterFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String semesterDisplay = prefs.getString("selected_semester", "HK1");
        if (semesterDisplay == null || semesterDisplay.isEmpty()) {
            semesterDisplay = "HK1";
        }
        semesterDisplay = semesterDisplay.trim().toUpperCase();

        if (semesterDisplay.contains("HK2") || semesterDisplay.contains("2")) {
            return "HK2";
        } else {
            return "HK1";
        }
    }

    /**
     * Lấy năm học từ SharedPreferences
     */
    private String getCurrentYearFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        return prefs.getString("selected_year", "2025-2026");
    }

    /**
     * Kiểm tra và xử lý thay đổi cấu hình
     */
    private void checkAndHandleConfigChange() {
        String newSemesterCode = getCurrentSemesterFromPrefs();
        String newYear = getCurrentYearFromPrefs();

        boolean semesterChanged = !newSemesterCode.equals(currentSemesterCode);
        boolean yearChanged = !newYear.equals(currentYear);

        if (semesterChanged || yearChanged) {
            Log.d(TAG, "===== CONFIG CHANGED ON RESUME =====");
            currentSemesterCode = newSemesterCode;
            currentYear = newYear;

            resetAllData();
            loadScheduleData();
        }
    }

    /**
     * Khởi tạo thông tin người dùng
     */
    private void initUserInfo() {
        SharedPreferences userPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = userPrefs.getString("StudentID", userPrefs.getString("Username", ""));

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Khởi tạo các view
     */
    private void initViews() {
        containerSchedule = findViewById(R.id.container_schedule);
        gridWeeks = findViewById(R.id.grid_weeks);
        txtCurrentWeek = findViewById(R.id.txt_current_week);
        tvSemesterInfo = findViewById(R.id.tv_semester_info);
        progressBar = findViewById(R.id.loading_progress);
        btnViewWeek = findViewById(R.id.btn_view_week);
        btnViewFull = findViewById(R.id.btn_view_full);
        btnPickWeek = findViewById(R.id.btn_pick_week);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        btnExportPdf = findViewById(R.id.btn_export_pdf);
        btnExportExcel = findViewById(R.id.btn_export_excel);
        txtCurrentWeekBadge = findViewById(R.id.txt_current_week_badge);
        cardCurrentWeek = findViewById(R.id.card_current_week);
        gridWeeksCard = findViewById(R.id.grid_weeks_card);

        if (containerSchedule != null) {
            containerSchedule.setVisibility(View.GONE);
        }
    }

    /**
     * Fix toolbar bị đè bởi thanh trạng thái
     */
    private void fixToolbarOverlap() {
        // Lấy chiều cao thanh trạng thái
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;

        // Tìm toolbar và set padding top
        View toolbar = findViewById(R.id.toolbar_header);
        if (toolbar != null) {
            toolbar.setPadding(
                    toolbar.getPaddingLeft(),
                    statusBarHeight,
                    toolbar.getPaddingRight(),
                    toolbar.getPaddingBottom()
            );
            Log.d(TAG, "Toolbar padding top set to: " + statusBarHeight);
        }
    }

    /**
     * Thiết lập insets
     */
    private void setupInsets() {
        View main = findViewById(R.id.main_thoikhoabieu_layout);
        if (main != null) {
            ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);

                // Fix toolbar overlap
                fixToolbarOverlap();

                return insets;
            });
        }
    }

    /**
     * Thiết lập các sự kiện click
     */
    private void setupListeners() {
        btnViewWeek.setOnClickListener(v -> {
            isFullSemesterMode = false;
            updateTabUI();
            showWeeklyMode();
            updateCurrentWeekInfo();
            renderSchedule(selectedWeekNumber);
        });

        btnViewFull.setOnClickListener(v -> {
            isFullSemesterMode = true;
            updateTabUI();
            showFullSemesterMode();
            renderFullSemesterSchedule();
        });

        btnPickWeek.setOnClickListener(v -> {
            showQuickWeekPicker();
        });

        if (cardCurrentWeek != null) {
            cardCurrentWeek.setOnClickListener(v -> {
                scrollToWeekGrid();
            });
        }

        btnExportPdf.setOnClickListener(v -> downloadExcel("TKB_PDF.xlsx"));
        btnExportExcel.setOnClickListener(v -> downloadExcel("TKB_Excel.xlsx"));
    }

    /**
     * Xác định tuần hiện tại dựa trên thời gian thực
     * So sánh ngày hôm nay với ngày bắt đầu và kết thúc của từng tuần
     */
    private int determineCurrentWeek() {
        if (allWeeks == null || allWeeks.isEmpty()) {
            return 1;
        }

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(today);

        Log.d(TAG, "===== DETERMINE CURRENT WEEK =====");
        Log.d(TAG, "Today: " + todayStr);

        // Duyệt qua tất cả các tuần để tìm tuần chứa ngày hôm nay
        for (KhoaBieuModel.AcademicWeek week : allWeeks) {
            if (week == null || week.getStartDate() == null || week.getEndDate() == null) {
                continue;
            }

            try {
                Date startDate = sdf.parse(week.getStartDate());
                Date endDate = sdf.parse(week.getEndDate());

                // Kiểm tra nếu hôm nay nằm trong khoảng thời gian của tuần
                if (today.after(startDate) && today.before(endDate)) {
                    Log.d(TAG, "Current week found: " + week.getWeekNumber() +
                            " (" + week.getStartDate() + " -> " + week.getEndDate() + ")");
                    return week.getWeekNumber();
                }

                // Nếu hôm nay trùng với ngày bắt đầu hoặc kết thúc
                if (todayStr.equals(week.getStartDate()) || todayStr.equals(week.getEndDate())) {
                    Log.d(TAG, "Current week (boundary): " + week.getWeekNumber());
                    return week.getWeekNumber();
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
            }
        }

        // Nếu không tìm thấy tuần hiện tại (có thể đã qua hết tuần trong học kỳ)
        // Lấy tuần cuối cùng trong danh sách
        int lastWeek = allWeeks.get(allWeeks.size() - 1).getWeekNumber();
        Log.d(TAG, "No current week found, using last week: " + lastWeek);
        return lastWeek;
    }

    /**
     * Hiển thị popup chọn tuần nhanh
     */
    private void showQuickWeekPicker() {
        if (allWeeks == null || allWeeks.isEmpty()) {
            Toast.makeText(this, "Chưa có dữ liệu tuần", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quick_week_picker, null);
        builder.setView(dialogView);
        builder.setTitle("📅 Chọn tuần nhanh");

        GridLayout gridDialog = dialogView.findViewById(R.id.grid_quick_weeks);
        gridDialog.setColumnCount(7);

        for (KhoaBieuModel.AcademicWeek week : allWeeks) {
            if (week == null) continue;

            final int weekNum = week.getWeekNumber();
            TextView btn = new TextView(this);
            btn.setText(String.valueOf(weekNum));
            btn.setGravity(android.view.Gravity.CENTER);
            btn.setPadding(12, 16, 12, 16);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4);
            btn.setLayoutParams(params);

            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(8);

            boolean isCurrentWeek = (weekNum == currentWeekNumber);
            boolean isSelectedWeek = (weekNum == selectedWeekNumber);

            if (isSelectedWeek) {
                bg.setColor(Color.parseColor("#3B82F6"));
                btn.setTextColor(Color.WHITE);
            } else if (isCurrentWeek) {
                bg.setColor(Color.parseColor("#FBBF24"));
                btn.setTextColor(Color.parseColor("#1E293B"));
                btn.setText(weekNum + " ⭐");
            } else {
                bg.setColor(Color.parseColor("#F3F4F6"));
                btn.setTextColor(Color.BLACK);
            }
            btn.setBackground(bg);

            btn.setOnClickListener(v -> {
                selectedWeekNumber = weekNum;
                isFullSemesterMode = false;
                updateTabUI();
                updateCurrentWeekInfo();
                renderSchedule(selectedWeekNumber);
                setupWeekGrid();

                AlertDialog dialog = (AlertDialog) v.getTag();
                if (dialog != null) dialog.dismiss();
            });

            gridDialog.addView(btn);
        }

        AlertDialog dialog = builder.create();

        for (int i = 0; i < gridDialog.getChildCount(); i++) {
            View child = gridDialog.getChildAt(i);
            child.setTag(dialog);
        }

        dialog.show();
    }

    /**
     * Scroll đến Grid chọn tuần
     */
    private void scrollToWeekGrid() {
        if (nestedScrollView != null && gridWeeksCard != null) {
            new Handler().postDelayed(() -> {
                int[] location = new int[2];
                gridWeeksCard.getLocationOnScreen(location);

                int[] scrollLocation = new int[2];
                nestedScrollView.getLocationOnScreen(scrollLocation);

                int targetY = location[1] - scrollLocation[1] - 80;
                nestedScrollView.smoothScrollTo(0, Math.max(0, targetY));

                highlightGridWeeks();
            }, 200);
        }
    }

    /**
     * Highlight grid tuần
     */
    private void highlightGridWeeks() {
        if (gridWeeksCard != null) {
            gridWeeksCard.animate()
                    .scaleX(1.03f)
                    .scaleY(1.03f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        gridWeeksCard.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();
                    })
                    .start();
        }
    }

    /**
     * Tải dữ liệu lịch học từ API
     */
    private void loadScheduleData() {
        loadConfigFromPreferences();
        showLoading(true);

        Log.d(TAG, "===== LOADING SCHEDULE =====");
        Log.d(TAG, "StudentID: " + studentId);
        Log.d(TAG, "Semester: " + currentSemesterCode);

        thoikhoabieuapi api = RetrofitClient.getClient().create(thoikhoabieuapi.class);
        api.getIndex(studentId, currentSemesterCode)
                .enqueue(new Callback<KhoaBieuModel.IndexResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<KhoaBieuModel.IndexResponse> call,
                                           @NonNull Response<KhoaBieuModel.IndexResponse> response) {
                        showLoading(false);

                        if (!response.isSuccessful()) {
                            Toast.makeText(thoikhoabieuActivity.this,
                                    "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (response.body() == null) {
                            Toast.makeText(thoikhoabieuActivity.this,
                                    "Không nhận được dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        KhoaBieuModel.IndexResponse data = response.body();

                        if (!data.isSuccess()) {
                            String message = data.getMessage();
                            Toast.makeText(thoikhoabieuActivity.this,
                                    message != null ? message : "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        cachedData = data;
                        isDataLoaded = true;
                        displayData(data);
                    }

                    @Override
                    public void onFailure(@NonNull Call<KhoaBieuModel.IndexResponse> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Toast.makeText(thoikhoabieuActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Hiển thị dữ liệu đã được filter từ server
     */
    private void displayData(KhoaBieuModel.IndexResponse data) {
        allWeeks.clear();
        fullSemesterSchedule.clear();

        // ===== Thông tin học kỳ =====
        if (data.getStudentInfo() != null && tvSemesterInfo != null) {
            KhoaBieuModel.StudentInfo info = data.getStudentInfo();
            String displayText = "";

            if (info.getSemesterName() != null && !info.getSemesterName().isEmpty()) {
                displayText += info.getSemesterName();
            }

            if (info.getClassName() != null && !info.getClassName().isEmpty()) {
                if (!displayText.isEmpty()) displayText += " | ";
                displayText += "Lớp: " + info.getClassName();
            }

            tvSemesterInfo.setText(displayText);
        }

        // ===== Lấy dữ liệu tuần =====
        if (data.getWeeksInSemester() != null) {
            for (KhoaBieuModel.AcademicWeek week : data.getWeeksInSemester()) {
                if (week != null) {
                    allWeeks.add(week);
                }
            }
        }

        // ===== Lấy dữ liệu lịch học =====
        if (data.getSchedule() != null) {
            for (KhoaBieuModel item : data.getSchedule()) {
                if (item != null) {
                    fullSemesterSchedule.add(item);
                }
            }
        }

        // ===== Xác định tuần hiện tại theo thời gian thực =====
        currentWeekNumber = determineCurrentWeek();
        selectedWeekNumber = currentWeekNumber;

        Log.d(TAG, "===== FINAL RESULT =====");
        Log.d(TAG, "Current week: " + currentWeekNumber);
        Log.d(TAG, "Selected week: " + selectedWeekNumber);
        Log.d(TAG, "Total weeks: " + allWeeks.size());
        Log.d(TAG, "=======================");

        // ===== HIỂN THỊ UI =====
        runOnUiThread(() -> {
            isFullSemesterMode = false;
            updateTabUI();
            setupWeekGrid();
            updateCurrentWeekInfo();
            showWeeklyMode();
            renderSchedule(selectedWeekNumber);

            if (containerSchedule != null) {
                containerSchedule.setVisibility(View.VISIBLE);
            }

            // Hiển thị badge tuần hiện tại
            if (txtCurrentWeekBadge != null) {
                txtCurrentWeekBadge.setVisibility(View.VISIBLE);
                txtCurrentWeekBadge.setText("⭐ Tuần " + currentWeekNumber);
            }
        });
    }

    /**
     * Thiết lập grid hiển thị các tuần
     */
    private void setupWeekGrid() {
        if (gridWeeks == null || allWeeks == null || allWeeks.isEmpty()) {
            if (gridWeeks != null) gridWeeks.setVisibility(View.GONE);
            return;
        }

        gridWeeks.removeAllViews();
        gridWeeks.setColumnCount(7);
        gridWeeks.setVisibility(View.VISIBLE);

        for (KhoaBieuModel.AcademicWeek week : allWeeks) {
            if (week == null) continue;

            final int weekNum = week.getWeekNumber();
            TextView btn = createWeekButton(weekNum);
            gridWeeks.addView(btn);
        }
    }

    /**
     * Tạo button cho từng tuần
     */
    private TextView createWeekButton(final int weekNum) {
        TextView btn = new TextView(this);
        btn.setText(String.valueOf(weekNum));
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setPadding(10, 15, 10, 15);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(6, 6, 6, 6);
        btn.setLayoutParams(params);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(10);

        boolean isCurrentWeek = (weekNum == currentWeekNumber);
        boolean isSelectedWeek = (weekNum == selectedWeekNumber);

        if (isSelectedWeek) {
            // Tuần được chọn: màu xanh dương
            bg.setColor(Color.parseColor("#3B82F6"));
            btn.setTextColor(Color.WHITE);
            // Nếu tuần được chọn cũng là tuần hiện tại, thêm ⭐
            if (isCurrentWeek) {
                btn.setText(weekNum + " ⭐");
            }
        } else if (isCurrentWeek) {
            // Tuần hiện tại (không được chọn): màu vàng
            bg.setColor(Color.parseColor("#FBBF24"));
            btn.setTextColor(Color.parseColor("#1E293B"));
            btn.setText(weekNum + " ⭐");
        } else {
            // Tuần bình thường
            bg.setColor(Color.WHITE);
            bg.setStroke(2, Color.parseColor("#D1D5DB"));
            btn.setTextColor(Color.BLACK);
        }

        btn.setBackground(bg);

        btn.setOnClickListener(v -> {
            selectedWeekNumber = weekNum;
            isFullSemesterMode = false;
            updateTabUI();
            updateCurrentWeekInfo();
            renderSchedule(selectedWeekNumber);
            setupWeekGrid(); // Refresh để cập nhật màu
        });

        return btn;
    }

    /**
     * Cập nhật thông tin tuần hiện tại
     */
    private void updateCurrentWeekInfo() {
        if (txtCurrentWeek == null) return;

        for (KhoaBieuModel.AcademicWeek w : allWeeks) {
            if (w.getWeekNumber() == selectedWeekNumber) {
                String startDate = formatDate(w.getStartDate());
                String endDate = formatDate(w.getEndDate());
                String icon = (selectedWeekNumber == currentWeekNumber) ? "⭐ " : "";
                txtCurrentWeek.setText(String.format("%sTuần %d (%s - %s)",
                        icon, selectedWeekNumber, startDate, endDate));
                return;
            }
        }
        txtCurrentWeek.setText("Tuần " + selectedWeekNumber);
    }

    /**
     * Hiển thị lịch theo tuần
     */
    private void renderSchedule(int weekNumber) {
        if (containerSchedule == null) return;
        containerSchedule.removeAllViews();

        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();

        if (fullSemesterSchedule != null) {
            for (KhoaBieuModel item : fullSemesterSchedule) {
                if (item == null || item.getWeekNumber() == null || item.getDayOfWeek() == null) continue;

                if (item.getWeekNumber() == weekNumber) {
                    Integer day = item.getDayOfWeek();
                    if (!dayMap.containsKey(day)) {
                        dayMap.put(day, new ArrayList<>());
                    }
                    dayMap.get(day).add(item);
                }
            }
        }

        renderDays(dayMap);
    }

    /**
     * Hiển thị lịch cả học kỳ
     */
    private void renderFullSemesterSchedule() {
        if (containerSchedule == null) return;
        containerSchedule.removeAllViews();

        Map<Integer, List<KhoaBieuModel>> dayMap = new HashMap<>();

        if (fullSemesterSchedule != null) {
            for (KhoaBieuModel item : fullSemesterSchedule) {
                if (item == null || item.getDayOfWeek() == null) continue;

                Integer day = item.getDayOfWeek();
                if (!dayMap.containsKey(day)) {
                    dayMap.put(day, new ArrayList<>());
                }

                // Kiểm tra trùng lặp
                boolean exists = false;
                for (KhoaBieuModel m : dayMap.get(day)) {
                    if (m.getSubjectName() != null && m.getSubjectName().equals(item.getSubjectName()) &&
                            m.getPeriod() != null && m.getPeriod().equals(item.getPeriod())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    dayMap.get(day).add(item);
                }
            }
        }

        renderDays(dayMap);
    }

    /**
     * Render các ngày trong tuần
     */
    private void renderDays(Map<Integer, List<KhoaBieuModel>> dayMap) {
        int[] daysOrder = {2, 3, 4, 5, 6, 7, 1};
        String[] dayNames = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};

        for (int i = 0; i < daysOrder.length; i++) {
            int day = daysOrder[i];

            View dayView = LayoutInflater.from(this).inflate(R.layout.item_thoikhoabieu_day, containerSchedule, false);
            TextView tvHeader = dayView.findViewById(R.id.tv_day_header);
            LinearLayout morningContainer = dayView.findViewById(R.id.layout_morning_periods);
            LinearLayout afternoonContainer = dayView.findViewById(R.id.layout_afternoon_periods);

            if (tvHeader != null) {
                tvHeader.setText(dayNames[i]);
            }

            Map<Integer, KhoaBieuModel> periodMap = new HashMap<>();
            if (dayMap.containsKey(day) && dayMap.get(day) != null) {
                for (KhoaBieuModel m : dayMap.get(day)) {
                    if (m != null && m.getPeriod() != null) {
                        periodMap.put(m.getPeriod(), m);
                    }
                }
            }

            if (morningContainer != null) morningContainer.removeAllViews();
            if (afternoonContainer != null) afternoonContainer.removeAllViews();

            // Tiết 1-5 (Sáng)
            for (int p = 1; p <= 5; p++) {
                View row = createPeriodRow(p, periodMap.get(p), true);
                morningContainer.addView(row);
            }

            // Tiết 6-10 (Chiều)
            for (int p = 6; p <= 10; p++) {
                View row = createPeriodRow(p, periodMap.get(p), false);
                afternoonContainer.addView(row);
            }

            containerSchedule.addView(dayView);
        }
    }

    /**
     * Tạo row cho từng tiết học
     */
    private View createPeriodRow(int periodNum, KhoaBieuModel model, boolean isMorning) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_timetable_row, null);
        TextView tvNum = row.findViewById(R.id.tv_period_num);
        TextView tvInfo = row.findViewById(R.id.tv_subject_info);

        if (tvNum != null) {
            tvNum.setText(String.valueOf(periodNum));
        }

        if (tvInfo != null) {
            if (model != null && model.getSubjectName() != null && !model.getSubjectName().isEmpty()) {
                String text = model.getSubjectName();
                if (model.getTeacherName() != null && !model.getTeacherName().isEmpty()) {
                    text += " - " + model.getTeacherName();
                }
                tvInfo.setText(text);
                tvInfo.setTextColor(Color.parseColor("#0F172A"));
            } else {
                tvInfo.setText("");
                tvInfo.setTextColor(Color.parseColor("#94A3B8"));
            }
        }

        // Thêm màu nền cho buổi sáng/chiều
        if (isMorning) {
            row.setBackgroundColor(Color.parseColor("#F8FAFC"));
        } else {
            row.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return row;
    }

    /**
     * Hiển thị chế độ xem theo tuần
     */
    private void showWeeklyMode() {
        if (gridWeeks != null) gridWeeks.setVisibility(View.VISIBLE);
        if (containerSchedule != null) containerSchedule.setVisibility(View.VISIBLE);
    }

    /**
     * Hiển thị chế độ xem cả kỳ
     */
    private void showFullSemesterMode() {
        if (gridWeeks != null) gridWeeks.setVisibility(View.GONE);
        if (containerSchedule != null) containerSchedule.setVisibility(View.VISIBLE);
        if (txtCurrentWeek != null) txtCurrentWeek.setText("Toàn bộ học kỳ");
    }

    /**
     * Cập nhật UI cho tab xem tuần/cả kỳ
     */
    private void updateTabUI() {
        if (btnViewWeek == null || btnViewFull == null) return;

        if (!isFullSemesterMode) {
            btnViewWeek.setBackgroundResource(R.drawable.segment_selected_blue);
            btnViewWeek.setTextColor(Color.WHITE);
            btnViewFull.setBackgroundResource(R.drawable.segment_unselected_white);
            btnViewFull.setTextColor(Color.parseColor("#64748B"));
        } else {
            btnViewFull.setBackgroundResource(R.drawable.segment_selected_blue);
            btnViewFull.setTextColor(Color.WHITE);
            btnViewWeek.setBackgroundResource(R.drawable.segment_unselected_white);
            btnViewWeek.setTextColor(Color.parseColor("#64748B"));
        }
    }

    /**
     * Hiển thị/ẩn loading
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Tải file Excel từ server
     */
    private void downloadExcel(String fileName) {
        thoikhoabieuapi api = RetrofitClient.getClient().create(thoikhoabieuapi.class);
        api.exportExcel(studentId, currentSemesterCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call,
                                           @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            saveFile(response.body(), fileName);
                        } else {
                            Toast.makeText(thoikhoabieuActivity.this,
                                    "Không tải được file", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(thoikhoabieuActivity.this,
                                "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Lưu file xuống bộ nhớ
     */
    private void saveFile(ResponseBody body, String fileName) {
        try {
            File file = new File(getExternalFilesDir(null), fileName);
            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Toast.makeText(this, "Đã lưu file:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi lưu file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Format ngày tháng
     */
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String clean = dateStr.split("T")[0];
            String[] p = clean.split("-");
            if (p.length >= 3) {
                return p[2] + "/" + p[1];
            }
            return clean;
        } catch (Exception e) {
            return "";
        }
    }
}