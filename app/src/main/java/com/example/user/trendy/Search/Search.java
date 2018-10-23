package com.example.user.trendy.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.Category.ProductModel;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Search extends Fragment implements SearchView.OnQueryTextListener {
    RecyclerView search_recycler;
    private SearchAdapter adapter;
    private ArrayList<SearchModel> searchlist = new ArrayList<>();
    SearchView search;
    private RequestQueue mRequestQueue;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search, container, false);

        search_recycler = view.findViewById(R.id.search_recycler);
        search = view.findViewById(R.id.search);
        search.onActionViewExpanded();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        search_recycler.setLayoutManager(layoutManager1);
        search_recycler.setItemAnimator(new DefaultItemAnimator());


        adapter = new SearchAdapter(getActivity(), searchlist);
        search_recycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        search.setOnQueryTextListener(this);
        return view;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        collectionList1(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        adapter.filter(newText);
        return true;
    }


    private void collectionList1(String s) {

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.search + s,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

//                            Iterator keys = obj.keys();
//                            Log.e("Keys", "" + String.valueOf(keys));
//
//                            while (keys.hasNext()) {
//                                String dynamicKey = (String) keys.next();
//                                Log.d("Dynamic Key", "" + dynamicKey);
//
//                                JSONArray array = null;

                                JSONArray array = obj.getJSONArray("products");
//                                    array = obj.getJSONArray(dynamicKey);


                                for (int i = 0; i < array.length(); i++) {
                                    Log.e("inti", String.valueOf(i));
                                    JSONObject object1 = array.getJSONObject(i);
                                    JSONArray array1 = object1.getJSONArray("options");
                                    for (int j = 0; j < array1.length(); j++) {
                                        Log.e("inti", String.valueOf(j));
                                        JSONObject object = array1.getJSONObject(j);
                                    }

//                                        collectionid = object1.getString("id");
//                                        collectionname = object1.getString("title");
//                                        if (collectionname.trim().toLowerCase().equals("home page")) {
//                                            collectionname = "Trending";
//                                        }
//
//                                        JSONArray array1 = object1.getJSONArray("products");
//                                        for (int j = 0; j < array1.length(); j++) {
//                                            JSONObject objec = array1.getJSONObject(j);
//
//                                            title = objec.getString("title");
//                                            if (title.trim().toLowerCase().equals("home page")) {
//                                                title = "Treding";
//                                            }
//                                            JSONArray varientsarray = objec.getJSONArray("variants");
//                                            for (int k = 0; k < varientsarray.length(); k++) {
//                                                JSONObject objec1 = varientsarray.getJSONObject(k);
//
//                                                id = objec1.getString("product_id");
//                                                price = objec1.getString("price");
//
//                                            }
//                                            JSONArray array2 = objec.getJSONArray("images");
//                                            for (int l = 0; l < array2.length(); l++) {
//                                                JSONObject objec1 = array2.getJSONObject(l);
//                                                image = objec1.getString("src");
//                                            }
//                                        }
                                }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
                    }
                }) {

        };
        stringRequest.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);

    }


}
