package com.tools.tabletop;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class SpinnerFragment extends Fragment implements
        View.OnClickListener, Animation.AnimationListener {
    private View v;

    private PieChart pC;
    private ImageView p;

    private PieEntry[] pe;
    private int[] colors;
    private static final Random rdm = new Random();
    private float dgr = -98764f;
    private boolean spinning = false;

    private ArrayList<String[]> spinHis;
    private HistoryFragment<String[]> spinHisFrg;

    private ArrayList<String[]> custom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_spinner, container, false);

        if (spinHis == null) {
            this.spinHis = new ArrayList<>();
            this.spinHisFrg = new HistoryFragment<>(this.spinHis, 2);
        }

        if (custom == null) {
            custom = new ArrayList<>();

            // TODO: temporary
            custom.add(new String[]{"Red", "#d63031", "30f"});
            custom.add(new String[]{"Blue", "#0984e3", "35f"});
        }

        pC = v.findViewById(R.id.circular);
        p = v.findViewById(R.id.pointer);

        Button b = v.findViewById(R.id.cir_btn);
        b.setOnClickListener(this);

        pe = new PieEntry[this.custom.size() + 1];
        colors = new int[this.custom.size() + 1];

        this.pieChartSetup();
        this.loadPieChart();

        if (this.dgr == -98764f) this.dgr = 0f;
        else {
            this.p.setRotation(this.dgr);
        }

        setHasOptionsMenu(true);
        return v;
    }

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


        super.onCreateOptionsMenu(menu, inflater);
    }

    private void pieChartSetup() {
        pC.setDrawHoleEnabled(false);
        pC.setRotationEnabled(false);
        pC.getDescription().setEnabled(false);
        pC.getLegend().setEnabled(false);
        pC.setDrawEntryLabels(false);
    }

    private void loadPieChart() {
        // code reference: https://youtu.be/S3zqxVoIUig
        for (int i = 0; i < this.custom.size(); i++) {
            String[] data = this.custom.get(i);
            pe[i] = new PieEntry(Float.parseFloat(data[2]), data[0]);
            colors[i] = Color.parseColor(data[1]);
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

    @Override
    public void onAnimationEnd(Animation animation) {
        int result = -1;
        float temp = 0f;

        for (int i = 0; i < pe.length; i++) {
            float prev = temp;
            temp += 360 / (100 / pe[i].getValue());
            if (prev <= this.dgr && this.dgr < temp) {
                result = i;
                break;
            }
        }

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
