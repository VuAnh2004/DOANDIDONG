package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import api.RetrofitClient;
import api.vietdonapi;
import model.VietDonModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class vietdonActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VietDonAdapter adapter;
    private String studentId; // Đã bỏ gán cứng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Kích hoạt EdgeToEdge để sát Header
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vietdon);

        // Lấy StudentID từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        
        recyclerView = findViewById(R.id.recycler_leave_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VietDonAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Chuyển sang trang tạo đơn mới khi nhấn nút
        findViewById(R.id.btn_new_leave).setOnClickListener(v -> {
            startActivity(new Intent(this, create_vietdonActivity.class));
        });
        // 5. Nút Cấu hình
        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // 6. Nút Back (Logo)
        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        //  Click Icon thong bao
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(vietdonActivity.this, thongbaoActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (studentId != null && !studentId.isEmpty()) {
            loadData(); // Tải lại danh sách mỗi khi quay lại trang này
        }
    }

    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, cauhinhActivity.class));
        });
    }

    private void loadData() {
        vietdonapi api = RetrofitClient.getClient().create(vietdonapi.class);
        api.getDanhSachDon(studentId).enqueue(new Callback<List<VietDonModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<VietDonModel>> call, @NonNull Response<List<VietDonModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<VietDonModel>> call, @NonNull Throwable t) {
                Toast.makeText(vietdonActivity.this, "Không thể tải danh sách đơn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class VietDonAdapter extends RecyclerView.Adapter<VietDonAdapter.ViewHolder> {
        private List<VietDonModel> list;
        public VietDonAdapter(List<VietDonModel> list) { this.list = list; }
        public void updateData(List<VietDonModel> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vietdon, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VietDonModel item = list.get(position);
            holder.txtReason.setText(String.format("Lý do: %s", item.getReason()));
            holder.txtDate.setText(String.format("Ngày gửi: %s", item.getRequestDate()));
            holder.txtStatus.setText(item.getStatusText());
            holder.txtDuration.setText(String.format("Từ %s - Đến %s", item.getStartDate(), item.getEndDate()));

            // Màu sắc trạng thái theo mã IsActive
            if (item.getIsActive() == 2) holder.txtStatus.setTextColor(0xFFF59E0B); // Chờ duyệt
            else if (item.getIsActive() == 1) holder.txtStatus.setTextColor(0xFF10B981); // Đã duyệt
            else holder.txtStatus.setTextColor(0xFFEF4444); // Từ chối
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtReason, txtDate, txtStatus, txtDuration;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtReason = itemView.findViewById(R.id.txt_reason);
                txtDate = itemView.findViewById(R.id.txt_request_date);
                txtStatus = itemView.findViewById(R.id.txt_status);
                txtDuration = itemView.findViewById(R.id.txt_duration);
            }
        }
    }
}