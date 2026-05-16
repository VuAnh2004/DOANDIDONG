package com.example.doanqldiem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import api.RetrofitClient;
import api.profileapi;
import model.ProfileResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateHosohoaFragment extends Fragment {

    private AutoCompleteTextView spinDocType;
    private TextInputEditText edtNotes;
    private TextView tvFileName;
    private RecyclerView rvHosoList;
    private Uri selectedFileUri;
    private String studentId; // Đã bỏ gán cứng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_hosohoa, container, false);

        // Lấy StudentID từ SharedPreferences
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
            studentId = prefs.getString("StudentID", "");
        }

        spinDocType = view.findViewById(R.id.spin_doc_type);
        edtNotes = view.findViewById(R.id.edt_notes);
        tvFileName = view.findViewById(R.id.tv_file_name);
        rvHosoList = view.findViewById(R.id.rv_hoso_list);

        rvHosoList.setLayoutManager(new LinearLayoutManager(getContext()));

        setupDocTypeSpinner();
        
        view.findViewById(R.id.btn_pick_file).setOnClickListener(v -> openFilePicker());
        view.findViewById(R.id.btn_upload_doc).setOnClickListener(v -> uploadHoso());

        if (studentId != null && !studentId.isEmpty()) {
            loadHosoList();
        }
        return view;
    }

    private void setupDocTypeSpinner() {
        String[] types = {"Giấy khai sinh", "Căn cước công dân", "Hộ khẩu", "Giấy chứng nhận tốt nghiệp THCS","Học bạ THCS","Giấy khám sức khỏe","Ảnh 3x4","Giấy xác nhận đối tượng chính sách\n" +
                "(nếu có)"};
        spinDocType.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, types));
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    tvFileName.setText(getFileName(selectedFileUri));
                }
            }
    );

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            }
        }
        return result != null ? result : "Tệp không tên";
    }

    private void loadHosoList() {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getProfile(studentId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Logic hiển thị danh sách hồ sơ
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {}
        });
    }

    private void uploadHoso() {
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String docType = spinDocType.getText().toString();
        String notes = edtNotes.getText().toString();

        if (docType.isEmpty() || selectedFileUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn loại giấy tờ và tệp tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream is = requireContext().getContentResolver().openInputStream(selectedFileUri);
            byte[] bytes = getBytes(is);
            RequestBody requestFile = RequestBody.create(MediaType.parse(requireContext().getContentResolver().getType(selectedFileUri)), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", getFileName(selectedFileUri), requestFile);

            RequestBody rStudentId = RequestBody.create(MediaType.parse("text/plain"), studentId);
            RequestBody rDocType = RequestBody.create(MediaType.parse("text/plain"), docType);
            RequestBody rNotes = RequestBody.create(MediaType.parse("text/plain"), notes);

            profileapi api = RetrofitClient.getClient().create(profileapi.class);
            api.updateHosohoa(rStudentId, rDocType, rNotes, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Tải lên thành công", Toast.LENGTH_SHORT).show();
                        loadHosoList();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Lỗi tải lên", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead; byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) { buffer.write(data, 0, nRead); }
        return buffer.toByteArray();
    }
}