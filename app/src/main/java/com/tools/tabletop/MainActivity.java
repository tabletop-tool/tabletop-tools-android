package com.tools.tabletop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

/**
 * MainActivity class inherit from AppCompatActivity.
 * Responsible for activity_main layout and be considered the main / start code for the app.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Private Map variable with the component ID (int) as its key for the associated
     * Fragment class
     */
    private Map<Integer, Fragment> frgs;

    /**
     * Method to be called on activity start that initialize necessarily components.
     * This method should only be called by "system"
     *
     * @param savedInstanceState Bundle of the saved data from a saved instance if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Guide followed for navigation bar and fragments: https://youtu.be/AL_1UDa9l3U
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frgs = new HashMap<Integer, Fragment>() {{
            put(R.id.coin_flip, new CoinFragment());
            put(R.id.dice, new DiceFragment());
            put(R.id.spinner, new SpinnerFragment());
            put(R.id.pts, new PointsFragment());
            put(R.id.more_options, new AdditionalFragment());
        }};

        this.setFragment(frgs.get(R.id.coin_flip));

        BottomNavigationView btmNav = findViewById(R.id.btm_nav);
        btmNav.setOnItemSelectedListener(item -> {
            setFragment(frgs.get(item.getItemId()));
            return true;
        });
    }

    /**
     * Private method that replaces the fragment container in main activity with the passed in one
     * @param frg the fragment to replace the existing one
     */
    private void setFragment(Fragment frg) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.frg_container, frg
        ).commit();
    }
}