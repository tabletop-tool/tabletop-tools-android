package com.tools.tabletop;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

/**
 * CoinSetting class that inherits from PreferenceFragmentCompat associated wit coin_preference
 */
public class CoinSetting extends PreferenceFragmentCompat {
    /**
     * method to be called to initialize preference view graphics
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.coin_preference, rootKey);
    }
}
