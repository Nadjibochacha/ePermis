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
        displayRequests();
    }
    private void displayRequests() {
        Button btnLogout = findViewById(R.id.btn_logout);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Query all requests
        // SQLite Cursor must include "_id" for SimpleCursorAdapter to work
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.KEY_ID + " AS _id, " +
                DatabaseHelper.KEY_REQ_TYPE + ", " +
                DatabaseHelper.KEY_REQ_ADDRESS + ", " +
                DatabaseHelper.KEY_REQ_STATUS +
                " FROM " + DatabaseHelper.TABLE_REQUESTS, null);

        // Map database columns to XML views
        String[] from = new String[] {
                DatabaseHelper.KEY_REQ_TYPE,
                DatabaseHelper.KEY_REQ_ADDRESS,
                DatabaseHelper.KEY_REQ_STATUS
        };
        int[] to = new int[] {
                R.id.txtReqType,
                R.id.txtReqAddress,
                R.id.txtReqStatus
        };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.request_item, cursor, from, to, 0);
        listRequests.setAdapter(adapter);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}