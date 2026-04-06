package com.example.doanqldiem;

import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import api.RetrofitClient;
import api.cauhinhapi;
import api.thongbaoapi;
import model.ThongBaoModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class thongbaoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ThongBaoAdapter adapter;
    private ProgressBar progressBar;
    private final String userName = "24290001";
    private SharedPreferences readPrefs;
    private static final String READ_NOTIFS_KEY = "read_notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongbao);

        readPrefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);

        initViews();
        setupInsets();
        setupToolbar();
        
        autoSyncAndLoad();
    }

    private void initViews() {
        progressBar = findViewById(R.id.loading_progress);
        recyclerView = findViewById(R.id.recycler_thongbao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ThongBaoAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void setupInsets() {
        View main = findViewById(R.id.main_diem_layout); // Note: Assuming the ID in thongbao.xml is same or use root
        if (main == null) main = findViewById(android.R.id.content);
        
        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        TextView btnClose = findViewById(R.id.btn_close_thongbao);
        if (btnClose != null) btnClose.setOnClickListener(v -> finish());

        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        
        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> finish());
    }

    private void autoSyncAndLoad() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String year = prefs.getString("selected_year", "");
        String semester = prefs.getString("selected_semester", "");

        if (!year.isEmpty() && !semester.isEmpty()) {
            cauhinhapi api = RetrofitClient.getClient().create(cauhinhapi.class);
            api.saveConfig(userName, year, semester).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    loadThongBao();
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    loadThongBao();
                }
            });
        } else {
            loadThongBao();
        }
    }

    private void loadThongBao() {
        thongbaoapi api = RetrofitClient.getClient().create(thongbaoapi.class);
        api.getThongBao(userName).enqueue(new Callback<thongbaoapi.ThongBaoResponse>() {
            @Override
            public void onResponse(@NonNull Call<thongbaoapi.ThongBaoResponse> call, @NonNull Response<thongbaoapi.ThongBaoResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    adapter.updateData(response.body().data);
                }
            }
            @Override
            public void onFailure(@NonNull Call<thongbaoapi.ThongBaoResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(thongbaoActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ThongBaoAdapter extends RecyclerView.Adapter<ThongBaoAdapter.ViewHolder> {
        private List<ThongBaoModel> list;

        public ThongBaoAdapter(List<ThongBaoModel> list) { this.list = list; }

        public void updateData(List<ThongBaoModel> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thongbao, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ThongBaoModel item = list.get(position);
            holder.txtTitle.setText(item.getTitle());
            holder.txtSender.setText("Người gửi: " + item.getSenderName());
            holder.txtContent.setText(item.getContent());
            holder.txtTime.setText(getTimeAgo(item.getSenDate()));

            // Logic đánh dấu đã đọc
            Set<String> readIds = readPrefs.getStringSet(READ_NOTIFS_KEY, new HashSet<>());
            if (readIds.contains(String.valueOf(item.getID()))) {
                holder.unreadDot.setVisibility(View.GONE);
                holder.itemView.setAlpha(0.7f);
            } else {
                holder.unreadDot.setVisibility(View.VISIBLE);
                holder.itemView.setAlpha(1.0f);
            }

            holder.itemView.setOnClickListener(v -> {
                markAsRead(item.getID());
                notifyItemChanged(position);
                // Hiển thị nội dung chi tiết nếu cần
                Toast.makeText(thongbaoActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
            });
        }

        private void markAsRead(int id) {
            Set<String> readIds = new HashSet<>(readPrefs.getStringSet(READ_NOTIFS_KEY, new HashSet<>()));
            readIds.add(String.valueOf(id));
            readPrefs.edit().putStringSet(READ_NOTIFS_KEY, readIds).apply();
        }

        private String getTimeAgo(String dateStr) {
            if (dateStr == null) return "Vừa xong";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(dateStr);
                if (date == null) return "---";
                long diff = new Date().getTime() - date.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (minutes < 1) return "Vừa xong";
                if (minutes < 60) return minutes + " phút trước";
                if (hours < 24) return hours + " giờ trước";
                return days + " ngày trước";
            } catch (ParseException e) {
                return dateStr;
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtTitle, txtSender, txtContent, txtTime;
            View unreadDot;
            ImageView imgAvatar;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txt_notif_title);
                txtSender = itemView.findViewById(R.id.txt_notif_sender);
                txtContent = itemView.findViewById(R.id.txt_notif_content);
                txtTime = itemView.findViewById(R.id.txt_notif_time);
                unreadDot = itemView.findViewById(R.id.view_unread_dot);
                imgAvatar = itemView.findViewById(R.id.img_sender_avatar);
            }
        }
    }
}