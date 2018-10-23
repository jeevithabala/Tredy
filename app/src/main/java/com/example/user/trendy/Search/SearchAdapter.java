package com.example.user.trendy.Search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.user.trendy.Interface.FragmentRecyclerViewClick;
import com.example.user.trendy.R;
import com.example.user.trendy.databinding.SearchAdapterBinding;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context mContext;
    ArrayList<SearchModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;

    public SearchAdapter(Context mContext, ArrayList<SearchModel> itemsList) {
        this.mContext = mContext;
        this.itemsList = itemsList;
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


//            binding.setItemclick(new FragmentRecyclerViewClick() {
//                @Override
//                public void onClickPostion() {
//
//                    discountinterface.discountValue(itemsList.get(getAdapterPosition()).getValue(), itemsList.get(getAdapterPosition()).getTitle());
//
//                }
//            });
        }


    }

//    public void filter(String queryText)
//    {
//        itemsList.clear();
//
//        if(queryText.isEmpty())
//        {
//            itemsList.addAll(copyList);
//        }
//        else
//        {
//
//            for(String name: copyList)
//            {
//                if(name.toLowerCase().contains(queryText.toLowerCase()))
//                {
//                    itemsList.add(name);
//                }
//            }
//
//        }
//
//        notifyDataSetChanged();
//    }


}



