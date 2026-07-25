package com.example.myfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(ProfileActivity.this);
        sessionManager = new SessionManager(ProfileActivity.this);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    private void loadUser() {
        User user = databaseHelper.getUser(sessionManager.getUserId());
        if (user == null) {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, Login.class);
            startActivity(intent);
            finishAffinity();
            return;
        }

        binding.tvProfileName.setText(user.getName());
        binding.tvEmailValueRow.setText(user.getEmail());
        binding.tvPhoneValueRow.setText(user.getPhone() == null || user.getPhone().isEmpty() ? "غير محدد" : user.getPhone());
        binding.tvDateValue.setText(user.getDueDate() == null || user.getDueDate().isEmpty() ? "غير محدد" : user.getDueDate());
        
        if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
            binding.ImProfile.setImageURI(Uri.parse(user.getProfileImagePath()));
        }
    }
}
