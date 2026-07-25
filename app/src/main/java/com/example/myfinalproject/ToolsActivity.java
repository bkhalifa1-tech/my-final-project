package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityToolsBinding;

public class ToolsActivity extends AppCompatActivity {
    private ActivityToolsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityToolsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.vTipsBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToolsActivity.this, WeeklyTipsActivity.class);
                startActivity(intent);
            }
        });

        binding.vEmergencyBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToolsActivity.this, EmergencyNumbersActivity.class);
                startActivity(intent);
            }
        });

        binding.navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToolsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.navAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToolsActivity.this, AppointmentsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToolsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
