package com.marmeto.user.tredy.foryou.groceryhome;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.category.productDetail.ProductView;
import com.marmeto.user.tredy.groceries.GroceryInterface;
import com.marmeto.user.tredy.callback.CartController;
import com.marmeto.user.tredy.callback.CommanCartControler;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.databinding.Groceryadapter1Binding;

import java.util.ArrayList;
import java.util.List;

public class GroceryHomeAdapter extends RecyclerView.Adapter<GroceryHomeAdapter.ViewHolder> {

    Context mContext;
    ArrayList<GroceryHomeModel> itemsList;
    CartController cartController;
    CommanCartControler commanCartControler;
    int pos = 0;
    List<AddToCart_Model> addToCart_modelArrayList = new ArrayList<>();
    CartDailog cartDailog;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private int pos1 = 0;


    public GroceryHomeAdapter(Context mContext, ArrayList<GroceryHomeModel> itemsList, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        Groceryadapter1Binding groceryadapter1Binding = DataBindingUtil.inflate(layoutInflater, R.layout.groceryadapter1, parent, false);
        return new ViewHolder(groceryadapter1Binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setGrocery1(itemsList.get(position));


    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private final Groceryadapter1Binding binding;
        TextView textView, addgrocery, add_to_cart;
        Spinner spinner;

        public ViewHolder(final Groceryadapter1Binding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;
            textView = itemView.findViewById(R.id.qty);
            spinner = itemView.findViewById(R.id.options);
            addgrocery = itemView.findViewById(R.id.addgrocery);

            spinner.setOnItemSelectedListener(this);

            addgrocery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos1 = getAdapterPosition();
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler) cartController;
                    commanCartControler.AddToCartGrocery(String.valueOf(itemsList.get(getAdapterPosition()).getProduct().getId()), pos, Integer.parseInt(itemsList.get(getAdapterPosition()).getQty()));
                    Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();
                }
            });
//            spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) mContext);

            binding.setCounter(new GroceryInterface() {
                @Override
                public void increase() {
                    String getQuantity = textView.getText().toString();
                    int increase_qty = Integer.parseInt(getQuantity) + 1;
                    getQuantity = String.valueOf(increase_qty);
                    itemsList.get(getAdapterPosition()).setQty(getQuantity);
                    notifyItemChanged(getAdapterPosition());
                }

                @Override
                public void decrease() {
                    String getQuantity = textView.getText().toString();
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
                    bundle.putString("category", "groceryhome");
                    bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                    Fragment fragment = new ProductView();
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
            });
        }


        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            pos = i;
            String item = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public interface CartDailog {
        public void cart(int adapter_pos, int varient_pos);
    }


}