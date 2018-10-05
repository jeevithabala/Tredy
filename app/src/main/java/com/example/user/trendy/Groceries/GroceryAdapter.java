package com.example.user.trendy.Groceries;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.trendy.Category.ProductDetail.ProductView;
import com.example.user.trendy.Category.SubCategoryModel;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Interface.CommanCartControler;
import com.example.user.trendy.Interface.FragmentRecyclerViewClick;
import com.example.user.trendy.R;
import com.example.user.trendy.databinding.GroceryadapterBinding;

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    Context mContext;
    ArrayList<GroceryModel> itemsList;
    CartController cartController;
    CommanCartControler commanCartControler;
    int pos=0;
    String getQuantity = "1";

    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;

    public GroceryAdapter(Context mContext, ArrayList<GroceryModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        GroceryadapterBinding groceryadapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.groceryadapter, parent, false);
        return new ViewHolder(groceryadapterBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setGrocery(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private final GroceryadapterBinding binding;
        TextView textView ,addgrocery;
        Spinner spinner;

        public ViewHolder(final GroceryadapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;
            textView = itemView.findViewById(R.id.qty);
            spinner = itemView.findViewById(R.id.options);
            addgrocery=itemView.findViewById(R.id.addgrocery);

            spinner.setOnItemSelectedListener(this);
            addgrocery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler)cartController;
                    commanCartControler.AddToCartGrocery(String.valueOf(itemsList.get(getAdapterPosition()).getProduct().getId()),pos,Integer.parseInt(getQuantity));
                }
            });
//            spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) mContext);

            binding.setCounter(new GroceryInterface() {
                @Override
                public void increase() {
                    getQuantity = textView.getText().toString();
                    int increase_qty = Integer.parseInt(getQuantity) + 1;
                    getQuantity = String.valueOf(increase_qty);
                    itemsList.get(getAdapterPosition()).setQty(getQuantity);
                    notifyItemChanged(getAdapterPosition());
                }

                @Override
                public void decrease() {
                    getQuantity = textView.getText().toString();
                    if (getQuantity.trim().equals("1")) {

                    } else {
                        int decrease_qty = Integer.parseInt(getQuantity) - 1;
                        getQuantity = String.valueOf(decrease_qty);
                        itemsList.get(getAdapterPosition()).setQty(getQuantity);
                        notifyItemChanged(getAdapterPosition());
                    }
                }

                @Override
                public void click() {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "grocery");
                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.commit();
                }

//                @Override
//                public void spinnervalue() {
//                    Log.e("mjnk","nj");
//                    int size = itemsList.get(getAdapterPosition()).getProduct().getVariants().getEdges().size();
//                    Log.e("size", String.valueOf(size));
//                    List<String> categories = new ArrayList<String>();
//
//                    for (int i = 0; i < size; i++) {
//                        String a = itemsList.get(getAdapterPosition()).getProduct().getVariants().getEdges().get(i).getNode().getWeight().toString();
//                        categories.add(a);
//                    }
//                    Log.e("cate", String.valueOf(categories.size()));
//
//                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, categories);
//                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setAdapter(dataAdapter);
//////                    notifyItemChanged(getAdapterPosition());
//
////                }
            });
        }


        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            pos=i;
            String item = adapterView.getItemAtPosition(i).toString();
            Log.e("itemselected",item);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

