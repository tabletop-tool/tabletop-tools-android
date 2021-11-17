package com.tools.tabletop;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

public class CoinSetting extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.coin_preference, rootKey);

        SeekBarPreference sb = findPreference("coin_ratio");
        sb.setOnPreferenceChangeListener((i, v) -> {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            int val = sp.getInt("coin_ratio", 50);
            sb.setValue(val);
            return true;
        });
    }
}
