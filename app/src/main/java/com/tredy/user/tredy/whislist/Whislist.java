package com.tredy.user.tredy.whislist;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.Tawk;
import com.tredy.user.tredy.whislist.whislistDB.DBWhislist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Whislist extends Fragment implements WhislistAdapter.GetTotalCost {
    RecyclerView whislist;
    DBWhislist db;
    private List<AddWhislistModel> cartList = new ArrayList<>();
    WhislistAdapter adapter;
    TextView items;
    private TextView nobag;
    private FloatingActionButton chat_button;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.whislist, container, false);

        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Wishlist");

        cartList.clear();
        items = view.findViewById(R.id.items);
        nobag = view.findViewById(R.id.nobag);
        chat_button=view.findViewById(R.id.chat_button);
        items.setVisibility(View.VISIBLE);

        db = new DBWhislist(getActivity());
        db.deletDuplicates();
        cartList.clear();
        cartList = db.getCartList();
        Collections.reverse(cartList);

        whislist = view.findViewById(R.id.whislist_recycler);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        whislist.setLayoutManager(layoutManager1);
        whislist.setItemAnimator(new DefaultItemAnimator());


        adapter = new WhislistAdapter(cartList, getActivity(), this, getFragmentManager(), items);
        whislist.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        visibilityCheck();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    private void visibilityCheck() {
        if (cartList.size() == 0) {
//            items.setText(cartList.size() + " " + "Items");
            nobag.setVisibility(View.VISIBLE);
            items.setVisibility(View.GONE);
        } else {
            items.setVisibility(View.VISIBLE);
            nobag.setVisibility(View.GONE);
        }
    }
}
