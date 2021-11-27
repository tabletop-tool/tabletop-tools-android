package com.tools.tabletop;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SpinnerSetting extends Fragment {
    private SharedPreferences sp;
    private ArrayList<String[]> data;

    private String[] deleted;
    private RecyclerView rv;
    private TextView tv;

    private final ItemTouchHelper itTh = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            );
        }

        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {

            int start = viewHolder.getAdapterPosition();
            int end = target.getAdapterPosition();

            Collections.swap(data, start, end);

            Objects.requireNonNull(rv.getAdapter()).notifyItemMoved(start, end);
            saveData();
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // code reference: https://youtu.be/Aup-aPj24eU
            int pos = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {
                deleted = data.get(pos);
                data.remove(pos);
                Objects.requireNonNull(rv.getAdapter()).notifyItemRemoved(pos);

                Snackbar.make(
                        rv, String.format("Deleted: %s", deleted[0]),
                        Snackbar.LENGTH_LONG).setAction("Undo", view -> {
                    data.add(pos, deleted);
                    rv.getAdapter().notifyItemInserted(pos);
                    tv.setText("");
                    saveData();
                }).show();
                if (data.size() == 0) tv.setText(R.string.only_default);
                saveData();

            } else { // right
                // code reference: https://youtu.be/eslYJArppnQ

                createBuilder(pos).show();
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spin_setting, container, false);

        if(sp == null) sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        loadData();

        this.rv = v.findViewById(R.id.pts_custom_list);
        this.tv = v.findViewById(R.id.empty_msg_3);

        this.rv.setAdapter(new SpinnerCustomAdapter(this.data));
        this.itTh.attachToRecyclerView(this.rv);

        if (data.size() > 0) tv.setText("");

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.spin_setting_menu, menu);

        MenuItem add = menu.findItem(R.id.add_btn);
        add.setOnMenuItemClickListener(x -> {
            this.createBuilder(-1).show();
            return true;
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    private AlertDialog.Builder createBuilder(int position) {
        float limit = 0f;
        for (String[] i: this.data) limit += Float.parseFloat(i[2]);
        limit = 100 - limit;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        final int[] c = new int[1];

        if (position == -1) c[0] = Color.RED;
        else c[0] = Integer.parseInt(this.data.get(position)[1]);

        LinearLayout l = new LinearLayout(builder.getContext());
        l.setOrientation(LinearLayout.VERTICAL);

        EditText et = new EditText(builder.getContext());
        EditText percent = new EditText(builder.getContext());
        Button btn = new Button(builder.getContext());
        l.addView(et);
        l.addView(percent);
        l.addView(btn);

        et.setHint("Label Name");
        btn.setBackgroundColor(c[0]);

        percent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        btn.setText(R.string.set_color);
        btn.setOnClickListener(n -> {
            ColorPickerPopUp cp = new ColorPickerPopUp(builder.getContext());
            cp.setDefaultColor(c[0]);
            cp.setDialogTitle("Pick a color");
            cp.setOnPickColorListener(new ColorPickerPopUp.OnPickColorListener() {
                @Override
                public void onColorPicked(int color) {
                    btn.setBackgroundColor(color);
                    c[0] = color;
                }

                @Override
                public void onCancel() {
                    cp.dismissDialog();
                }
            });

            cp.show();
        });

        builder.setCancelable(true);
        builder.setView(l);

        builder.setNeutralButton("Cancel", (d, v) ->
                Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(position));
        builder.setOnCancelListener(k ->
                Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(position));

        if (position == -1) {
            builder.setTitle("Adding New Spinner Portion");
            builder.setPositiveButton("Add", (d, v) -> {
                String check1, check2;
                check1 = et.getText().toString();
                check2 = percent.getText().toString();
                if (check1.equals("") || check2.equals("")) {
                    Toast.makeText(
                            requireContext(),
                            "Invalid input, action aborted",
                            Toast.LENGTH_LONG).show();
                } else {
                    data.add(new String[]{
                            check1,
                            String.valueOf(c[0]),
                            check2 });
                    saveData();
                    tv.setText("");
                    Objects.requireNonNull(
                            rv.getAdapter()).notifyItemInserted(data.size() - 1);
                }
            });
        } else {
            String[] target = this.data.get(position);

            et.setText(target[0]);
            percent.setText(target[2]);

            limit += Float.parseFloat(target[2]);

            builder.setTitle("Editing Spinner Portion");
            builder.setPositiveButton("Update", (d, v) -> {
                String check1, check2;
                check1 = et.getText().toString();
                check2 = percent.getText().toString();
                if (check1.equals("") || check2.equals("")) {
                    Toast.makeText(
                            requireContext(),
                            "Invalid input, action aborted",
                            Toast.LENGTH_LONG).show();
                } else {
                    target[0] = check1;
                    target[2] = check2;
                    target[1] = String.valueOf(c[0]);
                    saveData();
                }
                Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(position);
            });
        }

        percent.setHint(String.format("Percentage: 0 - %.2f", limit));
        percent.setFilters(new InputFilter[]{new CustomInputFilter(0, limit)});

        return builder;
    }

    private void loadData() {
        Gson gson = new Gson();

        data = gson.fromJson(
                sp.getString("spin_data", ""),
                new TypeToken<ArrayList<String[]>>() {}.getType()
        );

        if (data == null) this.data = new ArrayList<>();
    }

    private void saveData() {
        SharedPreferences.Editor edit = sp.edit();
        Gson gson = new Gson();

        edit.putString("spin_data", gson.toJson(this.data));
        edit.apply();
    }

    private static class CustomInputFilter implements InputFilter {
        // some code from:
        // https://stackoverflow.com/questions/14212518/is-there-a-way-to-define-a-min-and-max-value-for-edittext-in-android

        private final float min, max;

        public CustomInputFilter(float min, float max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(
                CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                float input = Float.parseFloat(dest.toString() + source.toString());
                if (max > min ? input >= min && input <= max : input >= max && input <= min) {
                    return null;
                }
            } catch (NumberFormatException ignored) {}

            return "";
        }
    }
}

class SpinnerCustomAdapter extends RecyclerView.Adapter<SpinnerCustomAdapter.Viewholder> {

    private final ArrayList<String[]> custom;

    SpinnerCustomAdapter(ArrayList<String[]> custom) {
        this.custom = custom;
    }

    @NonNull
    @Override
    public SpinnerCustomAdapter.Viewholder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_spin_setting, parent, false);
        return new SpinnerCustomAdapter.Viewholder(view);
    }

    @Override
    public int getItemCount() {
        return this.custom.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private final CardView bk;
        private final TextView label, per;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.bk = itemView.findViewById(R.id.card_spin_setting);
            this.label = itemView.findViewById(R.id.spin_label);
            this.per = itemView.findViewById(R.id.spin_percent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerCustomAdapter.Viewholder holder, int position) {
        String[] tmp = this.custom.get(position);
        holder.label.setText(tmp[0]);
        holder.per.setText(String.format("%.2f%%", Float.parseFloat(tmp[2])));
        holder.bk.setCardBackgroundColor(Integer.parseInt(tmp[1]));
    }
}
