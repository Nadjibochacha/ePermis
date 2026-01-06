package com.example.epermis;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgentDashboard extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listRequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agent_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.agent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DatabaseHelper(this);
        listRequests = findViewById(R.id.listRequests);
        setupDashboard();
        displayRequests();
    }
    private void setupDashboard() {
        listRequests.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(AgentDashboard.this, RequestDetailActivity.class);
            intent.putExtra("REQ_ID", (int) id);
            startActivity(intent);
        });

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    private void displayRequests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.KEY_ID + " AS _id, " +
                DatabaseHelper.KEY_REQ_TYPE + ", " +
                DatabaseHelper.KEY_REQ_ADDRESS + ", " +
                DatabaseHelper.KEY_REQ_STATUS +
                " FROM " + DatabaseHelper.TABLE_REQUESTS, null);
        String[] from = { DatabaseHelper.KEY_REQ_TYPE, DatabaseHelper.KEY_REQ_ADDRESS, DatabaseHelper.KEY_REQ_STATUS };
        int[] to = { R.id.txtReqType, R.id.txtReqAddress, R.id.txtReqStatus };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.request_item, cursor, from, to, 0);
        listRequests.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRequests();
    }
}