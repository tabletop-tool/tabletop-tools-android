package com.tools.tabletop;

import android.content.SharedPreferences;
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
 * DiceFragment class inherits from Fragment and is associated wit fragment_dice
 */
public class DiceFragment extends Fragment {
    private static final Random rand = new Random(); // random generator

    private Button dice; // dice button
    private Integer result = null; // integer result of the current roll
    private int[] range = {1, 6};

    private HistoryFragment<int[]> diceHisFrg; // Dice history fragment variable
    private ArrayList<int[]> diceHis; // Array list of the dice history in int

    private DiceSetting setting; // Dice setting preference fragment
    private SharedPreferences sp; // Shared Preference reference

    /**
     * Method called to initialize view graphics
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);

        // initialize dice history if null
        if (this.diceHis == null) {
            this.diceHis = new ArrayList<>();
            this.diceHisFrg = new HistoryFragment<>(this.diceHis, 1);
        }

        // get shared preference reference if null
        if (this.sp == null) this.sp = PreferenceManager.getDefaultSharedPreferences(
                requireContext());

        // initialize dice setting if null
        if (this.setting == null) this.setting = new DiceSetting();

        // initialize dice button
        this.dice = v.findViewById(R.id.dice);
        this.dice.setOnClickListener(v1 -> {
            if (this.range[0] > this.range[1]) {
                Toast.makeText(requireContext(),
                        "Uhhh, why is the min bigger than the max?",
                        Toast.LENGTH_LONG).show();
                return;
            }

            this.changeDice(true);
            int[] insert = {this.result, this.range[0], this.range[1]};
            this.diceHis.add(0, insert);
        });

        if (this.result != null) this.changeDice(false);

        setHasOptionsMenu(true);
        this.loadSettings();
        return v;
    }

    /**
     * private method that loads the range variable with integers from shared preference
     */
    private void loadSettings() {
        int mini, maxi;
        SharedPreferences.Editor editor = this.sp.edit();

        try {
            mini = Integer.parseInt(sp.getString("dice_min", "1"));
        } catch (NumberFormatException e) {
            mini = 1;
            editor.putString("dice_min", "1");
            editor.apply();
        }

        try {
            maxi = Integer.parseInt(sp.getString("dice_max", "6"));
        } catch (NumberFormatException e) {
            maxi = 6;
            editor.putString("dice_max", "6");
            editor.apply();
        }

        this.range = new int[]{mini, maxi};
    }

    /**
     * Method called to initialize menu view graphics
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.basic_menu, menu);

        MenuItem history = menu.findItem(R.id.history_btn);
        history.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.diceHisFrg
            ).addToBackStack(null).commit();
            return true;
        });

        MenuItem s = menu.findItem(R.id.settings_btn);
        s.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.setting
            ).addToBackStack(null).commit();
           return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * private method that changes the dice base on the result variable
     * @param change boolean indicate whether or not a new random result should be generated
     */
    private void changeDice(boolean change) {
        if (change) {
            int temp = rand.nextInt(this.range[1] - this.range[0] + 1) + this.range[0];

            try {
                if (temp == this.result) Toast.makeText(
                        requireContext(), "Got the same value!", Toast.LENGTH_SHORT
                    ).show();
                else this.result = temp;
            } catch (NullPointerException npe) { // result is null
                this.result = temp;
            }
        }

        dice.setText(String.valueOf(this.result));
        if (dice.getTextSize() != 80) dice.setTextSize(80);
    }
}
