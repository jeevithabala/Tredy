package com.example.user.trendy.Groceries;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.trendy.Bag.Db.AddToCart_Model;
import com.example.user.trendy.Bag.Db.DBHelper;
import com.example.user.trendy.Bag.ShippingAddress;
import com.example.user.trendy.Category.ProductDetail.ProductView;
import com.example.user.trendy.Category.SubCategoryModel;
import com.example.user.trendy.ForYou.ForYou;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Interface.CommanCartControler;
import com.example.user.trendy.Interface.FragmentRecyclerViewClick;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.SharedPreference;
import com.example.user.trendy.Whislist.Whislist;
import com.example.user.trendy.databinding.GroceryadapterBinding;

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    Context mContext;
    ArrayList<GroceryModel> itemsList;
    CartController cartController;
    CommanCartControler commanCartControler;
    int pos = 0;
    DBHelper db;
    List<AddToCart_Model> addToCart_modelArrayList = new ArrayList<>();
    CartDailog cartDailog;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private int pos1 = 0;

    public GroceryAdapter(Context mContext, ArrayList<GroceryModel> itemsList, FragmentManager fragmentManager, CartDailog cartDailog) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
        this.cartDailog = cartDailog;
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

    public void display() {

        Dialog dialog;
        dialog = new Dialog(mContext);

        dialog.setContentView(R.layout.dialog_layout);

        dialog.setTitle("Cart");

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView itemCount = dialog.findViewById(R.id.txt_items);
        TextView subTotal = dialog.findViewById(R.id.txt_subtotal);

        TextView btn_continue = dialog.findViewById(R.id.btn_continue_shopping);
        TextView btn_checkout = dialog.findViewById(R.id.btn_checkout);

        db = new DBHelper(mContext);
        addToCart_modelArrayList = db.getCartList();
        Log.e("array", "" + db.getCartList());
        int cart_size = addToCart_modelArrayList.size();
        cart_size++;
        itemCount.setText("Items : " + cart_size);

        CartController cartController;
        CommanCartControler commanCartControler;
        cartController = new CartController(mContext);
        commanCartControler = (CommanCartControler) cartController;
        int cost = commanCartControler.getTotalPrice();
        int current_cost = itemsList.get(pos1).product.getVariants().getEdges().get(pos).getNode().getPrice().intValue();
        cost = cost + current_cost;
        subTotal.setText("SubTotal : Rs. " + cost);

        cost = commanCartControler.getTotalPrice();


        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                Fragment fragment = new ShippingAddress();
                FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.commit();
            }
        });


        dialog.show();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private final GroceryadapterBinding binding;
        TextView textView, addgrocery, add_to_cart;
        Spinner spinner;


        public ViewHolder(final GroceryadapterBinding itembinding) {
            super(itembinding.getRoot());
            this.binding = itembinding;
            textView = itemView.findViewById(R.id.qty);
            spinner = itemView.findViewById(R.id.options);
            addgrocery = itemView.findViewById(R.id.addgrocery);
            add_to_cart = itemView.findViewById(R.id.add_to_cart);
//
            spinner.setOnItemSelectedListener(this);


            addgrocery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos1 = getAdapterPosition();
//                    display();
                    cartController = new CartController(mContext);
                    commanCartControler = (CommanCartControler) cartController;
                    commanCartControler.AddToCartGrocery(String.valueOf(itemsList.get(getAdapterPosition()).getProduct().getId()), pos, Integer.parseInt(itemsList.get(getAdapterPosition()).getQty()));
                    add_to_cart.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cartDailog.cart(pos1, pos, Integer.parseInt(itemsList.get(getAdapterPosition()).getQty()));
                        }
                    }, 1000);



                }
            });

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
                    bundle.putString("category", "grocery");
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
            pos = i;
            String item = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public interface CartDailog {
        public void cart(int adapter_pos, int varient_pos, int qty);
    }


}
