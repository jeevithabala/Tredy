package com.example.user.trendy.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.user.trendy.Category.ProductDetail.ProductView;
import com.example.user.trendy.Category.ProductModel;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Interface.CommanCartControler;
import com.example.user.trendy.Interface.ProductClickInterface;
import com.example.user.trendy.Navigation;
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

public class Search extends Fragment implements SearchView.OnQueryTextListener, ProductClickInterface {
    RecyclerView search_recycler;
    private SearchAdapter adapter;
    private ArrayList<SearchModel> searchlist = new ArrayList<>();
    SearchView search;
    private RequestQueue mRequestQueue;
    private String src;
    CartController cartController;
    CommanCartControler commanCartControler;
    TextView noproduct;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search, container, false);

        ((Navigation) getActivity()).getSupportActionBar().setTitle("Search");

        search_recycler = view.findViewById(R.id.search_recycler);
        search = view.findViewById(R.id.search);
        search.onActionViewExpanded();

        noproduct=view.findViewById(R.id.no_product);


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        search_recycler.setLayoutManager(layoutManager1);
        search_recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new SearchAdapter(getActivity(), searchlist, this);
        search_recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        search.setOnQueryTextListener(this);

        return view;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        collectionList1(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        searchlist.clear();
        collectionList1(s);
        return true;
    }


    private void collectionList1(String s) {
        searchlist.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.search + s,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            Log.e("response", response);

                            JSONArray array = obj.getJSONArray("products");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                String title = object1.getString("title");
                                String price = object1.getString("min_price");
                                String productid = object1.getString("id");

                                JSONArray array1 = object1.getJSONArray("images");
                                for (int j = 0; j < array1.length(); j++) {
                                    Log.e("inti", String.valueOf(j));
                                    JSONObject object = array1.getJSONObject(j);
                                    src = object.getString("src");
                                }
                                SearchModel searchModel = new SearchModel(productid, price, title, src);
                                searchlist.add(searchModel);
                            }

                            adapter.notifyDataSetChanged();
                            if (searchlist.size() == 0) {
                                noproduct.setVisibility(View.VISIBLE);
                            } else {
                                noproduct.setVisibility(View.GONE);
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


    @Override
    public void clickProduct(String productid) {

        Log.e("productvalue", " "+productid);
        Fragment fragment = new ProductView();
        Bundle bundle = new Bundle();
        bundle.putString("category", "search");
        bundle.putString("product_id", productid);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "search");
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        if(getFragmentManager().findFragmentByTag("search")==null)
        {
            ft.addToBackStack("search");
            ft.commit();
        }
        else
        {
            ft.commit();
        }


    }

    @Override
    public void OnclickPlus(String productid) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cartController = new CartController(getActivity());
                commanCartControler = (CommanCartControler) cartController;
                commanCartControler.AddToCart(productid.trim());
//                productAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void OnclickWhislilst(String productid) {
        cartController = new CartController(getActivity());
        commanCartControler = (CommanCartControler) cartController;
        commanCartControler.AddToWhislist(productid.trim());
        Toast.makeText(getActivity(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
    }
}
