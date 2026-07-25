package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myfinalproject.databinding.ActivityHomeBinding;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private AppointmentAdapter appointmentAdapter;
    private TipAdapter tipAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(HomeActivity.this);
        sessionManager = new SessionManager(HomeActivity.this);

        appointmentAdapter = new AppointmentAdapter(new AppointmentAdapter.OnAppointmentClickListener() {
            @Override
            public void onAppointmentClick(Appointment appointment) {
                openAppointment(appointment);
            }
        });
        tipAdapter = new TipAdapter();

        binding.rvUpcomingAppointments.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        binding.rvUpcomingAppointments.setAdapter(appointmentAdapter);

        binding.rvWeeklyTips.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        binding.rvWeeklyTips.setAdapter(tipAdapter);

        binding.rvUpcomingAppointments.setNestedScrollingEnabled(false);
        binding.rvWeeklyTips.setNestedScrollingEnabled(false);

        binding.tvViewAllAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AppointmentsListActivity.class);
                startActivity(intent);
            }
        });

        binding.tvViewAllTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, WeeklyTipsActivity.class);
                startActivity(intent);
            }
        });

        binding.navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // نحن بالفعل في الشاشة الرئيسية
            }
        });

        binding.navAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AppointmentsListActivity.class);
                startActivity(intent);
            }
        });

        binding.navTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ToolsActivity.class);
                startActivity(intent);
            }
        });

        binding.navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        User user = databaseHelper.getUser(sessionManager.getUserId());

        if (user == null) {
            sessionManager.logout();
            Intent intent = new Intent(HomeActivity.this, Login.class);
            startActivity(intent);
            finishAffinity();
            return;
        }

        binding.tvWelcome.setText("مرحبًا، " + user.getName());

        if (user.hasPregnancyData()) {
            int weekNumber = PregnancyUtils.calculateWeek(user.getLastPeriodDate());
            int month = PregnancyUtils.calculateMonth(weekNumber);
            int remaining = Math.max(0, 40 - weekNumber);

            binding.tvCurrentWeek.setText("الأسبوع " + weekNumber + " (الشهر " + month + ")");
            binding.tvRemaining.setText("متبقي تقريبًا " + remaining + " أسبوع");
            
            PregnancyWeek weekData = databaseHelper.getPregnancyWeek(weekNumber);
            if (weekData != null) {
                binding.tvDevelopment.setText(weekData.getDevelopment());
                binding.tvBabySize.setText(weekData.getSizeLabel());
                
                ArrayList<String> tips = new ArrayList<>();
                tips.add(weekData.getTipOne());
                tips.add(weekData.getTipTwo());
                tips.add(weekData.getTipThree());
                tipAdapter.setItems(tips);
            } else {
                binding.tvDevelopment.setText(PregnancyUtils.getWeekInfo(weekNumber));
                binding.tvBabySize.setText(PregnancyUtils.getBabySize(weekNumber));
                tipAdapter.setItems(PregnancyUtils.getWeeklyTips(weekNumber));
            }
        } else {
            binding.tvCurrentWeek.setText("لم يتم تحديد أسبوع الحمل");
            binding.tvRemaining.setText("");
            binding.tvDevelopment.setText("");
            binding.tvBabySize.setText("");
            tipAdapter.setItems(new ArrayList<String>());
        }

        ArrayList<Appointment> appointments = databaseHelper.getUpcomingAppointments(sessionManager.getUserId(), 3);

        appointmentAdapter.setItems(appointments);

        if (appointments.isEmpty()) {
            binding.tvNoUpcoming.setVisibility(View.VISIBLE);
            binding.rvUpcomingAppointments.setVisibility(View.GONE);
        } else {
            binding.tvNoUpcoming.setVisibility(View.GONE);
            binding.rvUpcomingAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void openAppointment(Appointment appointment) {
        Intent intent = new Intent(HomeActivity.this, AppointmentDetailsActivity.class);
        intent.putExtra("appointment_id", appointment.getId());
        startActivity(intent);
    }
}
