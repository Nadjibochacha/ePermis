package com.example.epermis;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Signup extends AppCompatActivity {
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        dbHelper = new DatabaseHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void handleSignup(View v) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String name = ((EditText)findViewById(R.id.editName)).getText().toString();
        String email = ((EditText)findViewById(R.id.editEmail)).getText().toString();
        String pass = ((EditText)findViewById(R.id.editPass)).getText().toString();
        String role = ((Spinner)findViewById(R.id.spinnerRole)).getSelectedItem().toString();
        if(name.isEmpty() || email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USER_NAME, name);
        values.put(DatabaseHelper.KEY_USER_EMAIL, email);
        values.put(DatabaseHelper.KEY_USER_PASSWORD, pass);
        values.put(DatabaseHelper.KEY_USER_ROLE, role);
        long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        if (id != -1) {
            Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
        }
    }
}