package com.example.doanqldiem;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import api.RetrofitClient;
import api.phananhapi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class phananhActivity extends AppCompatActivity {

    private TextInputEditText edtStudentId, edtSubmitDate, edtContent;
    private TextView txtFileName;
    private ProgressBar progressBar;
    private final List<Uri> selectedFiles = new ArrayList<>();
    private final String studentId = "24290001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phananh);

        initViews();
        setupToolbar();
        
        // Tự động gán ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        edtSubmitDate.setText(sdf.format(Calendar.getInstance().getTime()));
        edtStudentId.setText(studentId);

        // Xử lý chọn file
        findViewById(R.id.card_upload).setOnClickListener(v -> openFilePicker());

        // Nút gửi
        findViewById(R.id.btn_submit).setOnClickListener(v -> submitPhanAnh());
        //  Click Icon thong bao
        ImageView thongbao = findViewById(R.id.btn_bell);
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                Intent intent = new Intent(phananhActivity.this, thongbaoActivity.class);
                startActivity(intent);
                // Hiệu ứng chuyển cảnh mượt
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void initViews() {
        edtStudentId = findViewById(R.id.edt_student_id);
        edtSubmitDate = findViewById(R.id.edt_submit_date);
        edtContent = findViewById(R.id.edt_content);
        txtFileName = findViewById(R.id.txt_file_name);
        progressBar = findViewById(R.id.loading_progress);
    }

    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());
        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> finish());
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFiles.clear();
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            selectedFiles.add(result.getData().getClipData().getItemAt(i).getUri());
                        }
                        txtFileName.setText("Đã chọn " + count + " tệp");
                    } else if (result.getData().getData() != null) {
                        selectedFiles.add(result.getData().getData());
                        txtFileName.setText(getFileName(result.getData().getData()));
                    }
                }
            }
    );

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filePickerLauncher.launch(intent);
    }

    private void submitPhanAnh() {
        String content = edtContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung phản ánh", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RequestBody rStudentId = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody rContent = RequestBody.create(MediaType.parse("text/plain"), content);

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : selectedFiles) {
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                byte[] bytes = getBytes(is);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), bytes);
                fileParts.add(MultipartBody.Part.createFormData("files", getFileName(uri), requestFile));
            } catch (Exception e) { e.printStackTrace(); }
        }

        phananhapi api = RetrofitClient.getClient().create(phananhapi.class);
        api.guiPhanAnh(rStudentId, rContent, fileParts).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(phananhActivity.this, "Gửi phản ánh thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(phananhActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(phananhActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            }
        }
        return result != null ? result : "Tệp không tên";
    }

    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead; byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) { buffer.write(data, 0, nRead); }
        return buffer.toByteArray();
    }
}