package com.marmeto.user.tredy.groceries;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.bag.cartdatabase.DBHelper;
import com.marmeto.user.tredy.bag.ShippingAddress;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.foryou.ForYou;
import com.marmeto.user.tredy.callback.CartController;
import com.marmeto.user.tredy.callback.CommanCartControler;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Groceries extends Fragment implements GroceryAdapter.CartDailog, View.OnClickListener {
    View view;
    int adapter_posi, varient_posi;
    GraphClient graphClient;
    RecyclerView grocery_recycler;
    GroceryAdapter adapter;
    ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();
    LinearLayout title_layout, filter_layout;
    RelativeLayout cartframe;
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
    private int check = 0;
    private String sort_string = "TITLE";
    TextView filter,notfound;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.grocery, container, false);


        cartController = new CartController(getActivity());
        commanCartControler =  cartController;

        grocery_recycler = view.findViewById(R.id.grocery_recycler);
        title_layout = view.findViewById(R.id.title_layout);
        filter = view.findViewById(R.id.filter);
        notfound=view.findViewById(R.id.notfound);
        filter_layout=view.findViewById(R.id.filter_layout);

        title_layout.setVisibility(View.GONE);
        String id = "58881703997";
        String text = "gid://shopify/Collection/" + id.trim();
        converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);


        add_to_cart = view.findViewById(R.id.cart_frame);
        itemCount = view.findViewById(R.id.txt_items);
        subTotal = view.findViewById(R.id.txt_subtotal);

        btn_continue = view.findViewById(R.id.btn_continue_shopping);
        btn_checkout = view.findViewById(R.id.btn_checkout);

        cost = commanCartControler.getTotalPrice();

        addToCart_modelArrayList.clear();
        db = new DBHelper(getActivity());
        addToCart_modelArrayList = db.getCartList();


        int cart_size = 0;
        for (int i = 0; i < addToCart_modelArrayList.size(); i++) {
            cart_size = cart_size + addToCart_modelArrayList.get(i).getQty();
        }

        if (cart_size != 0) {
            add_to_cart.setVisibility(View.VISIBLE);
            itemCount.setText("Items : " + cart_size);
            cost = commanCartControler.getTotalPrice();
            subTotal.setText("SubTotal : ₹ " + cost);

        }


        graphClient = GraphClient.builder(Objects.requireNonNull(getActivity()))
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        grocery_recycler.setLayoutManager(layoutManager1);
        grocery_recycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new GroceryAdapter(getActivity(), groceryModelArrayList, getFragmentManager(), this);
        grocery_recycler.setAdapter(adapter);

        getCollection(converted.trim(), sort_string);
        adapter.notifyDataSetChanged();

        grocery_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    getNext();
                }
            }
        });

        btn_continue.setOnClickListener(view -> {
            Fragment fragment = new ForYou();
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "ForYou");
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            if (getFragmentManager().findFragmentByTag("ForYou") == null) {
                ft.addToBackStack("ForYou");
                ft.commit();
            } else {
                ft.commit();
            }
        });

        btn_checkout.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putString("collection", "allcollection");
            bundle.putString("totalcost", Integer.toString(cost));
            Fragment fragment = new ShippingAddress();
            fragment.setArguments(bundle);
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "fragment");
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            if (getFragmentManager().findFragmentByTag("fragment") == null) {
                ft.addToBackStack("fragment");
                ft.commit();
            } else {
                ft.commit();
            }

        });
        filter.setOnClickListener(this);
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }


    public void getNext() {
        if (productStringPageCursor.size() != 0) {
            getCollectionCursur(converted.trim(), productStringPageCursor.get(productStringPageCursor.size() - 1), sort_string);
        }

    }


    private void getCollection(String trim, String sort) {
        groceryModelArrayList.clear();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10).sortKey(Storefront.ProductCollectionSortKeys.valueOf(sort)), productConnectionQuery -> productConnectionQuery
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
                                                                                .sku()
                                                                                .availableForSale()
                                                                                .selectedOptions(Storefront.SelectedOptionQuery::name)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )


                                ))));


        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                assert response.data() != null;
                if(response.data().getNode()!=null){
                Storefront.Collection product = (Storefront.Collection) response.data().getNode();
//                Log.e("pagin", " " + product.getProducts().getPageInfo().getHasNextPage());
//                boolean hasNextProductPage = product.getProducts().getPageInfo().getHasNextPage().booleanValue();

                for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {

                    if (i == 0) {
                        for (int i = 0; i < product.getProducts().getEdges().size(); i++) {
                            productPageCursor = productEdge.getCursor();

                            productStringPageCursor.add(productPageCursor);
                        }
                        if ((!productEdge.getNode().getVariants().getEdges().get(0).getNode().getPrice().toString().trim().equals("0.00"))) {
                            GroceryModel groceryModel = new GroceryModel();
                            groceryModel.setProduct(productEdge.getNode());
                            groceryModel.setQty("1");
                            addToCart_modelArrayList.clear();
                            addToCart_modelArrayList = db.getCartList();
//                        Log.e("array", "" + db.getCartList());
                            for (int j = 0; j < addToCart_modelArrayList.size(); j++) {
                                if (addToCart_modelArrayList.get(j).getProduct_id().trim().equals(productEdge.getNode().getId().toString())) {
                                    groceryModel.setVisible("true");
                                }
                            }

                            groceryModelArrayList.add(groceryModel);
                        }

                    }

                }
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    notfound.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    adapter.notifyDataSetChanged();
                });
            }else {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        notfound.setVisibility(View.VISIBLE);
                        filter_layout.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    });
                }
        }
            @Override
            public void onFailure(@NonNull GraphError error) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> progressDialog.dismiss());
            }
        });
    }

    private void getCollectionCursur(String trim, String productCursor, String sort) {
        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10).after(productCursor).sortKey(Storefront.ProductCollectionSortKeys.valueOf(sort)), productConnectionQuery -> productConnectionQuery
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
                                                                        )
                                                                )
                                                        )
                                                )
                                        )


                                ))));

        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                assert response.data() != null;
                if(response.data().getNode()!=null) {
                    Storefront.Collection product = (Storefront.Collection) response.data().getNode();
//                Log.e("pagin1"," "+ product.getProducts().getPageInfo().getHasNextPage());
                    productStringPageCursor.clear();
                    boolean hasNextProductPage = product.getProducts().getPageInfo().getHasNextPage();
                    for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                        if (hasNextProductPage) {
//                        productPageCursor = productEdge.getCursor().toString();
//                        Log.e("pagin11", " " + productEdge.getCursor().toString());
                            for (int i = 0; i < product.getProducts().getEdges().size(); i++) {
                                productPageCursor = productEdge.getCursor();

                                productStringPageCursor.add(productPageCursor);
                            }
                            i = 1;
                        }
                        if ((!productEdge.getNode().getVariants().getEdges().get(0).getNode().getPrice().toString().trim().equals("0.00"))) {
                            GroceryModel groceryModel = new GroceryModel();
                            groceryModel.setProduct(productEdge.getNode());
                            groceryModel.setQty("1");
                            addToCart_modelArrayList.clear();
                            addToCart_modelArrayList = db.getCartList();
                            for (int j = 0; j < addToCart_modelArrayList.size(); j++) {
                                if (addToCart_modelArrayList.get(j).getProduct_id().trim().equals(productEdge.getNode().getId().toString())) {
                                    groceryModel.setVisible("true");
                                }
                            }
                            groceryModelArrayList.add(groceryModel);
                        }

                    }


                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void cart(int adapter_pos, int varient_pos, int qty) {
        adapter_posi = adapter_pos;
        varient_posi = varient_pos;

        add_to_cart.setVisibility(View.VISIBLE);
        addToCart_modelArrayList.clear();
        addToCart_modelArrayList = db.getCartList();
//        for (int j = 0; j < addToCart_modelArrayList.size(); j++) {
//            if (addToCart_modelArrayList.get(j).getProduct_id().equals(groceryModelArrayList.get(adapter_pos).getProduct().getId().toString())) {
//            }
//
//        }


        cost = commanCartControler.getTotalPrice();
        int current_cost = (groceryModelArrayList.get(adapter_pos).getProduct().getVariants().getEdges().get(varient_pos).getNode().getPrice().intValue()) * qty;
        cost = cost + current_cost;
        subTotal.setText("SubTotal : ₹ " + cost);


        int cart_size = 0;
        for (int i = 0; i < addToCart_modelArrayList.size(); i++) {
            cart_size = cart_size + addToCart_modelArrayList.get(i).getQty();
        }

        if (cart_size != 0) {
            add_to_cart.setVisibility(View.VISIBLE);
            itemCount.setText("Items : " + cart_size);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter:
                final CharSequence[] relevance = new String[]{"Best Selling",  "Lowest Price", "Relevance", "Product Title A - Z", "Manual"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select");
                builder.setSingleChoiceItems(relevance, check, (dialogInterface, value) -> {
                    dialogInterface.cancel();
                    if (value == 0) {
                        sort_string = "BEST_SELLING";
                    } else if (value == 1) {
                        sort_string = "PRICE";
                    } else if (value == 2) {
                        sort_string = "RELEVANCE";
                    } else if (value == 3) {
                        sort_string = "TITLE";
                    } else if (value == 4) {
                        sort_string = "MANUAL";
                    } else {
                        sort_string = "BEST_SELLING";
                    }
                    //   sort_string = relevance[i].toString();
                    check = value;
                    i=0;
                    getCollection(converted.trim(), sort_string);
                    adapter.notifyDataSetChanged();

                });
                AlertDialog alertDialog1 = builder.create();
                alertDialog1.show();

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Grocery");

    }
}