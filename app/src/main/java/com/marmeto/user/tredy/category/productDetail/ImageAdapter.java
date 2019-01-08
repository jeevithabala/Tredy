package com.marmeto.user.tredy.category.productDetail;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.databinding.SelectitemAdapterBinding;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{


     Context mContext;
    private ArrayList<Storefront.Image> itemsList;
    private LayoutInflater layoutInflater;

     ImageAdapter(Context mContext, ArrayList<Storefront.Image> itemsList) {
        this.mContext = mContext;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        SelectitemAdapterBinding productbinding =  DataBindingUtil.inflate(layoutInflater, R.layout.selectitem_adapter, parent, false);
        return new ViewHolder(productbinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




        holder.binding.setImage(itemsList.get(position));

    }

    @BindingAdapter("itemsList")
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(view);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final SelectitemAdapterBinding binding;
        public ViewHolder(final SelectitemAdapterBinding itembinding) {
            super(itembinding.getRoot());

            this.binding= itembinding;
        }
    }

}
