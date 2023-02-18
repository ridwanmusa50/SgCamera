package com.example.sgcamera.camera.network;

import com.example.sgcamera.camera.model.ListTrafficCamera;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/v1/transport/traffic-images")
    Call<ListTrafficCamera> getAllLocation();
}
