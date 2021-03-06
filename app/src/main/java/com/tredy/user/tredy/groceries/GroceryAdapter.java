package com.tredy.user.tredy.groceries;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import com.tredy.user.tredy.category.productDetail.ProductView;
import com.tredy.user.tredy.callback.CartController;
import com.tredy.user.tredy.callback.CommanCartControler;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.databinding.GroceryadapterBinding;
import com.tredy.user.tredy.util.Config;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<GroceryModel> itemsList;
    private CartController cartController;
    private CommanCartControler commanCartControler;
    private int pos = 0;
    private CartDailog cartDailog;
    private FragmentManager fragmentManager;
    private LayoutInflater layoutInflater;
    private int pos1 = 0;

    GroceryAdapter(Context mContext, ArrayList<GroceryModel> itemsList, FragmentManager fragmentManager, CartDailog cartDailog) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.fragmentManager = fragmentManager;
        this.cartDailog = cartDailog;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        GroceryadapterBinding groceryadapterBinding = DataBindingUtil.inflate(layoutInflater, R.layout.groceryadapter, parent, false);
        return new ViewHolder(groceryadapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setGrocery(itemsList.get(position));


    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


//    public void display() {
//
//        Dialog dialog;
//        dialog = new Dialog(mContext);
//
//        dialog.setContentView(R.layout.dialog_layout);
//
//        dialog.setTitle("Cart");
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.BOTTOM);
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
//
//        TextView itemCount = dialog.findViewById(R.id.txt_items);
//        TextView subTotal = dialog.findViewById(R.id.txt_subtotal);
//
//        TextView btn_continue = dialog.findViewById(R.id.btn_continue_shopping);
//        TextView btn_checkout = dialog.findViewById(R.id.btn_checkout);
//
//        DBHelper db = new DBHelper(mContext);
//        List<AddToCart_Model> addToCart_modelArrayList = db.getCartList();
//        Log.e("array", "" + db.getCartList());
//        int cart_size = addToCart_modelArrayList.size();
//        cart_size++;
//        itemCount.setText("Items : " + cart_size);
//
//        CartController cartController;
//        CommanCartControler commanCartControler;
//        cartController = new CartController(mContext);
//        commanCartControler = cartController;
//        int cost = commanCartControler.getTotalPrice();
//        int current_cost = itemsList.get(pos1).product.getVariants().getEdges().get(pos).getNode().getPrice().intValue();
//        cost = cost + current_cost;
//        subTotal.setText("SubTotal : Rs. " + cost);
//
//
//
//        btn_continue.setOnClickListener(view -> dialog.cancel());
//
//        btn_checkout.setOnClickListener(view -> {
//            dialog.cancel();
//            Fragment fragment = new ShippingAddress();
//            FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, fragment, "fragment");
//            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//            ft.commit();
//        });
//
//
//        dialog.show();
//    }


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


            addgrocery.setOnClickListener(view -> {
                if (Config.isNetworkAvailable(mContext)) {

                    pos1 = getAdapterPosition();
//                    display();
                    cartController = new CartController(mContext);
                    commanCartControler = cartController;
                    commanCartControler.AddToCartGrocery(String.valueOf(itemsList.get(getAdapterPosition()).getProduct().getId()), pos, Integer.parseInt(itemsList.get(getAdapterPosition()).getQty()));
                    add_to_cart.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> cartDailog.cart(pos1, pos, Integer.parseInt(itemsList.get(getAdapterPosition()).getQty())), 1500);


                } else {
                    Toast.makeText(mContext, "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
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
                    if (Config.isNetworkAvailable(mContext)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("category", "grocery");
                        bundle.putSerializable("category_id", itemsList.get(getAdapterPosition()));
                        Fragment fragment = new ProductView();
                        fragment.setArguments(bundle);
                        FragmentTransaction ft = fragmentManager.beginTransaction().add(R.id.home_container, fragment, "productview");
                        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                        if (fragmentManager.findFragmentByTag("productview") == null) {
                            ft.addToBackStack("productview");
                            ft.commit();
                        } else {
                            ft.commit();
                        }
                    } else {
                        Toast.makeText(mContext, "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
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
//            String item = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public interface CartDailog {
        void cart(int adapter_pos, int varient_pos, int qty);
    }


}
