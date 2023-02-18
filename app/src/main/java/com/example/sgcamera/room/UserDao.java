package com.example.sgcamera.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    User getUser(String username, String password);
}