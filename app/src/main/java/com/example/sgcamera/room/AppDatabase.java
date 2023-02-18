package com.example.sgcamera.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sgcamera.roomFavourite.Camera;
import com.example.sgcamera.roomFavourite.CameraDao;

// user_database consists two table: camera_table and user_table
@Database(entities = {User.class, Camera.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String dbName = "user_database";
    private static AppDatabase appDatabase;

    public static synchronized AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null)
        {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }

    public abstract UserDao userDao();
    public abstract CameraDao cameraDao();
}