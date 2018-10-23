package com.example.user.trendy.Groceries;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.trendy.Bag.Db.AddToCart_Model;
import com.example.user.trendy.Bag.Db.DBHelper;
import com.example.user.trendy.Bag.ShippingAddress;
import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.Category.ProductAdapter;
import com.example.user.trendy.ForYou.ForYou;
import com.example.user.trendy.ForYou.NewArrival.NewArrivalModel;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Interface.CommanCartControler;
import com.example.user.trendy.Navigation;
import com.example.user.trendy.R;
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

public class Groceries extends Fragment implements GroceryAdapter.CartDailog {
    View view;
    int adapter_posi, varient_posi;
    GraphClient graphClient;
    RecyclerView grocery_recycler;
    GroceryAdapter adapter;
    ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();
    LinearLayout title_layout;
    private String productPageCursor = "";
    ArrayList<String> productStringPageCursor = new ArrayList<>();
    int i = 0;
    private String converted;
    DBHelper db;
    TextView itemCount, subTotal, btn_continue, btn_checkout;
    private List<AddToCart_Model> addToCart_modelArrayList = new ArrayList<>();
    RelativeLayout add_to_cart;
    CartController cartController;
    CommanCartControler commanCartControler;
    int cost;
    private ProgressDialog progressDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.grocery, container, false);

        ((Navigation) getActivity()).getSupportActionBar().setTitle("Grocery");

        cartController = new CartController(getActivity());
        commanCartControler = (CommanCartControler) cartController;

        grocery_recycler = view.findViewById(R.id.grocery_recycler);
        title_layout = view.findViewById(R.id.title_layout);
        title_layout.setVisibility(View.GONE);
        String id = "58881703997";
        String text = "gid://shopify/Collection/" + id.trim();
        converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
        Log.e("coverted", converted.trim());

        add_to_cart = view.findViewById(R.id.cart_frame);
        itemCount = view.findViewById(R.id.txt_items);
        subTotal = view.findViewById(R.id.txt_subtotal);

        btn_continue = view.findViewById(R.id.btn_continue_shopping);
        btn_checkout = view.findViewById(R.id.btn_checkout);

        cost = commanCartControler.getTotalPrice();

        addToCart_modelArrayList.clear();
        db = new DBHelper(getActivity());
        addToCart_modelArrayList = db.getCartList();
        Log.e("array", "" + db.getCartList());
        int cart_size = addToCart_modelArrayList.size();
        if (cart_size != 0) {
            add_to_cart.setVisibility(View.VISIBLE);
            itemCount.setText("Items : " + cart_size);
            cost = commanCartControler.getTotalPrice();
            subTotal.setText("SubTotal : ₹ " + cost);

        }


        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        grocery_recycler.setLayoutManager(layoutManager1);
        grocery_recycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new GroceryAdapter(getActivity(), groceryModelArrayList, getFragmentManager(), this);
        grocery_recycler.setAdapter(adapter);

        getCollection(converted.trim());
        adapter.notifyDataSetChanged();

        grocery_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    getNext();
                }
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ForYou();
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "ForYou");
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("collection", "allcollection");
                bundle.putString("totalcost", Integer.toString(cost));
                Fragment fragment = new ShippingAddress();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "fragment");
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.addToBackStack("grocery");
                ft.commit();
            }
        });

        return view;
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    public void getNext() {
        if (productStringPageCursor.size() != 0) {
            getCollectionCursur(converted.trim(), productStringPageCursor.get(productStringPageCursor.size() - 1));
        }

    }


    private void getCollection(String trim) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10), productConnectionQuery -> productConnectionQuery
                                        .pageInfo(pageInfoQuery -> pageInfoQuery
                                                .hasNextPage()
                                                .hasPreviousPage()
                                        )
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .cursor()
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
                                                        .options(option -> option
                                                                .name())
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
                Log.e("pagin", " " + product.getProducts().getPageInfo().getHasNextPage());
                boolean hasNextProductPage = product.getProducts().getPageInfo().getHasNextPage().booleanValue();


                for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {

                    if (i == 0) {
                        for (int i = 0; i < product.getProducts().getEdges().size(); i++) {
                            productPageCursor = productEdge.getCursor().toString();

                            productStringPageCursor.add(productPageCursor);
                        }
                        GroceryModel groceryModel = new GroceryModel();
                        groceryModel.setProduct(productEdge.getNode());
                        groceryModel.setQty("1");
                        groceryModelArrayList.add(groceryModel);
                    }


//                    if (hasNextProductPage) {
//
////                        getCollectionCursur(trim.trim(), productStringPageCursor.get(productStringPageCursor.size()-1));
//                        getCollectionCursur(trim.trim(), productStringPageCursor.get(productStringPageCursor.size() - 1));
//                    }

                }


//                Log.e("groceryModelArrayList", String.valueOf(groceryModelArrayList.size()));
//                Log.e("groceryModelArrayList", String.valueOf(product.getProducts().getEdges().size()));
//                Log.e("productch", ""+product.getProducts().getEdges().get(0).getNode().getOptions().get(0).getName());
//
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getCollectionCursur(String trim, String productCursor) {
        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10).after(productCursor), productConnectionQuery -> productConnectionQuery
                                        .pageInfo(pageInfoQuery -> pageInfoQuery
                                                .hasNextPage()
                                                .hasPreviousPage()
                                        )
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .cursor()
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
                                                        .options(option -> option
                                                                .name())
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
//                Log.e("pagin1"," "+ product.getProducts().getPageInfo().getHasNextPage());
                productStringPageCursor.clear();
                Log.e("pagincursur", " " + productCursor);
                boolean hasNextProductPage = product.getProducts().getPageInfo().getHasNextPage().booleanValue();
                Log.e("hasNextProductPage", " " + hasNextProductPage);
                for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                    if (hasNextProductPage) {
//                        productPageCursor = productEdge.getCursor().toString();
//                        Log.e("pagin11", " " + productEdge.getCursor().toString());
                        Log.e("product_name", " " + productEdge.getNode().getTitle());
                        for (int i = 0; i < product.getProducts().getEdges().size(); i++) {
                            productPageCursor = productEdge.getCursor().toString();

                            productStringPageCursor.add(productPageCursor);
                        }
                        i = 1;
                    }
                    GroceryModel groceryModel = new GroceryModel();
                    groceryModel.setProduct(productEdge.getNode());
                    groceryModel.setQty("1");
                    groceryModelArrayList.add(groceryModel);

                }
//
//                Log.e("groceryModelArrayList", String.valueOf(groceryModelArrayList.size()));
//                Log.e("groceryModelArrayList", String.valueOf(product.getProducts().getEdges().size()));
//                Log.e("productch", ""+product.getProducts().getEdges().get(0).getNode().getOptions().get(0).getName());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }


    @Override
    public void cart(int adapter_pos, int varient_pos, int qty) {
        adapter_posi = adapter_pos;
        varient_posi = varient_pos;

        add_to_cart.setVisibility(View.VISIBLE);
        addToCart_modelArrayList.clear();
        addToCart_modelArrayList = db.getCartList();
        Log.e("array", "" + db.getCartList());


        cost = commanCartControler.getTotalPrice();
        int current_cost = (groceryModelArrayList.get(adapter_pos).getProduct().getVariants().getEdges().get(varient_pos).getNode().getPrice().intValue()) * qty;
        cost = cost + current_cost;
        subTotal.setText("SubTotal : ₹ " + cost);

        int cart_size = addToCart_modelArrayList.size();

        itemCount.setText("Items : " + cart_size);
    }
}