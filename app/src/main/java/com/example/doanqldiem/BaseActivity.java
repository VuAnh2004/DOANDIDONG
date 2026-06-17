package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.PopupMenu;
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
        resetIdleTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIdleTimer();
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

    /**
     * Thiết lập Toolbar chung cho các Activity sử dụng layout toolbar_header
     */
    protected void setupCommonToolbar() {
        View logotruong = findViewById(R.id.logotruong);
        View btnBell = findViewById(R.id.btn_bell);
        View btnSetting = findViewById(R.id.btn_setting);
        View btnUser = findViewById(R.id.btn_user);

        if (logotruong != null) logotruong.setOnClickListener(v -> finish());
        
        if (btnBell != null) btnBell.setOnClickListener(v -> 
            startActivity(new Intent(this, thongbaoActivity.class)));
            
        if (btnSetting != null) btnSetting.setOnClickListener(v -> 
            startActivity(new Intent(this, cauhinhActivity.class)));

        if (btnUser != null) {
            btnUser.setOnClickListener(this::showProfileMenu);
        }
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, hosonguoihocActivity.class));
                return true;
            } else if (id == R.id.menu_logout) {
                logout(null);
                return true;
            }
            return false;
        });

        popup.show();
    }

    protected void logout(String reason) {
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
