package com.tools.tabletop;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * CoinFragment class inherits from Fragment that is responsible for fragment_coin layout
 */
public class CoinFragment extends Fragment {
    private static final Random rand = new Random(); // random generator

    private int result = -1; // current result
    private Button flipBtn; // coin flip button

    private HistoryFragment<Boolean> coinHstFrg; // coin flip history fragment
    private CoinSetting cs; // coin setting preference fragment

    private int range = 50; // ratio of head : tail

    /**
     * Array list of boolean representing the coin flip history, true for head else tail
     */
    private ArrayList<Boolean> coinHis;

    /**
     * Method called to initialize view graphics
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // initialize coin history fragment if null
        if (coinHis == null) {
            coinHis = new ArrayList<>();
            coinHstFrg = new HistoryFragment<>(this.coinHis, 0);
        }

        // initialize coin setting fragment if null
        if (cs == null) cs = new CoinSetting();

        View v = inflater.inflate(R.layout.fragment_coin, container, false);

        // initialize the coin fip button
        this.flipBtn = v.findViewById(R.id.coin_btn);
        this.flipBtn.setOnClickListener(v1 -> {
            if (v1.getId() != R.id.coin_btn) return;

            int temp = rand.nextInt(100);
            int now;

            if (temp <= this.range - 1) now = 1;
            else now = 0;

            if (this.result != now) {
                this.result = now;
                this.changeBtn();
            } else {
                // toast message if user got the same result
                Toast.makeText(
                        requireContext(), "Got the same value!", Toast.LENGTH_SHORT
                ).show();
            }

            if (result == 1) this.coinHis.add(0, true);
            else this.coinHis.add(0, false);
        });

        if (this.result != -1) this.changeBtn();

        setHasOptionsMenu(true);
        this.loadSetting();
        return v;
    }

    /**
     * private method that sets the range variable with the one stored in SharedPreference
     */
    private void loadSetting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        this.range = sp.getInt("coin_ratio", 50);
    }

    /**
     * private method that change the coin flip button color and text base on the result variable
     */
    private void changeBtn() {
        if (this.result == 1) {
            flipBtn.setBackgroundColor(Color.parseColor("#0984e3"));
            flipBtn.setText(getString(R.string.h));
        } else {
            flipBtn.setBackgroundColor(Color.parseColor("#00cec9"));
            flipBtn.setText(getString(R.string.t));
        }
    }

    /**
     * Method called to initialize menu view graphics
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.basic_menu, menu);

        // history button that sets a backtrack coin history fragment on button press
        MenuItem history = menu.findItem(R.id.history_btn);
        history.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.coinHstFrg
            ).addToBackStack(null).commit();
            return true;
        });

        // setting button that sets a backtrack coin setting fragment on button press
        MenuItem settings = menu.findItem(R.id.settings_btn);
        settings.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.cs
            ).addToBackStack(null).commit();
            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
