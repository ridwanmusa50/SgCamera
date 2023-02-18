package com.example.sgcamera.camera.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListTrafficCamera {
    @SerializedName("items")
    private List<TrafficCamera> mData;

    public List<TrafficCamera> getmData() {
        return mData;
    }
}
