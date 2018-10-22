package com.example.user.trendy.Bag.Db;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.user.trendy.Bag.Bag;
import com.example.user.trendy.Category.CategoryProduct;
import com.example.user.trendy.Category.ProductDetail.ProductView;
import com.example.user.trendy.Category.SubCategory;
import com.example.user.trendy.Interface.AddRemoveCartItem;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Interface.CommanCartControler;
import com.example.user.trendy.Interface.FragmentRecyclerViewClick;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.SharedPreference;
import com.example.user.trendy.databinding.AddtocartAdapterBinding;

import java.util.ArrayList;
import java.util.List;

public class AddToCart_Adapter extends RecyclerView.Adapter<AddToCart_Adapter.ViewHolder> {

    List<AddToCart_Model> items;
    Context mContext;
    private LayoutInflater layoutInflater;
    CartController cartController;
    CommanCartControler commanCartControler;
    GetTotalCost getTotalCost;
    TextView textView, textView1;
    String state;
    private FragmentManager fragmentManager;

    public AddToCart_Adapter(List<AddToCart_Model> items, Context mContext, GetTotalCost getTotalCost) {
        this.items = items;
        this.mContext = mContext;
        this.getTotalCost = getTotalCost;
    }

    public AddToCart_Adapter(List<AddToCart_Model> items, Context mContext, GetTotalCost getTotalCost, TextView textView, TextView textView1, FragmentManager fragmentManager) {
        this.items = items;
        this.mContext = mContext;
        this.getTotalCost = getTotalCost;
        this.textView = textView;
        this.textView1 = textView1;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }


        AddtocartAdapterBinding addtocartAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.addtocart_adapter, parent, false);
        return new ViewHolder(addtocartAdapterBinding);
//        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setCartitem(items.get(position));
        Log.d("Product varient id ", items.get(position).getProduct_varient_id());
        if (items.get(position).getShip().equals("false")) {
            holder.shipping_visibility.setVisibility(View.VISIBLE);
        } else {
            holder.shipping_visibility.setVisibility(View.GONE);
        }

        textView1.setText(items.size() + " items");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

//    @Override
//    public void shippingvisibility(String state, ArrayList<String> productlist) {
//        state=state;
//        productlist=productlist;
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView remove, shipping_visibility, product_name;
        LinearLayout decrease, increase;
        DBHelper db = new DBHelper(mContext);

        private final AddtocartAdapterBinding binding;

        public ViewHolder(final AddtocartAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;
            remove = itemView.findViewById(R.id.remove);
            decrease = itemView.findViewById(R.id.decrease);
            increase = itemView.findViewById(R.id.increase);
            shipping_visibility = itemView.findViewById(R.id.shipping_visibility);
            String state = SharedPreference.getData("state", mContext);
            shipping_visibility.setText("Oops! The product cannot be shipped to  " + state);

            product_name = itemView.findViewById(R.id.product_name);

            binding.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "bag");
                    bundle.putSerializable("category_id", items.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.addToBackStack("Bag");
                    ft.commit();
                }
            });

            product_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "bag");
                    bundle.putSerializable("category_id", items.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.addToBackStack("Bag");
                    ft.commit();
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("iddd", items.get(getAdapterPosition()).getProduct_varient_id());
//                    remove1.removeItem(items.get(getAdapterPosition()).getProduct_varient_id());
                    DBHelper db = new DBHelper(mContext);
                    if (db.deleteRow(items.get(getAdapterPosition()).getProduct_varient_id().trim())) {
                        ((AddRemoveCartItem) mContext).RemoveCartItem();
                        items.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        notifyItemRemoved(getAdapterPosition());
                        getTotalCost.totalcostinjterface();

                    }
                }
            });

            increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler) cartController;
                    commanCartControler.AddQuantity(items.get(getAdapterPosition()).getProduct_varient_id().trim());

                    items.get(getAdapterPosition()).setQty(Integer.parseInt(db.getQuantity(items.get(getAdapterPosition()).getProduct_varient_id())));
                    textView.setText("Rs. " + Integer.toString(commanCartControler.getTotalPrice()));
                    SharedPreference.saveData("total", String.valueOf(commanCartControler.getTotalPrice()), mContext);
                    Log.e("costcheckadapter", "" + String.valueOf(commanCartControler.getTotalPrice()));
//                    getTotalCost.totalcostinjterface(commanCartControler.getTotalPrice());

                }
            });

            decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler) cartController;
                    commanCartControler.RemoveQuantity(items.get(getAdapterPosition()).getProduct_varient_id().trim());
                    items.get(getAdapterPosition()).setQty(Integer.parseInt(db.getQuantity(items.get(getAdapterPosition()).getProduct_varient_id())));
                    textView.setText("Rs. " + Integer.toString(commanCartControler.getTotalPrice()));
                    SharedPreference.saveData("total", String.valueOf(commanCartControler.getTotalPrice()), mContext);
                }
            });
        }
    }

    public interface GetTotalCost {
        void totalcostinjterface();
    }


}
