package com.example.epermis;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NewRequestActivity extends AppCompatActivity {

    private Spinner spinType;
    private EditText editAddress;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        dbHelper = new DatabaseHelper(this);
        spinType = findViewById(R.id.spinProjectType);
        editAddress = findViewById(R.id.editAddress);
        btnSubmit = findViewById(R.id.btnSubmitRequest);

        btnSubmit.setOnClickListener(v -> saveRequest());
    }

    private void saveRequest() {
        String type = spinType.getSelectedItem().toString();
        String address = editAddress.getText().toString().trim();

        if (address.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer une adresse", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Data Mapping
        values.put(DatabaseHelper.KEY_REQ_USER_ID, 1); // Temporary Dummy ID
        values.put(DatabaseHelper.KEY_REQ_TYPE, type);
        values.put(DatabaseHelper.KEY_REQ_ADDRESS, address);
        values.put(DatabaseHelper.KEY_REQ_STATUS, "En attente");

        long result = db.insert(DatabaseHelper.TABLE_REQUESTS, null, values);

        if (result != -1) {
            Toast.makeText(this, "Demande envoyée avec succès !", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
        }
    }
}