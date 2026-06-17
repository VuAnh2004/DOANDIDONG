package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import api.RetrofitClient;
import api.muonphongapi;
import model.BookingModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class muonphongActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private ProgressBar progressBar;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muonphong);

        // Lấy StudentID từ SharedPreferences - Đảm bảo key này khớp với LoginActivity
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");
        
        // Nếu StudentID trống, thử lấy UserID làm dự phòng
        if (studentId.isEmpty()) {
            studentId = prefs.getString("UserID", "");
        }

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Thông tin người dùng không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        loadHistory();
    }

    private void initViews() {
        progressBar = findViewById(R.id.loading_progress);
        recyclerView = findViewById(R.id.recycler_bookings);
        
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new BookingAdapter(new ArrayList<>());
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupClickListeners() {
        View btnDangKy = findViewById(R.id.btn_dang_ky);
        if (btnDangKy != null) {
            btnDangKy.setOnClickListener(v -> {
                startActivity(new Intent(this, create_muonphongActivity.class));
            });
        }

        // Toolbar Header listeners
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        
        View btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
            });
        }

        View btnBell = findViewById(R.id.btn_bell);
        if (btnBell != null) {
            btnBell.setOnClickListener(v -> {
                startActivity(new Intent(this, thongbaoActivity.class));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (studentId != null && !studentId.isEmpty()) {
            loadHistory();
        }
    }

    private void loadHistory() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        muonphongapi api = RetrofitClient.getClient().create(muonphongapi.class);
        api.getBookings(studentId).enqueue(new Callback<List<BookingModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<BookingModel>> call, @NonNull Response<List<BookingModel>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                } else if (response.code() == 404) {
                    // Không có dữ liệu cũng không báo lỗi
                    adapter.updateData(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BookingModel>> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(muonphongActivity.this, "Không thể kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
        private List<BookingModel> list;
        
        public BookingAdapter(List<BookingModel> list) { this.list = list; }

        public void updateData(List<BookingModel> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Sử dụng item_muonphong.xml đã được thiết kế
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_muonphong, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BookingModel m = list.get(position);
            
            holder.txtRoomName.setText(m.getRoomName() != null ? m.getRoomName() : "Phòng học");
            holder.txtStatus.setText(m.getStatusText());
            holder.txtTimeRange.setText(m.getStartTime() + " - " + m.getEndTime());
            holder.txtPurpose.setText(m.getPurpose());
            
            // Cập nhật giao diện trạng thái dựa trên màu sắc
            String status = m.getStatus() != null ? m.getStatus() : "";
            switch (status) {
                case "Approved":
                case "Returned":
                    holder.txtStatus.setBackgroundResource(R.drawable.status_approved_bg);
                    break;
                case "Pending":
                    holder.txtStatus.setBackgroundResource(R.drawable.status_pending_bg);
                    break;
                default:
                    holder.txtStatus.setBackgroundResource(R.drawable.status_rejected_bg);
                    break;
            }
        }

        @Override
        public int getItemCount() { return list != null ? list.size() : 0; }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtRoomName, txtStatus, txtTimeRange, txtPurpose;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtRoomName = itemView.findViewById(R.id.txt_room_name);
                txtStatus = itemView.findViewById(R.id.txt_status);
                txtTimeRange = itemView.findViewById(R.id.txt_time_range);
                txtPurpose = itemView.findViewById(R.id.txt_purpose);
            }
        }
    }
}
