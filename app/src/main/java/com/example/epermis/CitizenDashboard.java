package com.example.epermis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class CitizenDashboard extends AppCompatActivity {
    private int loggedInUserId;
    private Button btnNewRequest, btnMyRequests, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citizen_dashboard);
        initializeViews();
        setupClickListeners();
        loggedInUserId = getIntent().getIntExtra("USER_ID", -1);
    }

    private void initializeViews() {
        btnNewRequest = findViewById(R.id.btn_new_request);
        btnMyRequests = findViewById(R.id.btn_my_requests);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        // 1. New Request
        btnNewRequest.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewRequestActivity.class);
            intent.putExtra("USER_ID", loggedInUserId);
            startActivity(intent);
        });
        // 2. My Requests (History)
        btnMyRequests.setOnClickListener(v -> {
            Toast.makeText(this, "Chargement de vos demandes...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyRequestsActivity.class);
            intent.putExtra("USER_ID", loggedInUserId);
            startActivity(intent);
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