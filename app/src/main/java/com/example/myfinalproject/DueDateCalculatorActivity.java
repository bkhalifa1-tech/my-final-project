package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityDueDateCalculatorBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DueDateCalculatorActivity extends AppCompatActivity {
    private ActivityDueDateCalculatorBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDueDateCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(DueDateCalculatorActivity.this);
        sessionManager = new SessionManager(DueDateCalculatorActivity.this);

        binding.btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // قيد يمنع اختيار أي تاريخ بعد الوقت الحالي (تحديد الأيام السابقة فقط)
                CalendarConstraints constraints = new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointBackward.now())
                        .build();

                // بناء التقويم مع تمرير القيود
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("اختر تاريخ آخر دورة شهرية")
                        .setCalendarConstraints(constraints)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(getSupportFragmentManager(), "PREGNANCY_DATE_PICKER");

                // استقبال التاريخ المختار بالطريقة التقليدية بدلاً من Lambda
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);

                        selectedDate = PregnancyUtils.formatDate(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                        binding.tvSelectedDate.setText("تاريخ آخر دورة: " + sdf.format(calendar.getTime()));
                    }
                });
            }
        });

        binding.btnCalcAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DueDateCalculatorActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void calculate() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "اختاري تاريخ آخر دورة أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        String dueDate = PregnancyUtils.calculateDueDate(selectedDate);

        boolean success = databaseHelper.updatePregnancyDates(
                sessionManager.getUserId(), selectedDate, dueDate
        );

        if (!success) {
            Toast.makeText(this, "تعذر حفظ البيانات", Toast.LENGTH_SHORT).show();
            return;
        }

        // إظهار النتائج والزر الجديد
        binding.tvResultLabel.setVisibility(View.VISIBLE);
        binding.tvResultValue.setVisibility(View.VISIBLE);
        binding.vResultBg.setVisibility(View.VISIBLE);
        binding.btnGoHome.setVisibility(View.VISIBLE);

        binding.tvResultValue.setText(dueDate);

        // إخفاء أزرار الحساب بعد ظهور النتيجة لتجنب التكرار
        binding.btnPickDate.setVisibility(View.GONE);
        binding.btnCalcAction.setVisibility(View.GONE);
    }
}
