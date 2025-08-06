package com.example.saskenproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtCurrentPassword, edtNewPassword;
    private Button btnUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed(); // Or finish() works too
        });

        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        btnUpdatePassword.setOnClickListener(v -> {
            String currentPassword = edtCurrentPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPassword)) {
                Toast.makeText(this, "Please enter current password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null || user.getEmail() == null) {
                Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Reauthenticate with current password
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Now update to new password
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

}
