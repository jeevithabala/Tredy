package com.marmeto.user.tredy.filter.filtertype;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.foryou.MainAdapter;
import com.marmeto.user.tredy.foryou.newarrivalrecycler.NewMainAdapter;

import java.util.ArrayList;


public class FilterHetroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements FilterAdapter.selected {
    private Context context;
    private FragmentManager fragmentManager;
    ArrayList<FilterTilteAndTag> items;
    private final int HORIZONTAL=0;
    private ArrayList<String> selectedList=new ArrayList<>();



    public FilterHetroAdapter(Context context, ArrayList<FilterTilteAndTag> items, FragmentManager fragmentManager) {
        this.context = context;
        this.items = items;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;

                view = inflater.inflate(R.layout.filterrecycler, viewGroup, false);
                holder = new FilterViewHolder(view);
                return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            filterview((FilterViewHolder) viewHolder,i);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void filterview(FilterViewHolder holder, int i) {

        Log.e("filter title", items.get(i).getTitle());
        FilterAdapter adapter = new FilterAdapter(context, items.get(i).getFilterModels(),items.get(i).getTitle(), fragmentManager,this);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.recyclerView.setAdapter(adapter);
        holder.typename.setText(items.get(i).getTitle());

    }

    @Override
    public void selectedli(String select) {
        if(!selectedList.contains(select)) {
            selectedList.add(select);
        }

    }

    @Override
    public void selectedlistremove(String select) {
        selectedList.remove(select);
    }

    public void typeclear() {
        FilterAdapter adapter=new FilterAdapter();
        for (int i = 0; i <items.size() ; i++) {
            adapter.typeclear(items.get(i).getFilterModels());
        }
    }


    public class FilterViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView typename;

        FilterViewHolder(View itemView) {
            super(itemView);
            typename = itemView.findViewById(R.id.typename);
            recyclerView = itemView.findViewById(R.id.filter_recycler);
        }
    }

    public ArrayList<String> getSelectedContactList() {
        return selectedList;
    }


}
