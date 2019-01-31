package com.tredy.user.tredy.foryou.viewmodel;

import android.annotation.SuppressLint;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tredy.user.tredy.BuildConfig;
import com.tredy.user.tredy.foryou.allcollection.AllCollectionModel;
import com.tredy.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.tredy.user.tredy.foryou.newarrival.NewArrivalModel;
import com.tredy.user.tredy.foryou.topselling.TopSellingModel;
import com.tredy.user.tredy.util.Constants;
import com.tredy.user.tredy.util.SharedPreference;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ForYouViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ForyouInterface foryouInterface;
    private ArrayList<TopSellingModel> topSellingModelArray = new ArrayList<>();
    //    ArrayList<TopCollectionModel> topCollectionModelArray = new ArrayList<>();
    private ArrayList<NewArrivalModel> newArrivalModelArray = new ArrayList<>();
    private ArrayList<AllCollectionModel> allCollectionModelArrayList = new ArrayList<>();
    private ArrayList<GroceryHomeModel> GroceryHomeModelArrayList = new ArrayList<>();
    private String collectionid, title, id, price, image;
    private String date;
    private String collectionname;
    private ArrayList<String> bannerlist = new ArrayList<>();
    private GraphClient graphClient;

    public ForYouViewModel(Context mContext, ForyouInterface foryouInterface) {
        this.mContext = mContext;
        this.foryouInterface = foryouInterface;
        banner();
        collectionList();
        //  collectionList1();
//        getCollection();
//        getTopCollection();
//        getNewArrival();
        getallhomecollection();
        getNotiCount();

    }

    public ForYouViewModel(Context mContext) {
        this.mContext = mContext;
    }

    private void collectionList() {
        mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.navigation,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);
                        allCollectionModelArrayList.clear();
                        JSONObject menu = obj.getJSONObject("menu");
                        JSONArray jsonarray = menu.getJSONArray("items");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject collectionobject = jsonarray.getJSONObject(i);

                            String nav = collectionobject.getString("type");

//                                if (id.trim().length() != 0) {
//                                    String text = "gid://shopify/Collection/" + id.trim();
//                                    String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
//                                }

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
                                            if (!subid.trim().equals("null")) {
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
                },
                error -> {
                    Log.d("error", "" + error.getMessage());
//                        progressDialog.dismiss();
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.collectionid + "?sortBy=created_at",
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);
//                            topCollectionModelArray.clear();
                        newArrivalModelArray.clear();


                        Iterator keys = obj.keys();

                        while (keys.hasNext()) {
                            String dynamicKey = (String) keys.next();

                            JSONArray array;
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
//                                        if (i == 0) {
//                                                TopCollectionModel topCollectionModel = new TopCollectionModel(id, title, price, image, collectionname);
//                                                Log.e("product", title);
//                                                topCollectionModel.setCollectionid(collectionid);
//                                                topCollectionModelArray.add(topCollectionModel);

//                                        } else if (i == 1) {


//                                                TopSellingModel topSellingModel = new TopSellingModel(id, title, price, image, collectionname);
//                                                topSellingModel.setCollectionid(collectionid);
//                                                topSellingModelArray.add(topSellingModel);

//                                                resultCallBackInterface.bestCollection(collectionid, id, title, price, image, collectionname);}
                                        if (i == 2) {
                                            NewArrivalModel newArrivalModel = new NewArrivalModel(id, title, price, image, collectionname);
                                            newArrivalModel.setCollectionid(collectionid);
                                            Date date1 = null;
                                            try {
//                                                    date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'").parse(date);
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
// use UTC as timezone
                                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                date1 = sdf.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            newArrivalModel.setDateTime(date1);
                                            newArrivalModelArray.add(newArrivalModel);

//                                                resultCallBackInterface.newArrivals(collectionid, id, title, price, image, collectionname);
                                        }

                                    }
                                }
                                Collections.sort(newArrivalModelArray, (m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()));
                                Collections.reverse(newArrivalModelArray);
                                foryouInterface.collectionlist(newArrivalModelArray);
                            } catch (JSONException e1) {
                                e1.printStackTrace();

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
//                        progressDialog.dismiss();
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
                jsonArray -> {
                    bannerlist.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String bannerimage = jsonObject.getString("image_src");
                            bannerlist.add(bannerimage);

                        } catch (JSONException ignored) {

                        }
                    }
                    foryouInterface.bannerlist(bannerlist);

                },
                error -> {

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
                                                                        .node(Storefront.ImageQuery::src
                                                                        )
                                                                )
                                                        )
                                                        .tags()
                                                        .options(Storefront.ProductOptionQuery::name)
                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                                                .edges(variantEdgeQuery -> variantEdgeQuery
                                                                        .node(productVariantQuery -> productVariantQuery
                                                                                .price()
                                                                                .title()
                                                                                .image(Storefront.ImageQuery::src)
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
                Storefront.Collection product;
                if (response.data() != null) {
                    product = (Storefront.Collection) response.data().getNode();
                    for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                        GroceryHomeModel GroceryHomeModel = new GroceryHomeModel();
                        GroceryHomeModel.setProduct(productEdge.getNode());
                        GroceryHomeModel.setTitle(product.getTitle());
                        GroceryHomeModel.setQty("1");
                        GroceryHomeModelArrayList.add(GroceryHomeModel);
                    }
                    foryouInterface.grocerylist(GroceryHomeModelArrayList);
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }

    private String getCalculatedDate() {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
        cal.add(Calendar.DAY_OF_YEAR, -10);
        return s.format(new Date(cal.getTimeInMillis()));
    }


    private void getNotiCount() {
        String customerid = SharedPreference.getData("customerid", mContext);
//        String minusdatet = getCalculatedDate("MM/dd/yyyy");
        String minusdatet = getCalculatedDate();


        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.unreadcount + customerid.trim() + "?from=" + minusdatet,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);
                        Log.e("response", response);
                        String count = obj.getString("count");
                        int noti_counnt = Integer.parseInt(count);
                        foryouInterface.getcount(noti_counnt);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                }) {

        };
        stringRequest.setTag("noti");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);


    }

    private void getTopCollection() {
        String id = "345069894";
        String text = "gid://shopify/Collection/" + id.trim();
        String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT);

        topSellingModelArray.clear();

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
                                .products(arg -> arg.first(10).sortKey(Storefront.ProductCollectionSortKeys.valueOf("MANUAL")), productConnectionQuery -> productConnectionQuery
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .node(productQuery -> productQuery
                                                        .title()
                                                        .productType()
                                                        .description()
                                                        .descriptionHtml()
                                                        .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                                                .edges(imageEdgeQuery -> imageEdgeQuery
                                                                        .node(Storefront.ImageQuery::src
                                                                        )
                                                                )
                                                        )
                                                        .tags()
                                                        .options(Storefront.ProductOptionQuery::name)
                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                                                .edges(variantEdgeQuery -> variantEdgeQuery
                                                                        .node(productVariantQuery -> productVariantQuery
                                                                                .price()
                                                                                .title()
                                                                                .image(Storefront.ImageQuery::src)
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
                Storefront.Collection product;
                if (response.data() != null) {
                    product = (Storefront.Collection) response.data().getNode();
                    String collectionname = product.getTitle();
                    for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {

                        String id = productEdge.getNode().getId().toString();
                        String title = productEdge.getNode().getTitle();
                        String price = productEdge.getNode().getVariants().getEdges().get(0).getNode().getPrice().toString();
                        String image = "";
                        if (productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage() != null) {
                            image = productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage().getSrc();
                        }
                        TopSellingModel topSellingModel = new TopSellingModel(id, title, price, image, collectionname);
                        topSellingModel.setCollectionid(converted.trim());
                        topSellingModelArray.add(topSellingModel);
                    }
                    foryouInterface.topselling1(topSellingModelArray);
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }

    private void getNewArrival() {
        String id = "33238122615";
        String text = "gid://shopify/Collection/" + id.trim();
        String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT);

        newArrivalModelArray.clear();

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
                                .products(arg -> arg.first(10).sortKey(Storefront.ProductCollectionSortKeys.valueOf("MANUAL")), productConnectionQuery -> productConnectionQuery
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .node(productQuery -> productQuery
                                                        .title()
                                                        .productType()
                                                        .description()
                                                        .descriptionHtml()
                                                        .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                                                .edges(imageEdgeQuery -> imageEdgeQuery
                                                                        .node(Storefront.ImageQuery::src
                                                                        )
                                                                )
                                                        )
                                                        .tags()
                                                        .options(Storefront.ProductOptionQuery::name)
                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                                                .edges(variantEdgeQuery -> variantEdgeQuery
                                                                        .node(productVariantQuery -> productVariantQuery
                                                                                .price()
                                                                                .title()
                                                                                .image(Storefront.ImageQuery::src)
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
                Storefront.Collection product;
                if (response.data() != null) {
                    product = (Storefront.Collection) response.data().getNode();
                    String collectionname = product.getTitle();
                    for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {

                        String id = productEdge.getNode().getId().toString();
                        String title = productEdge.getNode().getTitle();
                        String price = productEdge.getNode().getVariants().getEdges().get(0).getNode().getPrice().toString();
                        String image = "";
                        if (productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage() != null) {
                            image = productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage().getSrc();
                        }
                        NewArrivalModel newArrivalModel = new NewArrivalModel(id, title, price, image, collectionname);
                        newArrivalModel.setCollectionid(converted.trim());

                        newArrivalModelArray.add(newArrivalModel);
                    }
                    foryouInterface.collectionlist(newArrivalModelArray);
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }

    private void getallhomecollection() {
        String sortby = "";
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                topSellingModelArray.clear();
                String id = "345069894";
                String text = "gid://shopify/Collection/" + id.trim();
                String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT);
                sortby = "MANUAL";
                gethomeCollection(converted, i, sortby);
            } else if (i == 1) {
                newArrivalModelArray.clear();
                String id = "33238122615";
                String text = "gid://shopify/Collection/" + id.trim();
                String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT);
                sortby = "COLLECTION_DEFAULT";
                gethomeCollection(converted, i, sortby);
            } else {
                GroceryHomeModelArrayList.clear();
                String id = "58881703997";
                String text = "gid://shopify/Collection/" + id.trim();
                String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT);
                sortby = "TITLE";
                gethomeCollection(converted, i, sortby);
            }

        }
    }

    private void gethomeCollection(String collectionid, int i, String sortby) {

        graphClient = GraphClient.builder(mContext)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(mContext.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(collectionid.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10).sortKey(Storefront.ProductCollectionSortKeys.valueOf(sortby)), productConnectionQuery -> productConnectionQuery
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .node(productQuery -> productQuery
                                                        .title()
                                                        .productType()
                                                        .description()
                                                        .descriptionHtml()
                                                        .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                                                .edges(imageEdgeQuery -> imageEdgeQuery
                                                                        .node(Storefront.ImageQuery::src
                                                                        )
                                                                )
                                                        )
                                                        .tags()
                                                        .options(Storefront.ProductOptionQuery::name)
                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                                                .edges(variantEdgeQuery -> variantEdgeQuery
                                                                        .node(productVariantQuery -> productVariantQuery
                                                                                .price()
                                                                                .title()
                                                                                .image(Storefront.ImageQuery::src)
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
                Storefront.Collection product;
                if (response.data() != null && response.data().getNode() != null) {
                    product = (Storefront.Collection) response.data().getNode();
                    if (i == 0) {
                        String collectionname = product.getTitle();
                        for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {

                            String id = productEdge.getNode().getId().toString();
                            String title = productEdge.getNode().getTitle();
                            String price = productEdge.getNode().getVariants().getEdges().get(0).getNode().getPrice().toString();
                            String image = "";
                            if (productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage() != null) {
                                image = productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage().getSrc();
                            }
                            TopSellingModel topSellingModel = new TopSellingModel(id, title, price, image, collectionname);
                            topSellingModel.setCollectionid(collectionid.trim());
                            if (topSellingModelArray != null) {
                                topSellingModelArray.add(topSellingModel);
                            }
                        }
                        foryouInterface.topselling1(topSellingModelArray);
                    } else if (i == 1) {
                        String collectionname = product.getTitle();
                        for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {

                            String id = productEdge.getNode().getId().toString();
                            String title = productEdge.getNode().getTitle();
                            String price = productEdge.getNode().getVariants().getEdges().get(0).getNode().getPrice().toString();
                            String image = "";
                            if (productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage() != null) {
                                image = productEdge.getNode().getVariants().getEdges().get(0).getNode().getImage().getSrc();
                            }
                            NewArrivalModel newArrivalModel = new NewArrivalModel(id, title, price, image, collectionname);
                            newArrivalModel.setCollectionid(collectionid.trim());
                            if (newArrivalModelArray != null) {
                                newArrivalModelArray.add(newArrivalModel);
                            }
                        }
                        foryouInterface.collectionlist(newArrivalModelArray);
                    } else if (i == 2) {
                        for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                            GroceryHomeModel GroceryHomeModel = new GroceryHomeModel();
                            GroceryHomeModel.setProduct(productEdge.getNode());
                            GroceryHomeModel.setTitle(product.getTitle());
                            GroceryHomeModel.setQty("1");
                            if (GroceryHomeModelArrayList != null) {
                                GroceryHomeModelArrayList.add(GroceryHomeModel);
                            }
                        }
                        foryouInterface.grocerylist(GroceryHomeModelArrayList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }


}
