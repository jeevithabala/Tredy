package com.tredy.user.tredy.filter.sortby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;


import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.FilterSharedPreference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SortByAdapter extends RecyclerView.Adapter<SortByAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<SortByModel> itemsList;
    private ArrayList<String> selectedList = new ArrayList<>();
     FragmentManager fragmentManager;

    public SortByAdapter(Context mContext, ArrayList<SortByModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }


    // Create new views
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        // create a new view
        @SuppressLint("InflateParams") View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.sortbyadapter, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.Name.setText(itemsList.get(position).getTitle());

//        viewHolder.chkSelected.setChecked(itemsList.get(position).isChecked());
        viewHolder.chkSelected.setChecked(getFromSP(itemsList.get(position).title));
        viewHolder.chkSelected.setTag(itemsList.get(position));

        selectedList.clear();
        for (int i = 0; i < itemsList.size(); i++) {
            String value = String.valueOf(getFromSP(itemsList.get(i).title));
            if (value.equals("true")) {
                selectedList.add(itemsList.get(i).getTitle());
            }
        }

        viewHolder.chkSelected.setOnClickListener(v -> {


            RadioButton cb = (RadioButton) v;
            SortByModel filterModel = (SortByModel) cb.getTag();

            for (int i = 0; i < itemsList.size(); i++) {
                itemsList.get(i).setChecked(false);
                FilterSharedPreference.saveInSp_sort(itemsList.get(i).getTitle(), false, getApplicationContext());
//                    viewHolder.chkSelected.setVisibility(View.GONE);
                selectedList.remove(itemsList.get(i).getTitle());
            }
            notifyDataSetChanged();

            filterModel.setChecked(cb.isChecked());
            itemsList.get(position).setChecked(cb.isChecked());
//                viewHolder.chkSelected.setVisibility(View.VISIBLE);

            if (cb.isChecked()) {
                selectedList.add(itemsList.get(position).getTitle());
                FilterSharedPreference.saveInSp_sort(itemsList.get(position).getTitle(), true, getApplicationContext());
            } else {
                selectedList.remove(itemsList.get(position).getTitle());
                FilterSharedPreference.saveInSp_sort(itemsList.get(position).getTitle(), false, getApplicationContext());
            }

        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private boolean getFromSP(String key) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("sort_by", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


    public void sortclear() {
        for (int i = 0; i < itemsList.size(); i++) {

            itemsList.get(i).setChecked(false);
            FilterSharedPreference.saveInSp_sort(itemsList.get(i).getTitle(), false, getApplicationContext());
//                    viewHolder.chkSelected.setVisibility(View.GONE);
            selectedList.remove(itemsList.get(i).getTitle());
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView Name;
        RadioButton chkSelected;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            Name = itemLayoutView.findViewById(R.id.txt_item_list_title);
            chkSelected = itemLayoutView.findViewById(R.id.cbSelected);
        }

    }

    public ArrayList<String> getSelectedSortList() {
        return selectedList;
    }


}


