package com.example.epermis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CitizenDashboard extends AppCompatActivity {

    private Button btnNewRequest, btnMyRequests, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_dashboard);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnNewRequest = findViewById(R.id.btn_new_request);
        btnMyRequests = findViewById(R.id.btn_my_requests);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        // 1. New Request
        btnNewRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, NewRequestActivity.class));
        });
        // 2. My Requests (History)
        btnMyRequests.setOnClickListener(v -> {
            Toast.makeText(this, "Chargement de vos demandes...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyRequestsActivity.class));
        });
        // 3. Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}