package com.example.epermis;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class MyRequestsActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private int loggedInUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);
        // 1. Retrieve the ID passed from the Dashboard
        loggedInUserId = getIntent().getIntExtra("USER_ID", -1);
        listView = findViewById(R.id.listViewMyRequests);
        dbHelper = new DatabaseHelper(this);
        loadMyRequests();
    }
    private void loadMyRequests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 2. Filter query using the dynamic ID
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.KEY_ID + " AS _id, " +
                        DatabaseHelper.KEY_REQ_TYPE + ", " +
                        DatabaseHelper.KEY_REQ_STATUS + ", " +
                        DatabaseHelper.KEY_REQ_ADDRESS +
                        " FROM " + DatabaseHelper.TABLE_REQUESTS +
                        " WHERE " + DatabaseHelper.KEY_REQ_USER_ID + " = ?",
                new String[]{String.valueOf(loggedInUserId)});
        // 3. Bind to UI
        String[] from = {DatabaseHelper.KEY_REQ_TYPE, DatabaseHelper.KEY_REQ_STATUS, DatabaseHelper.KEY_REQ_ADDRESS};
        int[] to = {R.id.txtReqType, R.id.txtReqStatus, R.id.txtReqAddress};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.request_item,
                cursor,
                from,
                to,
                0
        );

        listView.setAdapter(adapter);
    }
}