package com.example.user.trendy.search;

import android.app.ProgressDialog;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.category.ProductModel;
import com.example.user.trendy.category.productDetail.ProductView;
import com.example.user.trendy.callback.CartController;
import com.example.user.trendy.callback.CommanCartControler;
import com.example.user.trendy.callback.ProductClickInterface;
import com.example.user.trendy.Navigation;
import com.example.user.trendy.R;
import com.example.user.trendy.groceries.GroceryModel;
import com.example.user.trendy.util.Constants;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Search extends Fragment implements SearchView.OnQueryTextListener, ProductClickInterface {
    RecyclerView search_recycler;
    private SearchAdapter adapter;
    private ArrayList<SearchModel> searchlist = new ArrayList<>();
    SearchView search;
    private RequestQueue requestQueue;
    private String src;
    CartController cartController;
    CommanCartControler commanCartControler;
    TextView noproduct;
    private GraphClient graphClient;
    ArrayList<String> productStringPageCursor = new ArrayList<>();
    private String productPageCursor = "";
    int i = 0;
    String searchtext = "";
    private Boolean isFilterData = false;
    private int requestCount = 1;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search, container, false);

        ((Navigation) getActivity()).getSupportActionBar().setTitle("Search");

        search_recycler = view.findViewById(R.id.search_recycler);
        search = view.findViewById(R.id.search);
        search.onActionViewExpanded();


        noproduct = view.findViewById(R.id.no_product);
        requestQueue = Volley.newRequestQueue(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        search_recycler.setLayoutManager(layoutManager1);
        search_recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new SearchAdapter(getActivity(), searchlist, this);
        search_recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        search.setOnQueryTextListener(this);
        search_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    if (isFilterData) {
                        postFilter();
                    } else {
                        getData();
                    }
                }
            }
        });

    }

    private void getData() {
        requestQueue.add(collectionList(searchtext, requestCount));
        Log.d("request counter", String.valueOf(requestCount));
        requestCount++;
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchtext = s;
        requestCount = 1;
        searchlist.clear();
        if (s.trim().length() != 0) {
            getData();

        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        i = 0;
        searchlist.clear();
        requestCount = 1;
        searchtext = s;
        if (s.trim().length() == 0) {
            adapter.notifyDataSetChanged();
            if (searchlist.size() == 0) {
                noproduct.setVisibility(View.VISIBLE);
            } else {
                noproduct.setVisibility(View.GONE);
            }
        } else {
            getData();
        }
        return true;
    }

    public void postFilter() {
        isFilterData = true;
        requestQueue.add(collectionList(searchtext, requestCount));
        Log.d("request counter1", String.valueOf(requestCount));
        requestCount++;

    }


    private StringRequest collectionList(String search, int count) {
//        searchlist.clear();

        String URL = "http://...";
        JSONObject jsonBody = new JSONObject();


        Log.d("check JSON", jsonBody.toString());


        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.search + search + "?&page=" + count, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray array = obj.getJSONArray("products");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        String title = object1.getString("title");
                        String price = object1.getString("min_price");
                        String productid = object1.getString("id");

                        JSONArray array1 = object1.getJSONArray("images");
                        for (int j = 0; j < array1.length(); j++) {
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
                    String last_page = obj.getString("last_page");
                    if (last_page != null) {
                        isFilterData = true;
                    } else {
                        isFilterData = false;
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

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
//                    return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");

                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                //TODO if you want to use the status code for any other purpose like to handle 401, 403, 404
                String statusCode = String.valueOf(response.statusCode);
                //Handling logic
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
//}
        };

        return stringRequest;
    }


    @Override
    public void clickProduct(String productid) {

        Fragment fragment = new ProductView();
        Bundle bundle = new Bundle();
        bundle.putString("category", "search");
        bundle.putString("product_id", productid);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "search");
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        if (getFragmentManager().findFragmentByTag("search") == null) {
            ft.addToBackStack("search");
            ft.commit();
        } else {
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
