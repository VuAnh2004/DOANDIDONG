package com.example.doanqldiem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class create_vietdonActivity extends AppCompatActivity {

    private TextInputEditText edtStartDate, edtEndDate, edtReason, edtRequestDate;
    private TextView txtFileName;

    private Uri selectedFileUri;

    private final String studentId = "24290001";
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vietdon);

        initViews();
        setupToolbar();
        setupDatePickers();
        setCurrentDate();

        findViewById(R.id.btn_choose_file).setOnClickListener(v -> openFilePicker());
        findViewById(R.id.btn_submit_leave).setOnClickListener(v -> submitLeaveRequest());
    }

    // ================= INIT =================
    private void initViews() {
        edtRequestDate = findViewById(R.id.edt_request_date);
        edtStartDate = findViewById(R.id.edt_start_date);
        edtEndDate = findViewById(R.id.edt_end_date);
        edtReason = findViewById(R.id.edt_reason);
        txtFileName = findViewById(R.id.txt_file_name);

        TextInputEditText edtSid = findViewById(R.id.edt_student_id);
        if (edtSid != null) edtSid.setText(studentId);
    }

    // ================= TOOLBAR =================
    private void setupToolbar() {
        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) logo.setOnClickListener(v -> finish());

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) btnSetting.setOnClickListener(v -> finish());
    }

    // ================= DATE =================
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

            // 🔥 FORMAT CHUẨN ASP.NET
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            target.setText(sdf.format(calendar.getTime()));

        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= FILE =================
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
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

    // ================= SUBMIT =================
    private void submitLeaveRequest() {

        String reason = edtReason.getText().toString().trim();
        String start = edtStartDate.getText().toString().trim();
        String end = edtEndDate.getText().toString().trim();

        if (reason.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== TEXT PART =====
        RequestBody rStudentId = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody rReason = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), reason);
        RequestBody rStart = RequestBody.create(MediaType.parse("text/plain"), start);
        RequestBody rEnd = RequestBody.create(MediaType.parse("text/plain"), end);

        // ===== FILE PART =====
        MultipartBody.Part filePart = null;

        if (selectedFileUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                byte[] bytes = getBytes(inputStream);

                String type = getContentResolver().getType(selectedFileUri);
                if (type == null) type = "application/octet-stream";

                RequestBody requestFile = RequestBody.create(MediaType.parse(type), bytes);

                // 🔥 QUAN TRỌNG: tên phải giống backend
                filePart = MultipartBody.Part.createFormData(
                        "fileUpload",
                        getFileName(selectedFileUri),
                        requestFile
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ===== CALL API =====
        vietdonapi api = RetrofitClient.getClient().create(vietdonapi.class);

        api.taoDon(rStudentId, rStart, rEnd, rReason, filePart)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call,
                                           @NonNull Response<ResponseBody> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(create_vietdonActivity.this,
                                    "Gửi đơn thành công!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(create_vietdonActivity.this,
                                    "Lỗi server: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call,
                                          @NonNull Throwable t) {

                        Toast.makeText(create_vietdonActivity.this,
                                "Lỗi: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int n;

        while ((n = inputStream.read(data)) != -1) {
            buffer.write(data, 0, n);
        }

        return buffer.toByteArray();
    }
}