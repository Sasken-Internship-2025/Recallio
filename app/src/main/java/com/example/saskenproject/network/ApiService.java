package com.example.saskenproject.network;

import com.example.saskenproject.models.OtpRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("send-otp")
    Call<OtpRequest> sendOtp(@Body OtpRequest request);
}
