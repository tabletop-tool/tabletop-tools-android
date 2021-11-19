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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Random;

public class CoinFragment extends Fragment {
    private int result = -1;
    private static final Random rand = new Random();
    private Button flipBtn;

    private HistoryFragment<Boolean> coinHstFrg;
    private CoinSetting cs;

    private int range = 50;

    private ArrayList<Boolean> coinHis;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        if (coinHis == null) {
            coinHis = new ArrayList<>();
            coinHstFrg = new HistoryFragment<>(this.coinHis, 0);
        }

        if (cs == null) {
            cs = new CoinSetting();
        }

        View v = inflater.inflate(R.layout.fragment_coin, container, false);

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
            }

            if (result == 1) this.coinHis.add(0, true);
            else this.coinHis.add(0, false);
        });

        if (this.result != -1) this.changeBtn();

        setHasOptionsMenu(true);
        this.loadSetting();
        return v;
    }

    private void loadSetting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        this.range = sp.getInt("coin_ratio", 50);
    }

    private void changeBtn() {
        if (this.result == 1) {
            flipBtn.setBackgroundColor(Color.parseColor("#0984e3"));
            flipBtn.setText(getString(R.string.h));
        } else {
            flipBtn.setBackgroundColor(Color.parseColor("#00cec9"));
            flipBtn.setText(getString(R.string.t));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.basic_menu, menu);

        MenuItem history = menu.findItem(R.id.history_btn);
        history.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.coinHstFrg
            ).addToBackStack(null).commit();
            return true;
        });

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
