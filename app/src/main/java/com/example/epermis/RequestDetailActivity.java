package com.example.epermis;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RequestDetailActivity extends AppCompatActivity {
    private TextView detName, detType, detAddress;
    private Spinner spinStatus;
    private DatabaseHelper dbHelper;
    private int requestId;
    private String currentStatus, projectType, filePath;
    private final List<String> statusList = Arrays.asList("En attente", "En cours de traitement", "Acceptée", "Refusée");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        dbHelper = new DatabaseHelper(this);
        requestId = getIntent().getIntExtra("REQ_ID", -1);
        initViews();
        setupSpinner();
        loadRequestData();
        findViewById(R.id.btnUpdateStatus).setOnClickListener(v -> saveStatusChange());
        findViewById(R.id.btnViewDoc).setOnClickListener(v -> openFile());
    }

    private void initViews() {
        detName = findViewById(R.id.detName);
        detType = findViewById(R.id.detType);
        detAddress = findViewById(R.id.detAddress);
        spinStatus = findViewById(R.id.spinStatus);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStatus.setAdapter(adapter);
    }

    private void loadRequestData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_REQUESTS + " WHERE id = ?", new String[]{String.valueOf(requestId)});
        if (c.moveToFirst()) {
            projectType = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_REQ_TYPE));
            currentStatus = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_REQ_STATUS));
            filePath = c.getString(c.getColumnIndexOrThrow("file_path")); // Get the path we saved
            detName.setText(c.getString(c.getColumnIndexOrThrow("applicant_name")));
            detType.setText(projectType);
            detAddress.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_REQ_ADDRESS)));
            int statusPosition = statusList.indexOf(currentStatus);
            if (statusPosition != -1) {
                spinStatus.setSelection(statusPosition);
            }
        }
        c.close();
    }
    private void openFile() {
        if (filePath == null || filePath.isEmpty()) {
            Toast.makeText(this, "Chemin du fichier invalide", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "Le fichier est introuvable sur le téléphone", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String authority = getApplicationContext().getPackageName() + ".provider";

            Uri contentUri = FileProvider.getUriForFile(this, authority, file);
            String mime = "*/*";
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            if (extension != null) {
                String detectedMime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
                if (detectedMime != null) {
                    mime = detectedMime;
                }
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent chooser = Intent.createChooser(intent, "Ouvrir avec...");
            startActivity(chooser);

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Erreur configuration FileProvider", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Impossible d'ouvrir le fichier", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveStatusChange() {
        String newStatus = spinStatus.getSelectedItem().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.KEY_REQ_STATUS, newStatus);
        int rowsAffected = db.update(DatabaseHelper.TABLE_REQUESTS, cv, "id = ?", new String[]{String.valueOf(requestId)});
        if (rowsAffected > 0) {
            Toast.makeText(this, "Statut mis à jour !", Toast.LENGTH_SHORT).show();
            if (!newStatus.equals(currentStatus)) {
                NotificationHelper.showStatusNotification(this, projectType, newStatus);
            }
            finish();
        }
    }
}