package com.marmeto.user.tredy.filter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.filter.filtertype.FilterAdapter;
import com.marmeto.user.tredy.filter.filtertype.FilterHetroAdapter;
import com.marmeto.user.tredy.filter.filtertype.FilterModel;
import com.marmeto.user.tredy.filter.filtertype.FilterTilteAndTag;
import com.marmeto.user.tredy.filter.price.PriceAdapter;
import com.marmeto.user.tredy.filter.price.PriceModel;
import com.marmeto.user.tredy.filter.sortby.SortByAdapter;
import com.marmeto.user.tredy.filter.sortby.SortByModel;
import com.marmeto.user.tredy.callback.OnFilterDataCallBack;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class Filter_Fragment extends Fragment {
    RecyclerView sortby_recycler, filter_recycler, price_recycler;
    FilterAdapter filterAdapter;
    PriceAdapter priceAdapter;
    SortByAdapter sortByAdapter;
    ArrayList<PriceModel> priceModelArrayList = new ArrayList<>();
    ArrayList<SortByModel> sortByModelArrayList = new ArrayList<>();
    ArrayList<String> producttag = new ArrayList<>();
    private String collectionid;
    Button btn_filter, btn_clear;
    private ArrayList<String> selectedFilterList;
    private ArrayList<String> selectedsortList;
    private ArrayList<String> selectedpriceList;
    int firstsplit, secondsplit, thirdsplit, fourthsplit;
    ArrayList<String> pricelist = new ArrayList<>();
    ArrayList<String> sortlist = new ArrayList<>();
    ArrayList<FilterTilteAndTag> filterTilteAndTags = new ArrayList<>();
//    TextView type;
    String min_price, max_price;
    String dynamicKey = "", sortlistkey = "";
    FilterHetroAdapter filterHetroAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_fragment, container, false);

        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Filter");


        assert getArguments() != null;
        collectionid = getArguments().getString("collectionid");
        Log.e("collection", collectionid);
        getTaglist();


        sortby_recycler = view.findViewById(R.id.sortby_recycler);
        filter_recycler = view.findViewById(R.id.filter_recycler);
        price_recycler = view.findViewById(R.id.price_recycler);
        btn_filter = view.findViewById(R.id.btn_filter1);
        btn_clear = view.findViewById(R.id.btn_clearall);
//        type = view.findViewById(R.id.type);


        filterHetroAdapter = new FilterHetroAdapter(getActivity(), filterTilteAndTags, getFragmentManager());
        filter_recycler.setAdapter(filterHetroAdapter);
        filter_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        priceAdapter = new PriceAdapter(getActivity(), priceModelArrayList, getFragmentManager());
        price_recycler.setAdapter(priceAdapter);
        price_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        sortByAdapter = new SortByAdapter(getActivity(), sortByModelArrayList, getFragmentManager());
        sortby_recycler.setAdapter(sortByAdapter);
        sortby_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        selectedFilterList = new ArrayList<>();

        if (selectedFilterList.size() > 0) {
            Toast.makeText(getActivity(), String.valueOf(selectedFilterList.size()), Toast.LENGTH_SHORT).show();
        }
        sortlist.clear();
        sortByModelArrayList.clear();
        sortlist.add("Price : Low to High");
        sortlist.add("Price : High to Low");


        for (String tag : sortlist) {
            SortByModel sortByModel = new SortByModel(tag, false);
            sortByModelArrayList.add(sortByModel);
        }
        sortByAdapter.notifyDataSetChanged();

        btn_filter.setOnClickListener(view12 -> {
            selectedFilterList = (filterHetroAdapter).getSelectedContactList();
            selectedsortList = (sortByAdapter).getSelectedSortList();
            selectedpriceList = (priceAdapter).getSelectedPriceList();

            Log.e("adaa", "" + selectedFilterList.toString() + selectedpriceList.toString() + selectedsortList.toString());
            if (selectedpriceList.size() != 0) {
                String price = selectedpriceList.get(0).trim();
                StringTokenizer tokens = new StringTokenizer(price, "-");
                min_price = tokens.nextToken().trim();// this will contain "Fruit"
                max_price = tokens.nextToken().trim();

            }

            if (selectedsortList.size() != 0) {

                if (selectedsortList.get(0).trim().equals("Price : High to Low")) {
                    sortlistkey = "sortBy=min_price&order=desc";
                } else {
                    sortlistkey = "sortBy=min_price&order=asc";
                }
            }


            OnFilterDataCallBack onFilterDataCallBack = (OnFilterDataCallBack) getActivity();
            onFilterDataCallBack.onFilterValueSelectCallBack(min_price, max_price, sortlistkey, collectionid, selectedFilterList, dynamicKey);

            getActivity().onBackPressed();

//                getFragmentManager().beginTransaction().remove(Filter_Fragment.this).commit();

//                postFilter();

        });
        btn_clear.setOnClickListener(view1 -> {
//                selectedFilterList.clear();
//                selectedsortList.clear();
//                selectedpriceList.clear();
//                ((FilterAdapter) filterAdapter).getSelectedContactList().clear();
//                ((SortByAdapter) sortByAdapter).getSelectedSortList().clear();
//                ((PriceAdapter) priceAdapter).getSelectedPriceList().clear();

            (sortByAdapter).sortclear();
            (filterHetroAdapter).typeclear();
            (priceAdapter).priceclear();
            filterHetroAdapter.notifyDataSetChanged();
        });

//        filterAdapter.notifyDataSetChanged();


        return view;
    }


    public void getTaglist() {
        producttag.clear();

//        filterModelArrayList.clear();

        RequestQueue mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.filter_tag1 + collectionid.trim(),
                response -> {

                    try {


                        JSONObject obj = new JSONObject(response);
                        JSONObject obj1 = obj.getJSONObject("filters");
                        Log.e("obj1", String.valueOf(obj1.length()));
                        Iterator keys = obj1.keys();


                        while (keys.hasNext()) {

                            dynamicKey = (String) keys.next();
                            producttag.clear();
//                            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                            assert layoutInflater != null;
//                            @SuppressLint("InflateParams") final View addView = layoutInflater.inflate(R.layout.row, null);
//                            TextView textOut = (TextView)addView.findViewById(R.id.type1);
//                            textOut.setText(dynamicKey);

//                            if(dynamicKey.equalsIgnoreCase("Kitchenwares")) {
//                                type.setText(dynamicKey);
                            JSONArray array = obj1.getJSONArray(dynamicKey);
                            ArrayList<FilterModel> filterModelArrayList = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                FilterModel model = new FilterModel(array.get(i).toString(), false);
                                filterModelArrayList.add(model);
                            }


                            FilterTilteAndTag filterTilte = new FilterTilteAndTag(dynamicKey, filterModelArrayList);
                            filterTilteAndTags.add(filterTilte);

                        }
//                        Collections.reverse(filterTilteAndTags);

                        filterHetroAdapter.notifyDataSetChanged();
                        min_price = obj.getString("min_price");
                        max_price = obj.getString("max_price");
                        pricelist.clear();

                        int splitvalue;
                        splitvalue = (Integer.parseInt(max_price) - Integer.parseInt(min_price)) / 4;
                        firstsplit = Math.round(Integer.parseInt(min_price) + splitvalue);
                        secondsplit = Math.round(firstsplit + splitvalue);
                        thirdsplit = Math.round(secondsplit + splitvalue);
                        fourthsplit = Math.round(Integer.parseInt(max_price));

                        String first = min_price + " - " + String.valueOf(firstsplit);
                        String second = String.valueOf(firstsplit + 1 + " - " + secondsplit);
                        String third = String.valueOf(secondsplit + 1 + " - " + thirdsplit);
                        String fourth = String.valueOf(thirdsplit + 1 + " - " + max_price);

                        pricelist.add(first);
                        pricelist.add(second);
                        pricelist.add(third);
                        pricelist.add(fourth);
                        priceModelArrayList.clear();
                        for (String tag : pricelist) {
                            PriceModel priceModel = new PriceModel(tag, false);
                            priceModelArrayList.add(priceModel);

                        }

//
//                        for (String tag : producttag) {
//
//                            /* Create new FilterDefaultMultipleListModel object for brand and set array value to brand model {@model}
//                             * Description:
//                             * -- Class: FilterDefaultMultipleListModel.java
//                             * -- Package:main.shop.javaxerp.com.shoppingapp.model
//                             * NOTE: #checked value @FilterDefaultMultipleListModel is false;
//                             * */
//                            FilterModel model = new FilterModel(tag, false);
////                                model.setName(tag);
//
//                            /*add brand model @model to ArrayList*/
//                            filterModelArrayList.add(model);
//
//                        }
//                        filterAdapter.notifyDataSetChanged();
                        priceAdapter.notifyDataSetChanged();

//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(() -> type.setText(dynamicKey));
//
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                String insurance_id = SharedPreference.getData("insurance_id", getActivity());
//
                params.put("collection_id", collectionid.trim());

                return params;
            }
        };
        stringRequest.setTag("filter");
        //  VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);


    }


}
