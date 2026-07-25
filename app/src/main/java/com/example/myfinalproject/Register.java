package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private DatabaseHelper databaseHelper;
    private boolean saving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(Register.this);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        binding.tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register() {
        if (saving) return;

        String name = binding.edFullName.getText().toString().trim();
        String email = binding.edEmail.getText().toString().trim();
        String password = binding.edPassword.getText().toString();
        String confirm = binding.edConfirmPassword.getText().toString();
        String phone = binding.edPhone.getText().toString().trim();

        if (name.isEmpty()) {
            binding.edFullName.setError("أدخلي الاسم الكامل");
            binding.edFullName.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edEmail.setError("أدخلي بريدًا إلكترونيًا صحيحًا");
            binding.edEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            binding.edPassword.setError("كلمة المرور 6 أحرف على الأقل");
            binding.edPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            binding.edConfirmPassword.setError("كلمتا المرور غير متطابقتين");
            binding.edConfirmPassword.requestFocus();
            return;
        }

        if (databaseHelper.isEmailRegistered(email)) {
            binding.edEmail.setError("البريد الإلكتروني مسجل مسبقًا");
            binding.edEmail.requestFocus();
            return;
        }

        saving = true;
        binding.btnRegister.setEnabled(false);

        long result = databaseHelper.registerUser(name, email, phone, password);

        if (result != -1) {
            Toast.makeText(this, "تم إنشاء الحساب، سجلي الدخول الآن", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Register.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            saving = false;
            binding.btnRegister.setEnabled(true);
            Toast.makeText(this, "تعذر إنشاء الحساب", Toast.LENGTH_SHORT).show();
        }
    }
}
