package com.example.epermis;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView txtCopyright = findViewById(R.id.txtCopyright);
//        onclick listner usage:
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
//            explicit intent
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });
        txtCopyright.setOnClickListener(new View.OnClickListener() {
            @Override
//            implicit intent
            public void onClick(View v) {
                String url = "https://nadjib-chacha.vercel.app/";
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            }
        });
    }
    public void go_login(View view) {
        // Intent to open Login Activity
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}