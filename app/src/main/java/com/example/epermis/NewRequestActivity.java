package com.example.epermis;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class NewRequestActivity extends AppCompatActivity {

    private EditText dName, date, phone, editAddress;
    private Spinner spinType;
    private TextView txtFileName;
    private Uri selectedFileUri;
    private DatabaseHelper dbHelper;
    // 1. Logic to handle the file picker result
    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    txtFileName.setText("Fichier: " + selectedFileUri.getLastPathSegment());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_request);
        dbHelper = new DatabaseHelper(this);
        // Initialize all fields
        dName = findViewById(R.id.dName);
        date = findViewById(R.id.date);
        phone = findViewById(R.id.phone);
        editAddress = findViewById(R.id.editAddress);
        spinType = findViewById(R.id.spinProjectType);
        txtFileName = findViewById(R.id.txtFileName);
        // Upload Button Logic
        findViewById(R.id.btnUpload).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });
        // Submit Button Logic
        findViewById(R.id.btnSubmitRequest).setOnClickListener(v -> saveToDatabase());
    }
    private void saveToDatabase() {
        String name = dName.getText().toString();
        String bDate = date.getText().toString();
        String tel = phone.getText().toString();
        String type = spinType.getSelectedItem().toString();
        String addr = editAddress.getText().toString();
        int currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (selectedFileUri == null) {
            Toast.makeText(this, "Veuillez joindre un document", Toast.LENGTH_SHORT).show();
            return;
        }
        String internalPath = copyFileToInternalStorage(selectedFileUri);
        if (internalPath != null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.KEY_REQ_NAME, name);
            v.put(DatabaseHelper.KEY_REQ_DATE, bDate);
            v.put(DatabaseHelper.KEY_REQ_PHONE, tel);
            v.put(DatabaseHelper.KEY_REQ_TYPE, type);
            v.put(DatabaseHelper.KEY_REQ_ADDRESS, addr);
            v.put(DatabaseHelper.KEY_REQ_FILE, internalPath);
            v.put(DatabaseHelper.KEY_REQ_USER_ID, currentUserId);
            v.put(DatabaseHelper.KEY_REQ_STATUS, "En attente");
            v.put("file_path", internalPath);
            v.put(DatabaseHelper.KEY_REQ_USER_ID, currentUserId);
            long id = db.insert(DatabaseHelper.TABLE_REQUESTS, null, v);
            if (id != -1) {
                Toast.makeText(this, "Demande et document enregistrés !", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Erreur lors de la copie du fichier", Toast.LENGTH_SHORT).show();
        }
    }
    private String copyFileToInternalStorage(Uri uri) {
        try {
            // 1. Create the 'documents' directory if it doesn't exist
            File directory = new File(getFilesDir(), "documents");
            if (!directory.exists()) directory.mkdir();

            // 2. Create a unique name for the file to avoid overwriting
            String fileName = "doc_" + System.currentTimeMillis() + ".pdf";
            File newFile = new File(directory, fileName);

            // 3. Copy the data
            InputStream in = getContentResolver().openInputStream(uri);
            OutputStream out = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();

            // 4. Return the absolute path to save in SQLite
            return newFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}