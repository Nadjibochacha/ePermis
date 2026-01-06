package com.example.epermis;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        loadUsers();
        btnToggle.setOnClickListener(v -> {
            showingUsers = !showingUsers;
            if (showingUsers) {
                loadUsers();
                btnToggle.setText("Voir les Demandes");
            } else {
                loadRequests();
                btnToggle.setText("Voir les Utilisateurs");
            }
        });
        adminListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog((int) id);
            return true;
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
                    if (showingUsers) loadUsers(); else loadRequests();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
    private void showUserDialog(final Integer userId, String currentName, String currentEmail, String currentRole) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_user_edit, null);

        EditText etName = view.findViewById(R.id.etAdminName);
        EditText etEmail = view.findViewById(R.id.etAdminEmail);
        Spinner spRole = view.findViewById(R.id.spAdminRole);

        // If editing, fill the fields
        if (userId != null) {
            etName.setText(currentName);
            etEmail.setText(currentEmail);
            // Set spinner selection based on role string...
        }

        builder.setView(view)
                .setTitle(userId == null ? "Ajouter Utilisateur" : "Modifier Utilisateur")
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String role = spRole.getSelectedItem().toString();
                    dbHelper.updateUser(userId, name, email, role);
                    loadUsers();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}