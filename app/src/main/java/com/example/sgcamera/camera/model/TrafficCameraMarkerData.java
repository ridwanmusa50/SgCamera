package com.example.sgcamera.camera.model;

public class TrafficCameraMarkerData {
    private String timestampinside, image, camera_id, md5;
    private double latitude, longitude;
    private int height, width;

    public TrafficCameraMarkerData(String timestampinside, String image, double latitude, double longitude, String camera_id, int height, int width, String md5) {
        this.timestampinside = timestampinside;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.camera_id = camera_id;
        this.height = height;
        this.width = width;
        this.md5 = md5;
    }

    public String getTimestampinside() {
        return timestampinside;
    }

    public String getImage() {
        return image;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCamera_id() {
        return camera_id;
    }

    public String getMd5() {
        return md5;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}

