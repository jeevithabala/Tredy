package com.marmeto.user.tredy.category;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.category.model.CategoryModel;
import com.marmeto.user.tredy.category.model.ProductModel;
import com.marmeto.user.tredy.category.productDetail.ProductView;
import com.marmeto.user.tredy.filter.Filter_Fragment;
import com.marmeto.user.tredy.foryou.allcollection.AllCollectionModel;
import com.marmeto.user.tredy.foryou.newarrival.NewArrivalModel;
import com.marmeto.user.tredy.foryou.topcollection.TopCollectionModel;
import com.marmeto.user.tredy.foryou.topselling.TopSellingModel;
import com.marmeto.user.tredy.callback.CartController;
import com.marmeto.user.tredy.callback.CommanCartControler;
import com.marmeto.user.tredy.callback.ProductClickInterface;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.util.Config;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.FilterSharedPreference;
import com.marmeto.user.tredy.util.SharedPreference;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CategoryProduct extends Fragment implements ProductAdapter.OnItemClick, View.OnClickListener, ProductClickInterface {
    GraphClient graphClient;
    RecyclerView recyclerView;
    ArrayList<ProductModel> productDetalList = new ArrayList<>();
    ArrayList<ProductModel> productDetalList1 = new ArrayList<>();
    ProductAdapter productAdapter;
    ProductAdapter productAdapter1;
    String productid = "", productidapi = "", price = "";
    TextView category_title;
    TextView view1, grid, filter;
    public static int i = 0;
    public static boolean isViewWithCatalog = true;
    String id, title = "";
    String min_price = "", max_price = "", dynamicKey, dynamicKey1;
    ArrayList<String> selectedFilterList = new ArrayList<>();
    CartController cartController;
    CommanCartControler commanCartControler;
    private int requestCount = 1, requestCount1 = 1;
    RequestQueue requestQueue;
    private String sortbykey;
    TextView noproduct;
    private Boolean isFilterData = false;
    String sortbystring = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.category_product, container, false);

        graphClient = GraphClient.builder(Objects.requireNonNull(getActivity()))
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        category_title = view.findViewById(R.id.category_title);
        noproduct = view.findViewById(R.id.no_product);
        filter = view.findViewById(R.id.filter);
        view1 = view.findViewById(R.id.list);
        grid = view.findViewById(R.id.grid);
        filter.setOnClickListener(this);
        view1.setOnClickListener(this);
        grid.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(getActivity());


        recyclerView = view.findViewById(R.id.product_recyclerview);


//        isViewWithCatalog = !isViewWithCatalog;


//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager1);

        recyclerView.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


//        getProductByCollection(id.trim());


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        String category = null;
        if (getArguments() != null) {
            category = getArguments().getString("collection");
        }
        if (category != null) {
            switch (category.trim()) {
                case "topselling":
                    TopSellingModel topSellingModel = (TopSellingModel) getArguments().getSerializable("category_id");
                    //            detail.setCollection(topSellingModel.getCollection());
                    if (topSellingModel != null) {
                        id = topSellingModel.getCollectionid().trim();
                        byte[] tmp2 = Base64.decode(id, Base64.DEFAULT);
                        String val2 = new String(tmp2);
                        String[] str = val2.split("/");
                        id = str[4];
                        title = topSellingModel.getCollectionTitle();
                    }

                    break;
                case "bestcollection":
                    TopCollectionModel topCollectionModel = (TopCollectionModel) getArguments().getSerializable("category_id");
                    if (topCollectionModel != null) {
                        id = topCollectionModel.getCollectionid().trim();
                        title = topCollectionModel.getCollectionTitle();
                    }

                    break;
                case "api":
                    CategoryModel detail = (CategoryModel) getArguments().getSerializable("category_id");
                    if (detail != null) {
                        id = detail.getId().trim();
                        title = detail.getCollectiontitle();
                    }
                    break;
                case "allproduct":
                    id = "349437318";
                    //            String text = "gid://shopify/Collection/" + id1.trim();
                    //            String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
                    //            id=converted.trim();
                    title = "All Products";
                    break;
                case "allcollection":
                    AllCollectionModel allCollectionModel = (AllCollectionModel) getArguments().getSerializable("category_id");
                    if (allCollectionModel != null) {
                        id = allCollectionModel.getId().trim();
                        title = allCollectionModel.getTitle();
                    }
                    break;
                case "newarrival":

                    NewArrivalModel newArrivalModel = (NewArrivalModel) getArguments().getSerializable("category_id");
                    if (newArrivalModel != null) {
                        id = newArrivalModel.getCollectionid().trim();
                        title = newArrivalModel.getCollectionTitle();
                    }
                    break;
                case "filter":

                    break;
            }
        }
        if (title != null) {
            if (title.trim().length() != 0) {
                Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(title);

            }
        } else {
            Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Categories");

        }

        category_title.setText(title);
        SharedPreference.saveData("collectionid", id, getActivity());

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//        if (isFilterData=true) {
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                }
//
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//
//                    if (isLastItemDisplaying(recyclerView)) {
//                        //Calling the method getdata again
//                        postFilter();
//                    }
//                }
//            });
//        }
//        else {
//            requestCount=1;

//            collectionList(id.trim(),requestCount);


        if (isFilterData) {

            productAdapter1 = new ProductAdapter(getActivity(), productDetalList1, getFragmentManager(), this);
            recyclerView.setAdapter(productAdapter1);
            productAdapter1.notifyDataSetChanged();

        } else {
            onBackPressed();
            productAdapter = new ProductAdapter(getActivity(), productDetalList, getFragmentManager(), this);
            recyclerView.setAdapter(productAdapter);
            productAdapter.notifyDataSetChanged();
        }

        if (Config.isNetworkAvailable(getActivity())) {
            getData();
        } else {
            Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    if (isFilterData) {
                        if (Config.isNetworkAvailable(getActivity())) {
                            postFilter();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Config.isNetworkAvailable(getActivity())) {
                            getData();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


//        }


    }

    public void getFilterData(String minprice, String maxprice, String sortby, String collectionid, ArrayList<String> selectedFilterLists, String CollectionName) {

ArrayList<String> arrayList=new ArrayList<>();
        if (FilterSharedPreference.getArrayList("filter", getActivity()) != null) {
            for (int i = 0; i < FilterSharedPreference.getArrayList("filter", getActivity()).size(); i++) {
                FilterSharedPreference.saveInSp(FilterSharedPreference.getArrayList("filter", getActivity()).get(i), false, Objects.requireNonNull(getActivity()));
            }
        }
        requestCount1 = 1;

        isFilterData = true;
        min_price = minprice;
        max_price = maxprice;
        sortbykey = sortby;
        id = collectionid;
        selectedFilterList = selectedFilterLists;
        for (int i = 0; i < selectedFilterLists.size(); i++) {
            String[] splitStr = selectedFilterLists.get(i).trim().split("\\s+");
            String selectedf = "";

            for (int j = 0; j < splitStr.length; j++) {
                if (j + 1 < splitStr.length) {
                    selectedf = selectedf + " "+splitStr[j + 1].trim();
                    arrayList.add(selectedf.trim());
                }
            }
//            FilterSharedPreference.saveInSp(selectedFilterLists.get(i), true, Objects.requireNonNull(getActivity()));
            FilterSharedPreference.saveInSp(selectedf.trim(), true, Objects.requireNonNull(getActivity()));


        }
        FilterSharedPreference.saveArrayList(arrayList, "filter", getActivity());
        if (sortby.equals("sortBy=min_price&order=desc")) {
            sortby = "Price : High to Low";
            FilterSharedPreference.saveData("sort", sortby, getActivity());
        } else {
            sortby = "Price : Low to High";
            FilterSharedPreference.saveData("sort", sortby, getActivity());
        }

//        String price=getResources().getString(R.string.Rs)+minprice+" - "+maxprice;
        String price = minprice + " - " + maxprice;
        Log.e("price", price);
        FilterSharedPreference.saveData("price", price, getActivity());
        dynamicKey1 = CollectionName;

        productDetalList1.clear();
//        productAdapter.notifyDataSetChanged();
        postFilter();
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (isLastItemDisplaying(recyclerView)) {
//                    //Calling the method getdata again
//                    postFilter();
//                }
//            }
//        });

    }


    public void onBackPressed() {

        if (FilterSharedPreference.getArrayList("filter", getActivity()) != null) {
            for (int i = 0; i < FilterSharedPreference.getArrayList("filter", getActivity()).size(); i++) {
                FilterSharedPreference.saveInSp(FilterSharedPreference.getArrayList("filter", getActivity()).get(i), false, Objects.requireNonNull(getActivity()));
            }
        }

        if (FilterSharedPreference.getData("sort", getActivity()) != null) {
            FilterSharedPreference.saveInSp_sort(FilterSharedPreference.getData("sort", getActivity()), false, getActivity());
        }
        if (FilterSharedPreference.getData("price", getActivity()) != null) {
            FilterSharedPreference.saveInSp_price(FilterSharedPreference.getData("price", getActivity()), false, getActivity());
        }
    }

    public void postFilter() {
        isFilterData = true;
        requestQueue.add(postfilter(id, requestCount1));
        Log.d("request counter1", String.valueOf(requestCount1));
        requestCount1++;

    }


    private StringRequest postfilter(String id, int count) {
        StringRequest stringRequest = null;
        try {

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("collection_id", id);

            JSONObject price = new JSONObject();
            price.put("min_price", min_price);
            price.put("max_price", max_price);
            jsonBody.put("price", price);

            JSONArray food = new JSONArray();


            for (int i = 0; i < selectedFilterList.size(); i++) {
                JSONObject food1 = new JSONObject();
                String type = selectedFilterList.get(i).trim();
                Log.e("type", type);
                String[] splitStr = type.trim().split("\\s+");
                dynamicKey1 = splitStr[0];
                food1.put("name", dynamicKey1);
//                food1.put("value", "Filter" + " " + dynamicKey1 + " " + type);
                food1.put("value", "Filter" + " " + type);
                food.put(food1);
            }

            jsonBody.put("food", food);


            Log.d("check JSON", jsonBody.toString());


            final String requestBody = jsonBody.toString();
            String a;
            if (sortbykey.trim().length() == 0) {
                a = "?page_size=10&page=" + count;
            } else {
                a = "?" + sortbykey.trim() + "&page_size=10&page=" + count;
            }

            stringRequest = new StringRequest(Request.Method.POST, Constants.filter_post + a, response -> {
                Log.i("VOLLEY", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    title = obj.getString("collection_name");
                    if (title.toLowerCase().equals("home page")) {
                        category_title.setText("Trending");
                    } else {
                        category_title.setText(title);
                    }

                    Log.e("title", "" + title);
                    Iterator keys = obj.keys();
                    Log.e("Keys", "" + String.valueOf(keys));

                    while (keys.hasNext()) {
                        dynamicKey = (String) keys.next();
                        Log.d("Dynamic Key", "" + dynamicKey);

                        JSONArray array;
                        try {
                            array = obj.getJSONArray(dynamicKey);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                String title = object1.getString("title");
                                String min_price = object1.getString("min_price");

                                String imagesrc = "";
                                JSONArray array1 = object1.getJSONArray("images");
                                for (int j = 0; j < array1.length(); j++) {
                                    JSONObject object = array1.getJSONObject(j);
                                    productidapi = object.getString("product_id");
                                    imagesrc = object.getString("src");
                                }
                                ProductModel productModel = new ProductModel(productidapi, min_price, title, imagesrc);
                                productDetalList1.add(productModel);
                            }

//                                productAdapter1 = new ProductAdapter(getActivity(), productDetalList1, getFragmentManager(), CategoryProduct.this);
//                                productAdapter = new ProductAdapter(getActivity(), productDetalList, getFragmentManager(), CategoryProduct.this);
//                                recyclerView.setAdapter(productAdapter);
                            productAdapter1.notifyDataSetChanged();
                            if (productDetalList1.size() == 0) {
                                noproduct.setVisibility(View.VISIBLE);
                            } else {
                                noproduct.setVisibility(View.GONE);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.e("VOLLEY", error.toString())) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//                        return requestBody == null;
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    //TODO if you want to use the status code for any other purpose like to handle 401, 403, 404
//                    String statusCode = String.valueOf(response.statusCode);
                    //Handling logic
                    return super.parseNetworkResponse(response);
                }

            };

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stringRequest;


    }

    private void getData() {
//        isFilterData=false;
//        newarrival = "33238122615";
//        String topselling="345069894";
//        if (newarrival.trim().equals(id.trim())||topselling.trim().equals(id.trim())) {
//            sortbystring = "?sortBy=created_at&page_size=10&page=";
//        } else {
//            sortbystring = "?page_size=10&page=";
//        }
        sortbystring = "?page_size=10&page=";

        requestQueue.add(collectionList(id, requestCount));
        Log.d("request counter", String.valueOf(requestCount));
        requestCount++;
    }

    private StringRequest collectionList(String id, int count) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("collection_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("check JSON", jsonBody.toString());


        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.filter_post + sortbystring + count, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    Iterator keys = obj.keys();
                    Log.e("Keys", "" + String.valueOf(keys));

                    while (keys.hasNext()) {
                        String dynamicKey = (String) keys.next();
                        Log.d("Dynamic Key", "" + dynamicKey);

                        JSONArray array;
                        try {
                            array = obj.getJSONArray(dynamicKey);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                String title = object1.getString("title");
                                price = object1.getString("min_price");
                                Log.e("image1", title + price);

                                JSONArray array1 = object1.getJSONArray("images");
                                String imagesrc = "";

                                for (int j = 0; j < array1.length(); j++) {
                                    JSONObject object = array1.getJSONObject(j);
                                    productidapi = object.getString("product_id");
                                    imagesrc = object.getString("src");
                                }
                                ProductModel productModel = new ProductModel(productidapi, price, title, imagesrc);
                                productDetalList.add(productModel);


                            }
                            productAdapter.notifyDataSetChanged();
                        } catch (JSONException e1) {
                            e1.printStackTrace();

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Log.e("VOLLEY", "" + error.toString())) {
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
//                String statusCode = String.valueOf(response.statusCode);
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

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter:


//                String ps2 = "dGVjaFBhC3M=";
//               byte[] tmp2 = Base64.decode(id,Base64.DEFAULT);
//
//                String val2 = new String(tmp2);
//                String[] str = val2.split("/");
//
////                String decodeid=Base64.decode(id,)
//                Log.d("str value", str[4]);

                Fragment fragment = new Filter_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("collectionid", id);
//                bundle.putStringArrayList("vendorarray", vendorarray);
//                bundle.putStringArrayList("producttag", producttag);
//                bundle.putStringArrayList("producttype", producttype);
                fragment.setArguments(bundle);
                FragmentTransaction ft;
                if (getFragmentManager() != null) {
                    ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "filter");
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                ft.addToBackStack(null);
                    if (getFragmentManager().findFragmentByTag("filter") == null) {
                        ft.addToBackStack("filter");
                        ft.commit();
                    } else {
                        ft.commit();
                    }
                }


                break;

            case R.id.list:
                isViewWithCatalog = true;
//                isViewWithCatalog = !isViewWithCatalog;
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager1);
                recyclerView.setAdapter(productAdapter);

//                recyclerView.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 2));
//                recyclerView.setAdapter(productAdapter);

                break;

            case R.id.grid:
                isViewWithCatalog = false;
//                isViewWithCatalog = !isViewWithCatalog;
                LinearLayoutManager layoutManager2 = new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager2);
                recyclerView.setAdapter(productAdapter);

                break;
        }
    }


    @Override
    public void clickProduct(String productid) {

        Log.d("product value", productid);
        Fragment fragment = new ProductView();
        Bundle bundle = new Bundle();
        bundle.putString("category", "ca_adapter");
        bundle.putString("product_id", productid);
        fragment.setArguments(bundle);
        FragmentTransaction ft;
        if (getFragmentManager() != null) {
            ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "productview");
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            if (getFragmentManager().findFragmentByTag("productview") == null) {
                ft.addToBackStack("productview");
                ft.commit();
            } else {
                ft.commit();
            }
        }

    }

    @Override
    public void OnclickPlus(String productid) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            cartController = new CartController(getActivity());
            commanCartControler = cartController;
            commanCartControler.AddToCart(productid.trim());
//                productAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void OnclickWhislilst(String productid) {
        cartController = new CartController(getActivity());
        commanCartControler = cartController;
        commanCartControler.AddToWhislist(productid.trim());
        Toast.makeText(getActivity(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(String value) {
        productid = value;
//        cart(productid);
    }


}

