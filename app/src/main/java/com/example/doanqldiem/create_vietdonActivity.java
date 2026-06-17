package com.example.doanqldiem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import api.RetrofitClient;
import api.vietdonapi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Kế thừa BaseActivity để tự động có Menu đổ xuống ở Header
 */
public class create_vietdonActivity extends BaseActivity {

    private TextInputEditText edtStartDate, edtEndDate, edtReason, edtRequestDate;
    private TextView txtFileName;
    private Uri selectedFileUri;
    private String studentId;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vietdon);

        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbarActions();
        setupDatePickers();
        setCurrentDate();

        findViewById(R.id.btn_choose_file).setOnClickListener(v -> openFilePicker());
        findViewById(R.id.btn_submit_leave).setOnClickListener(v -> submitLeaveRequest());
    }

    private void initViews() {
        edtRequestDate = findViewById(R.id.edt_request_date);
        edtStartDate = findViewById(R.id.edt_start_date);
        edtEndDate = findViewById(R.id.edt_end_date);
        edtReason = findViewById(R.id.edt_reason);
        txtFileName = findViewById(R.id.txt_file_name);

        TextInputEditText edtSid = findViewById(R.id.edt_student_id);
        if (edtSid != null) edtSid.setText(studentId);
    }

    private void setupToolbarActions() {
        // Nút Quay lại
        View logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        // Nút Cài đặt
        View btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> startActivity(new Intent(this, cauhinhActivity.class)));
        }
        
        // Nút Thông báo
        View btnBell = findViewById(R.id.btn_bell);
        if (btnBell != null) {
            btnBell.setOnClickListener(v -> startActivity(new Intent(this, thongbaoActivity.class)));
        }
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        edtRequestDate.setText(sdf.format(calendar.getTime()));
    }

    private void setupDatePickers() {
        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));
    }

    private void showDatePicker(TextInputEditText target) {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            target.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    txtFileName.setText(getFileName(selectedFileUri));
                }
            });

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void submitLeaveRequest() {
        String reason = edtReason.getText().toString().trim();
        String start = edtStartDate.getText().toString().trim();
        String end = edtEndDate.getText().toString().trim();

        if (reason.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rStudentId = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody rReason = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), reason);
        RequestBody rStart = RequestBody.create(MediaType.parse("text/plain"), start);
        RequestBody rEnd = RequestBody.create(MediaType.parse("text/plain"), end);

        MultipartBody.Part filePart = null;
        if (selectedFileUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                byte[] bytes = getBytes(inputStream);
                String type = getContentResolver().getType(selectedFileUri);
                if (type == null) type = "application/octet-stream";
                RequestBody requestFile = RequestBody.create(MediaType.parse(type), bytes);
                filePart = MultipartBody.Part.createFormData("fileUpload", getFileName(selectedFileUri), requestFile);
            } catch (Exception e) { e.printStackTrace(); }
        }

        vietdonapi api = RetrofitClient.getClient().create(vietdonapi.class);
        api.taoDon(rStudentId, rStart, rEnd, rReason, filePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(create_vietdonActivity.this, "Gửi đơn thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(create_vietdonActivity.this, "Lỗi server", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(create_vietdonActivity.this, "Lỗi kết nối", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int n;
        while ((n = inputStream.read(data)) != -1) { buffer.write(data, 0, n); }
        return buffer.toByteArray();
    }
}
