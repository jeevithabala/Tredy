package com.marmeto.user.tredy.foryou.topselling;

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

import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.bag.cartdatabase.DBHelper;
import com.marmeto.user.tredy.category.productDetail.ProductView;
import com.marmeto.user.tredy.category.productDetail.SelectItemModel;
import com.marmeto.user.tredy.callback.CartController;
import com.marmeto.user.tredy.callback.CommanCartControler;
import com.marmeto.user.tredy.callback.TopSellingInterface;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.databinding.TopsellingAdapterBinding;

import java.util.ArrayList;
import java.util.List;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TopSellingModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private List<AddToCart_Model> cartList = new ArrayList<>();
    DBHelper db;
    SelectItemModel model;
    CartController cartController;
    CommanCartControler commanCartControler;





    public TopSellingAdapter(Context mContext, ArrayList<TopSellingModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }

    public TopSellingAdapter(ArrayList<TopSellingModel> itemsList, FragmentManager fragmentManager) {

        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        TopsellingAdapterBinding topSellingAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.topselling_adapter, parent, false);
        return new ViewHolder(topSellingAdapterBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

            binding.setOnitemclick(new TopSellingInterface() {
                @Override
                public void onClicksellingPostion() {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "topselling");
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
