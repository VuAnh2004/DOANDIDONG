package com.example.doanqldiem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Lớp cơ sở giúp tất cả Activity có cùng Header Menu.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupHeaderMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupHeaderMenu();
    }

    public void setupHeaderMenu() {
        String currentActivity = getClass().getSimpleName();

        // Chỉ tự động gắn PopupMenu cho các màn hình có icon avatar tiêu chuẩn (btn_user)
        // và KHÔNG PHẢI MainActivity (vì MainActivity dùng popup tùy chỉnh)
        if (!currentActivity.equals("MainActivity")) {
            View btnUser = findViewById(R.id.btn_user);
            if (btnUser != null) {
                btnUser.setOnClickListener(this::showModernPopupMenu);
            }
        }

        // Logo trường -> Trang chủ
        View logoTruong = findViewById(R.id.logotruong);
        if (logoTruong != null) {
            logoTruong.setOnClickListener(v -> {
                if (!currentActivity.equals("MainActivity")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
        }

        // Icon thông báo -> Danh sách thông báo
        View btnBell = findViewById(R.id.btn_bell);
        if (btnBell != null) {
            btnBell.setOnClickListener(v -> {
                if (!currentActivity.equals("thongbaoActivity")) {
                    Intent intent = new Intent(this, thongbaoActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Icon cài đặt -> Giao diện cấu hình
        View btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                if (!currentActivity.equals("cauhinhActivity")) {
                    Intent intent = new Intent(this, cauhinhActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private void showModernPopupMenu(View view) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.PopupMenuSmallTheme);
        PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popup);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                if (!getClass().getSimpleName().equals("MainActivity")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;
            } else if (itemId == R.id.menu_logout) {
                performLogout();
                return true;
            }
            return false;
        });
        popup.show();
    }

    protected void performLogout() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
