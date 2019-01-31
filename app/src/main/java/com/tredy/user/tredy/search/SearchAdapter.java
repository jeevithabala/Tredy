package com.tredy.user.tredy.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.tredy.user.tredy.callback.ProductClickInterface;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.databinding.SearchAdapterBinding;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context mContext;
    private ArrayList<SearchModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private ProductClickInterface productClickInterface;

     SearchAdapter(Context mContext, ArrayList<SearchModel> itemsList,ProductClickInterface productClickInterface) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.productClickInterface=productClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }


        SearchAdapterBinding searchAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.search_adapter, parent, false);
        return new ViewHolder(searchAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setSearchproduct(itemsList.get(position));
        holder.binding.setVariable(BR.itemclick,productClickInterface);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
//        Log.e("itemsize_discount", String.valueOf(itemsList.size()));
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final SearchAdapterBinding binding;


        public ViewHolder(final SearchAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;


        }


    }


}



