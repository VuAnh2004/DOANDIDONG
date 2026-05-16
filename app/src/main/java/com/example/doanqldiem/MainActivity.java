package com.example.doanqldiem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import api.RetrofitClient;
import api.profileapi;
import model.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View overlayPopup;
    private TextView txtUserNameMain, popupUserName, popupUserEmail;
    private final String studentId = "24290001"; // ID mẫu, bạn có thể lấy từ SharedPreferences nếu có login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Khởi tạo các view với ID chính xác từ activity_main.xml
        drawerLayout = findViewById(R.id.main_drawer_layout);
        overlayPopup = findViewById(R.id.overlay_popup);
        txtUserNameMain = findViewById(R.id.txt_user_name_main);
        popupUserName = findViewById(R.id.popup_user_name);
        popupUserEmail = findViewById(R.id.popup_user_email);
        
        // Khai báo NavigationView để tránh lỗi symbol nếu code cũ vẫn tham chiếu
        NavigationView navigationView = findViewById(R.id.nav_view);

        // 2. Xử lý Insets cho giao diện tràn viền dựa trên main_content_root
        View contentRoot = findViewById(R.id.main_content_root);
        if (contentRoot != null) {
            ViewCompat.setOnApplyWindowInsetsListener(contentRoot, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 3. Hiển thị Menu nổi khi nhấn vào ảnh đại diện (btn_menu)
        View btnUserAvatar = findViewById(R.id.btn_menu);
        if (btnUserAvatar != null && overlayPopup != null) {
            btnUserAvatar.setOnClickListener(v -> {
                overlayPopup.setVisibility(View.VISIBLE);
            });
        }

        // 4. Đóng Menu nổi khi nhấn vào vùng mờ (lớp phủ)
        if (overlayPopup != null) {
            overlayPopup.setOnClickListener(v -> {
                overlayPopup.setVisibility(View.GONE);
            });
        }

        // 5. Xử lý các nút bên trong Menu nổi
        View menuHome = findViewById(R.id.popup_home);
        if (menuHome != null) {
            menuHome.setOnClickListener(v -> {
                overlayPopup.setVisibility(View.GONE);
                Toast.makeText(this, "Bạn đang ở Trang chủ", Toast.LENGTH_SHORT).show();
            });
        }

        View menuLogout = findViewById(R.id.popup_logout);
        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> {
                overlayPopup.setVisibility(View.GONE);
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                
                // Thực hiện logic chuyển về màn hình đăng nhập
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // Xóa các Activity trước đó để không thể quay lại bằng nút Back
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // 6. Tải dữ liệu người dùng từ database
        loadUserProfile();

        // 7. Cài đặt sự kiện click cho các tính năng Dashboard
        setupDashboardActions();
    }

    private void loadUserProfile() {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getProfile(studentId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse data = response.body();
                    String fullName = data.getProfile().getFullName();
                    String email = data.getAccount().getEmail();

                    // Hiển thị họ tên lên giao diện chính
                    if (txtUserNameMain != null) txtUserNameMain.setText(fullName);
                    
                    // Hiển thị họ tên và email lên Popup Menu
                    if (popupUserName != null) popupUserName.setText(fullName);
                    if (popupUserEmail != null) popupUserEmail.setText(email);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDashboardActions() {
        // Gán sự kiện click cho các nút tính năng
        setSafeClick(R.id.btn_thoikhoabieu, thoikhoabieuActivity.class);
        setSafeClick(R.id.btn_diem, diemActivity.class);
        setSafeClick(R.id.btn_vietdon, vietdonActivity.class);
        setSafeClick(R.id.btn_phananh, phananhActivity.class);
        setSafeClick(R.id.btn_muonphong, muonphongActivity.class);
        setSafeClick(R.id.btn_naptien, hocphiActivity.class);
        setSafeClick(R.id.hosonguoihoc, hosonguoihocActivity.class);

        // Nút Tin tức trong lưới (nếu cần xử lý riêng)
        View btnTinTuc = findViewById(R.id.btn_tintuc);
        if (btnTinTuc != null) {
            btnTinTuc.setOnClickListener(v -> {
                Toast.makeText(this, "Mở trang Tin tức", Toast.LENGTH_SHORT).show();
            });
        }

        // Các nút Bottom Bar
        setSafeClick(R.id.btn_bell, thongbaoActivity.class);
        setSafeClick(R.id.btn_capnhathoso, capnhathosoActivity.class);

        View btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setSafeClick(int id, Class<?> targetActivity) {
        View v = findViewById(id);
        if (v != null) {
            v.setOnClickListener(view -> startActivity(new Intent(this, targetActivity)));
        }
    }
}
