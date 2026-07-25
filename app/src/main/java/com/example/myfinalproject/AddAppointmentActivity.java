package com.example.myfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityAddAppointmentBinding;
import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private ActivityAddAppointmentBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private long appointmentId = -1;
    private boolean saving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(AddAppointmentActivity.this);
        sessionManager = new SessionManager(AddAppointmentActivity.this);

        binding.etDate.setFocusable(false);
        binding.etTime.setFocusable(false);

        appointmentId = getIntent().getLongExtra("appointment_id", -1);

        if (appointmentId > 0) {
            loadAppointment();
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        binding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointment();
            }
        });
    }

    private void loadAppointment() {
        Appointment appointment = databaseHelper.getAppointment(appointmentId, sessionManager.getUserId());

        if (appointment == null) {
            Toast.makeText(this, "الموعد غير موجود", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.tvAddAppTitle.setText("تعديل موعد");
        binding.btnSave.setText("حفظ التعديلات");
        binding.etSubject.setText(appointment.getTitle());
        binding.etDoctor.setText(appointment.getDoctorName());
        binding.etDate.setText(appointment.getDate());
        binding.etTime.setText(appointment.getTime());
        binding.etLocation.setText(appointment.getLocation());
        binding.etPhone.setText(appointment.getPhone());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                AddAppointmentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.etDate.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                AddAppointmentActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        binding.etTime.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void saveAppointment() {
        if (saving) return;

        String title = binding.etSubject.getText().toString().trim();
        String doctor = binding.etDoctor.getText().toString().trim();
        String date = binding.etDate.getText().toString().trim();
        String time = binding.etTime.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (title.isEmpty()) {
            binding.etSubject.setError("أدخلي اسم الموعد");
            binding.etSubject.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            binding.etDate.setError("اختاري التاريخ");
            return;
        }

        if (time.isEmpty()) {
            binding.etTime.setError("اختاري الوقت");
            return;
        }

        saving = true;
        binding.btnSave.setEnabled(false);

        Appointment appointment = new Appointment(
                appointmentId,
                sessionManager.getUserId(),
                title, doctor, date, time, location, phone
        );

        boolean success;
        if (appointmentId > 0) {
            success = databaseHelper.updateAppointment(appointment);
        } else {
            success = databaseHelper.insertAppointment(appointment) != -1;
        }

        if (success) {
            if (appointmentId > 0) {
                Toast.makeText(this, "تم تعديل الموعد", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "تمت إضافة الموعد", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK);
            finish();
        } else {
            saving = false;
            binding.btnSave.setEnabled(true);
            Toast.makeText(this, "تعذر حفظ الموعد", Toast.LENGTH_SHORT).show();
        }
    }
}
