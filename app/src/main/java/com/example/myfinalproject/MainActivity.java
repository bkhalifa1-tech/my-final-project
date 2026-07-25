package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // الانتظار لمدة ثانيتين (Splash Screen) ثم الانتقال حسب حالة المستخدم
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSession();
            }
        }, 2000);
    }

    private void checkSession() {
        SessionManager sessionManager = new SessionManager(MainActivity.this);
        if (!sessionManager.isLoggedIn()) {
            openActivity(Login.class);
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        User user = databaseHelper.getUser(sessionManager.getUserId());
        
        if (user == null) {
            sessionManager.logout();
            openActivity(Login.class);
        } else if (user.hasPregnancyData()) {
            openActivity(HomeActivity.class);
        } else {
            openActivity(DueDateCalculatorActivity.class);
        }
    }

    private void openActivity(Class<?> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
        finish();
    }
}
