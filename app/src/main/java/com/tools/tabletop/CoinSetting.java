package com.tools.tabletop;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class CoinSetting extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.coin_preference, rootKey);
    }
}
