package com.marmeto.user.tredy.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.account.orderList.OrderList;
import com.marmeto.user.tredy.account.orderList.OrderListModel;
import com.marmeto.user.tredy.account.orderList.OrderlistAdapter;
import com.marmeto.user.tredy.account.orders.OrderAdapter;
import com.marmeto.user.tredy.account.orders.OrderModel;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.Internet;
import com.marmeto.user.tredy.util.VolleySingleton;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class NotificationDataFragment extends AppCompatActivity {
    //implements FragmentManager.OnBackStackChangedListener
    String title, dec = "", date = "", name, id, pnew, pread, orderid;
    ImageView imageView;
    TextView textView, pdtitle;
    private GraphClient graphClient;
    private ArrayList<OrderListModel> orderModelArrayList = new ArrayList<>();
    private OrderlistAdapter adapter;
    private RecyclerView order_recyclerview;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationdata);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        pnew = intent.getStringExtra("pnew");
        title = intent.getStringExtra("title");
        orderid = intent.getStringExtra("orderid").trim();
        pdtitle = (TextView) findViewById(R.id.pd_title);
        order_recyclerview = findViewById(R.id.order_recyclerview);
        pdtitle.setText(title);

        if (pnew.equals("null")) {

            if (Internet.isConnected(getApplicationContext())) {
                registperp(id);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

        graphClient = GraphClient.builder(this)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        order_recyclerview.setLayoutManager(layoutManager1);
//        order_recyclerview.setItemAnimator(new DefaultItemAnimator());
//        adapter = new OrderlistAdapter(this, orderModelArrayList, getSupportFragmentManager());
//        order_recyclerview.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        String order_id="gid://shopify/Order/" + orderid.trim();
//        String orderid1 = Base64.encodeToString(order_id.trim().getBytes(), Base64.DEFAULT).trim();
//        getOrders(orderid1.trim());


    }

    private void registperp(String s) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constants.readnotification + s,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

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
        stringRequest.setTag("read");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (VolleySingleton.getInstance(this).getRequestQueue() != null) {
            VolleySingleton.getInstance(this).getRequestQueue().cancelAll("read");
            VolleySingleton.getInstance(this).getRequestQueue().cancelAll("noti");

        }
    }

    private void getOrders(String id) {

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(id.trim()), nodeQuery -> nodeQuery
                                .onOrder(ar -> ar

                                                .totalPrice()
                                                .orderNumber()
                                                .processedAt()
                                                .lineItems(args -> args
                                                                .first(10),
                                                        lineItemsArguments -> lineItemsArguments
                                                                .edges(orderLineItemEdgeQuery -> orderLineItemEdgeQuery
                                                                                .node(orderLineItemQuery -> orderLineItemQuery
                                                                                                .quantity()
                                                                                                .customAttributes(attributeQuery -> attributeQuery.key().value())
                                                                                                .variant(productVariantQuery -> productVariantQuery
                                                                                                                .title()
                                                                                                                .price()
                                                                                                                .sku()
                                                                                                                .weight()
                                                                                                                .weightUnit()
                                                                                                                .image(image -> image.src())
                                                                                                                .product(produt1 -> produt1
                                                                                                                        .title()
                                                                                                                )
//

                                                                                                )
                                                                                )
                                                                )
                                                )
                                ))
        );
        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {

            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if (response.data() != null && response.data().getNode() != null) {
                    Storefront.Order order1 = (Storefront.Order) response.data().getNode();


                    for (Storefront.OrderLineItemEdge orderEdge : order1.getLineItems().getEdges()) {
                        OrderListModel orderlist = new OrderListModel();
                        orderlist.setTitle(orderEdge.getNode().getTitle());
                        orderlist.setTotalcost(String.valueOf(order1.getTotalPrice()));
                        //                        orderModel.setLineitemsize(order.getNode().getLineItems().getEdges().size());
                        orderModelArrayList.add(orderlist);


                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });


                }
                else {
                    Log.e("lk","l");
                }
                return;

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e("TAG", "Failed to execute query", error);
            }
        });
    }

}
