package com.example.user.trendy.foryou.newarrival;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.trendy.category.productDetail.ProductView;
import com.example.user.trendy.foryou.topselling.Plus;
import com.example.user.trendy.callback.CartController;
import com.example.user.trendy.callback.CommanCartControler;
import com.example.user.trendy.callback.FragmentRecyclerViewClick;
import com.example.user.trendy.R;
import com.example.user.trendy.databinding.NewarrivalAdapterBinding;

import java.util.ArrayList;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ViewHolder> {

    Context mContext;
    ArrayList<NewArrivalModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    CartController cartController;
    CommanCartControler commanCartControler;


    public NewArrivalAdapter(Context mContext, ArrayList<NewArrivalModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }

    public NewArrivalAdapter(ArrayList<NewArrivalModel> itemsList, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.itemsList = itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        NewarrivalAdapterBinding newarrivalAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.newarrival_adapter, parent, false);
        return new ViewHolder(newarrivalAdapterBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setNewarrival(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final NewarrivalAdapterBinding binding;


        public ViewHolder(final NewarrivalAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;

            binding.setItemclick(new FragmentRecyclerViewClick() {
                @Override
                public void onClickPostion() {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "newarrival");
                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    if(fragmentManager.findFragmentByTag("fragment")==null)
                    {
                        ft.addToBackStack("fragment");
                        ft.commit();
                    }
                    else
                    {
                        ft.commit();
                    }

                }
            });
            binding.setOnitemclickplus(new Plus() {

                @Override
                public void OnclickPlus() {
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler)cartController;
                    commanCartControler.AddToCart(itemsList.get(getAdapterPosition()).getProduct_ID().trim());
                    Toast.makeText(mContext,"Added to cart",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnclickWhislilst() {
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler)cartController;
                    commanCartControler.AddToWhislist(itemsList.get(getAdapterPosition()).getProduct_ID().trim());
                    Toast.makeText(mContext,"Added to Wishlist",Toast.LENGTH_SHORT).show();
                }


            });


        }

    }


}

