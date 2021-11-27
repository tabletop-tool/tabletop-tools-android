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

public class DiceFragment extends Fragment {
    private Button dice;
    private Integer result = null;
    private static final Random rand = new Random();
    private int[] range = {1, 6};

    private HistoryFragment<int[]> diceHisFrg;
    private ArrayList<int[]>  diceHis;

    private DiceSetting setting;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);

        if (this.diceHis == null) {
            this.diceHis = new ArrayList<>();
            this.diceHisFrg = new HistoryFragment<>(this.diceHis, 1);
        }

        if (this.sp == null) this.sp = PreferenceManager.getDefaultSharedPreferences(
                requireContext());

        if (this.setting == null) this.setting = new DiceSetting();

        this.dice = v.findViewById(R.id.dice);

        this.dice.setOnClickListener(v1 -> {
            if (v1.getId() != R.id.dice) return;
            if (this.range[0] > this.range[1]) {
                Toast.makeText(requireContext(),
                        "Uhhh, why is the min bigger than the max?",
                        Toast.LENGTH_LONG).show();
                return;
            }

            this.changeDice();
            int[] insert = {this.result, this.range[0], this.range[1]};
            this.diceHis.add(0, insert);
        });

        if (this.result != null) this.changeDice(false);

        setHasOptionsMenu(true);
        this.loadSettings();
        return v;
    }

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

    private void changeDice(boolean change) {
        int temp = rand.nextInt(this.range[1] - this.range[0] + 1) + this.range[0];
        if (change) {
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

    private void changeDice() {
        this.changeDice(true);
    }
}
