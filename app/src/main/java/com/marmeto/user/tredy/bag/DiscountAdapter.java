package com.marmeto.user.tredy.bag;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.marmeto.user.tredy.callback.FragmentRecyclerViewClick;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.databinding.DiscountAdapterBinding;

import java.util.ArrayList;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    Context mContext;
    ArrayList<DiscountModel> itemsList;
    Discountinterface discountinterface;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;

    public DiscountAdapter(Context mContext, ArrayList<DiscountModel> itemsList, Discountinterface discountinterface) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.discountinterface = discountinterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }


        DiscountAdapterBinding discountAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.discount_adapter, parent, false);
        return new ViewHolder(discountAdapterBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setDiscount(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.e("itemsize_discount", String.valueOf(itemsList.size()));
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final DiscountAdapterBinding binding;


        public ViewHolder(final DiscountAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;


            binding.setItemclick(new FragmentRecyclerViewClick() {
                @Override
                public void onClickPostion() {
                    discountinterface.discountValue(itemsList.get(getAdapterPosition()).getValue(), itemsList.get(getAdapterPosition()).getTitle());

                }
            });
        }


    }

    public interface Discountinterface {
        void discountValue(String discounted_amount, String coupon);
    }
}


