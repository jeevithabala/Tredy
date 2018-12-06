package com.example.user.trendy.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.example.user.trendy.callback.ProductClickInterface;
import com.example.user.trendy.R;
import com.example.user.trendy.databinding.SearchAdapterBinding;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context mContext;
    ArrayList<SearchModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    ProductClickInterface productClickInterface;

    public SearchAdapter(Context mContext, ArrayList<SearchModel> itemsList,ProductClickInterface productClickInterface) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.productClickInterface=productClickInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }


        SearchAdapterBinding searchAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.search_adapter, parent, false);
        return new ViewHolder(searchAdapterBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setSearchproduct(itemsList.get(position));
        holder.binding.setVariable(BR.itemclick,productClickInterface);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        Log.e("itemsize_discount", String.valueOf(itemsList.size()));
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



