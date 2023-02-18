package com.example.sgcamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sgcamera.room.AppDatabase;
import com.example.sgcamera.room.User;
import com.example.sgcamera.room.UserDao;

import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {
    private EditText username, password;
    SharedPreferences sp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        username = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        TextView hello = findViewById(R.id.hello);
        TextView usernameLabel = findViewById(R.id.userNameLabel);
        Button loginButton = findViewById(R.id.loginButton);

        // Shared Preferences to save username
        sp = getSharedPreferences("userAccount", Context.MODE_PRIVATE);
        String userSaved = sp.getString("Username", ""); // get username in shared preferences

        loginButton.setOnClickListener(v1 -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // Declare editor for shared preferences
            SharedPreferences.Editor editor = sp.edit();

            // Declare room entity
            User userEntity = new User();

            if (user.isEmpty())
            {
                username.setError("The username cannot be empty.");
                username.requestFocus();
                return;
            }
            else if (user.length() < 3 || user.length() > 20)
            {
                username.setError("The username must be between 3 to 20 characters");
                username.requestFocus();
                return;
            }

            if (pass.isEmpty())
            {
                password.setError("The password cannot be empty.");
                password.requestFocus();
            }
            else if (!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", pass))
            {
                password.setError("The password should contain 8 characters: 1 number, 1 upper case, 1 lower case and 1 special.");
                password.requestFocus();
            }
            else
            {
                // Declare local database
                AppDatabase appDatabase = AppDatabase.getAppDatabase(getApplicationContext());
                final UserDao userDao = appDatabase.userDao();

                // userSaved represent exist user or new user
                if (userSaved.isEmpty())
                {
                    runOnUiThread(() -> {
                        if (validateInput(userEntity))
                        {
                            new Thread(() -> {
                                userEntity.setUsername(user);
                                userEntity.setPassword(pass);
                                userDao.insert(userEntity);

                                editor.putString("Username", user);
                                editor.apply();

                                runOnUiThread(() -> {
                                    Intent intent = new Intent(LoginPage.this, HomePage.class);
                                    startActivity(intent);
                                });
                            }).start();
                        }
                    });
                }
                else
                {
                    new Thread(() -> {
                        User userGet = userDao.getUser(user, pass);
                        if (userGet == null)
                        {
                            if (!user.equals(userSaved))
                            {
                                openDialog("Please insert correct username.");
                            }
                            else
                            {
                                openDialog("Please insert correct password.");
                            }
                        }
                        else
                        {
                            Intent intent = new Intent(LoginPage.this, HomePage.class);
                            startActivity(intent);

                        }
                    }).start();
                }
            }
        });
        if (!userSaved.isEmpty())
        {
            hello.setText("Hello " + userSaved);
            username.setText(userSaved);
            usernameLabel.setText("User's Name");
        }
    }

    private void openDialog(String message)
    {
        DialogMessage dialog_message = new DialogMessage();
        Bundle args = new Bundle();
        args.putString("message", message);
        dialog_message.setArguments(args);
        dialog_message.show(getSupportFragmentManager(), "Error Message");
    }

    private Boolean validateInput(User user) {
        if (user.getUsername() != null && user.getPassword() != null)
        {
            if (user.getUsername().isEmpty() || user.getPassword().isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}