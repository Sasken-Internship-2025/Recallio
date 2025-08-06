package com.example.saskenproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.saskenproject.models.OtpVerifyRequest;

public class OtpVerifyActivity extends AppCompatActivity {

    EditText[] otpDigits = new EditText[6];
    Button verifyButton;
    TextView resendOtp;
    String email, name, actualOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        otpDigits[0] = findViewById(R.id.otpDigit1);
        otpDigits[1] = findViewById(R.id.otpDigit2);
        otpDigits[2] = findViewById(R.id.otpDigit3);
        otpDigits[3] = findViewById(R.id.otpDigit4);
        otpDigits[4] = findViewById(R.id.otpDigit5);
        otpDigits[5] = findViewById(R.id.otpDigit6);

        verifyButton = findViewById(R.id.buttonVerifyOtp);
        resendOtp = findViewById(R.id.resendOtpText);

        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        actualOtp = getIntent().getStringExtra("otp");

        for (int i = 0; i < 6; i++) {
            int finalI = i;
            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && finalI < 5) {
                        otpDigits[finalI + 1].requestFocus();
                    }
                }
            });
        }

        verifyButton.setOnClickListener(v -> {
            StringBuilder enteredOtp = new StringBuilder();
            for (EditText editText : otpDigits) {
                enteredOtp.append(editText.getText().toString().trim());
            }

            if (enteredOtp.toString().equals(actualOtp)) {
                Intent intent = new Intent(OtpVerifyActivity.this, CompleteSignupActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });

        resendOtp.setOnClickListener(v -> {
            Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show();
            // You can hit the send-otp API again from here
        });
    }
}
