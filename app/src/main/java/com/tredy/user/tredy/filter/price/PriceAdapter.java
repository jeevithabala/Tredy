package com.tredy.user.tredy.filter.price;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.FilterSharedPreference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<PriceModel> itemsList;
    private ArrayList<String> selectedList = new ArrayList<>();
     FragmentManager fragmentManager;

    public PriceAdapter(Context mContext, ArrayList<PriceModel> itemsList, FragmentManager fragmentManager) {
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
                R.layout.pricebyadapter, null);

        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        viewHolder.Name.setText("\u20B9" + itemsList.get(position).getTitle());

//        viewHolder.chkSelected.setChecked(itemsList.get(position).isChecked());
        viewHolder.chkSelected.setChecked(getFromSP(itemsList.get(position).title));
        viewHolder.chkSelected.setTag(itemsList.get(position));

        selectedList.clear();
        for (int i = 0; i <itemsList.size() ; i++) {
            String value = String.valueOf(getFromSP(itemsList.get(i).title));
            if(value.equals("true")){
                selectedList.add(itemsList.get(i).getTitle());
            }
        }

        viewHolder.chkSelected.setOnClickListener(v -> {


            RadioButton cb = (RadioButton) v;
            PriceModel filterModel = (PriceModel) cb.getTag();

            for (int i = 0; i < itemsList.size(); i++) {
                itemsList.get(i).setChecked(false);
                FilterSharedPreference.saveInSp_price(itemsList.get(i).getTitle(), false, getApplicationContext());
                selectedList.remove(itemsList.get(i).getTitle());
            }
            notifyDataSetChanged();

            filterModel.setChecked(cb.isChecked());
            itemsList.get(position).setChecked(cb.isChecked());


            if (cb.isChecked()) {
                selectedList.add(itemsList.get(position).getTitle());
                FilterSharedPreference.saveInSp_price(itemsList.get(position).getTitle(), true, getApplicationContext());

            } else {
                selectedList.remove(itemsList.get(position).getTitle());
                FilterSharedPreference.saveInSp_price(itemsList.get(position).getTitle(), false, getApplicationContext());

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private boolean getFromSP(String key) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("price", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
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

    public ArrayList<String> getSelectedPriceList() {
        return selectedList;
    }

    public void priceclear() {
        for (int i = 0; i < itemsList.size(); i++) {
            Log.e("selected", String.valueOf(selectedList.size()));

            itemsList.get(i).setChecked(false);
            FilterSharedPreference.saveInSp_price(itemsList.get(i).getTitle(),false,getApplicationContext());
//                    viewHolder.chkSelected.setVisibility(View.GONE);
            selectedList.remove(itemsList.get(i).getTitle());
            Log.e("selected1", String.valueOf(selectedList.size()));
        }
        notifyDataSetChanged();
    }

}


