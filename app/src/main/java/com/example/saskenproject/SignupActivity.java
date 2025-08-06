package com.example.saskenproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.saskenproject.models.OtpRequest;
import com.example.saskenproject.network.ApiClient;
import com.example.saskenproject.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText emailEditText;
    Button sendOtpButton;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        loginText = findViewById(R.id.loginText);

        sendOtpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            sendOtp(email);
        });

        loginText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }

    private void sendOtp(String email) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<OtpRequest> call = apiService.sendOtp(new OtpRequest(email));

        call.enqueue(new Callback<OtpRequest>() {
            @Override
            public void onResponse(Call<OtpRequest> call, Response<OtpRequest> response) {
                if (response.isSuccessful()) {
                    String otp = response.body().getOtp();

                    Intent intent = new Intent(SignupActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("otp", otp);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpRequest> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
