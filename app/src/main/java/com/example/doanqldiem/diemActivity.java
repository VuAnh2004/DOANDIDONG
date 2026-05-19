package com.example.doanqldiem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import api.RetrofitClient;
import api.diemapi;
import model.DiemModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class diemActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerSubject;
    private TextInputLayout layoutSubject;
    private LinearLayout containerItems;
    private ProgressBar loadingProgress;

    private String studentId;

    private List<DiemModel.SubjectOption> listSubjects =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.diem);

        initViews();

        handleSystemInsets();

        loadStudentId();

        fetchMetadata();

        loadDiemData();

        Button btnSearch =
                findViewById(R.id.btn_search);

        if (btnSearch != null) {

            btnSearch.setOnClickListener(v ->
                    loadDiemData());
        }

        View logo =
                findViewById(R.id.logotruong);

        if (logo != null) {

            logo.setOnClickListener(v ->
                    finish());
        }
    }

    private void initViews() {

        spinnerSubject =
                findViewById(R.id.spinner_subject);

        layoutSubject =
                findViewById(R.id.layout_subject);

        containerItems =
                findViewById(R.id.container_items);

        loadingProgress =
                findViewById(R.id.loading_progress);

        if (spinnerSubject != null &&
                layoutSubject != null) {

            setupSmartFilter(
                    layoutSubject,
                    spinnerSubject
            );
        }
    }

    private void handleSystemInsets() {

        View mainLayout =
                findViewById(R.id.main_diem_layout);

        if (mainLayout != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                    mainLayout,
                    (v, insets) -> {

                        Insets systemBars =
                                insets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                );

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom
                        );

                        return insets;
                    }
            );
        }
    }

    private void setupSmartFilter(
            TextInputLayout layout,
            AutoCompleteTextView spinner
    ) {

        spinner.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        if (s != null &&
                                s.length() > 0) {

                            layout.setEndIconMode(
                                    TextInputLayout.END_ICON_CLEAR_TEXT
                            );

                        } else {

                            layout.setEndIconMode(
                                    TextInputLayout.END_ICON_DROPDOWN_MENU
                            );
                        }
                    }

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        spinner.setOnItemClickListener(
                (parent, view, position, id) ->
                        loadDiemData()
        );
    }

    private void loadStudentId() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "USER",
                        MODE_PRIVATE
                );

        studentId =
                prefs.getString(
                        "StudentID",
                        "24290001"
                );
    }

    private void fetchMetadata() {

        diemapi api =
                RetrofitClient
                        .getClient()
                        .create(diemapi.class);

        api.getMetadata()
                .enqueue(
                        new Callback<diemapi.MetadataResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<diemapi.MetadataResponse> call,
                                    @NonNull Response<diemapi.MetadataResponse> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    listSubjects =
                                            response.body().subjects;

                                    ArrayAdapter<DiemModel.SubjectOption> adapter =
                                            new ArrayAdapter<>(
                                                    diemActivity.this,
                                                    android.R.layout.simple_list_item_1,
                                                    listSubjects
                                            );

                                    spinnerSubject.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<diemapi.MetadataResponse> call,
                                    @NonNull Throwable t
                            ) {

                                Log.e(
                                        "API_ERROR",
                                        t.getMessage()
                                );
                            }
                        }
                );
    }

    private void loadDiemData() {

        if (loadingProgress != null) {

            loadingProgress.setVisibility(
                    View.VISIBLE
            );
        }

        String subName =
                spinnerSubject != null
                        ? spinnerSubject.getText().toString().trim()
                        : "";

        Integer subId = null;

        if (listSubjects != null &&
                !subName.isEmpty()) {

            for (DiemModel.SubjectOption s : listSubjects) {

                if (s.SubjectName != null &&
                        s.SubjectName.equals(subName)) {

                    subId = s.SubjectID;
                    break;
                }
            }
        }

        diemapi api =
                RetrofitClient
                        .getClient()
                        .create(diemapi.class);

        api.getDiem(
                        studentId,
                        null,
                        null,
                        subId
                )
                .enqueue(
                        new Callback<List<DiemModel>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<List<DiemModel>> call,
                                    @NonNull Response<List<DiemModel>> response
                            ) {

                                if (loadingProgress != null) {

                                    loadingProgress.setVisibility(
                                            View.GONE
                                    );
                                }

                                containerItems.removeAllViews();

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && !response.body().isEmpty()) {

                                    int stt = 1;

                                    for (DiemModel m : response.body()) {

                                        View row =
                                                LayoutInflater
                                                        .from(diemActivity.this)
                                                        .inflate(
                                                                R.layout.diem_item_row,
                                                                containerItems,
                                                                false
                                                        );

                                        populateRow(
                                                row,
                                                m,
                                                stt++
                                        );

                                        containerItems.addView(row);
                                    }

                                } else {

                                    Toast.makeText(
                                            diemActivity.this,
                                            "Không có dữ liệu",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<List<DiemModel>> call,
                                    @NonNull Throwable t
                            ) {

                                if (loadingProgress != null) {

                                    loadingProgress.setVisibility(
                                            View.GONE
                                    );
                                }

                                Toast.makeText(
                                        diemActivity.this,
                                        "Lỗi kết nối",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    private void populateRow(
            View v,
            DiemModel m,
            int stt
    ) {

        try {

            TextView txtStt =
                    v.findViewById(R.id.txt_stt);

            TextView txtYear =
                    v.findViewById(R.id.txt_year_item);

            TextView txtSubject =
                    v.findViewById(R.id.txt_subject_name);

            TextView txtAvg =
                    v.findViewById(R.id.txt_avg_year);

            txtStt.setText(String.valueOf(stt));

            txtYear.setText(m.getSemesterCode());

            txtSubject.setText(m.getSubjectName());

            double avg =
                    m.getAverageScore() != null
                            ? m.getAverageScore()
                            : 0.0;

            txtAvg.setText(String.valueOf(avg));

            if (avg >= 5) {

                txtAvg.setBackgroundColor(0xFF10B981);

            } else {

                txtAvg.setBackgroundColor(0xFFEF4444);
            }

            ImageView imgStatus =
                    v.findViewById(R.id.img_status_check);

            if (imgStatus != null) {

                if (avg >= 5) {

                    imgStatus.setImageResource(
                            android.R.drawable.checkbox_on_background
                    );

                    imgStatus.setColorFilter(0xFF10B981);

                } else {

                    imgStatus.setImageResource(
                            android.R.drawable.checkbox_off_background
                    );

                    imgStatus.setColorFilter(0xFFEF4444);
                }
            }

            View header =
                    v.findViewById(R.id.layout_header_clickable);

            LinearLayout detail =
                    v.findViewById(R.id.layout_subject_detail);

            ImageView arrow =
                    v.findViewById(R.id.img_expand);

            if (header != null &&
                    detail != null) {

                header.setOnClickListener(view -> {

                    boolean isVisible =
                            detail.getVisibility()
                                    == View.VISIBLE;

                    detail.setVisibility(
                            isVisible
                                    ? View.GONE
                                    : View.VISIBLE
                    );

                    if (arrow != null) {

                        arrow.animate()
                                .rotation(
                                        isVisible ? 0 : 180
                                )
                                .setDuration(200);
                    }
                });
            }

            String hk =
                    m.getSemesterCode();

            if (hk != null) {

                if (hk.contains("1")) {

                    hk = "HK1";

                } else if (hk.contains("2")) {

                    hk = "HK2";
                }
            }

            ((TextView) v.findViewById(R.id.txt_sem_name))
                    .setText(hk);

            ((TextView) v.findViewById(R.id.txt_score_mid))
                    .setText(
                            formatScore(
                                    m.getMidtermScore()
                            )
                    );

            ((TextView) v.findViewById(R.id.txt_score_final))
                    .setText(
                            formatScore(
                                    m.getFinal_score()
                            )
                    );

            ((TextView) v.findViewById(R.id.txt_score_sem_avg))
                    .setText(
                            formatScore(
                                    m.getAverageScore()
                            )
                    );

            ((TextView) v.findViewById(R.id.txt_score_oral))
                    .setText("10, 6, 7");

            ((TextView) v.findViewById(R.id.txt_score_15m))
                    .setText("7, 9");

        } catch (Exception e) {

            Log.e(
                    "UI_ERROR",
                    "PopulateRow: " + e.getMessage()
            );
        }
    }

    private String formatScore(Double d) {

        return (d != null && d >= 0)
                ? String.valueOf(d)
                : "---";
    }
}