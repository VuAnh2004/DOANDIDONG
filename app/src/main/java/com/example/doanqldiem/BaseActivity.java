package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static int runningActivities = 0;
    protected static final long BACKGROUND_LOGOUT_TIME = 2 * 60 * 1000; // 2 phút khi thoát app
    protected static final long IDLE_LOGOUT_TIME = 5 * 60 * 1000;       // 5 phút khi không thao tác

    private Handler idleHandler = new Handler(Looper.getMainLooper());
    private Runnable idleRunnable = () -> logout("Phiên làm việc hết hạn do không thao tác");

    @Override
    protected void onStart() {
        super.onStart();
        if (runningActivities == 0) {
            checkBackgroundLogout();
        }
        runningActivities++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetIdleTimer(); // Bắt đầu đếm ngược khi quay lại màn hình
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIdleTimer(); // Dừng đếm ngược khi màn hình không còn tương tác
    }

    @Override
    protected void onStop() {
        super.onStop();
        runningActivities--;
        if (runningActivities == 0) {
            SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
            if (prefs.getBoolean("isLoggedIn", false)) {
                prefs.edit().putLong("LastPauseTime", System.currentTimeMillis()).apply();
            }
        }
    }

    /**
     * Lắng nghe mọi tương tác của người dùng (chạm, vuốt, nhấn nút)
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetIdleTimer();
    }

    private void resetIdleTimer() {
        stopIdleTimer();
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            idleHandler.postDelayed(idleRunnable, IDLE_LOGOUT_TIME);
        }
    }

    private void stopIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable);
    }

    private void checkBackgroundLogout() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        long lastPauseTime = prefs.getLong("LastPauseTime", 0);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn && lastPauseTime > 0) {
            long diff = System.currentTimeMillis() - lastPauseTime;
            if (diff >= BACKGROUND_LOGOUT_TIME) {
                logout(null);
            }
        }
        prefs.edit().remove("LastPauseTime").apply();
    }

    private void logout(String reason) {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit()
            .remove("AuthToken")
            .putBoolean("isLoggedIn", false)
            .remove("LastPauseTime")
            .apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
