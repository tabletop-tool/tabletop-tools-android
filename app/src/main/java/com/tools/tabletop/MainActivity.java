package com.tools.tabletop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity<ActivityMainBinding> extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Guide followed for navigation bar and fragments: https://youtu.be/tPV8xA7m-iw
        getSupportFragmentManager().beginTransaction().replace(
                R.id.frg_container, new CoinFragment()
        ).commit();

        BottomNavigationView btmNav = findViewById(R.id.btm_nav);
        btmNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp = null;
                switch (item.getItemId()) {
                    case R.id.coin_flip: temp = new CoinFragment(); break;
                    case R.id.dice: temp = new DiceFragment(); break;
                    case R.id.spinner: temp = new SpinnerFragment(); break;
                    case R.id.guides: temp = new GuideFragment(); break;
                    default: temp = new AdditionalFragment(); break;
                }

                getSupportFragmentManager().beginTransaction().replace(
                        R.id.frg_container, temp
                ).commit();
                return true;
            }
        });
    }
}