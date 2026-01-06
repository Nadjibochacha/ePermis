package com.example.epermis;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void handleLogin(View v) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String email = ((EditText)findViewById(R.id.loginEmail)).getText().toString();
        String pass = ((EditText)findViewById(R.id.loginPass)).getText().toString();
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_EMAIL + "=? AND " +
                DatabaseHelper.KEY_USER_PASSWORD + "=?", new String[]{email, pass});
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
            int roleIndex = cursor.getColumnIndex(DatabaseHelper.KEY_USER_ROLE);
            int userId = cursor.getInt(idIndex);
            String role = cursor.getString(roleIndex);
            Intent intent;
            if (role.equals("Admin")) {
                intent = new Intent(this, AdminDashboard.class);
            } else if (role.equals("Agent")) {
                intent = new Intent(this, AgentDashboard.class);
            } else {
                intent = new Intent(this, CitizenDashboard.class);
            }
            startActivity(intent);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}