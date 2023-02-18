package com.example.sgcamera.roomFavourite;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CameraDao {
    @Insert
    void insert(Camera camera);

   @Update
   void update(Camera camera);

    @Query("DELETE FROM camera_table where camera_id=:camera_id")
    void delete(String camera_id);

    @Query("SELECT * FROM camera_table")
    List<Camera> getAllCamera();

    @Query("SELECT * FROM camera_table where camera_id=:camera_id")
    Camera getCamera(String camera_id);
}