package com.tools.tabletop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity<ActivityMainBinding> extends AppCompatActivity {
    private Fragment[] frgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frgs = new Fragment[]{new CoinFragment(), new DiceFragment(), new SpinnerFragment(), new GuideFragment(), new AdditionalFragment()};

        // Guide followed for navigation bar and fragments: https://youtu.be/tPV8xA7m-iw
        getSupportFragmentManager().beginTransaction().replace(
                R.id.frg_container, new CoinFragment()
        ).commit();

        BottomNavigationView btmNav = findViewById(R.id.btm_nav);
        btmNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment temp;
                        switch (item.getItemId()) {
                            case R.id.coin_flip: temp = frgs[0]; break;
                            case R.id.dice: temp = frgs[1]; break;
                            case R.id.spinner: temp = frgs[2]; break;
                            case R.id.guides: temp = frgs[3]; break;
                            default: temp = frgs[4]; break;
                        }

                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.frg_container, temp
                        ).commit();
                        return true;
                    }
                });
    }
}