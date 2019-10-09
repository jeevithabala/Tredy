package com.tredy.user.tredy.category;

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

import com.tredy.user.tredy.category.model.CategoryModel;
import com.tredy.user.tredy.groceries.Groceries;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.databinding.CategoreyAdapterBinding;
import com.tredy.user.tredy.util.Config;

import java.util.ArrayList;


public class CategoreDetailAdapter extends RecyclerView.Adapter<CategoreDetailAdapter.ViewHolder> {

    Context mContext;
    private   ArrayList<CategoryModel> itemsList;

    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;

    CategoreDetailAdapter(Context mContext, ArrayList<CategoryModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CategoreDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        CategoreyAdapterBinding categoreyAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.categorey_adapter, parent, false);
        return new ViewHolder(categoreyAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setCategory(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final CategoreyAdapterBinding binding;


        public ViewHolder(final CategoreyAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;


            binding.setItemclick(() -> {
                if (Config.isNetworkAvailable(mContext)) {

                    if (itemsList.get(getAdapterPosition()).getCollectiontitle().trim().toLowerCase().equals("grocery")) {
                    Groceries groceries = new Groceries();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction.replace(R.id.home_container, groceries, "grocery");
                    if (fragmentManager.findFragmentByTag("grocery") == null) {
                        transaction.addToBackStack("grocery");
                        transaction.commit();
                    } else {
                        transaction.commit();
                    }

                }   else if (itemsList.get(getAdapterPosition()).getCollectiontitle().trim().toLowerCase().equals("all products")) {
                    Fragment fragment = new CategoryProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("collection", "allproduct");
//                        bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "categoryproduct");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    if (fragmentManager.findFragmentByTag("categoryproduct") == null) {
                        ft.addToBackStack("categoryproduct");
                        ft.commit();
                    } else {
                        ft.commit();
                    }

                } else if (itemsList.get(getAdapterPosition()).getSubCategoryModelArrayList() == null) {

                    Fragment fragment = new CategoryProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("collection", "api");
                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "categoryproduct");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    if (fragmentManager.findFragmentByTag("categoryproduct") == null) {
                        ft.addToBackStack("categoryproduct");
                        ft.commit();
                    } else {
                        ft.commit();
                    }

                } else {

                    Fragment fragment = new SubCategory();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    if (fragmentManager.findFragmentByTag("fragment") == null) {
                        ft.addToBackStack("fragment");
                        ft.commit();
                    } else {
                        ft.commit();
                    }


                }
                } else {
                    Toast.makeText(mContext, "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();

                }
//
//                    Intent intent = new Intent(mContext, Main2Activity.class);
//                    intent.putExtra("productDetail",itemsList.get(getAdapterPosition()).getCollection().getId());
//                    mContext.startActivity(intent);

            });
        }


    }
}
