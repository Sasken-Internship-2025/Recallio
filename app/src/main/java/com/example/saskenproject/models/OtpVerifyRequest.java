package com.example.saskenproject.models;

public class OtpVerifyRequest {
    private String email;
    private String otp;

    public OtpVerifyRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() { return email; }
    public String getOtp() { return otp; }
}
