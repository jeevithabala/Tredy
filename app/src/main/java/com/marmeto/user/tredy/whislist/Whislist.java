package com.marmeto.user.tredy.whislist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.whislist.whislistDB.DBWhislist;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Whislist extends Fragment implements WhislistAdapter.GetTotalCost {
    RecyclerView whislist;
    DBWhislist db;
    private List<AddWhislistModel> cartList = new ArrayList<>();
    private GraphClient graphClient;
    WhislistAdapter adapter;
    TextView items;
    private TextView nobag;
    Toolbar toolbar;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.whislist, container, false);

        ((Navigation) getActivity()).getSupportActionBar().setTitle("Wishlist");

        cartList.clear();
        items = view.findViewById(R.id.items);
        nobag = view.findViewById(R.id.nobag);
        items.setVisibility(View.VISIBLE);

        db = new DBWhislist(getActivity());
        db.deletDuplicates();
        cartList.clear();
        cartList = db.getCartList();
        Collections.reverse(cartList);
        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        Log.e("whislist", String.valueOf(cartList.size()));

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