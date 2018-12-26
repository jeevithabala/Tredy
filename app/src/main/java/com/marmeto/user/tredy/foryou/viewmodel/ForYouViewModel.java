package com.marmeto.user.tredy.foryou.viewmodel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.foryou.allcollection.AllCollectionModel;
import com.marmeto.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.marmeto.user.tredy.foryou.newarrival.NewArrivalModel;
import com.marmeto.user.tredy.foryou.topcollection.TopCollectionModel;
import com.marmeto.user.tredy.foryou.topselling.TopSellingModel;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.MObject;
import com.marmeto.user.tredy.util.SharedPreference;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ForYouViewModel extends ViewModel {

    private ProgressDialog progressDialog;
    Context mContext;
    private RequestQueue mRequestQueue;
    ForyouInterface foryouInterface;
    ArrayList<TopSellingModel> topSellingModelArray = new ArrayList<>();
    ArrayList<TopCollectionModel> topCollectionModelArray = new ArrayList<>();
    ArrayList<NewArrivalModel> newArrivalModelArray = new ArrayList<>();
    ArrayList<AllCollectionModel> allCollectionModelArrayList = new ArrayList<>();
    private ArrayList<GroceryHomeModel> GroceryHomeModelArrayList = new ArrayList<>();
    private String collectionid, title, id, price, image;
    String date;
    private String collectionname;
    ArrayList<String> bannerlist = new ArrayList<>();
    GraphClient graphClient;

    public ForYouViewModel(Context mContext, ForyouInterface foryouInterface) {
        this.mContext = mContext;
        this.foryouInterface = foryouInterface;
        banner();
        collectionList();
        collectionList1();
        getCollection();
        getNotiCount();
    }

    public ForYouViewModel(Context mContext) {
        this.mContext = mContext;
    }

    private void collectionList() {
        mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.navigation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            JSONObject obj = new JSONObject(response);
                            allCollectionModelArrayList.clear();
                            JSONObject menu = obj.getJSONObject("menu");
                            String title = menu.getString("title");
                            JSONArray jsonarray = menu.getJSONArray("items");

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject collectionobject = jsonarray.getJSONObject(i);


                                String id = "" + collectionobject.getString("subject_id");
                                String collectiontitle = collectionobject.getString("title");
                                String nav = collectionobject.getString("type");

                                if (id.trim().length() != 0) {
                                    String text = "gid://shopify/Collection/" + id.trim();

                                    String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
                                }

                                if (nav.trim().equals("http") || nav.trim().equals("collection")) {


                                    JSONArray jsonarray1 = collectionobject.getJSONArray("items");
                                    if (jsonarray1.length() != 0) {
                                        for (int j = 0; j < jsonarray1.length(); j++) {
                                            JSONObject subcollectionobject = jsonarray1.getJSONObject(j);

                                            String subid = "" + subcollectionobject.getString("subject_id");
                                            String subcollectiontitle = subcollectionobject.getString("title");
                                            String type = subcollectionobject.getString("type");
                                            if (type.trim().equals("collection")) {
                                                String image1 = subcollectionobject.getString("image");
                                                Log.e("immmm", " " + image1);
                                                if (!subid.trim().equals("null")) {


                                                    if (subid.trim().length() != 0) {
                                                        String text = "gid://shopify/Collection/" + subid.trim();

                                                        String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
                                                        Log.e("coverted", converted.trim());
                                                    }

                                                    AllCollectionModel allCollectionModel = new AllCollectionModel(subid, image1, subcollectiontitle);
                                                    allCollectionModelArrayList.add(allCollectionModel);
                                                }


                                            }

                                        }
                                        foryouInterface.allcollection(allCollectionModelArrayList);
//
                                    }

                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", "" + error.getMessage());
//                        progressDialog.dismiss();
                    }
                }) {

            @Override
            protected void deliverResponse(String response) {
                Log.e("ree", " " + response);
                super.deliverResponse(response);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.e("reen", " " + response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);

    }


    private void collectionList1() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.collectionid,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            topSellingModelArray.clear();
                            topCollectionModelArray.clear();
                            newArrivalModelArray.clear();


                            Iterator keys = obj.keys();
                            Log.e("Keys", "" + String.valueOf(keys));

                            while (keys.hasNext()) {
                                String dynamicKey = (String) keys.next();
                                Log.d("Dynamic Key", "" + dynamicKey);

                                JSONArray array = null;
                                try {

                                    array = obj.getJSONArray(dynamicKey);


                                    for (int i = 0; i < array.length(); i++) {
                                        Log.e("inti", String.valueOf(i));
                                        JSONObject object1 = array.getJSONObject(i);
                                        collectionid = object1.getString("id");
                                        collectionname = object1.getString("title");
                                        if (collectionname.trim().toLowerCase().equals("home page")) {
                                            collectionname = "Trending";
                                        }

                                        JSONArray array1 = object1.getJSONArray("products");
                                        for (int j = 0; j < array1.length(); j++) {
                                            JSONObject objec = array1.getJSONObject(j);

                                            title = objec.getString("title");
                                            date = objec.getString("published_at");
//                                            if (title.trim().toLowerCase().equals("home page")) {
//                                                title = "Treding";
//                                            }
                                            JSONArray varientsarray = objec.getJSONArray("variants");
                                            for (int k = 0; k < varientsarray.length(); k++) {
                                                JSONObject objec1 = varientsarray.getJSONObject(k);

                                                id = objec1.getString("product_id");
                                                price = objec1.getString("price");

                                            }
                                            JSONArray array2 = objec.getJSONArray("images");
                                            for (int l = 0; l < array2.length(); l++) {
                                                JSONObject objec1 = array2.getJSONObject(l);
                                                image = objec1.getString("src");
                                            }
                                            if (i == 0) {
//                                                TopCollectionModel topCollectionModel = new TopCollectionModel(id, title, price, image, collectionname);
//                                                Log.e("product", title);
//                                                topCollectionModel.setCollectionid(collectionid);
//                                                topCollectionModelArray.add(topCollectionModel);

                                            } else if (i == 1) {


                                                TopSellingModel topSellingModel = new TopSellingModel(id, title, price, image, collectionname);
                                                Log.e("product", title);
                                                topSellingModel.setCollectionid(collectionid);
                                                topSellingModelArray.add(topSellingModel);

//                                                resultCallBackInterface.bestCollection(collectionid, id, title, price, image, collectionname);
                                            } else if (i == 2) {
                                                NewArrivalModel newArrivalModel = new NewArrivalModel(id, title, price, image, collectionname);
                                                Log.e("product", title);
                                                newArrivalModel.setCollectionid(collectionid);
                                                Date date1= null;
                                                try {
//                                                    date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'").parse(date);
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
// use UTC as timezone
                                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                     date1 = sdf.parse(date);
                                                    Log.e("date", String.valueOf(date1));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                newArrivalModel.setDateTime(date1);
                                                newArrivalModelArray.add(newArrivalModel);

//                                                resultCallBackInterface.newArrivals(collectionid, id, title, price, image, collectionname);
                                            }

                                        }
                                    }
                                    Collections.sort(newArrivalModelArray, new Comparator<NewArrivalModel>() {
                                        public int compare(NewArrivalModel m1, NewArrivalModel m2) {
                                            return m1.getDateTime().compareTo(m2.getDateTime());
                                        }
                                    });
                                    foryouInterface.collectionlist(topSellingModelArray, newArrivalModelArray);
//                                    resultCallBackInterface.topSelling(topSellingModelArray);
//                                    resultCallBackInterface.bestCollection(topCollectionModelArray);
//                                    resultCallBackInterface.newArrivals(newArrivalModelArray);
//                                    progressDialog.dismiss();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();

                                }
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

    private void banner() {

        mRequestQueue = Volley.newRequestQueue(mContext);
        JsonArrayRequest request = new JsonArrayRequest(Constants.banner,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        bannerlist.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String bannerimage = jsonObject.getString("image_src");
                                bannerlist.add(bannerimage);

                                foryouInterface.bannerlist(bannerlist);
                            } catch (JSONException e) {

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        request.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        mRequestQueue.add(request);

    }

    private void getCollection() {
        String id = "58881703997";
        String text = "gid://shopify/Collection/" + id.trim();
        String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT);

        GroceryHomeModelArrayList.clear();

        graphClient = GraphClient.builder(mContext)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(mContext.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(converted.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10), productConnectionQuery -> productConnectionQuery
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .node(productQuery -> productQuery
                                                        .title()
                                                        .productType()
                                                        .description()
                                                        .descriptionHtml()
                                                        .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                                                .edges(imageEdgeQuery -> imageEdgeQuery
                                                                        .node(imageQuery -> imageQuery
                                                                                .src()
                                                                        )
                                                                )
                                                        )
                                                        .tags()
                                                        .options(option -> option.name())
                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                                                .edges(variantEdgeQuery -> variantEdgeQuery
                                                                        .node(productVariantQuery -> productVariantQuery
                                                                                .price()
                                                                                .title()
                                                                                .image(args -> args.src())
                                                                                .weight()
                                                                                .weightUnit()
                                                                                .available()
                                                                        )
                                                                )
                                                        )
                                                )
                                        )


                                ))));

        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                Storefront.Collection product = (Storefront.Collection) response.data().getNode();

                for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                    GroceryHomeModel GroceryHomeModel = new GroceryHomeModel();
                    GroceryHomeModel.setProduct(productEdge.getNode());
                    GroceryHomeModel.setTitle(product.getTitle());
                    GroceryHomeModel.setQty("1");
                    GroceryHomeModelArrayList.add(GroceryHomeModel);
                }
                foryouInterface.grocerylist(GroceryHomeModelArrayList);
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }


    public void getNotiCount() {
        String customerid = SharedPreference.getData("customerid", mContext);
        String minusdatet = getCalculatedDate("MM/dd/yyyy", -10);


        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.unreadcount + customerid.trim() + "?from=" + minusdatet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            Log.e("response", response);
                            String count = obj.getString("count");
                            int noti_counnt = Integer.parseInt(count);
                            foryouInterface.getcount(noti_counnt);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

        };
        stringRequest.setTag("noti");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);


    }


}
