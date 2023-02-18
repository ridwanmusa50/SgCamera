package com.example.sgcamera;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomePage extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    MapsLocation mapsLocation = new MapsLocation();
    ListFavourite listFavourite = new ListFavourite();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        chipNavigationBar = findViewById(R.id.bottomNavigation);

        if (savedInstanceState==null)
        {
            chipNavigationBar.setItemSelected(R.id.map, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mapsLocation).commit();
        }

        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i)
            {
                case R.id.map:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mapsLocation).commit();
                    break;
                case R.id.list:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, listFavourite).commit();
                    break;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // do nothing or show a message to the user
    }
}