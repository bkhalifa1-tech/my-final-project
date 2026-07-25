package com.example.myfinalproject;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myfinalproject.databinding.ActivityWeeklyTipsBinding;
import java.util.ArrayList;

public class WeeklyTipsActivity extends AppCompatActivity {
    private ActivityWeeklyTipsBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private TipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWeeklyTipsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(WeeklyTipsActivity.this);
        sessionManager = new SessionManager(WeeklyTipsActivity.this);
        adapter = new TipAdapter();

        binding.rvTips.setLayoutManager(new LinearLayoutManager(WeeklyTipsActivity.this));
        binding.rvTips.setAdapter(adapter);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.tvDisclaimer.setText("هذه المعلومات عامة ولا تُغني عن استشارة الطبيب.");

        loadWeeklyTips();
    }

    private void loadWeeklyTips() {
        User user = databaseHelper.getUser(sessionManager.getUserId());
        ArrayList<String> tips = new ArrayList<>();

        if (user == null || !user.hasPregnancyData()) {
            tips.add("يرجى إدخال تاريخ آخر دورة أولًا.");
            adapter.setItems(tips);
            return;
        }

        int weekNumber = PregnancyUtils.calculateWeek(user.getLastPeriodDate());
        PregnancyWeek weekData = databaseHelper.getPregnancyWeek(weekNumber);

        if (weekData != null) {
            tips.add("تطور الجنين: " + weekData.getDevelopment());
            tips.add("نصيحة غذائية: " + weekData.getTipOne());
            tips.add("نصيحة صحية: " + weekData.getTipTwo());
            tips.add("تنبيه مهم: " + weekData.getTipThree());
        } else {
            tips = PregnancyUtils.getWeeklyTips(weekNumber);
        }

        adapter.setItems(tips);
    }
}
