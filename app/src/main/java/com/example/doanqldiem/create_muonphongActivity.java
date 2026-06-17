package com.example.doanqldiem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import api.RetrofitClient;
import api.muonphongapi;
import model.BookingRequest;
import model.RoomModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class create_muonphongActivity extends AppCompatActivity {

    private AutoCompleteTextView spRoom;
    private TextInputEditText edtPurpose, edtStartTime, edtEndTime;
    private MaterialButton btnSubmit, btnCancel;

    private String studentId; // Đã bỏ gán cứng

    private List<RoomModel> roomList;
    private RoomModel selectedRoom;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    private boolean isSubmitting = false; // chống spam

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_muonphong);

        // Lấy StudentID từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        studentId = prefs.getString("StudentID", "");

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupEvents();
        loadRooms();
    }

    private void initViews() {
        spRoom = findViewById(R.id.sp_room);
        edtPurpose = findViewById(R.id.edt_purpose);
        edtStartTime = findViewById(R.id.edt_start_time);
        edtEndTime = findViewById(R.id.edt_end_time);
        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel = findViewById(R.id.btn_cancel);

        ImageView btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
            });
        }
    }

    private void setupEvents() {

        edtStartTime.setOnClickListener(v -> showDateTimePicker(startCalendar, edtStartTime));
        edtEndTime.setOnClickListener(v -> showDateTimePicker(endCalendar, edtEndTime));

        btnSubmit.setOnClickListener(v -> submitBooking());

        btnCancel.setOnClickListener(v -> finish());
    }

    // ================= LOAD ROOM =================
    private void loadRooms() {
        muonphongapi api = RetrofitClient.getClient().create(muonphongapi.class);

        api.getRooms().enqueue(new Callback<List<RoomModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<RoomModel>> call, @NonNull Response<List<RoomModel>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    roomList = response.body();

                    ArrayAdapter<RoomModel> adapter =
                            new ArrayAdapter<>(create_muonphongActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    roomList);

                    spRoom.setAdapter(adapter);

                    spRoom.setOnItemClickListener((parent, view, position, id) -> {
                        selectedRoom = roomList.get(position);
                    });

                } else {
                    Toast.makeText(create_muonphongActivity.this, "Không load được phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RoomModel>> call, @NonNull Throwable t) {
                Toast.makeText(create_muonphongActivity.this,
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ================= PICK DATE TIME =================
    private void showDateTimePicker(Calendar calendar, TextInputEditText editText) {

        new DatePickerDialog(this, (view, year, month, day) -> {

            calendar.set(year, month, day);

            new TimePickerDialog(this, (view1, hour, minute) -> {

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                // 🔥 FORMAT CHUẨN ASP.NET
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                editText.setText(sdf.format(calendar.getTime()));

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= SUBMIT =================
    private void submitBooking() {

        if (isSubmitting) return;

        String purpose = edtPurpose.getText().toString().trim();
        String startTime = edtStartTime.getText().toString().trim();
        String endTime = edtEndTime.getText().toString().trim();

        // 🔥 VALIDATE
        if (selectedRoom == null) {
            Toast.makeText(this, "Chọn phòng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (purpose.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        isSubmitting = true;
        btnSubmit.setEnabled(false);

        BookingRequest request = new BookingRequest(
                selectedRoom.getRoomID(),
                studentId,
                startTime,
                endTime,
                purpose
        );

        muonphongapi api = RetrofitClient.getClient().create(muonphongapi.class);

        api.bookRoom(request).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {

                isSubmitting = false;
                btnSubmit.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(create_muonphongActivity.this,
                            "Gửi thành công!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(create_muonphongActivity.this,
                            "Lỗi server: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {

                isSubmitting = false;
                btnSubmit.setEnabled(true);

                Toast.makeText(create_muonphongActivity.this,
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}