package com.example.saskenproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class CompleteSignupActivity extends AppCompatActivity {

    private EditText passwordEditText, confirmPasswordEditText;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private String emailFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_signup);

        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        submitButton = findViewById(R.id.buttonSubmit);

        mAuth = FirebaseAuth.getInstance();

        // Get email from intent
        emailFromIntent = getIntent().getStringExtra("email");

        submitButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (!validatePassword(password, confirmPassword)) return;

            mAuth.createUserWithEmailAndPassword(emailFromIntent, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private boolean validatePassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            passwordEditText.setError("Minimum 6 characters");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            passwordEditText.setError("Must contain an uppercase letter");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            passwordEditText.setError("Must contain a lowercase letter");
            return false;
        }
        if (!password.matches(".*[!@#$%^&*+=?.].*")) {
            passwordEditText.setError("Must contain a special character");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}
