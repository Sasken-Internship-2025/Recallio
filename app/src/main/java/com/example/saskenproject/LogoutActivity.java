package com.example.saskenproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    Button confirmLogoutButton, cancelLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        confirmLogoutButton = findViewById(R.id.confirmLogoutButton);
        cancelLogoutButton = findViewById(R.id.cancelLogoutButton);

        confirmLogoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(LogoutActivity.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        cancelLogoutButton.setOnClickListener(v -> {
            finish(); // return to previous screen (Settings)
        });
    }
}
