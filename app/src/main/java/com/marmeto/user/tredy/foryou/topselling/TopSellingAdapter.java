package com.marmeto.user.tredy.foryou.topselling;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marmeto.user.tredy.category.productDetail.ProductView;
import com.marmeto.user.tredy.callback.CartController;
import com.marmeto.user.tredy.callback.CommanCartControler;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.databinding.TopsellingAdapterBinding;
import com.marmeto.user.tredy.util.Config;

import java.util.ArrayList;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TopSellingModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private CartController cartController;
    private CommanCartControler commanCartControler;

    public TopSellingAdapter(Context mContext, ArrayList<TopSellingModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        TopsellingAdapterBinding topSellingAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.topselling_adapter, parent, false);
        return new ViewHolder(topSellingAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setTopselling(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private final TopsellingAdapterBinding binding;


        public ViewHolder(final TopsellingAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;

            binding.setOnitemclick(() -> {
                if (Config.isNetworkAvailable(mContext)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "topselling");
                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "productview");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    if(fragmentManager.findFragmentByTag("productview")==null)
                    {
                        ft.addToBackStack("productview");
                        ft.commit();
                    }
                    else
                    {
                        ft.commit();
                    }
                } else {
                    Toast.makeText(mContext, "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }


            });

            binding.setOnitemclickplus(new Plus() {

                @Override
                public void OnclickPlus() {
                    if (Config.isNetworkAvailable(mContext)) {
                        cartController = new CartController(mContext);
                        commanCartControler = cartController;
//                    commanCartControler.AddToCart(itemsList.get(getAdapterPosition()).getProduct_ID().trim());
                        commanCartControler.AddToCartGrocery(String.valueOf(itemsList.get(getAdapterPosition()).getProduct_ID()), 0, 1);
                        Toast.makeText(mContext,"Added to cart",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void OnclickWhislilst() {
                    if (Config.isNetworkAvailable(mContext)) {
                        cartController = new CartController(mContext);
                        commanCartControler = cartController;
                        commanCartControler.AddToWhislist(itemsList.get(getAdapterPosition()).getProduct_ID().trim());
                        Toast.makeText(mContext,"Added to Wishlist",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }



}
