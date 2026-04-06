package com.example.doanqldiem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import api.RetrofitClient;
import api.hosonguoihocapi;
import model.hosonguoihocResponse;
import model.hosonguoihocmodel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class hosonguoihocActivity extends AppCompatActivity {

    private static final String TAG = "HOSO_DEBUG";
    private TextView txtFullName, txtStudentID;
    private TextView txtNgaySinh, txtGioiTinh, txtDanToc, txtTonGiao, txtLop;
    private TextView txtCha, txtMe, txtStatus, txtSDT, txtFullAddress;
    private TextView txtHosoHoa; // Cho mục 8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hosonguoihoc);

        initViews();
        setupToolbar();
        setupExpandableSections();

        loadStudentProfile("24290001");
    }

    private void initViews() {
        txtFullName = findViewById(R.id.txt_full_name);
        txtStudentID = findViewById(R.id.txt_student_id);
        
        // 1. Thông tin chung
        txtNgaySinh = findViewById(R.id.txt_ngay_sinh);
        txtGioiTinh = findViewById(R.id.txt_gioi_tinh);
        txtDanToc = findViewById(R.id.txt_dan_toc);
        txtTonGiao = findViewById(R.id.txt_ton_giao);
        txtLop = findViewById(R.id.txt_lop_hoc);
        
        // 2. Thân nhân
        txtCha = findViewById(R.id.txt_cha);
        txtMe = findViewById(R.id.txt_me);
        
        // 3. Chính sách
        txtStatus = findViewById(R.id.txt_status);
        
        // 7. Liên hệ
        txtSDT = findViewById(R.id.txt_sdt);
        txtFullAddress = findViewById(R.id.txt_full_address);

        // 8. Hồ sơ số hóa (Tìm TextView trong content_8)
        LinearLayout content8 = findViewById(R.id.content_8);
        if (content8 != null && content8.getChildAt(0) instanceof TextView) {
            txtHosoHoa = (TextView) content8.getChildAt(0);
        }
    }

    private void loadStudentProfile(String studentId) {
        hosonguoihocapi api = RetrofitClient.getClient().create(hosonguoihocapi.class);
        api.getStudentProfile(studentId).enqueue(new Callback<hosonguoihocResponse>() {
            @Override
            public void onResponse(@NonNull Call<hosonguoihocResponse> call, @NonNull Response<hosonguoihocResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hosonguoihocResponse result = response.body();
                    if (result.isSuccess() && result.getStudent() != null) {
                        updateUI(result.getStudent());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<hosonguoihocResponse> call, @NonNull Throwable t) {
                Toast.makeText(hosonguoihocActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(hosonguoihocmodel model) {
        if (txtFullName != null) txtFullName.setText(model.getFullName());
        if (txtStudentID != null) txtStudentID.setText(model.getStudentID());
        
        // Mục 1
        if (txtNgaySinh != null) txtNgaySinh.setText(String.format("Ngày sinh: %s", model.getBirth()));
        if (txtGioiTinh != null) txtGioiTinh.setText(String.format("Giới tính: %s", model.getGender()));
        if (txtDanToc != null) txtDanToc.setText(String.format("Dân tộc: %s", model.getNation()));
        if (txtTonGiao != null) txtTonGiao.setText(String.format("Tôn giáo: %s", model.getReligion()));
        if (model.getLopHoc() != null && txtLop != null) {
            txtLop.setText(String.format("Lớp: %s (%s)", model.getLopHoc().getClassName(), model.getLopHoc().getSchoolYear()));
        }

        // Mục 2
        if (model.getRelatives() != null) {
            for (hosonguoihocmodel.Relative r : model.getRelatives()) {
                if (r.getRelationship() != null) {
                    if (r.getRelationship().contains("Cha") && txtCha != null) {
                        txtCha.setText(String.format("Cha: %s - %s", r.getFullName(), r.getPhone()));
                    } else if (r.getRelationship().contains("Mẹ") && txtMe != null) {
                        txtMe.setText(String.format("Mẹ: %s - %s", r.getFullName(), r.getPhone()));
                    }
                }
            }
        }

        // Mục 3
        if (txtStatus != null) txtStatus.setText(String.format("Trạng thái: %s", model.getStatusStudent()));

        // Mục 7: Xử lý Address
        if (txtSDT != null) txtSDT.setText(String.format("Số điện thoại: %s", model.getNumberPhone()));
        
        // Fix Address: Nếu FullAddress chỉ có dấu phẩy thì dùng Address
        String addr = model.getAddress();
        if (model.getFullAddress() != null && model.getFullAddress().replaceAll("[,\\s]", "").length() > 0) {
            addr = model.getFullAddress();
        }
        if (txtFullAddress != null) txtFullAddress.setText(String.format("Địa chỉ: %s", addr));

        // Mục 8: Hồ sơ số hóa (Hiển thị trạng thái giấy tờ)
        if (model.getDocuments() != null && !model.getDocuments().isEmpty() && txtHosoHoa != null) {
            StringBuilder docs = new StringBuilder("Danh sách hồ sơ:\n");
            for (hosonguoihocmodel.Document d : model.getDocuments()) {
                String status = (d.getAttachment() != null && !d.getAttachment().trim().isEmpty()) 
                                ? " ✅ (Đã có bản quét)" : " ❌ (Chưa nộp)";
                docs.append("- ").append(d.getDocumentType()).append(status).append("\n");
            }
            txtHosoHoa.setText(docs.toString());
            txtHosoHoa.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    @SuppressWarnings("deprecation")
    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                Intent intent = new Intent(this, cauhinhActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setupExpandableSections() {
        for (int i = 1; i <= 8; i++) {
            int headerId = getResources().getIdentifier("header_" + i, "id", getPackageName());
            int contentId = getResources().getIdentifier("content_" + i, "id", getPackageName());
            int arrowId = getResources().getIdentifier("arrow_" + i, "id", getPackageName());
            setupExpandableSection(headerId, contentId, arrowId);
        }
    }

    private void setupExpandableSection(int headerId, int contentId, int arrowId) {
        LinearLayout header = findViewById(headerId);
        LinearLayout content = findViewById(contentId);
        ImageView arrow = findViewById(arrowId);

        if (header != null && content != null && arrow != null) {
            header.setOnClickListener(v -> {
                if (content.getVisibility() == View.GONE) {
                    content.setVisibility(View.VISIBLE);
                    arrow.animate().rotation(180).setDuration(300).start();
                } else {
                    content.setVisibility(View.GONE);
                    arrow.animate().rotation(0).setDuration(300).start();
                }
            });
        }
    }
}