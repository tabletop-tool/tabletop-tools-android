package com.tools.tabletop;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class PointsSetting extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.points_preference, rootKey);

        EditTextPreference min = findPreference("pts_add");
        EditTextPreference max = findPreference("pts_sub");

        assert min != null;
        min.setOnBindEditTextListener(e -> e.setInputType(InputType.TYPE_CLASS_NUMBER));
        assert max != null;
        max.setOnBindEditTextListener(e -> e.setInputType(InputType.TYPE_CLASS_NUMBER));
    }
}
