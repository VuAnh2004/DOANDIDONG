package com.example.doanqldiem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private String studentId;
    private final DecimalFormat formatter = new DecimalFormat("#,### ₫");
    private boolean isBalanceVisible = true;
    private double currentBalanceValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hocphi);

        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupInsets();
        setupToolbar();
        setupQuickAmountButtons();
        
        loadDashboard();
        loadNghiaVu();

        findViewById(R.id.btn_deposit).setOnClickListener(v -> handleDeposit());
        
        ImageButton btnToggle = findViewById(R.id.btn_toggle_visibility);
        if (btnToggle != null) {
            btnToggle.setOnClickListener(v -> toggleBalanceVisibility());
        }

        View cardNavUnpaid = findViewById(R.id.card_nav_unpaid);
        if (cardNavUnpaid != null) {
            cardNavUnpaid.setOnClickListener(v -> {
                if (rvNghiaVu.getVisibility() == View.VISIBLE) {
                    rvNghiaVu.setVisibility(View.GONE);
                } else {
                    rvNghiaVu.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void toggleBalanceVisibility() {
        isBalanceVisible = !isBalanceVisible;
        ImageButton btnToggle = findViewById(R.id.btn_toggle_visibility);
        if (isBalanceVisible) {
            txtBalance.setText(formatter.format(currentBalanceValue));
            btnToggle.setImageResource(R.drawable.ic_eye);
        } else {
            txtBalance.setText("********");
            btnToggle.setImageResource(R.drawable.ic_eye_off);
        }
    }

    private void setupQuickAmountButtons() {
        int[] ids = {R.id.btn_100k, R.id.btn_200k, R.id.btn_500k, R.id.btn_1m};
        String[] amounts = {"100000", "200000", "500000", "1000000"};

        for (int i = 0; i < ids.length; i++) {
            final String amount = amounts[i];
            TextView btn = findViewById(ids[i]);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    edtAmount.setText(amount);
                    // handleDeposit() call removed to require explicit click on "NẠP TIỀN"
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (studentId != null && !studentId.isEmpty()) {
            loadDashboard();
            loadNghiaVu();
        }
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
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        
        ImageView btnBell = findViewById(R.id.btn_bell);
        if (btnBell != null) {
            btnBell.setOnClickListener(v -> {
                Intent intent = new Intent(this, thongbaoActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                Intent intent = new Intent(this, cauhinhActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void handleDeposit() {
        String amountStr = edtAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 5000) {
            Toast.makeText(this, "Số tiền tối thiểu là 5.000đ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

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
                    Toast.makeText(hocphiActivity.this, "Lỗi tạo link thanh toán", Toast.LENGTH_SHORT).show();
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
                    currentBalanceValue = response.body().getCurrentBalance();
                    if (isBalanceVisible) {
                        txtBalance.setText(formatter.format(currentBalanceValue));
                    }
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nghia_vu, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NghiaVuModel.NghiaVuItem item = list.get(position);
            holder.txtTitle.setText(item.getNoiDung());
            holder.txtSubtitle.setText(item.getHocKy());
            holder.txtAmount.setText(formatter.format(item.getConNo()));
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtTitle, txtSubtitle, txtAmount;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txt_item_title);
                txtSubtitle = itemView.findViewById(R.id.txt_item_subtitle);
                txtAmount = itemView.findViewById(R.id.txt_item_amount);
            }
        }
    }
}