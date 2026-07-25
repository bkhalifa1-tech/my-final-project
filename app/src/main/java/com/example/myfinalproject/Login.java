package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(Login.this);
        sessionManager = new SessionManager(Login.this);

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        binding.tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String email = binding.edEmail.getText().toString().trim();
        String password = binding.edPassword.getText().toString();

        if (email.isEmpty()) {
            binding.edEmail.setError("أدخلي البريد الإلكتروني");
            binding.edEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edEmail.setError("صيغة البريد الإلكتروني غير صحيحة");
            binding.edEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.edPassword.setError("أدخلي كلمة المرور");
            binding.edPassword.requestFocus();
            return;
        }

        if (!databaseHelper.isEmailRegistered(email)) {
            Toast.makeText(this, "لا يوجد حساب مرتبط بهذا البريد الإلكتروني", Toast.LENGTH_LONG).show();
            return;
        }

        long userId = databaseHelper.authenticate(email, password);

        if (userId == -1) {
            Toast.makeText(this, "حدث خطأ أثناء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -2) {
            binding.edPassword.setError("كلمة المرور غير صحيحة");
            binding.edPassword.requestFocus();
            return;
        }

        User user = databaseHelper.getUser(userId);

        if (user == null) {
            Toast.makeText(this, "تعذر قراءة بيانات الحساب", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.createLoginSession(userId, user.getEmail());

        Intent intent;
        if (user.hasPregnancyData()) {
            intent = new Intent(Login.this, HomeActivity.class);
        } else {
            intent = new Intent(Login.this, DueDateCalculatorActivity.class);
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
