package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myfinalproject.databinding.ActivityAppointmentsListBinding;
import java.util.ArrayList;

public class AppointmentsListActivity extends AppCompatActivity {
    private ActivityAppointmentsListBinding binding;
    private AppointmentAdapter adapter;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(AppointmentsListActivity.this);
        sessionManager = new SessionManager(AppointmentsListActivity.this);

        adapter = new AppointmentAdapter(new AppointmentAdapter.OnAppointmentClickListener() {
            @Override
            public void onAppointmentClick(Appointment appointment) {
                openDetails(appointment);
            }
        });

        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(AppointmentsListActivity.this));
        binding.rvAppointments.setAdapter(adapter);

        binding.btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentsListActivity.this, AddAppointmentActivity.class);
                startActivity(intent);
            }
        });

        binding.navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(HomeActivity.class);
            }
        });

        binding.navTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ToolsActivity.class);
            }
        });

        binding.navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ProfileActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        ArrayList<Appointment> appointments = databaseHelper.getAppointments(sessionManager.getUserId());
        adapter.setItems(appointments);
        if (appointments.isEmpty()) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.rvAppointments.setVisibility(View.GONE);
        } else {
            binding.tvEmptyState.setVisibility(View.GONE);
            binding.rvAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void openDetails(Appointment appointment) {
        Intent intent = new Intent(AppointmentsListActivity.this, AppointmentDetailsActivity.class);
        intent.putExtra("appointment_id", appointment.getId());
        startActivity(intent);
    }

    private void openActivity(Class<?> activity) {
        if (getClass().equals(activity)) return;
        Intent intent = new Intent(AppointmentsListActivity.this, activity);
        startActivity(intent);
        finish();
    }
}
