package com.example.epermis;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {
    private ListView adminListView;
    private DatabaseHelper dbHelper;
    private boolean showingUsers = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        dbHelper = new DatabaseHelper(this);
        adminListView = findViewById(R.id.adminListView);
        Button btnToggle = findViewById(R.id.btnToggleView);
        Button btnLogout = findViewById(R.id.btnAdminLogout);
        btnToggle.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.accent_green));
        loadUsers();
        btnToggle.setOnClickListener(v -> {
            showingUsers = !showingUsers;
            if (showingUsers) {
                loadUsers();
                btnToggle.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.accent_green));
                btnToggle.setText("Voir les Demandes");
            } else {
                loadRequests();
                btnToggle.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.primary_blue));
                btnToggle.setText("Voir les Utilisateurs");
            }
        });
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        adminListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog((int) id);
            return true;
        });
        adminListView.setOnItemClickListener((parent, view, position, id) -> {
            if (showingUsers) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_EMAIL));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ROLE));
                showUserDialog(userId, name, email, role);
            } else {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int requestId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                Intent intent = new Intent(AdminDashboard.this, RequestDetailActivity.class);
                intent.putExtra("REQ_ID", requestId);
                startActivity(intent);
            }
        });
    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        String[] from = {DatabaseHelper.KEY_USER_NAME, DatabaseHelper.KEY_USER_ROLE};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to, 0);
        adminListView.setAdapter(adapter);
    }

    private void loadRequests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id AS _id, project_type, status FROM requests", null);
        String[] from = {"project_type", "status"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to, 0);
        adminListView.setAdapter(adapter);
    }

    private void showDeleteDialog(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous vraiment supprimer cet élément ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    if (showingUsers) dbHelper.deleteUser(id);
                    else dbHelper.deleteRequest(id);

                    Toast.makeText(this, "Élément supprimé", Toast.LENGTH_SHORT).show();
                    if (showingUsers) loadUsers(); else loadRequests();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showUserDialog(final int userId, String currentName, String currentEmail, String currentRole) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_user_edit, null);
        EditText etName = view.findViewById(R.id.etAdminName);
        EditText etEmail = view.findViewById(R.id.etAdminEmail);
        Spinner spRole = view.findViewById(R.id.spAdminRole);
        etName.setText(currentName);
        etEmail.setText(currentEmail);
        setSpinnerSelection(spRole, currentRole);
        builder.setView(view)
                .setTitle("Modifier Utilisateur")
                .setPositiveButton("Mettre à jour", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String role = spRole.getSelectedItem().toString();

                    // Perform update operation
                    boolean updated = dbHelper.updateUser(userId, name, email, role);
                    if (updated) {
                        Toast.makeText(this, "Utilisateur mis à jour", Toast.LENGTH_SHORT).show();
                    }
                    loadUsers();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void setSpinnerSelection(Spinner spinner, String role) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(role);
            if (position >= 0) spinner.setSelection(position);
        }
    }
}