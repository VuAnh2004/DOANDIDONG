package com.example.doanqldiem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kích hoạt EdgeToEdge (thay thế cho setStatusBarColor thủ công)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 3. Click Hồ sơ người học
        CardView hosonguoihoc = findViewById(R.id.hosonguoihoc);
        if (hosonguoihoc != null) {
            hosonguoihoc.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, hosonguoihocActivity.class);
                startActivity(intent);
            });
        }

        // 4. Xử lý Insets để layout không bị đè bởi thanh hệ thống
        View mainView = findViewById(R.id.main); // Đảm bảo ID này trùng với ID trong activity_main.xml
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        // 5. MỞ màn hình Cấu hình
        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, cauhinhActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        // 6. Click xem điểm
        CardView diem = findViewById(R.id.btn_diem);
        if (diem != null) {
            diem.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, diemActivity.class);
                startActivity(intent);
            });
        }
        // 7. Click xem viet don
        CardView vietdon = findViewById(R.id.btn_vietdon);
        if (vietdon != null) {
            vietdon.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, vietdonActivity.class);
                startActivity(intent);
            });
        }
        // 7. Click xem muon phong
        CardView muonphong = findViewById(R.id.btn_muonphong);
        if (muonphong != null) {
            muonphong.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, muonphongActivity.class);
                startActivity(intent);
            });
        }
        // 8. Click xem phananh
        CardView phananh = findViewById(R.id.btn_phananh);
        if (phananh != null) {
            phananh.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, phananhActivity.class);
                startActivity(intent);
            });
        }
        // 9. Click cập nhật hồ sơ
        CardView capnhathoso = findViewById(R.id.btn_capnhathoso);
        if (capnhathoso != null) {
           capnhathoso.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, capnhathosoActivity.class);
                startActivity(intent);
            });
        }
        // 10. Click Icon thong bao
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, thongbaoActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        // Click thoikhoabieu
        CardView thoikhoabieu = findViewById(R.id.btn_thoikhoabieu);
        if (thoikhoabieu != null) {
            thoikhoabieu.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, thoikhoabieuActivity.class);
                startActivity(intent);
            });
        }
        // Click nap tien
        CardView naptien = findViewById(R.id.btn_naptien);
        if (naptien != null) {
            naptien.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, hocphiActivity.class);
                startActivity(intent);
            });
        }
    }
}
