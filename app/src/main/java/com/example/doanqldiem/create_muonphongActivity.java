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

public class create_muonphongActivity extends BaseActivity {

    private AutoCompleteTextView spRoom;
    private TextInputEditText edtPurpose, edtStartTime, edtEndTime;
    private MaterialButton btnSubmit, btnCancel;

    private String studentId;

    private List<RoomModel> roomList;
    private RoomModel selectedRoom;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    
    // Format hiển thị ngắn gọn giúp giao diện cân đối (Ví dụ: 14:30 15/06/26)
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault());
    private final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private boolean isSubmitting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_muonphong);

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

        // Header click listeners
        View btnSetting = findViewById(R.id.btn_setting);
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                startActivity(new Intent(this, cauhinhActivity.class));
            });
        }

        View btnBell = findViewById(R.id.btn_bell);
        if (btnBell != null) {
            btnBell.setOnClickListener(v -> {
                startActivity(new Intent(this, thongbaoActivity.class));
            });
        }

        // Logic btn_user (avatar) đã được xử lý tự động bởi BaseActivity để hiện PopupMenu

        ImageView logo = findViewById(R.id.logotruong);
        if (logo != null) {
            logo.setOnClickListener(v -> finish());
        }
    }

    private void setupEvents() {
        edtStartTime.setOnClickListener(v -> showDateTimePicker(startCalendar, edtStartTime));
        edtEndTime.setOnClickListener(v -> showDateTimePicker(endCalendar, edtEndTime));
        btnSubmit.setOnClickListener(v -> submitBooking());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadRooms() {
        muonphongapi api = RetrofitClient.getClient().create(muonphongapi.class);
        api.getRooms().enqueue(new Callback<List<RoomModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<RoomModel>> call, @NonNull Response<List<RoomModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    roomList = response.body();
                    ArrayAdapter<RoomModel> adapter = new ArrayAdapter<>(create_muonphongActivity.this,
                                    android.R.layout.simple_list_item_1, roomList);
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
                Toast.makeText(create_muonphongActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDateTimePicker(Calendar calendar, TextInputEditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                editText.setText(displayFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void submitBooking() {
        if (isSubmitting) return;

        String purpose = edtPurpose.getText().toString().trim();
        
        if (selectedRoom == null) {
            Toast.makeText(this, "Vui lòng chọn phòng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (purpose.isEmpty() || edtStartTime.getText().toString().isEmpty() || edtEndTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (endCalendar.before(startCalendar)) {
            Toast.makeText(this, "Thời gian trả phải sau thời gian mượn!", Toast.LENGTH_SHORT).show();
            return;
        }

        isSubmitting = true;
        btnSubmit.setEnabled(false);

        BookingRequest request = new BookingRequest(
                selectedRoom.getRoomID(),
                studentId,
                apiFormat.format(startCalendar.getTime()),
                apiFormat.format(endCalendar.getTime()),
                purpose
        );

        muonphongapi api = RetrofitClient.getClient().create(muonphongapi.class);
        api.bookRoom(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                isSubmitting = false;
                btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(create_muonphongActivity.this, "Gửi đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(create_muonphongActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                isSubmitting = studentId != null; // Mượn tạm logic để reset state
                isSubmitting = false;
                btnSubmit.setEnabled(true);
                Toast.makeText(create_muonphongActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}