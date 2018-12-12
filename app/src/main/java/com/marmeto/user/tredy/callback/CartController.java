package com.marmeto.user.tredy.callback;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.bag.cartdatabase.DBHelper;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.category.productDetail.SelectItemModel;
import com.marmeto.user.tredy.whislist.AddWhislistModel;
import com.marmeto.user.tredy.whislist.whislistDB.DBWhislist;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CartController extends ViewModel implements CommanCartControler {

    DBHelper db;
    DBWhislist dbWhislist;
    SelectItemModel model;
    Context mContext;
    private List<AddToCart_Model> cartList = new ArrayList<>();
    private List<AddWhislistModel> whislist = new ArrayList<>();
    GraphClient graphClient;

    public CartController(Context mContext) {
        this.mContext = mContext;
        db = new DBHelper(mContext);
        dbWhislist = new DBWhislist(mContext);


    }

    @Override
    public void AddToCart(String id) {
        cartList.clear();

        cartList = db.getCartList();

        Log.e("plus", "plus");
        String text = "gid://shopify/Product/" + id.trim();

        String converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        Log.e("coverted", converted.trim());

        getProductVariantID(converted.trim());
    }

    @Override
    public void AddQuantity(String id) {
//        String getquantity = db.getQuantity(id.trim());
//        Log.e("getquantity", getquantity);
        db.update(id.trim(), 1);

    }


    @Override
    public void RemoveQuantity(String id) {
        db.decreaseqty(id);
    }

    @Override
    public int getTotalPrice() {
        cartList.clear();

        cartList = db.getCartList();
        int totalcost1 = 0;
        for (int i = 0; i < cartList.size(); i++) {
            int qty = cartList.get(i).getQty();
            Double cost = cartList.get(i).getProduct_price();
            Log.e("qty", "" + String.valueOf(qty));
            Log.e("cost", "" + String.valueOf(cost));
            Log.e("icost", "" + String.valueOf(totalcost1));
            totalcost1 = totalcost1 + (qty * (cost.intValue()));
            Log.e("cost", "" + String.valueOf(totalcost1));
        }
        return totalcost1;
    }

    @Override
    public int getItemCount() {
        cartList.clear();

        cartList = db.getCartList();
        return cartList.size();
    }

    @Override
    public void UpdateShipping(String id, String value) {

        db.updateshipping(id.trim(), value);
    }

    @Override
    public void AddToWhislist(String id) {
        whislist.clear();

        whislist = dbWhislist.getCartList();

        Log.e("plus", "plus");
        String text = "gid://shopify/Product/" + id.trim();

        String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
        Log.e("coverted", converted.trim());

        getProductVariantID1(converted.trim());
    }

    @Override
    public void AddToCartGrocery(String trim, int selectedID, int qty) {
        cartList.clear();

        cartList = db.getCartList();
        getProductVariantIDgrocery(trim.trim(),selectedID, qty);
    }

    private void getProductVariantIDgrocery(String trim, int selectedID, int qty) {
        GraphClient graphClient = GraphClient.builder(mContext)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(mContext.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();
        Log.e("id,"," "+trim);

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim), nodeQuery -> nodeQuery
                        .onProduct(productQuery -> productQuery
                                .title()
                                .description()
                                .descriptionHtml()
                                .tags()
                                .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                        .edges(imageEdgeQuery -> imageEdgeQuery
                                                .node(imageQuery -> imageQuery
                                                        .src()
                                                )
                                        )
                                )
                                .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                        .edges(variantEdgeQuery -> variantEdgeQuery
                                                .node(productVariantQuery -> productVariantQuery
                                                        .price()
                                                        .title()
                                                        .compareAtPrice()
                                                        .availableForSale()
                                                        .image(args -> args.src())
                                                        .weight()
                                                        .weightUnit()
                                                        .available()
                                                        .selectedOptions(Storefront.SelectedOptionQuery::name)
                                                )
                                        )
                                )
                        )
                )
        );

        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {

            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {


                if (response.data() != null) {
                    Storefront.Product product = (Storefront.Product) response.data().getNode();
//                    Log.e("titit", product.getTitle());
                    model = new SelectItemModel();
                    model.setProduct(product);
                    model.setShip("true");
                    Log.e("tttyt", String.valueOf(model.getProduct().getTags()));

                    List<Storefront.ProductVariant> productVariant = new ArrayList<>();
                    for (final Storefront.ProductVariantEdge productVariantEdge : product.getVariants().getEdges()) {
                        productVariant.add(productVariantEdge.getNode()
                        );
//
                        Log.d("Product varient Id : ", String.valueOf(productVariantEdge.getNode().getId()));


                    }
                    String available = productVariant.get(selectedID).getAvailableForSale().toString();
                    Log.e("available", available);
                    if (productVariant.get(0).getAvailableForSale()) {
                        if (cartList.size() == 0) {
                            Log.e("empty", "empty");
//                            int qty = 1;
                            db.insertToDo(trim.trim(),productVariant.get(selectedID), qty, model.getProduct().getTitle(), String.valueOf(model.getProduct().getTags()), model.getShip());
                        } else {

//                            db.checkUser(productVariant.get(0).getId().toString().trim());
                            if (db.checkUser(productVariant.get(selectedID).getId().toString().trim())) {

                                db.update(productVariant.get(selectedID).getId().toString().trim(), qty);
                            } else {
                                db.insertToDo(trim.trim(),productVariant.get(selectedID), qty, model.getProduct().getTitle(), String.valueOf(model.getProduct().getTags()), model.getShip());
                            }
                        }

                    }
                }
                return;

            }

            @Override
            public void onFailure(@NonNull GraphError error) {

//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void getProductVariantID1(String productID) {

        GraphClient graphClient = GraphClient.builder(mContext)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(mContext.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(productID), nodeQuery -> nodeQuery
                        .onProduct(productQuery -> productQuery
                                .title()
                                .description()
                                .descriptionHtml()
                                .tags()
                                .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                        .edges(imageEdgeQuery -> imageEdgeQuery
                                                .node(imageQuery -> imageQuery
                                                        .src()
                                                )
                                        )
                                )
                                .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                        .edges(variantEdgeQuery -> variantEdgeQuery
                                                .node(productVariantQuery -> productVariantQuery
                                                        .price()
                                                        .title()
                                                        .compareAtPrice()
                                                        .availableForSale()
                                                        .image(args -> args.src())
                                                        .weight()
                                                        .weightUnit()
                                                        .available()
                                                        .selectedOptions(Storefront.SelectedOptionQuery::name)
                                                )
                                        )
                                )
                        )
                )
        );

        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {

            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {


                if (response.data() != null) {
                    Storefront.Product product = (Storefront.Product) response.data().getNode();
//                    Log.e("titit", product.getTitle());
                    model = new SelectItemModel();
                    model.setProduct(product);
                    model.setShip("true");
                    Log.e("tttyt", String.valueOf(model.getProduct().getTags()));

                    List<Storefront.ProductVariant> productVariant = new ArrayList<>();
                    for (final Storefront.ProductVariantEdge productVariantEdge : product.getVariants().getEdges()) {
                        productVariant.add(productVariantEdge.getNode()
                        );
//
                        Log.d("Product varient Id : ", String.valueOf(productVariantEdge.getNode().getId()));


                    }
                    String available = productVariant.get(0).getAvailableForSale().toString();
                    Log.e("available", available);
                    if (productVariant.get(0).getAvailableForSale()) {
                        if (whislist.size() == 0) {
                            Log.e("empty", "empty");
                            int qty = 1;
                            dbWhislist.insertToDo(productID.trim(),productVariant.get(0), model.getProduct().getTitle());
                        } else {

//                            db.checkUser(productVariant.get(0).getId().toString().trim());
                            if (dbWhislist.checkUser(productVariant.get(0).getId().toString().trim())) {

//                                db.update(productVariant.get(0).getId().toString().trim(), 1);
                            } else {
                                dbWhislist.insertToDo(productID.trim(),productVariant.get(0), model.getProduct().getTitle());
                            }
                        }

                    }
                }
                return;

            }

            @Override
            public void onFailure(@NonNull GraphError error) {

//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }


        });


    }

    private void getProductVariantID(String productID) {

        graphClient= GraphClient.builder(mContext)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(mContext.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(productID), nodeQuery -> nodeQuery
                        .onProduct(productQuery -> productQuery
                                .title()
                                .description()
                                .descriptionHtml()
                                .tags()
                                .productType()
                                .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                        .edges(imageEdgeQuery -> imageEdgeQuery
                                                .node(imageQuery -> imageQuery
                                                        .src()
                                                )
                                        )
                                )
                                .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                        .edges(variantEdgeQuery -> variantEdgeQuery
                                                .node(productVariantQuery -> productVariantQuery
                                                        .price()
                                                        .title()
                                                        .compareAtPrice()
                                                        .availableForSale()
                                                        .image(args -> args.src())
                                                        .weight()
                                                        .weightUnit()
                                                        .available()
                                                        .selectedOptions(Storefront.SelectedOptionQuery::name)
                                                )
                                        )
                                )
                        )
                )
        );

        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {

            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {


                if (response.data() != null) {
//                    Log.e("productid",productID);
//                    Log.e("reponse"," "+response.data().toString());
//                    Log.e("reponse"," "+response.data().getNode());
                    Storefront.Product product = (Storefront.Product) response.data().getNode();
//                    Log.e("titit", " "+product.getTitle());
                    model = new SelectItemModel();
                    model.setProduct(product);
                    model.setShip("true");
//                    Log.e("tttyt", String.valueOf(model.getProduct().getTags()));

                    List<Storefront.ProductVariant> productVariant = new ArrayList<>();
                    for (final Storefront.ProductVariantEdge productVariantEdge : product.getVariants().getEdges()) {
                        productVariant.add(productVariantEdge.getNode()
                        );

                    }

                    if (productVariant.get(0).getAvailableForSale()) {
                        if (cartList.size() == 0) {
                            Log.e("empty", "empty");
                            int qty = 1;
                            db.insertToDo(productID.trim(),productVariant.get(0), qty, model.getProduct().getTitle(), String.valueOf(model.getProduct().getTags()), model.getShip());
                        } else {

//                            db.checkUser(productVariant.get(0).getId().toString().trim());
                            if (db.checkUser(productVariant.get(0).getId().toString().trim())) {

                                db.update(productVariant.get(0).getId().toString().trim(), 1);
                            } else {
                                db.insertToDo(productID.trim(),productVariant.get(0), 1, model.getProduct().getTitle(), String.valueOf(model.getProduct().getTags()), model.getShip());
                            }
                        }

                    }
                }
                return;

            }

            @Override
            public void onFailure(@NonNull GraphError error) {

//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }


        });


    }

}