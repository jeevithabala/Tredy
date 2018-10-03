package com.example.user.trendy.Groceries;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.Category.ProductAdapter;
import com.example.user.trendy.ForYou.NewArrival.NewArrivalModel;
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
import java.util.concurrent.TimeUnit;

public class Groceries extends Fragment {
    View view;
    GraphClient graphClient;
    RecyclerView grocery_recycler;
    GroceryAdapter adapter;
    ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();
    LinearLayout title_layout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.grocery, container, false);


        grocery_recycler = view.findViewById(R.id.grocery_recycler);
        title_layout=view.findViewById(R.id.title_layout);
        title_layout.setVisibility(View.GONE);
        String id = "58881703997";
        String text = "gid://shopify/Collection/" + id.trim();
        String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
        Log.e("coverted", converted.trim());


        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        grocery_recycler.setLayoutManager(layoutManager1);
        grocery_recycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new GroceryAdapter(getActivity(), groceryModelArrayList, getFragmentManager());
        grocery_recycler.setAdapter(adapter);

        getCollection(converted.trim());
        adapter.notifyDataSetChanged();

        return view;
    }

    private void getCollection(String trim) {
        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(150),productConnectionQuery -> productConnectionQuery
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
                                                        .options(option->option
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

                for(Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                    GroceryModel groceryModel = new GroceryModel();
                    groceryModel.setProduct(productEdge.getNode());
                    groceryModel.setQty("1");
                    groceryModelArrayList.add(groceryModel);
                }

                Log.e("groceryModelArrayList", String.valueOf(groceryModelArrayList.size()));
                Log.e("groceryModelArrayList", String.valueOf(product.getProducts().getEdges().size()));
                Log.e("productch", ""+product.getProducts().getEdges().get(0).getNode().getOptions().get(0).getName());

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

}
