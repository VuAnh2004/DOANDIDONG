package com.example.doanqldiem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
import java.text.DecimalFormat;

public class thanhtoanthanhcongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanhtoanthanhcong);

        // Xử lý dữ liệu từ Intent
        handleDeepLink(getIntent());

        // Nút quay về Home trong Toolbar
        findViewById(R.id.btn_back_home_toolbar).setOnClickListener(v -> goHome());
        
        // Nút Hoàn thành
        findViewById(R.id.btn_back_home).setOnClickListener(v -> goHome());

        // Gán Icon và màu sắc đặc trưng cho từng hàng chi tiết
        setupRowIcons();
    }

    private void goHome() {
        Intent intent = new Intent(this, hocphiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setupRowIcons() {
        // Hàng 1: Mã đơn hàng (Xanh dương)
        View row1 = findViewById(R.id.row_order_id);
        ((MaterialCardView) row1.findViewById(R.id.card_icon_bg)).setCardBackgroundColor(0xFFE0E7FF);
        ((ImageView) row1.findViewById(R.id.img_row_icon)).setImageResource(R.drawable.ic_excel);
        ((ImageView) row1.findViewById(R.id.img_row_icon)).setColorFilter(0xFF2563EB);
        row1.findViewById(R.id.img_copy).setVisibility(View.VISIBLE);

        // Hàng 2: Mã giao dịch (Xanh lá)
        View row2 = findViewById(R.id.row_transaction_id);
        ((MaterialCardView) row2.findViewById(R.id.card_icon_bg)).setCardBackgroundColor(0xFFDCFCE7);
        ((ImageView) row2.findViewById(R.id.img_row_icon)).setImageResource(R.drawable.vinaptien);
        ((ImageView) row2.findViewById(R.id.img_row_icon)).setColorFilter(0xFF10B981);
        row2.findViewById(R.id.img_copy).setVisibility(View.VISIBLE);

        // Hàng 3: Nội dung (Cam)
        View row3 = findViewById(R.id.row_description);
        ((MaterialCardView) row3.findViewById(R.id.card_icon_bg)).setCardBackgroundColor(0xFFFEF3C7);
        ((ImageView) row3.findViewById(R.id.img_row_icon)).setImageResource(R.drawable.phananh);
        ((ImageView) row3.findViewById(R.id.img_row_icon)).setColorFilter(0xFFD97706);

        // Hàng 4: Ngày thực hiện (Tím)
        View row4 = findViewById(R.id.row_date);
        ((MaterialCardView) row4.findViewById(R.id.card_icon_bg)).setCardBackgroundColor(0xFFF3E8FF);
        ((ImageView) row4.findViewById(R.id.img_row_icon)).setImageResource(R.drawable.ic_calendar);
        ((ImageView) row4.findViewById(R.id.img_row_icon)).setColorFilter(0xFF9333EA);
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        DecimalFormat df = new DecimalFormat("#,###");
        
        if (data != null) {
            String name = data.getQueryParameter("name");
            String amount = data.getQueryParameter("amount");
            String orderId = data.getQueryParameter("orderId");
            String txnId = data.getQueryParameter("txnId");
            String date = data.getQueryParameter("date");
            String desc = data.getQueryParameter("desc");

            if (!TextUtils.isEmpty(name)) {
                ((TextView) findViewById(R.id.tv_recipient_name)).setText("tới " + Uri.decode(name).toUpperCase());
            }

            double amountValue = 0;
            try { if (!TextUtils.isEmpty(amount)) amountValue = Double.parseDouble(amount); } catch (Exception e) {}
            ((TextView) findViewById(R.id.tv_amount_display)).setText(df.format(amountValue) + " VND");

            setupRowText(findViewById(R.id.row_order_id), "Mã đơn hàng", orderId);
            setupRowText(findViewById(R.id.row_transaction_id), "Mã giao dịch (VNPAY)", txnId);
            setupRowText(findViewById(R.id.row_description), "Nội dung", !TextUtils.isEmpty(desc) ? Uri.decode(desc) : "Nạp tiền vào tài khoản");
            setupRowText(findViewById(R.id.row_date), "Ngày thực hiện", !TextUtils.isEmpty(date) ? Uri.decode(date) : "Vừa xong");
        }
    }

    private void setupRowText(View rowView, String label, String value) {
        if (rowView != null) {
            ((TextView) rowView.findViewById(R.id.txt_label)).setText(label);
            ((TextView) rowView.findViewById(R.id.txt_value)).setText(!TextUtils.isEmpty(value) ? value : "---");
        }
    }
}