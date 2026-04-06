package com.example.doanqldiem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import api.AddressApi;
import api.RetrofitClient;
import api.profileapi;
import model.AddressModel;
import model.ProfileResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateInfoFragment extends Fragment {

    private TextInputEditText edtBirth, edtPhone, edtHamlet;
    private AutoCompleteTextView spinGender, spinProvince, spinCommune;
    private ImageView imgEditAvatar;
    private Uri selectedImageUri;
    private final String studentId = "24290001";
    
    private List<AddressModel> provinces = new ArrayList<>();
    private List<AddressModel> wards = new ArrayList<>();
    private AddressApi addressApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);
        
        initAddressApi();
        initViews(view);
        setupEvents(view);
        loadInitialData();
        loadProvinces();
        
        return view;
    }

    private void initAddressApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://provinces.open-api.vn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        addressApi = retrofit.create(AddressApi.class);
    }

    private void initViews(View v) {
        imgEditAvatar = v.findViewById(R.id.img_edit_avatar);
        edtBirth = v.findViewById(R.id.edt_birth);
        edtPhone = v.findViewById(R.id.edt_phone);
        edtHamlet = v.findViewById(R.id.edt_hamlet);
        spinGender = v.findViewById(R.id.spin_gender);
        spinProvince = v.findViewById(R.id.spin_province);
        spinCommune = v.findViewById(R.id.spin_commune);
    }

    private void setupEvents(View v) {
        if (edtBirth != null) {
            edtBirth.setOnClickListener(view -> showDatePicker());
        }

        View btnChangeAvatar = v.findViewById(R.id.btn_change_avatar);
        if (btnChangeAvatar != null) {
            btnChangeAvatar.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            });
        }

        View btnSaveInfo = v.findViewById(R.id.btn_save_info);
        if (btnSaveInfo != null) {
            btnSaveInfo.setOnClickListener(view -> updateInfo());
        }

        // Đảm bảo click vào là hiện dropdown ngay
        View.OnClickListener dropdownOpener = view -> ((AutoCompleteTextView)view).showDropDown();
        spinGender.setOnClickListener(dropdownOpener);
        spinProvince.setOnClickListener(dropdownOpener);
        spinCommune.setOnClickListener(dropdownOpener);

        String[] genders = {"Nam", "Nữ", "Khác"};
        if (spinGender != null) {
            spinGender.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, genders));
        }

        if (spinProvince != null) {
            spinProvince.setOnItemClickListener((parent, view1, position, id) -> {
                AddressModel selected = (AddressModel) parent.getItemAtPosition(position);
                loadWards(selected.getCode());
            });
        }
    }

    private void loadProvinces() {
        addressApi.getProvinces().enqueue(new Callback<List<AddressModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AddressModel>> call, @NonNull Response<List<AddressModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinces = response.body();
                    ArrayAdapter<AddressModel> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_list_item_1, provinces);
                    if (spinProvince != null) {
                        spinProvince.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<AddressModel>> call, @NonNull Throwable t) {}
        });
    }

    private void loadWards(int provinceCode) {
        // Depth = 2 để lấy danh sách Quận/Huyện/Xã tùy thuộc vào cấp
        addressApi.getWards(provinceCode, 2).enqueue(new Callback<AddressModel>() {
            @Override
            public void onResponse(@NonNull Call<AddressModel> call, @NonNull Response<AddressModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wards = response.body().getSubDivisions();
                    if (wards != null) {
                        ArrayAdapter<AddressModel> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_list_item_1, wards);
                        if (spinCommune != null) {
                            spinCommune.setAdapter(adapter);
                            spinCommune.setText(""); // Reset
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<AddressModel> call, @NonNull Throwable t) {}
        });
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (imgEditAvatar != null) {
                        imgEditAvatar.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            if (edtBirth != null) {
                edtBirth.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadInitialData() {
        profileapi api = RetrofitClient.getClient().create(profileapi.class);
        api.getProfile(studentId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse.ProfileData p = response.body().getProfile();
                    if (edtBirth != null) edtBirth.setText(p.getBirth());
                    if (edtPhone != null) edtPhone.setText(p.getNumberPhone());
                    if (edtHamlet != null) edtHamlet.setText(p.getHamlet());
                    if (spinGender != null) spinGender.setText(p.getGender(), false);
                    if (spinProvince != null) spinProvince.setText(p.getProvince(), false);
                    if (spinCommune != null) spinCommune.setText(p.getCommune(), false);
                    
                    if (p.getImages() != null && imgEditAvatar != null) {
                        String imageUrl = RetrofitClient.getClient().baseUrl().toString() + p.getImages().substring(1);
                        Glide.with(UpdateInfoFragment.this).load(imageUrl).placeholder(R.drawable.anhuser).into(imgEditAvatar);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {}
        });
    }

    private void updateInfo() {
        try {
            profileapi api = RetrofitClient.getClient().create(profileapi.class);
            
            String hamlet = edtHamlet != null ? edtHamlet.getText().toString() : "";
            String commune = spinCommune != null ? spinCommune.getText().toString() : "";
            String province = spinProvince != null ? spinProvince.getText().toString() : "";
            String birth = edtBirth != null ? edtBirth.getText().toString() : "";
            String gender = spinGender != null ? spinGender.getText().toString() : "";
            String phone = edtPhone != null ? edtPhone.getText().toString() : "";

            String fullAddress = hamlet + ", " + commune + ", " + province + ", Việt Nam";

            RequestBody rSid = RequestBody.create(studentId, MediaType.parse("text/plain"));
            RequestBody rBirth = RequestBody.create(birth, MediaType.parse("text/plain"));
            RequestBody rGender = RequestBody.create(gender, MediaType.parse("text/plain"));
            RequestBody rPhone = RequestBody.create(phone, MediaType.parse("text/plain"));
            RequestBody rProv = RequestBody.create(province, MediaType.parse("text/plain"));
            RequestBody rComm = RequestBody.create(commune, MediaType.parse("text/plain"));
            RequestBody rHaml = RequestBody.create(hamlet, MediaType.parse("text/plain"));
            RequestBody rAddr = RequestBody.create(fullAddress, MediaType.parse("text/plain"));

            MultipartBody.Part imagePart = null;
            if (selectedImageUri != null) {
                InputStream is = requireContext().getContentResolver().openInputStream(selectedImageUri);
                if (is != null) {
                    byte[] bytes = getBytes(is);
                    String mimeType = requireContext().getContentResolver().getType(selectedImageUri);
                    RequestBody reqFile = RequestBody.create(bytes, MediaType.parse(mimeType != null ? mimeType : "image/*"));
                    imagePart = MultipartBody.Part.createFormData("ImageFile", "avatar.jpg", reqFile);
                }
            }

            api.updateProfile(rSid, rSid, rBirth, rGender, rAddr, rPhone, rProv, rComm, rHaml, imagePart)
               .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int nRead; byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) { byteBuffer.write(data, 0, nRead); }
        return byteBuffer.toByteArray();
    }
}
