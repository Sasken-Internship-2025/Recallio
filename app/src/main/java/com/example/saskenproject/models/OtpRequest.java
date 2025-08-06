package com.example.saskenproject.models;

public class OtpRequest {
    private String email;
    private String otp;

    public OtpRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
    public String getOtp() { return otp; }
}
