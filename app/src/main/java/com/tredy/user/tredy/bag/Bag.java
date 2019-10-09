package com.tredy.user.tredy.bag;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tredy.user.tredy.Tawk;
import com.tredy.user.tredy.bag.cartdatabase.AddToCart_Adapter;
import com.tredy.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.tredy.user.tredy.bag.cartdatabase.DBHelper;
import com.tredy.user.tredy.callback.CartController;
import com.tredy.user.tredy.callback.CommanCartControler;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.SharedPreference;
import com.tredy.user.tredy.databinding.BagBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Bag extends Fragment implements AddToCart_Adapter.GetTotalCost {
    RecyclerView bag_recyclerview;
    List<AddToCart_Model> cartList;
    DBHelper db;
    AddToCart_Adapter adapter;
    TextView items, totalcost;
    BagBinding binding;
    LinearLayout checkoutbtn, check;
    View view;
    TextView nobag;
    ArrayList<String> productlist = new ArrayList<>();
    String state = "", totalcosts = "";
    CartController cartController;
    CommanCartControler commanCartControler;
FloatingActionButton chat_button;
String remove_cod=" ";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bag, container, false);

        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Cart");

        view = binding.getRoot();
        checkoutbtn = view.findViewById(R.id.checkoutbtn);
        check = view.findViewById(R.id.check);
        items = view.findViewById(R.id.items);
        totalcost = view.findViewById(R.id.total);
        nobag = view.findViewById(R.id.nobag);
        chat_button=view.findViewById(R.id.chat_button);



        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        cartController = new CartController(getActivity());
        commanCartControler = cartController;
        db = new DBHelper(getActivity());
        db.deletDuplicates();
        cartList = db.getCartList();
        Collections.reverse(cartList); // ADD THIS LINE TO REVERSE ORDER!
        getbagcount();

        if (getArguments() != null) {
            productlist = getArguments().getStringArrayList("nonshipping");
            state = getArguments().getString("state");
            SharedPreference.saveData("state", state, Objects.requireNonNull(getActivity()));
        }

        Log.d("statev", " " + state);
        if (state.trim().length() > 0) {
            cartList.clear();
            cartList = db.getCartList();
            Collections.reverse(cartList);
        } else {
            for (int i = 0; i < cartList.size(); i++) {
                commanCartControler.UpdateShipping(cartList.get(i).getProduct_varient_id().trim(), "true");
//                cartList.clear();
//                cartList = db.getCartList();
//                Log.d("cccc2", cartList.get(i).getShip());
            }
            cartList.clear();
            cartList = db.getCartList();

            Collections.reverse(cartList);
        }

        bag_recyclerview = view.findViewById(R.id.bag_recyclerview);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        bag_recyclerview.setLayoutManager(layoutManager1);
        bag_recyclerview.setItemAnimator(new DefaultItemAnimator());


        adapter = new AddToCart_Adapter(cartList, getActivity(), this, binding.total, items, getFragmentManager());
        bag_recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


//adapter.notifyDataSetChanged();


//        if (cartList.size() != 0) {
//            items.setText(cartList.size() + " " + "Items");
//
//        }


//            Toast.makeText(getApplicationContext(), cartList.get(0).getProduct_name(), Toast.LENGTH_SHORT).show();
        visibilityCheck();
        total();
//        for (int i = 0; i < cartList.size(); i++) {
//            String tag=cartList.get(i).getTag();
//            Log.e("tag",tag);
//            if (tag != null && tag.trim().toLowerCase().contains("remove_cod")) {
//                remove_cod = "remove_cod";
//            }
//
//        }


        checkoutbtn.setOnClickListener(view -> {
            String[] str_totalCost = totalcost.getText().toString().split(" ");
            totalcosts = str_totalCost[1];
            Bundle bundle = new Bundle();
            bundle.putString("collection", "allcollection");
            bundle.putString("totalcost", totalcosts);
//            bundle.putString("remove_cod", remove_cod);
            Fragment fragment = new ShippingAddress();
            fragment.setArguments(bundle);
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "fragment");
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            if (getFragmentManager().findFragmentByTag("fragment") == null) {
                ft.addToBackStack("fragment");
                ft.commit();
            } else {
                ft.commit();
            }

        });
    }

    @SuppressLint("SetTextI18n")
    public void total() {
//        for (int i = 0; i < cartList.size(); i++) {
//            int qty = cartList.get(i).getQty();
//            Double cost = cartList.get(i).getProduct_price();
//            Log.e("qty", "" + String.valueOf(qty));
//            Log.e("cost", "" + String.valueOf(cost));
//            Log.e("icost", "" + String.valueOf(totalcost1));
//            totalcost1 = totalcost1 + (qty * (cost.intValue()));
//            Log.e("cost", "" + String.valueOf(totalcost1));
//
//        }
//        if(totalcost1!=0)

//        totalcost.setText(String.valueOf(getResources().getString(R.string.Rs)+" "+totalcost1));
        CartController cartController;
        CommanCartControler commanCartControler;
        cartController = new CartController(getActivity());
        commanCartControler = cartController;
        SharedPreference.saveData("total", Integer.toString(commanCartControler.getTotalPrice()), Objects.requireNonNull(getActivity()));
        totalcost.setText(getResources().getString(R.string.Rs) + " " + Integer.toString(commanCartControler.getTotalPrice()));

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tawk tawk = new Tawk();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction1 = null;
                if (getFragmentManager() != null) {
                    transaction1 = getFragmentManager().beginTransaction();
                    transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction1.add(R.id.home_container, tawk, "tawk");
                    if (fragmentManager.findFragmentByTag("tawk") == null) {
                        transaction1.addToBackStack("tawk");
                        transaction1.commit();
                    } else {
                        transaction1.commit();
                    }
                }

            }
        });

    }


    @Override
    public void totalcostinjterface() {
        visibilityCheck();
    }

    public void visibilityCheck() {
//        cartList.clear();
//        cartList = db.getCartList();
        if (cartList.size() == 0) {
            nobag.setVisibility(View.VISIBLE);
            check.setVisibility(View.GONE);
        } else {
            nobag.setVisibility(View.GONE);
            check.setVisibility(View.VISIBLE);
            total();
        }
    }

    public void getbagcount(){
//        cartList.clear();
//        cartList = db.getCartList();
        Navigation.cart_count = 0;
        for (int i = 0; i < cartList.size(); i++) {
            cartList.get(i).getQty();
            Navigation.cart_count = Navigation.cart_count + cartList.get(i).getQty();
        }
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }

    }

}
