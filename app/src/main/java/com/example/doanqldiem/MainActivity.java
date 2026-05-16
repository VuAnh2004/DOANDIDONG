package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Kế thừa từ BaseActivity để thừa hưởng tính năng tự động đăng xuất sau 2 phút
 */
public class MainActivity extends BaseActivity {

    private TextView txtUserName, txtPopupName, txtPopupEmail;
    private View overlayPopup;
    private CircleImageView btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        displayUserInfo();
        setupProfilePopup();

        View mainView = findViewById(R.id.main_drawer_layout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        setupFeatureClicks();
    }

    private void initViews() {
        txtUserName = findViewById(R.id.txt_user_name_main);
        txtPopupName = findViewById(R.id.popup_user_name);
        txtPopupEmail = findViewById(R.id.popup_user_email);
        overlayPopup = findViewById(R.id.overlay_popup);
        btnMenu = findViewById(R.id.btn_menu);
    }

    private void setupProfilePopup() {
        if (btnMenu != null && overlayPopup != null) {
            btnMenu.setOnClickListener(v -> overlayPopup.setVisibility(View.VISIBLE));
            overlayPopup.setOnClickListener(v -> overlayPopup.setVisibility(View.GONE));

            View btnPopupHome = findViewById(R.id.popup_home);
            if (btnPopupHome != null) {
                btnPopupHome.setOnClickListener(v -> overlayPopup.setVisibility(View.GONE));
            }

            View btnLogout = findViewById(R.id.popup_logout);
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> handleLogoutManual());
            }
        }
    }

    /**
     * Đăng xuất thủ công từ Popup
     */
    private void handleLogoutManual() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        prefs.edit()
            .remove("AuthToken")
            .remove("UserID")
            .putBoolean("isLoggedIn", false)
            .remove("LastPauseTime") // Xóa luôn thời gian chờ
            .apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupFeatureClicks() {
        findViewById(R.id.btn_setting).setOnClickListener(v -> 
            startActivity(new Intent(this, cauhinhActivity.class)));
            
        findViewById(R.id.btn_diem).setOnClickListener(v -> 
            startActivity(new Intent(this, diemActivity.class)));
            
        findViewById(R.id.btn_vietdon).setOnClickListener(v -> 
            startActivity(new Intent(this, vietdonActivity.class)));
            
        findViewById(R.id.btn_muonphong).setOnClickListener(v -> 
            startActivity(new Intent(this, muonphongActivity.class)));
            
        findViewById(R.id.btn_phananh).setOnClickListener(v -> 
            startActivity(new Intent(this, phananhActivity.class)));
            
        findViewById(R.id.btn_capnhathoso).setOnClickListener(v -> 
            startActivity(new Intent(this, capnhathosoActivity.class)));
            
        findViewById(R.id.btn_bell).setOnClickListener(v -> 
            startActivity(new Intent(this, thongbaoActivity.class)));
            
        findViewById(R.id.btn_thoikhoabieu).setOnClickListener(v -> 
            startActivity(new Intent(this, thoikhoabieuActivity.class)));
            
        findViewById(R.id.btn_naptien).setOnClickListener(v -> 
            startActivity(new Intent(this, hocphiActivity.class)));
        
        View hosonguoihoc = findViewById(R.id.hosonguoihoc);
        if (hosonguoihoc != null) {
            hosonguoihoc.setOnClickListener(v -> 
                startActivity(new Intent(this, hosonguoihocActivity.class)));
        }
    }

    private void displayUserInfo() {
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        String fullName = prefs.getString("FullName", "Người dùng");
        String email = prefs.getString("Email", "---");

        if (txtUserName != null) txtUserName.setText(fullName);
        if (txtPopupName != null) txtPopupName.setText(fullName);
        if (txtPopupEmail != null) txtPopupEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        if (overlayPopup != null && overlayPopup.getVisibility() == View.VISIBLE) {
            overlayPopup.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
