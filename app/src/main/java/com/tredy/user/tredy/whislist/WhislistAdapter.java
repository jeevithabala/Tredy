package com.tredy.user.tredy.whislist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tredy.user.tredy.category.productDetail.ProductView;
import com.tredy.user.tredy.callback.CartController;
import com.tredy.user.tredy.callback.CommanCartControler;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.whislist.whislistDB.DBWhislist;
import com.tredy.user.tredy.databinding.WhislistAdapterBinding;

import java.util.List;

public class WhislistAdapter extends RecyclerView.Adapter<WhislistAdapter.ViewHolder> {

    private  List<AddWhislistModel> items;
    private  Context mContext;
    private LayoutInflater layoutInflater;
    private  CartController cartController;
    private  CommanCartControler commanCartControler;
    private FragmentManager fragmentManager;
    private TextView textView;
    private GetTotalCost getTotalCost;


     WhislistAdapter(List<AddWhislistModel> items, Context mContext, GetTotalCost getTotalCost, FragmentManager fragmentManager, TextView textView) {
        this.items = items;
        this.mContext = mContext;
        this.getTotalCost = getTotalCost;
        this.fragmentManager = fragmentManager;
        this.textView = textView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }


        WhislistAdapterBinding whislistAdapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.whislist_adapter, parent, false);
        return new ViewHolder(whislistAdapterBinding);
//        return null;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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
        TextView remove;
        LinearLayout  addcart;

        private final WhislistAdapterBinding binding;

        public ViewHolder(final WhislistAdapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;
            remove = itemView.findViewById(R.id.remove);
            addcart = itemView.findViewById(R.id.addcart);


            binding.setItemclick(() -> {


                Bundle bundle = new Bundle();
                bundle.putString("category", "wishlist");
                bundle.putSerializable("category_id", items.get(getAdapterPosition()));
                Fragment fragment = new ProductView();
                fragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "productview");
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                if (fragmentManager.findFragmentByTag("productview") == null) {
                    ft.addToBackStack("productview");
                    ft.commit();
                } else {
                    ft.commit();
                }

            });


            remove.setOnClickListener(view -> {
//                    Log.e("iddd", items.get(getAdapterPosition()).getProduct_varient_id());
//                    remove1.removeItem(items.get(getAdapterPosition()).getProduct_varient_id());
                DBWhislist db = new DBWhislist(mContext);
                if (db.deleteRow(items.get(getAdapterPosition()).getProduct_varient_id().trim())) {
                    items.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    notifyDataSetChanged();
                    getTotalCost.totalcostinjterface();
                }
            });

            addcart.setOnClickListener(view -> {
                cartController = new CartController(mContext);
                commanCartControler =  cartController;
                commanCartControler.AddToCartGrocery(items.get(getAdapterPosition()).getProduct_id().trim(), 0, 1);
                Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();
            });

        }
    }


    public interface GetTotalCost {
        void totalcostinjterface();
    }

}

