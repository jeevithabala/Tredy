package com.marmeto.user.tredy.notification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.account.orderList.OrderListModel;
import com.marmeto.user.tredy.account.orderList.OrderlistAdapter;
import com.marmeto.user.tredy.account.orders.OrderModel;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.Internet;
import com.marmeto.user.tredy.util.SharedPreference;
import com.marmeto.user.tredy.util.VolleySingleton;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class NotificationDataFragment extends AppCompatActivity {
    String title, id, pnew, orderid;
    TextView pdtitle;
    private GraphClient graphClient;
    private ArrayList<OrderListModel> orderModelArrayList = new ArrayList<>();
    private ArrayList<OrderModel> orderModelArrayList1 = new ArrayList<>();
    private OrderlistAdapter adapter;
    TextView shipping, subtotal, total;
    LinearLayout total_invisible;
    private ProgressDialog progressDoalog;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationdata);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        pnew = intent.getStringExtra("pnew");
        title = intent.getStringExtra("title");
        if (intent.getStringExtra("orderid") != null) {
            orderid = intent.getStringExtra("orderid").trim();
        } else {
            orderid = "";
        }
        pdtitle = findViewById(R.id.pd_title);
        RecyclerView order_recyclerview = findViewById(R.id.order_recyclerview);
        pdtitle.setText(title);

        shipping = findViewById(R.id.shipping);
        subtotal = findViewById(R.id.subtotal);
        total = findViewById(R.id.total);
        total_invisible = findViewById(R.id.total_invisible);


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

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        order_recyclerview.setLayoutManager(layoutManager1);
        order_recyclerview.setItemAnimator(new DefaultItemAnimator());
        adapter = new OrderlistAdapter(this, orderModelArrayList, getSupportFragmentManager());
        order_recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        orderid="40251";
//        String order_id="gid://shopify/Order/" + orderid.trim();
//        String orderid1 = Base64.encodeToString(order_id.trim().getBytes(), Base64.DEFAULT).trim();
//        getOrders(orderid1.trim());
        getOrders();

    }

    private void registperp(String s) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constants.readnotification + s,
                response -> {
                },
                error -> {
//                        progressDialog.dismiss();
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

    private void getOrders() {
        progressDoalog = new ProgressDialog(NotificationDataFragment.this);
        progressDoalog.setMessage("loading....");
        progressDoalog.setTitle("Processing");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();
        String accessToken = SharedPreference.getData("accesstoken", getApplicationContext());

        orderModelArrayList1.clear();
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .orders(arg -> arg.first(300), connection -> connection
                                .pageInfo(pageInfoQuery -> pageInfoQuery
                                        .hasNextPage()
                                        .hasPreviousPage()
                                )
                                .edges(edge -> edge
                                        .cursor()
                                        .node(node -> node
                                                .totalPrice()
                                                .processedAt()
                                                .orderNumber()
                                                .totalPrice()
                                                .email()
                                                .processedAt()
                                                .totalShippingPrice()
                                                .subtotalPrice()
                                                .shippingAddress(address -> address
                                                        .address1()
                                                        .address2()
                                                        .city()
                                                        .country()
                                                        .firstName()
                                                        .lastName())
                                                .lineItems(args -> args.first(10), lineItemsArguments -> lineItemsArguments
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
                                                                                .image(Storefront.ImageQuery::src)
                                                                                .product(Storefront.ProductQuery::title
                                                                                )

                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        QueryGraphCall call = graphClient.queryGraph(query);

        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                assert response.data() != null;
                if (response.data().getCustomer().getOrders() != null) {


                    for (Storefront.OrderEdge order : response.data().getCustomer().getOrders().getEdges()) {

                        OrderModel orderModel = new OrderModel();
                        orderModel.setOrderd(order.getNode());
                        orderModel.setLineitemsize(order.getNode().getLineItems().getEdges().size());
                        orderModelArrayList1.add(orderModel);

                    }

                    runOnUiThread(() -> {
                        progressDoalog.dismiss();
                        for (int i = 0; i < orderModelArrayList1.size(); i++) {
                            String o = "#" + String.valueOf(orderModelArrayList1.get(i).getOrderd().getOrderNumber());
                            if (orderid.equals(o)) {
                                for (int j = 0; j < orderModelArrayList1.get(i).getOrderd().getLineItems().getEdges().size(); j++) {

                                    OrderListModel orderListModel = new OrderListModel();
                                    orderListModel.setId(String.valueOf(orderModelArrayList1.get(i).getOrderd().getOrderNumber()));
                                    orderListModel.setTitle(orderModelArrayList1.get(i).getOrderd().getLineItems().getEdges().get(j).getNode().getVariant().getProduct().getTitle());
                                    if (orderModelArrayList1.get(i).getOrderd().getLineItems().getEdges().get(j).getNode().getVariant().getImage() == null) {
                                        orderListModel.setImage(null);
                                    } else {
                                        orderListModel.setImage(orderModelArrayList1.get(i).getOrderd().getLineItems().getEdges().get(j).getNode().getVariant().getImage().getSrc());
                                    }
                                    orderListModel.setShippingtax(String.valueOf(orderModelArrayList1.get(i).getOrderd().getShippingAddress()));
                                    orderListModel.setSubtotal(String.valueOf(orderModelArrayList1.get(i).getOrderd().getSubtotalPrice()));
                                    orderListModel.setTotalcost(String.valueOf(orderModelArrayList1.get(i).getOrderd().getTotalPrice()));
                                    orderListModel.setQuantity(String.valueOf(orderModelArrayList1.get(i).getOrderd().getLineItems().getEdges().get(j).getNode().getQuantity()));
                                    orderListModel.setProductcost(String.valueOf(orderModelArrayList1.get(i).getOrderd().getLineItems().getEdges().get(j).getNode().getVariant().getPrice()));

                                    shipping.setText(String.valueOf(orderModelArrayList1.get(i).getOrderd().getTotalShippingPrice()));
                                    total.setText(String.valueOf(orderModelArrayList1.get(i).getOrderd().getTotalPrice()));
                                    subtotal.setText(String.valueOf(orderModelArrayList1.get(i).getOrderd().getSubtotalPrice()));
                                    total_invisible.setVisibility(View.VISIBLE);
                                    orderModelArrayList.add(orderListModel);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }


                    });
                } else {
                    runOnUiThread(() -> progressDoalog.dismiss());
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                runOnUiThread(() -> progressDoalog.dismiss());
                Log.e("TAG", "Failed to execute query", error);
            }
        });


    }


}
