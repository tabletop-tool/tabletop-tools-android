package com.tools.tabletop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * AdditionalFragment class inherited from Fragment that is responsible for fragment_more layout
 */
public class AdditionalFragment extends Fragment {

    /**
     * Method called to initialize view graphics
     *
     * @param inflater LayoutInflater
     * @param container nullable ViewGroup
     * @param savedInstanceState nullable savedInstance
     * @return initialized view layout for the fragment
     */
    @SuppressLint({"ApplySharedPref", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_more, container, false);

        // code for share button
        CardView share = v.findViewById(R.id.share_section);
        share.setOnClickListener(c -> {
            // code reference: https://youtu.be/i41rmT-GDXc
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String main = "There is this cool app called Tabletop Tools! It's awesome!\n\n" +
                    "More info here:\nhttps://github.com/tabletop-tool/tabletop-tools-android";
            intent.putExtra(Intent.EXTRA_TEXT, main);
            startActivity(Intent.createChooser(intent, "Share using"));
        });

        // code for the reset button
        CardView reset = v.findViewById(R.id.reset_section);
        reset.setOnClickListener(c -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("You sure reset and restart Tabletop tools to it's default settings?");
            builder.setCancelable(true);

            builder.setNeutralButton("No", null);

            // code that sets the positive button to reset all data stored back to its default state
            builder.setPositiveButton("Yes", (d, i) -> {
                SharedPreferences sp = PreferenceManager.
                        getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sp.edit();
                Gson gson = new Gson();
                ArrayList<String[]> empty = new ArrayList<>();

                editor.putString("dice_max", "6");
                editor.putString("dice_min", "1");
                editor.putString("pts_add", "10");
                editor.putString("pts_sub", "10");

                editor.putInt("coin_ratio", 50);

                editor.putString("pts_data", gson.toJson(empty));
                editor.putString("spin_data", gson.toJson(empty));

                // must be commit instead of apply as we are restarting the app right after to
                // ensure data is saved
                editor.commit();

                // code from:
                // https://www.geeksforgeeks.org/different-ways-to-programmatically-restart-an-android-app-on-button-click/
                requireActivity().finish();
                startActivity(requireActivity().getIntent());
                requireActivity().overridePendingTransition(0, 0);
            });

            builder.show();
        });

        // code for version display
        TextView tv = v.findViewById(R.id.version);
        String version;

        try {
            version = requireContext().getPackageManager().getPackageInfo(
                    requireContext().getPackageName(), 0
            ).versionName;
        } catch (PackageManager.NameNotFoundException ignore) {
            // should never reach here but just in case
            version = "unknown";
        }

        tv.setText("Tabletop Tools " + version);

        return v;
    }
}
