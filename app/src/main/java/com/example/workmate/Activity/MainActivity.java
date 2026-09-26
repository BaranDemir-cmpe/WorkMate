package com.example.workmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.workmate.Fragment.AddJobFragment;
import com.example.workmate.Fragment.HomeFragment;
import com.example.workmate.Fragment.ProfileFragment;
import com.example.workmate.R;
import com.example.workmate.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");

        Fragment selectedFragment = new HomeFragment(); // default

        if ("home".equals(fragmentToLoad)) {
            selectedFragment = new HomeFragment();
            bottomNavigationView.setSelectedItemId(R.id.menu_Anasayfa);
        }

        // Başlangıç Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.white));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_Anasayfa) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.menu_ilan_olustur) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddJobFragment()).commit();
                return true;
            } else if (itemId == R.id.menu_Profil) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
                return true;

            }

            return false;
        });









    }
}