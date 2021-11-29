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
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


/**
 * SpinnerFragment class inherits from Fragment and implements View.OnClickListener and
 * Animation.AnimationListener. This class is associated with fragment_spinner.
 */
public class SpinnerFragment extends Fragment implements
        View.OnClickListener, Animation.AnimationListener {
    private static final Random rdm = new Random(); // random generator
    private View v; // view variable associated with fragment_spinner

    private PieChart pC; // pie chart display for the spinner
    private ImageView p; // image of the pointer

    private PieEntry[] pe; // pie chart data (label and value)
    private int[] colors; // pie chart color
    private float dgr = -98764f; // current degree for the pointer
    private boolean spinning = false; // whether or not spinner is spinning at the moment

    private ArrayList<String[]> spinHis; // spinner history
    private HistoryFragment<String[]> spinHisFrg; // spinner history fragment
    private SpinnerSetting setting; // spinner setting preference fragment

    private ArrayList<String[]> custom; // data of user's custom pie chart

    /**
     * Method called to initialize view graphics
     *
     * @param inflater LayoutInflater
     * @param container nullable ViewGroup
     * @param savedInstanceState nullable savedInstance
     * @return initialized view layout for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_spinner, container, false);

        // initialize spinner history if null
        if (this.spinHis == null) {
            this.spinHis = new ArrayList<>();
            this.spinHisFrg = new HistoryFragment<>(this.spinHis, 2);
        }

        // initialize spinner setting if null
        if (this.setting == null) this.setting = new SpinnerSetting();

        // initialize user's custom pie chart data if null
        if (this.custom == null) this.custom = new ArrayList<>();
        this.loadData();

        // initialize pie chart
        pC = v.findViewById(R.id.circular);
        p = v.findViewById(R.id.pointer);

        FloatingActionButton b = v.findViewById(R.id.cir_btn);
        b.setOnClickListener(this);

        pe = new PieEntry[this.custom.size() + 1];
        colors = new int[this.custom.size() + 1];

        // pie chart setup
        pC.setDrawHoleEnabled(false);
        pC.setRotationEnabled(false);
        pC.getDescription().setEnabled(false);
        pC.getLegend().setEnabled(false);
        pC.setDrawEntryLabels(false);
        this.loadPieChart();

        if (this.dgr == -98764f) this.dgr = 0f;
        else {
            this.p.setRotation(this.dgr);
        }

        setHasOptionsMenu(true);
        return v;
    }

    /**
     * private method that loads custom variable with data from shared preference
     */
    private void loadData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Gson gson = new Gson();

        this.custom = gson.fromJson(
                sp.getString("spin_data", ""),
                new TypeToken<ArrayList<String[]>>() {}.getType()
        );

        if (this.custom == null) this.custom = new ArrayList<>();
    }

    /**
     * Method called to initialize menu view graphics
     *
     * @param menu Menu
     * @param inflater MenuInflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.basic_menu, menu);

        MenuItem history = menu.findItem(R.id.history_btn);
        history.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.spinHisFrg
            ).addToBackStack(null).commit();
            return true;
        });

        MenuItem setting = menu.findItem(R.id.settings_btn);
        setting.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.setting
            ).addToBackStack(null).commit();
            return true;
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * private method that initialize the pie chart with data from custom variable
     */
    private void loadPieChart() {
        // code reference: https://youtu.be/S3zqxVoIUig
        for (int i = 0; i < this.custom.size(); i++) {
            String[] data = this.custom.get(i);
            pe[i] = new PieEntry(Float.parseFloat(data[2]), data[0]);
            colors[i] = Integer.parseInt(data[1]);
        }

        // the default
        float remain = 100f;
        for (int i = 0; i < this.custom.size(); i++)
            remain -= pe[i].getValue();
        pe[this.custom.size()] = new PieEntry(remain, "Default");
        colors[this.custom.size()] = Color.parseColor("#b2bec3");

        PieDataSet pds = new PieDataSet(Arrays.asList(pe), "Custom");
        pds.setColors(colors);

        PieData pd = new PieData(pds);
        pd.setDrawValues(false);

        pC.setData(pd);
        pC.invalidate();
    }

    /**
     * method associated with the spinner button click action
     * @param v View
     */
    @Override
    public void onClick(View v) {
        // code reference: https://youtu.be/5O2Uox-TR00
        if(this.spinning) return;
        this.spinning = true;

        this.p.setRotation(0f);

        float gen = rdm.nextFloat() * 360;
        int spins = rdm.nextInt(9) + 1;
        float cal = (360*spins) + gen;

        RotateAnimation rtAnim = new RotateAnimation(0, cal,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rtAnim.setDuration(1000);
        rtAnim.setInterpolator(new DecelerateInterpolator());
        rtAnim.setFillAfter(true);
        rtAnim.setAnimationListener(this);

        this.dgr = cal % 360;
        this.p.startAnimation(rtAnim);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    /**
     * method associated with the spinner image's end animation processing
     *
     * @param animation Animation
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        int result = -1;
        float temp = 0f;

        // calculate result
        for (int i = 0; i < pe.length; i++) {
            float prev = temp;
            temp += 360 / (100 / pe[i].getValue());
            if (prev <= this.dgr && this.dgr < temp) {
                result = i;
                break;
            }
        }

        // notify user of result
        String txt = "...";
        if (result > -1) txt = pe[result].getLabel();

        Toast.makeText(v.getContext(),
                "You got " + txt + " (" + this.dgr + ")", Toast.LENGTH_SHORT).show();
        this.spinning = false;

        String[] insert = {String.valueOf(this.dgr), String.valueOf(this.colors[result]), txt};
        this.spinHis.add(0, insert);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
