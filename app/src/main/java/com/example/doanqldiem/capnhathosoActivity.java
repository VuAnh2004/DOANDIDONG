package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import api.RetrofitClient;
import api.profileapi;
import de.hdodenhof.circleimageview.CircleImageView;
import model.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class capnhathosoActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;

    // ⭐ Views cho ảnh thẻ
    private CircleImageView imgAvatarCard;
    private ImageView imgQrCodeCard;
    private TextView tvStudentIdCard, tvCardFullnameCard, tvCardBirthCard, tvCardClassCard, tvCardCohortCard;

    private String studentId;
    private String fullName = "";
    private String birth = "";
    private String className = "";
    private String cohort = "";
    private String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capnhathoso);

        // Lấy StudentID từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewPager();
        loadStudentData();

        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(capnhathosoActivity.this, thongbaoActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.loading_progress);

        // ⭐ Ánh xạ các view trong ảnh thẻ
        imgAvatarCard = findViewById(R.id.img_student_avatar_card);
        imgQrCodeCard = findViewById(R.id.img_qr_code_card);
        tvStudentIdCard = findViewById(R.id.tv_student_id_card);
        tvCardFullnameCard = findViewById(R.id.tv_card_fullname_card);
        tvCardBirthCard = findViewById(R.id.tv_card_birth_card);
        tvCardClassCard = findViewById(R.id.tv_card_class_card);
        tvCardCohortCard = findViewById(R.id.tv_card_cohort_card);
    }

    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setupViewPager() {
        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Thông tin"); break;
                case 1: tab.setText("Bảo mật"); break;
                case 2: tab.setText("Hồ sơ số"); break;
            }
        }).attach();
    }

    private void loadStudentData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getProfile(studentId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse data = response.body();

                    if (data.getProfile() != null) {
                        ProfileResponse.ProfileData profile = data.getProfile();

                        fullName = profile.getFullName() != null ? profile.getFullName() : "Chưa cập nhật";
                        birth = profile.getBirth() != null ? profile.getBirth() : "Chưa cập nhật";
                        imageUrl = profile.getImages() != null ? profile.getImages() : "";
                        className = profile.getLopHoc() != null ? profile.getLopHoc() : "Chưa có lớp";
                        cohort = profile.getKhoaHoc() != null ? profile.getKhoaHoc() : "Chưa có niên khóa";

                        // ⭐ Cập nhật ảnh thẻ
                        updateStudentCard();
                        generateQRCode();
                        loadAvatar();

                        // Lưu FullName vào SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
                        prefs.edit().putString("FullName", fullName).apply();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(capnhathosoActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAvatar() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullImageUrl;
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                fullImageUrl = imageUrl;
            } else {
                fullImageUrl = RetrofitClient.BASE_URL + imageUrl;
            }

            Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.anhuser)
                    .error(R.drawable.anhuser)
                    .into(imgAvatarCard);
        }
    }

    private void updateStudentCard() {
        tvStudentIdCard.setText("MHS: " + studentId);
        tvCardFullnameCard.setText("Họ tên: " + fullName);
        tvCardBirthCard.setText("Ngày sinh: " + birth);
        tvCardClassCard.setText("Lớp: " + className);
        tvCardCohortCard.setText("Niên khóa: " + cohort);
    }

    private void generateQRCode() {
        String qrData = "Mã học sinh: " + studentId + "\n" +
                "Họ tên: " + fullName + "\n" +
                "Ngày sinh: " + birth + "\n" +
                "Lớp: " + className;

        new Thread(() -> {
            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                int[] pixels = new int[width * height];

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF1E3A8A : 0xFFFFFFFF;
                    }
                }

                Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                qrBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                runOnUiThread(() -> {
                    imgQrCodeCard.setImageBitmap(qrBitmap);
                });

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }).start();
    }
}