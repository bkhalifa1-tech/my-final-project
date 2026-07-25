package com.example.myfinalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myfinalproject.databinding.ActivityEmergencyNumbersBinding;
import java.util.ArrayList;

public class EmergencyNumbersActivity extends AppCompatActivity {
    private ActivityEmergencyNumbersBinding binding;
    private EmergencyContactAdapter adapter;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyNumbersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(EmergencyNumbersActivity.this);
        sessionManager = new SessionManager(EmergencyNumbersActivity.this);

        adapter = new EmergencyContactAdapter(new EmergencyContactAdapter.OnContactActionListener() {
            @Override
            public void onCall(EmergencyContact contact) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhoneNumber()));
                startActivity(intent);
            }

            @Override
            public void onDelete(EmergencyContact contact, int position) {
                showDeleteDialog(contact, position);
            }
        });

        binding.rvEmergency.setLayoutManager(new LinearLayoutManager(EmergencyNumbersActivity.this));
        binding.rvEmergency.setAdapter(adapter);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContact();
            }
        });

        binding.navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyNumbersActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.navAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyNumbersActivity.this, AppointmentsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.navTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyNumbersActivity.this, ToolsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyNumbersActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        loadContacts();
    }

    private void saveContact() {
        String name = binding.etContactName.getText().toString().trim();
        String phone = binding.etPhoneNumber.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etContactName.setError("أدخلي اسم جهة الاتصال");
            binding.etContactName.requestFocus();
            return;
        }

        if (phone.length() < 7) {
            binding.etPhoneNumber.setError("أدخلي رقم هاتف صحيحًا");
            binding.etPhoneNumber.requestFocus();
            return;
        }

        long userId = sessionManager.getUserId();
        long result = databaseHelper.insertContact(userId, name, phone);

        if (result != -1) {
            EmergencyContact contact = new EmergencyContact(result, userId, name, phone);
            adapter.addItem(contact);
            binding.etContactName.setText("");
            binding.etPhoneNumber.setText("");
            Toast.makeText(this, "تمت إضافة جهة الاتصال", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "تعذر حفظ جهة الاتصال", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadContacts() {
        ArrayList<EmergencyContact> contacts = databaseHelper.getContacts(sessionManager.getUserId());
        adapter.setItems(contacts);
    }

    private void showDeleteDialog(final EmergencyContact contact, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("حذف جهة الاتصال")
                .setMessage("هل تريدين حذف " + contact.getContactName() + "؟")
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = databaseHelper.deleteContact(contact.getId(), sessionManager.getUserId());
                        if (success) {
                            adapter.removeItem(position);
                        } else {
                            Toast.makeText(EmergencyNumbersActivity.this, "تعذر الحذف", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}
