package com.marmeto.user.tredy.category;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.category.model.CategoryModel;
import com.marmeto.user.tredy.category.model.SubCategoryModel;
import com.marmeto.user.tredy.util.Config;
import com.marmeto.user.tredy.util.Constants;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Categories extends Fragment {

    RecyclerView recyclerView;
    GraphClient graphClient;
    ArrayList<CategoryModel> categoryList = new ArrayList<>();
    ArrayList<SubCategoryModel> subCategoryModelArrayList;
    CategoreDetailAdapter categoreDetailAdapter;
    LinearLayout subcategory;
    String converted;
    private String image1 = "";
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.categories, container, false);



        TextView all = view.findViewById(R.id.all);
        all.setVisibility(View.GONE);
        subcategory = view.findViewById(R.id.sublayout);
        subcategory.setVisibility(View.GONE);

//        grocery=view.findViewById(R.id.grocery);


        recyclerView = view.findViewById(R.id.categories_recyclerview);

        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


        //    LinearLayoutManager layoutManager1 = new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.VERTICAL, false);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        categoreDetailAdapter = new CategoreDetailAdapter(getActivity(), categoryList, getFragmentManager());
        recyclerView.setAdapter(categoreDetailAdapter);
        // productlist();
        if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            collectionList();
        } else {
            Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    private void collectionList() {
        categoryList.clear();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestQueue mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.navigation,
                response -> {
                    try {
                        Log.e("response", response);

                        JSONObject obj = new JSONObject(response);
                        Log.e("response1", response);
                        categoryList.clear();
                        JSONObject menu = obj.getJSONObject("menu");
                        //  String status = obj.getString("menu");

                        String title = menu.getString("title");
                        Log.e("title", title);
                        // JSONObject allhistoryobj = obj.getJSONObject("insurance");
                        JSONArray jsonarray = menu.getJSONArray("items");
                        Log.e("jsonarray", String.valueOf(jsonarray));

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject collectionobject = jsonarray.getJSONObject(i);
                            CategoryModel categoreDetail = new CategoryModel();

                            String id = "" + collectionobject.getString("subject_id");
                            String collectiontitle = collectionobject.getString("title");
                            String nav = collectionobject.getString("type");
//                                String image = collectionobject.getString("image");


                            if (id.trim().length() != 0) {
                                String text = "gid://shopify/Collection/" + id.trim();

                                converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
                                Log.e("coverted", converted.trim());
                            }

                            if (nav.trim().equals("http") || nav.trim().equals("collection")) {
                                categoreDetail.setId(id.trim());
                                categoreDetail.setCollectiontitle(collectiontitle);
                                categoreDetail.setImageurl("");

                                JSONArray jsonarray1 = collectionobject.getJSONArray("items");
                                Log.e("jsonarray1", String.valueOf(jsonarray1));
                                subCategoryModelArrayList = new ArrayList<>();
                                if (jsonarray1.length() != 0) {
                                    for (int j = 0; j < jsonarray1.length(); j++) {
                                        JSONObject subcollectionobject = jsonarray1.getJSONObject(j);
                                        SubCategoryModel subCategoryModel = new SubCategoryModel();

                                        String subid = "" + subcollectionobject.getString("subject_id");
                                        String subcollectiontitle = subcollectionobject.getString("title");
                                        String type = subcollectionobject.getString("type");
                                        if (type.trim().equals("collection")) {
                                            image1 = subcollectionobject.getString("image");
                                        }

                                        if (!subid.trim().equals("null")) {


                                            if (subid.trim().length() != 0) {
                                                String text = "gid://shopify/Collection/" + subid.trim();

                                                converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
                                            }
                                            subCategoryModel.setId(subid.trim());
                                            subCategoryModel.setTitle(subcollectiontitle);
                                            subCategoryModel.setImage(image1);
                                            subCategoryModelArrayList.add(subCategoryModel);
                                            categoreDetail.setSubCategoryModelArrayList(subCategoryModelArrayList);

//                                                String text="gid://shopify/Product/"+subid.trim();
//
//                                                String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
//                                                Log.e("converted", "" + converted);
//                                                    getProductByCollection(converted);
//                                                    Log.e("imageurl1", "" + imageurl);
//                                                    categoreDetail.setImageurl(imageurl);
                                        }

                                    }
//                                    } else {
//                                        categoryList.add(categoreDetail);
                                }
                                categoryList.add(categoreDetail);
                            }
                        }
                        ArrayList<String> extra = new ArrayList<>();
                        extra.add("Grocery");
                        extra.add("All Products");

                        for (int i = 0; i < extra.size(); i++) {
                            CategoryModel categoryModel1 = new CategoryModel();
                            categoryModel1.setCollectiontitle(extra.get(i));
                            categoryList.add(categoryModel1);

                        }
//                           CategoryModel categoryModel1 = new CategoryModel();
//                            categoryModel1.setCollectiontitle("Grocery");
//                            categoryList.add(categoryModel1);
//
//                            CategoryModel categoryModel2 = new CategoryModel();
//                            categoryModel1.setCollectiontitle("All Products");
//                            categoryList.add(categoryModel2);

                        categoreDetailAdapter = new CategoreDetailAdapter(getActivity(), categoryList, getFragmentManager());

                        recyclerView.setAdapter(categoreDetailAdapter);
                        categoreDetailAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                    Toast.makeText(getActivity()," "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                },
                error -> progressDialog.dismiss()) {

        };
        stringRequest.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);

    }

//    public void getProductByCollection(String categoryID) {
//        imageurl = "";
//        Log.e("inside", "came");
//        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
//                .node(new ID(categoryID), nodeQuery -> nodeQuery
//                        .onCollection(collectionQuery -> collectionQuery
//                                .title()
//                                .image(args -> args.src())
//                                .products(arg -> arg.first(100), productConnectionQuery -> productConnectionQuery
//                                        .edges(productEdgeQuery -> productEdgeQuery
//                                                .node(productQuery -> productQuery
//                                                        .title()
//                                                        .productType()
//                                                        .description()
//                                                        .descriptionHtml()
//                                                        .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
//                                                                .edges(imageEdgeQuery -> imageEdgeQuery
//                                                                        .node(imageQuery -> imageQuery
//                                                                                .src()
//                                                                        )
//                                                                )
//                                                        )
//                                                        .tags()
//                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
//                                                                .edges(variantEdgeQuery -> variantEdgeQuery
//                                                                        .node(productVariantQuery -> productVariantQuery
//                                                                                .price()
//                                                                                .title()
//                                                                                .image(args -> args.src())
//                                                                                .weight()
//                                                                                .weightUnit()
//                                                                                .compareAtPrice()
//                                                                                .available()
//                                                                        )
//                                                                )
//                                                        )
//                                                )
//                                        )
//
//
//                                ))));
//
//        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
//            @Override
//            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
//
//
//                List<Storefront.Product> products = new ArrayList<>();
//                Storefront.Collection product = (Storefront.Collection) response.data().getNode();
//
////                if (product.responseData != null) {
////                    if (product.getImage() != null) {
////                        imageurl = product.getImage().getSrc();
////                        Log.e("imagee", "" + imageurl);
////                    }
//
////                for(Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
////                    ProductModel productDetail = new ProductModel();
////                    productDetail.setProduct_Name(productEdge.getNode().getTitle());
////                    productDetail.setProduct_description(productEdge.getNode().getDescription());
////
////                    Log.d("prodcut title : ", productEdge.getNode().getTitle());
////                }
//
//
//                Log.e("subinside", "came");
//
//                for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
//                    Log.e("product_title : ", productEdge.getNode().getTitle());
//                    Log.e("product_ID : ", String.valueOf(productEdge.getNode().getId()));
//
//
//                    ArrayList<Storefront.Image> productImages = new ArrayList<>();
//                    for (final Storefront.ImageEdge imageEdge : productEdge.getNode().getImages().getEdges()) {
//                        productImages.add(imageEdge.getNode());
//                        Log.d("Product Image: ", productImages.get(0).getSrc());
//                        imageurl = productImages.get(0).getSrc();
//                    }
//
//
//                    List<Storefront.ProductVariant> productVariants = new ArrayList<>();
//
//
//                    for (final Storefront.ProductVariantEdge productVariantEdge : productEdge.getNode().getVariants().getEdges()) {
//                        productVariants.add(productVariantEdge.getNode());
//
//
//                        if (productVariantEdge.getNode().getImage() != null)
//                            // productDetail.setImageUrl(productVariantEdge.getNode().getImage().getSrc());
//
//                            Log.d("Product varient Id : ", String.valueOf(productVariantEdge.getNode().getId()));
//                        Log.d("Product title : ", String.valueOf(productVariantEdge.getNode().getTitle()));
//                        Log.d("Product price : ", String.valueOf(productVariantEdge.getNode().getPrice()));
//
//
//                    }
//                }
//            }
//
//
//            @Override
//            public void onFailure(@NonNull GraphError error) {
//
//            }
//        });
//    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Categories");

//        Config.hideKeyboard(Objects.requireNonNull(getActivity()));
    }
}
