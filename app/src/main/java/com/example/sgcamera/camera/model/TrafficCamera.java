package com.example.sgcamera.camera.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrafficCamera {
    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("cameras")
    private List<CameraDetails> cameraDetails;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<CameraDetails> getCameraDetails() {
        return cameraDetails;
    }

    public void setCameraDetails(List<CameraDetails> cameraDetails) {
        this.cameraDetails = cameraDetails;
    }

    public class CameraDetails {
        @SerializedName("timestamp")
        private String timestampinside;

        @SerializedName("image")
        private String image;

        @SerializedName("location")
        private Location locations;

        @SerializedName("camera_id")
        private String camera_id;

        @SerializedName("image_metadata")
        private ImageData image_metadata;

        public String getTimestampinside() {
            return timestampinside;
        }

        public void setTimestampinside(String timestampinside) {
            this.timestampinside = timestampinside;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCamera_id() {
            return camera_id;
        }

        public void setCamera_id(String camera_id) {
            this.camera_id = camera_id;
        }

        public ImageData getImage_metadata() {
            return image_metadata;
        }

        public void setImage_metadata(ImageData image_metadata) {
            this.image_metadata = image_metadata;
        }

        public Location getLocations() {
            return locations;
        }

        public void setLocations(Location locations) {
            this.locations = locations;
        }
    }

    public class Location {
        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    public class ImageData {
        private int height;
        private int width;
        private String md5;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}
