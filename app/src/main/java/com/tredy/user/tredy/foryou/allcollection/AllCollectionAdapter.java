package com.tredy.user.tredy.foryou.allcollection;

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

import com.tredy.user.tredy.category.CategoryProduct;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.databinding.AllcollectionAdapterBinding;

import java.util.ArrayList;

public class AllCollectionAdapter extends RecyclerView.Adapter<AllCollectionAdapter.ViewHolder> {

     Context mContext;
    private ArrayList<AllCollectionModel> itemsList;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;


    public AllCollectionAdapter(Context mContext, ArrayList<AllCollectionModel> itemsList, FragmentManager fragmentManager) {
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

        AllcollectionAdapterBinding allcollectionAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.allcollection_adapter, parent, false);
        return new ViewHolder(allcollectionAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setAllcollection(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final AllcollectionAdapterBinding binding;


        public ViewHolder(final AllcollectionAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;


            binding.setItemclick(() -> {
                Bundle bundle = new Bundle();
                bundle.putString("collection", "allcollection");
                bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));

//                onItemClick.onClick(itemsList.get(getAdapterPosition()).getProduct_ID());
//                    Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
//                            .setLineItemsInput(Input.value(Arrays.asList(
//                                    new Storefront.CheckoutLineItemInput(5, new ID(itemsList.get(getAdapterPosition()).getProduct_ID()))
//                            )));
////
                Fragment fragment = new CategoryProduct();

                fragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "categoryproduct");
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                if (fragmentManager.findFragmentByTag("categoryproduct") == null) {
                    ft.addToBackStack("categoryproduct");
                    ft.commit();
                } else {
                    ft.commit();
                }


//                    Intent intent = new Intent(mContext, Main2Activity.class);
//                    intent.putExtra("productDetail",itemsList.get(getAdapterPosition()).getCollection().getId());
//                    mContext.startActivity(intent);
////
            });
        }

    }

}

