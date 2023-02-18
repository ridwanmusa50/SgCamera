package com.example.sgcamera.roomFavourite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "camera_table")
public class Camera {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "camera_id")
    String camera_id;

    @ColumnInfo(name = "favourite")
    Boolean favourite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(String camera_id) {
        this.camera_id = camera_id;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
