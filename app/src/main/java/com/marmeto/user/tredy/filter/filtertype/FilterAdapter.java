package com.marmeto.user.tredy.filter.filtertype;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.util.FilterSharedPreference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    Context mContext;
    ArrayList<FilterModel> itemsList;
    private ArrayList<String> selectedList = new ArrayList<>();
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;

    public FilterAdapter(Context mContext, ArrayList<FilterModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }


    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.filterbyadapter, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final int pos = position;

        viewHolder.Name.setText(itemsList.get(position).getTitle());

        viewHolder.chkSelected.setChecked(getFromSP(itemsList.get(position).title));

//        viewHolder.chkSelected.setChecked(getFromSP("cb1"));

        viewHolder.chkSelected.setTag(itemsList.get(position));

        selectedList.clear();
        for (int i = 0; i <itemsList.size() ; i++) {
            String value = String.valueOf(getFromSP(itemsList.get(i).title));
        if(value.equals("true")){
            selectedList.add(itemsList.get(i).getTitle());
        }
        }
        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                FilterModel filterModel = (FilterModel) cb.getTag();

                filterModel.setChecked(cb.isChecked());
                itemsList.get(pos).setChecked(cb.isChecked());

                if (cb.isChecked()) {
                    selectedList.add(itemsList.get(pos).getTitle());
//                    saveInSp(itemsList.get(pos).getTitle(),true);
                } else {
                    selectedList.remove(itemsList.get(pos).getTitle());
                    FilterSharedPreference.saveInSp(itemsList.get(pos).getTitle(),false,getApplicationContext());
//                    saveInSp(itemsList.get(pos).getTitle(),false);

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private boolean getFromSP(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Name;
        public CheckBox chkSelected;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            Name = (TextView) itemLayoutView.findViewById(R.id.txt_item_list_title);
            chkSelected = (CheckBox) itemLayoutView.findViewById(R.id.cbSelected);
        }

    }

    public ArrayList<String> getSelectedContactList() {
        return selectedList;
    }

    public void typeclear() {
        for (int i = 0; i < itemsList.size(); i++) {

            itemsList.get(i).setChecked(false);
            FilterSharedPreference.saveInSp(itemsList.get(i).getTitle(),false,getApplicationContext());
//                    viewHolder.chkSelected.setVisibility(View.GONE);
            selectedList.remove(itemsList.get(i).getTitle());
        }
        notifyDataSetChanged();
    }

}



