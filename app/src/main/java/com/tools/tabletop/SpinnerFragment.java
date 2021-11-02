package com.tools.tabletop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import java.util.Random;


public class SpinnerFragment extends Fragment implements View.OnClickListener, Animation.AnimationListener {
    private View v;

    private PieChart pC;
    private ImageView p;

    private ArrayList<PieEntry> pe;
    private ArrayList<Integer> colors;
    private static final Random rdm = new Random();
    private float dgr = 0f;
    private boolean spinning = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_spinner, container, false);

        pC = v.findViewById(R.id.circular);
        p = v.findViewById(R.id.pointer);

        Button b = v.findViewById(R.id.cir_btn);
        b.setOnClickListener(this);

        pe = new ArrayList<>();
        colors = new ArrayList<>();

        this.pieChartSetup();
        this.loadPieChart();

        return v;
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
        pe.add(new PieEntry(25f, "Red"));
        pe.add(new PieEntry(25f, "Blue"));
        colors.add(Color.parseColor("#d63031"));
        colors.add(Color.parseColor("#0984e3"));

        // the default
        float remain = 100f;
        for (PieEntry i: pe)
            remain -= i.getValue();
        pe.add(new PieEntry(remain, "Default"));
        colors.add(Color.parseColor("#b2bec3"));

        PieDataSet pds = new PieDataSet(pe, "Custom");
        pds.setColors(colors);

        PieData pd = new PieData(pds);
        pd.setDrawValues(false);

        pC.setData(pd);
        pC.invalidate();
    }

    private void spin() {
        // code reference: https://youtu.be/5O2Uox-TR00
        if(this.spinning) return;
        this.spinning = true;

        float gen = rdm.nextFloat() * 360;
        int spins = rdm.nextInt(9) + 1;
        float cal = (360*spins) + gen;

        RotateAnimation rtAnim = new RotateAnimation(0, cal,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rtAnim.setDuration(500);
        rtAnim.setInterpolator(new DecelerateInterpolator());
        rtAnim.setFillAfter(true);
        rtAnim.setAnimationListener(this);

        this.dgr = cal % 360;
        this.p.startAnimation(rtAnim);
    }

    @Override
    public void onClick(View v) {
        this.spin();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        String result = "...";
        float temp = 0f;

        for (PieEntry i: pe) {
            float prev = temp;
            temp += 360 / (100 / i.getValue());
            if (prev <= this.dgr && this.dgr < temp) {
                result = i.getLabel();
                break;
            }
        }

        Toast.makeText(v.getContext(),
                "You got " + result + " (" + this.dgr + ")",
                Toast.LENGTH_SHORT).show();
        this.spinning = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
