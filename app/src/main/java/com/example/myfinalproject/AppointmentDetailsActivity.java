package com.example.myfinalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityAppointmentDetailsBinding;

public class AppointmentDetailsActivity extends AppCompatActivity {
    private ActivityAppointmentDetailsBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private long appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(AppointmentDetailsActivity.this);
        sessionManager = new SessionManager(AppointmentDetailsActivity.this);
        appointmentId = getIntent().getLongExtra("appointment_id", -1);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnEditApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAppointment();
            }
        });

        binding.vMainInfoBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAppointment();
            }
        });

        binding.btnDeleteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointment();
    }

    private void loadAppointment() {
        Appointment appointment = databaseHelper.getAppointment(appointmentId, sessionManager.getUserId());
        if (appointment == null) {
            Toast.makeText(this, "الموعد غير موجود", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.tvDetailHeading.setText(appointment.getTitle());
        binding.tvDetailDate.setText(appointment.getDate());
        binding.tvDetailTime.setText(appointment.getTime());
        binding.tvDetailDoctor.setText("الطبيب: " + valueOrDefault(appointment.getDoctorName()));
        binding.tvLocationName.setText("المكان: " + valueOrDefault(appointment.getLocation()));
    }

    private String valueOrDefault(String value) {
        return value == null || value.isEmpty() ? "غير محدد" : value;
    }

    private void editAppointment() {
        Intent intent = new Intent(AppointmentDetailsActivity.this, AddAppointmentActivity.class);
        intent.putExtra("appointment_id", appointmentId);
        startActivity(intent);
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("حذف الموعد")
                .setMessage("هل تريدين حذف هذا الموعد؟")
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAppointment();
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void deleteAppointment() {
        boolean success = databaseHelper.deleteAppointment(appointmentId, sessionManager.getUserId());
        if (success) {
            Toast.makeText(this, "تم حذف الموعد", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "تعذر حذف الموعد", Toast.LENGTH_SHORT).show();
        }
    }
}
