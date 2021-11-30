package com.tools.tabletop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * PointsFragment inherits from Fragment and associated with fragment_points
 */
public class PointsFragment extends Fragment {

    private String[] deleted; // the most recently deleted points tracker

    private TextView msg; // text view to display empty list notifier
    private RecyclerView rv; // recycler view
    private ArrayList<String[]> data; // data of the points tracker
    private ArrayList<String[]> display; // array list of points tracker currently on screen

    private PointsSetting setting; // points setting fragment

    /**
     * item touch helper for the recycler view
     */
    private final ItemTouchHelper itTh = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        /**
         * method that sets the allowed movement for the item within the recycler view
         */
        @Override
        public int getMovementFlags(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            );
        }

        /**
         * Method responsible for movable item position within recycler view
         */
        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {

            int start = viewHolder.getAdapterPosition();
            int end = target.getAdapterPosition();

            Collections.swap(display, start, end);
            Collections.swap(data, start, end);

            Objects.requireNonNull(rv.getAdapter()).notifyItemMoved(start, end);
            saveData();
            return true;
        }

        /**
         * Method responsible for item swipe action
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // code reference: https://youtu.be/Aup-aPj24eU
            int pos = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) { // left swipe for deletion
                display.remove(pos);
                deleted = data.remove(pos);
                Objects.requireNonNull(rv.getAdapter()).notifyItemRemoved(pos);

                // bottom "backtrack" for the recently deleted item
                Snackbar.make(
                        rv, String.format("Deleted: %s", deleted[0]),
                        Snackbar.LENGTH_LONG).setAction("Undo", view -> {
                            display.add(pos, deleted);
                            data.add(pos, deleted);
                            rv.getAdapter().notifyItemInserted(pos);
                            msg.setText("");
                            saveData();
                        }).show();
                if (display.size() == 0) msg.setText(R.string.kind_of_lonely_in_here);
                saveData();

            } else { // right swipe for edit / updating data

                // code reference: https://youtu.be/eslYJArppnQ
                String[] target = display.get(pos);
                EditText et = new EditText(requireContext());
                et.setText(target[0]);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Change Name");
                builder.setCancelable(true);
                builder.setView(et);

                builder.setNeutralButton("Cancel", (d, v) ->
                        Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(pos));

                // event listener to restore the item when "blank spaces" are touched
                builder.setOnCancelListener(i ->
                        Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(pos));

                builder.setPositiveButton("Update", (d, v) -> {
                   target[0] = et.getText().toString();
                   data.get(pos)[0] = et.getText().toString();
                   Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(pos);
                   saveData();
                });

                builder.show();
            }
        }
    });

    /**
     * Method called to initialize view graphics
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_points, container, false);

        // Code Reference: https://youtu.be/xYmH61Ilglc
        this.rv = v.findViewById(R.id.pts_list);
        this.msg = v.findViewById(R.id.empty_msg_2);

        // initialize data and display ArrayList if null
        if (this.data == null) {
            this.data = new ArrayList<>();
            this.display = new ArrayList<>(this.data);
            this.loadData();
        }

        if (this.setting == null) this.setting = new PointsSetting();

        PointsAdapter pa = new PointsAdapter(this.display);
        this.loadSetting(pa);
        this.rv.setAdapter(pa);
        this.itTh.attachToRecyclerView(this.rv);

        if (data.size() > 0) this.msg.setText("");

        setHasOptionsMenu(true);

        return v;
    }

    /**
     * Method that loads data from shared preference into data and display ArrayList
     */
    private void loadData() {
        // code reference: https://youtu.be/TsASX0ZK9ak
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Gson gson = new Gson();

        data = gson.fromJson(
                sp.getString("pts_data", ""),
                new TypeToken<ArrayList<String[]>>() {}.getType()
        );

        if (data == null) this.data = new ArrayList<>();

        display.addAll(data);
    }

    /**
     * Method that saves data from the current data variable into shared preference (storage)
     */
    private void saveData() {
        // code reference: https://youtu.be/TsASX0ZK9ak
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();

        editor.putString("pts_data", gson.toJson(this.data));
        editor.apply();
    }

    /**
     * Loads the add and subtract integer from shared preference into the passed in PointsAdapter
     * @param ref PointsAdapter to change the ptsAdd and ptsMin variable
     */
    private void loadSetting(PointsAdapter ref) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        ref.ptsAdd = Integer.parseInt(sp.getString("pts_add", "10"));
        ref.ptsMin = Integer.parseInt(sp.getString("pts_sub", "10"));
    }

    /**
     * Method called to initialize menu view graphics
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.points_menu, menu);

        // code reference: https://youtu.be/V1aLOkwV84o

        // code for the search button / feature
        MenuItem search = menu.findItem(R.id.search);
        SearchView sv = (SearchView) search.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Method for handling action when search text is submitted
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // default handling
            }

            /**
             * Method for handling query text change
             */
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) { // empty text, add all data back to display
                    display.clear();
                    display.addAll(data);
                } else { // input -> if the text is found within the name in any form
                    display.clear();
                    newText = newText.toLowerCase(Locale.getDefault());

                    for (String[] i: data) {
                        if(i[0].toLowerCase(Locale.getDefault()).contains(newText)) {
                            display.add(i);
                        }
                    }

                }

                Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();

                return true;
            }
        });

        // points setting menu
        MenuItem s = menu.findItem(R.id.pts_settings);
        s.setOnMenuItemClickListener(i -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.frg_container, this.setting
            ).addToBackStack(null).commit();
            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Method for handling when an item is selected from the menu, specifically for adding
     * a new tracker in this case
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_plyer) {
            EditText et = new EditText(requireContext());
            et.setText(R.string.new_ch);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("New Points Tracker");
            builder.setCancelable(true);
            builder.setView(et);

            builder.setNeutralButton("Cancel", null);

            builder.setPositiveButton("Create", (d, v) -> {
                String[] created = {et.getText().toString(), "0"};
                data.add(created);
                display.add(created);
                Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(display.size() - 1);
                saveData();
            });
            this.msg.setText("");

            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

/**
 * PointsAdapter class inherits from RecyclerView.Adapter that deals with card_pts view within
 * recycler view.
 */
class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.Viewholder>{

    private final ArrayList<String[]> ptsAL; // points tracker reference
    public Integer ptsAdd = 10; // modifiable integer for points to add when add button is pressed
    public Integer ptsMin = 10; // modifiable integer for points to remove when minus button is pressed

    /**
     * PointsAdapter class constructor
     * @param ini points tracker reference
     */
    public PointsAdapter(ArrayList<String[]> ini) {
        this.ptsAL = ini;
    }

    /**
     * Method responsible for generating the view of the item(s) within recycler view
     */
    @NonNull
    @Override
    public PointsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_pts, parent, false);
        return new Viewholder(view) {
        };
    }

    /**
     * Method that modifies the item view base on tracker data
     */
    @Override
    public void onBindViewHolder(@NonNull PointsAdapter.Viewholder holder, int position) {
        String[] tmp = this.ptsAL.get(position);
        holder.pts.setText(tmp[1]);
        holder.ply.setText(tmp[0]);

        holder.pBtn.setOnClickListener(v -> {
            String[] temp = ptsAL.get(position);
            int val = Integer.parseInt(temp[1]) + ptsAdd;
            holder.pts.setText(String.valueOf(val));
            temp[1] = String.valueOf(val);
        });

        holder.mBtn.setOnClickListener(v -> {
            String[] temp = ptsAL.get(position);
            int val = Integer.parseInt(temp[1]) - ptsMin;
            holder.pts.setText(String.valueOf(val));
            temp[1] = String.valueOf(val);
        });
    }

    /**
     * Method that returns total item count
     * @return total item count within the "recycler list"
     */
    @Override
    public int getItemCount() {
        return this.ptsAL.size();
    }

    /**
     * Viewholder (points tracker) that inherits from RecyclerView.ViewHolder
     */
    public static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView pts, ply; // text views from card_pts
        private final ImageButton pBtn, mBtn; // image buttons from card_pts

        /**
         * Constructor of Viewholder (points tracker) class
         * @param itemView View
         */
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.pts = itemView.findViewById(R.id.card_pts_info);
            this.ply = itemView.findViewById(R.id.card_pts_player);
            this.pBtn = itemView.findViewById(R.id.plus_btn);
            this.mBtn = itemView.findViewById(R.id.minus_btn);
        }
    }
}
