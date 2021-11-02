package com.tools.tabletop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private Map<Integer, Fragment> frgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frgs = new HashMap<Integer, Fragment>() {{
            put(R.id.coin_flip, new CoinFragment());
            put(R.id.dice, new DiceFragment());
            put(R.id.spinner, new SpinnerFragment());
            put(R.id.guides, new GuideFragment());
            put(R.id.more_options, new AdditionalFragment());
        }};

        // Guide followed for navigation bar and fragments: https://youtu.be/AL_1UDa9l3U
        getSupportFragmentManager().beginTransaction().replace(
                R.id.frg_container, new CoinFragment()
        ).commit();

        BottomNavigationView btmNav = findViewById(R.id.btm_nav);
        btmNav.setOnItemSelectedListener(item -> {
            setFragment(frgs.get(item.getItemId()));
            return true;
        });
    }

    private void setFragment(Fragment frg) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.frg_container, frg
        ).commit();
    }
}