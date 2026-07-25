package com.example.myfinalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfinalproject.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String imageUri = "";

    private final ActivityResultLauncher<String[]> imageLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        imageUri = uri.toString();
                        binding.ImaEditProfile.setImageURI(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(EditProfileActivity.this);
        sessionManager = new SessionManager(EditProfileActivity.this);
        loadUser();

        binding.ImProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        binding.ImaEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

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

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        binding.etPregnancyWeek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCalculatedDates(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تسجيل الخروج")
                .setMessage("هل أنتِ متأكدة من رغبتكِ في تسجيل الخروج؟")
                .setPositiveButton("تسجيل الخروج", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logout();
                        Intent intent = new Intent(EditProfileActivity.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("إلغاء", null).show();
    }

    private void updateCalculatedDates(String weekStr) {
        if (weekStr.isEmpty() || weekStr.equals("غير محدد")) return;

        String cleanWeek = weekStr.replace("الأسبوع ", "").trim();
        if (cleanWeek.isEmpty() || !cleanWeek.matches("\\d+")) return;

        int week = Integer.parseInt(cleanWeek);
        if (week >= 1 && week <= 40) {
            String lastPeriod = PregnancyUtils.calculateDateFromWeek(week);
            String dueDate = PregnancyUtils.calculateDueDate(lastPeriod);
            binding.tvBirthDate.setText(dueDate);
        }
    }

    private void selectImage() {
        imageLauncher.launch(new String[]{"image/*"});
    }

    private void loadUser() {
        User user = databaseHelper.getUser(sessionManager.getUserId());
        if (user == null) {
            finish();
            return;
        }

        binding.etFullName.setText(user.getName());
        binding.etPhone.setText(user.getPhone());
        binding.tvEmail.setText(user.getEmail());
        imageUri = user.getProfileImagePath() == null ? "" : user.getProfileImagePath();

        if (!imageUri.isEmpty()) {
            binding.ImaEditProfile.setImageURI(Uri.parse(imageUri));
        }

        if (user.hasPregnancyData()) {
            int week = PregnancyUtils.calculateWeek(user.getLastPeriodDate());
            binding.etPregnancyWeek.setText(String.valueOf(week));
            binding.tvBirthDate.setText(user.getDueDate());
        } else {
            binding.etPregnancyWeek.setText("");
            binding.tvBirthDate.setText("غير محدد");
        }
    }

    private void saveChanges() {
        String name = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String weekStr = binding.etPregnancyWeek.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etFullName.setError("أدخلي الاسم");
            binding.etFullName.requestFocus();
            return;
        }

        String lastPeriodDate = "";
        String dueDate = "";

        if (!weekStr.isEmpty() && weekStr.matches("\\d+")) {
            int week = Integer.parseInt(weekStr);
            if (week >= 1 && week <= 40) {
                lastPeriodDate = PregnancyUtils.calculateDateFromWeek(week);
                dueDate = PregnancyUtils.calculateDueDate(lastPeriodDate);
            }
        }

        boolean success = databaseHelper.updateProfile(
                sessionManager.getUserId(),
                name,
                phone,
                lastPeriodDate,
                dueDate,
                imageUri
        );

        if (success) {
            Toast.makeText(this, "تم حفظ التعديلات", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "تعذر حفظ التعديلات", Toast.LENGTH_SHORT).show();
        }
    }
}
