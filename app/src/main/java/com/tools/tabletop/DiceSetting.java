package com.tools.tabletop;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class DiceSetting extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.dice_preference, rootKey);

        EditTextPreference min = findPreference("dice_min");
        EditTextPreference max = findPreference("dice_max");

        // code reference:
        // https://newbedev.com/how-to-set-only-numeric-value-for-edittextpreference-in-android
        min.setOnBindEditTextListener(e -> {
            e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        });
        max.setOnBindEditTextListener(e -> {
            e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        });
    }
}
