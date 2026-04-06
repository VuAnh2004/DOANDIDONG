package com.example.doanqldiem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import api.RetrofitClient;
import api.hocphiapi;
import model.HocPhiModel;
import model.NghiaVuModel;
import model.PaymentInformationModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class hocphiActivity extends AppCompatActivity {

    private TextView txtBalance;
    private EditText edtAmount;
    private RecyclerView rvNghiaVu;
    private NghiaVuAdapter adapter;
    private ProgressBar progressBar;
    private final String studentId = "24290001";
    private final DecimalFormat formatter = new DecimalFormat("#,### ₫");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hocphi);

        initViews();
        setupInsets();
        setupToolbar();
        
        loadDashboard();
        loadNghiaVu();

        findViewById(R.id.btn_deposit).setOnClickListener(v -> handleDeposit());
        //  Click Icon thong bao
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(hocphiActivity.this, thongbaoActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        // MỞ màn hình Cấu hình
        ImageView imgCauHinh = findViewById(R.id.btn_setting);
        if (imgCauHinh != null) {
            imgCauHinh.setOnClickListener(v -> {
                Intent intent = new Intent(hocphiActivity.this, cauhinhActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động tải lại dữ liệu khi người dùng quay lại từ trang thanh toán
        loadDashboard();
        loadNghiaVu();
    }

    private void initViews() {
        txtBalance = findViewById(R.id.txt_balance);
        edtAmount = findViewById(R.id.edt_amount);
        progressBar = findViewById(R.id.loading_progress);
        rvNghiaVu = findViewById(R.id.recycler_nghia_vu);
        rvNghiaVu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NghiaVuAdapter(new ArrayList<>());
        rvNghiaVu.setAdapter(adapter);
    }

    private void setupInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView == null) mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        
        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> finish());
    }

    private void handleDeposit() {
        String amountStr = edtAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount < 5000) {
            Toast.makeText(this, "Số tiền tối thiểu là 5.000đ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Đã cập nhật constructor thêm platform "mobile" để server nhận diện
        PaymentInformationModel paymentInfo = new PaymentInformationModel(
                "topup",
                amount,
                "NapTien_" + studentId,
                studentId,
                "mobile"
        );

        hocphiapi api = RetrofitClient.getClient().create(hocphiapi.class);
        api.createPayment(paymentInfo).enqueue(new Callback<hocphiapi.PaymentUrlResponse>() {
            @Override
            public void onResponse(@NonNull Call<hocphiapi.PaymentUrlResponse> call, @NonNull Response<hocphiapi.PaymentUrlResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().paymentUrl;
                    if (url != null && !url.isEmpty()) {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(ContextCompat.getColor(hocphiActivity.this, android.R.color.white));
                        builder.setShowTitle(true);
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(hocphiActivity.this, Uri.parse(url));
                    }
                } else {
                    String error = "Lỗi tạo link";
                    try { if (response.errorBody() != null) error = response.errorBody().string(); } catch (Exception e) {}
                    Toast.makeText(hocphiActivity.this, "Lỗi server: " + error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<hocphiapi.PaymentUrlResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(hocphiActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDashboard() {
        hocphiapi api = RetrofitClient.getClient().create(hocphiapi.class);
        api.getDashboard(studentId).enqueue(new Callback<HocPhiModel>() {
            @Override
            public void onResponse(@NonNull Call<HocPhiModel> call, @NonNull Response<HocPhiModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtBalance.setText(formatter.format(response.body().getCurrentBalance()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<HocPhiModel> call, @NonNull Throwable t) {}
        });
    }

    private void loadNghiaVu() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        hocphiapi api = RetrofitClient.getClient().create(hocphiapi.class);
        api.getNghiaVu(studentId).enqueue(new Callback<NghiaVuModel>() {
            @Override
            public void onResponse(@NonNull Call<NghiaVuModel> call, @NonNull Response<NghiaVuModel> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body().getItems());
                }
            }
            @Override
            public void onFailure(@NonNull Call<NghiaVuModel> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private class NghiaVuAdapter extends RecyclerView.Adapter<NghiaVuAdapter.ViewHolder> {
        private List<NghiaVuModel.NghiaVuItem> list;
        public NghiaVuAdapter(List<NghiaVuModel.NghiaVuItem> list) { this.list = list; }
        public void updateData(List<NghiaVuModel.NghiaVuItem> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NghiaVuModel.NghiaVuItem item = list.get(position);
            holder.t1.setText(item.getNoiDung());
            holder.t2.setText("Nợ: " + formatter.format(item.getConNo()) + " • " + item.getHocKy());
            holder.t1.setTextColor(0xFF1E293B);
            holder.t2.setTextColor(0xFFEF4444);
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                t1 = itemView.findViewById(android.R.id.text1);
                t2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}