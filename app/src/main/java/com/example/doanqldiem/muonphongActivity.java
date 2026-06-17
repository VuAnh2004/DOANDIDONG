package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class muonphongActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private ProgressBar progressBar;
    private String studentId; // Đã bỏ gán cứng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muonphong);

        // Lấy StudentID từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. Fix lỗi lấn Header
        View mainLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.loading_progress);
        recyclerView = findViewById(R.id.recycler_bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        setupClickListeners();
        loadHistory();
    }

    private void setupClickListeners() {
        findViewById(R.id.btn_dang_ky).setOnClickListener(v -> {
            startActivity(new Intent(this, create_muonphongActivity.class));
        });

        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                startActivity(new Intent(this, thongbaoActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BookingModel>> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(muonphongActivity.this, "Lỗi tải lịch sử", Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_muonphong, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BookingModel m = list.get(position);
            holder.txtRoomName.setText(m.getRoomName() != null ? m.getRoomName() : "N/A");
            holder.txtStatus.setText(m.getStatusText());
            holder.txtTimeRange.setText(m.getStartTime() + " -> " + m.getEndTime());
            holder.txtPurpose.setText("Lý do: " + m.getPurpose());

            if (m.getStatusText().contains("duyệt") || m.getStatusText().contains("trả")) {
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#10B981"));
            } else {
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B"));
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