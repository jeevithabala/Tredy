package com.example.user.trendy.whislist;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.trendy.category.productDetail.ProductView;
import com.example.user.trendy.callback.CartController;
import com.example.user.trendy.callback.CommanCartControler;
import com.example.user.trendy.callback.FragmentRecyclerViewClick;
import com.example.user.trendy.R;
import com.example.user.trendy.whislist.whislistDB.DBWhislist;
import com.example.user.trendy.databinding.WhislistAdapterBinding;

import java.util.List;

public class WhislistAdapter extends RecyclerView.Adapter<WhislistAdapter.ViewHolder> {

    List<AddWhislistModel> items;
    Context mContext;
    private LayoutInflater layoutInflater;
    CartController cartController;
    CommanCartControler commanCartControler;
    FragmentManager fragmentManager;
    TextView textView;
    GetTotalCost getTotalCost;

    public WhislistAdapter(List<AddWhislistModel> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    public WhislistAdapter(List<AddWhislistModel> items, Context mContext, GetTotalCost getTotalCost, FragmentManager fragmentManager, TextView textView) {
        this.items = items;
        this.mContext = mContext;
        this.getTotalCost = getTotalCost;
        this.fragmentManager = fragmentManager;
        this.textView = textView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }


        WhislistAdapterBinding whislistAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.whislist_adapter, parent, false);
        return new ViewHolder(whislistAdapterBinding);
//        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setWhislistitem(items.get(position));
        Log.d("Product varient id ", items.get(position).getProduct_varient_id());
        textView.setText(items.size() + " items");
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
        TextView remove, shipping_visibility;
        LinearLayout decrease, increase, addcart;
        DBWhislist db = new DBWhislist(mContext);

        private final WhislistAdapterBinding binding;

        public ViewHolder(final WhislistAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;
            remove = itemView.findViewById(R.id.remove);
            addcart = itemView.findViewById(R.id.addcart);


            binding.setItemclick(new FragmentRecyclerViewClick() {
                @Override
                public void onClickPostion() {


                    Bundle bundle = new Bundle();
                    bundle.putString("category", "wishlist");
                    bundle.putSerializable("category_id", items.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "whislist");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    if (fragmentManager.findFragmentByTag("whislist") == null) {
                        ft.addToBackStack("whislist");
                        ft.commit();
                    } else {
                        ft.commit();
                    }

                }
            });


            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("iddd", items.get(getAdapterPosition()).getProduct_varient_id());
//                    remove1.removeItem(items.get(getAdapterPosition()).getProduct_varient_id());
                    DBWhislist db = new DBWhislist(mContext);
                    if (db.deleteRow(items.get(getAdapterPosition()).getProduct_varient_id().trim())) {
                        items.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());

                        notifyDataSetChanged();
                        getTotalCost.totalcostinjterface();
                    }
                }
            });

            addcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler) cartController;
                    commanCartControler.AddToCartGrocery(items.get(getAdapterPosition()).getProduct_id().trim(), 0, 1);
                    Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    public interface GetTotalCost {
        void totalcostinjterface();
    }

}

