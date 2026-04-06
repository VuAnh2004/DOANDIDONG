package com.example.doanqldiem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class thanhtoanthanhcongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanhtoanthanhcong);

        // Xử lý dữ liệu từ Deep Link
        handleDeepLink(getIntent());

        // Nút quay về Home
        findViewById(R.id.btn_back_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, hocphiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "payment-success".equals(data.getHost())) {
            DecimalFormat df = new DecimalFormat("#,###");

            // Lấy các tham số
            String balance = data.getQueryParameter("balance");
            String orderId = data.getQueryParameter("orderId");
            String txnId = data.getQueryParameter("txnId");
            String amount = data.getQueryParameter("amount");
            String date = data.getQueryParameter("date");
            String desc = data.getQueryParameter("desc");
            String name = data.getQueryParameter("name");

            // Parse amount và balance an toàn
            double amountValue = 0;
            double balanceValue = 0;
            try {
                if (!TextUtils.isEmpty(amount)) amountValue = Double.parseDouble(amount);
                if (!TextUtils.isEmpty(balance)) balanceValue = Double.parseDouble(balance);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // Hiển thị tên người nhận
            if (!TextUtils.isEmpty(name)) {
                ((TextView) findViewById(R.id.tv_recipient_name))
                        .setText("tới " + Uri.decode(name).toUpperCase());
            }

            // Hiển thị số tiền nạp ở trung tâm
            TextView tvAmount = findViewById(R.id.tv_amount_display);
            tvAmount.setText("VND " + df.format(amountValue));

            // Đổ dữ liệu vào các dòng hóa đơn
            setupRow(findViewById(R.id.row_order_id), "Mã đơn hàng", orderId);
            setupRow(findViewById(R.id.row_transaction_id), "Mã giao dịch (VNPAY)", txnId);
            setupRow(findViewById(R.id.row_description), "Nội dung", !TextUtils.isEmpty(desc) ? Uri.decode(desc) : "Nạp tiền vào tài khoản");
            setupRow(findViewById(R.id.row_date), "Ngày thực hiện", !TextUtils.isEmpty(date) ? Uri.decode(date) : "---");


            // Thông báo thành công
            Toast.makeText(this, "Nạp tiền thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRow(View rowView, String label, String value) {
        if (rowView != null) {
            TextView txtLabel = rowView.findViewById(R.id.txt_label);
            TextView txtValue = rowView.findViewById(R.id.txt_value);
            if (txtLabel != null) txtLabel.setText(label);
            if (txtValue != null) txtValue.setText(!TextUtils.isEmpty(value) ? value : "---");
        }
    }
}