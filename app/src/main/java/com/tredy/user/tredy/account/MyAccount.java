package com.tredy.user.tredy.account;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tredy.user.tredy.BuildConfig;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.util.SharedPreference;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MyAccount extends Fragment {
    LinearLayout  edit_profile;
    String accessToken;
    private GraphClient graphClient;
    TextView name, email, mobile_number;
    String nametext, emailtext, mobiletext, firstname, lastname;
//    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
//    OrderAdapter adapter;
//    ArrayList<String> productStringPageCursor = new ArrayList<>();
//    private String productPageCursor = "";
//    private int i = 0;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.myaccount, container, false);

        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Account");

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        mobile_number = view.findViewById(R.id.mobile_number);
        edit_profile = view.findViewById(R.id.edit_profile);
//        order_recyclerview = view.findViewById(R.id.order_recyclerview);
//        order = view.findViewById(R.id.order);

        accessToken = SharedPreference.getData("accesstoken", getActivity());
        Log.e("accestoken", "" + accessToken);
        graphClient = GraphClient.builder(getActivity())
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

        if (accessToken != null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("loading, please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                getEmailId();
            } else {
                Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
            }

        }


        edit_profile.setOnClickListener(view -> {
            Fragment fragment = new MyAccountEdit();
            Bundle bundle = new Bundle();
            bundle.putString("firstname", firstname);
            bundle.putString("lastname", lastname);
            bundle.putString("mobile", mobiletext);
            bundle.putString("email", emailtext);
            fragment.setArguments(bundle);

            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.home_container, fragment, "accountedit");
            if (getFragmentManager().findFragmentByTag("accountedit") == null) {
                transaction.addToBackStack("accountedit");
                transaction.commit();
            } else {
                transaction.commit();
            }

        });


//        if (accessToken != null) {
//            getOrders();
//        }
//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        order_recyclerview.setLayoutManager(layoutManager1);
//        order_recyclerview.setItemAnimator(new DefaultItemAnimator());
//
//
//        adapter = new OrderAdapter(getActivity(), orderModelArrayList, getFragmentManager());
//        order_recyclerview.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//
//
//        order_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                    getNext();
//                }
//            }
//        });
//    }
//
//    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
//        if (recyclerView.getAdapter().getItemCount() != 0) {
//            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
//                return true;
//        }
//        return false;
    }


//    public void getNext() {
//        if (productStringPageCursor.size() != 0) {
//            getNextOrders(accessToken.trim(), productStringPageCursor.get(productStringPageCursor.size() - 1));
//        }
//
//    }


    public void getEmailId() {

        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .firstName()
                        .lastName()
                        .email()
                        .phone()
                        .displayName()
                        .id()
                )
        );

        QueryGraphCall call = graphClient.queryGraph(query);

        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                if (response.data() != null && response.data().getCustomer() != null) {
                    firstname = response.data().getCustomer().getFirstName();
                    lastname = response.data().getCustomer().getLastName();
                    nametext = response.data().getCustomer().getFirstName() + "" + response.data().getCustomer().getLastName();
                    emailtext = "" + response.data().getCustomer().getEmail();
                    mobiletext = "" + response.data().getCustomer().getPhone();
                    if (mobiletext.trim().length() != 0) {
                        SharedPreference.saveData("mobile", mobiletext.trim(), Objects.requireNonNull(getActivity()));
                    }

                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (nametext != null) {
                            name.setText(firstname + " " + lastname);
                            email.setText(emailtext.trim());
                            if (mobiletext.trim().equals("null")) {
                                mobiletext = "";
                                mobile_number.setText(mobiletext);
                            } else {
                                mobile_number.setText(mobiletext.trim());
                            }
                        }

                    });
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                progressDialog.dismiss();
                Log.e("TAG", "Failed to execute query", error);
            }
        });

    }

//    private void getOrders() {
//        orderModelArrayList.clear();
//        Storefront.QueryRootQuery query = Storefront.query(root -> root
//                        .customer(accessToken, customer -> customer
//                                        .orders(arg -> arg.first(10), connection -> connection
//                                                        .pageInfo(pageInfoQuery -> pageInfoQuery
//                                                                .hasNextPage()
//                                                                .hasPreviousPage()
//                                                        )
//                                                        .edges(edge -> edge
//                                                                        .cursor()
//                                                                        .node(node -> node
//
//                                                                                        .totalPrice()
//                                                                                        .processedAt()
//                                                                                        .orderNumber()
//                                                                                        .totalPrice()
//                                                                                        .email()
//                                                                                        .processedAt()
//                                                                                        .totalShippingPrice()
//                                                                                        .subtotalPrice()
//                                                                                        .shippingAddress(address -> address
//                                                                                                .address1()
//                                                                                                .address2()
//                                                                                                .city()
//                                                                                                .country()
//                                                                                                .firstName()
//                                                                                                .lastName())
//                                                                                        .lineItems(args -> args.first(10), lineItemsArguments -> lineItemsArguments
//                                                                                                        .edges(orderLineItemEdgeQuery -> orderLineItemEdgeQuery
//                                                                                                                        .node(orderLineItemQuery -> orderLineItemQuery
//                                                                                                                                        .quantity()
//                                                                                                                                        .customAttributes(attributeQuery -> attributeQuery.key().value())
//                                                                                                                                        .variant(productVariantQuery -> productVariantQuery
//                                                                                                                                                        .title()
//                                                                                                                                                        .price()
//                                                                                                                                                        .sku()
//                                                                                                                                                        .weight()
//                                                                                                                                                        .weightUnit()
//                                                                                                                                                        .image(image -> image.src())
//                                                                                                                                                        .product(produt1 -> produt1
//                                                                                                                                                                .title()
//                                                                                                                                                        )
////                                                                                        .tags()
////                                                                                        .images(image->image
////                                                                                        .edges(imageedge->imageedge
////                                                                                        .node(imagenode->imagenode
////                                                                                        .src()
////                                                                                        .id())))
//
//                                                                                                                                        )
//                                                                                                                        )
//                                                                                                        )
//                                                                                        )
//                                                                        )
//                                                        )
//                                        )
//                        )
//        );
//        QueryGraphCall call = graphClient.queryGraph(query);
//
//        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
//            @Override
//            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
////                Log.e("data", "user..." + response.data().getCustomer().getOrders().getEdges().get(0).getNode().getOrderNumber());
////                Log.e("data", "user..." + response.data().getCustomer().getOrders().getEdges().get(0).getNode().getLineItems().getEdges().get(0).getNode().getVariant().getProduct().getTitle());
////                Log.e("came", "inside");
////                Log.e("data", "user..." + response.data().getCustomer().getOrders().toString());
//
//                if (response.data().getCustomer().getOrders() != null) {
//
//
//                    for (Storefront.OrderEdge order : response.data().getCustomer().getOrders().getEdges()) {
//                        if (i == 0) {
//                            for (int i = 0; i < order.getNode().getLineItems().getEdges().size(); i++) {
//                                productPageCursor = order.getCursor().toString();
//
//                                productStringPageCursor.add(productPageCursor);
//                            }
//                            OrderModel orderModel = new OrderModel();
//                            orderModel.setOrderd(order.getNode());
//                            orderModel.setLineitemsize(order.getNode().getLineItems().getEdges().size());
//                            orderModelArrayList.add(orderModel);
//                        }
//
//                    }
//
//                    Log.e("orderModelArrayList", String.valueOf(orderModelArrayList.size()));
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (orderModelArrayList.size() == 0) {
//                                order.setVisibility(View.VISIBLE);
//
//                            } else {
//                                Log.e("came", "inside");
//                                adapter.notifyDataSetChanged();
//                            }
//
//                        }
//                    });
//                } else {
//                    order.setVisibility(View.VISIBLE);
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull GraphError error) {
//                Log.e("TAG", "Failed to execute query", error);
//            }
//        });
//
//
//    }

//    private void getNextOrders(String accessToken, String productCursor) {
//        orderModelArrayList.clear();
//        Storefront.QueryRootQuery query = Storefront.query(root -> root
//                        .customer(accessToken, customer -> customer
//                                        .orders(arg -> arg.first(10).after(productCursor), connection -> connection
//                                                        .pageInfo(pageInfoQuery -> pageInfoQuery
//                                                                .hasNextPage()
//                                                                .hasPreviousPage()
//                                                        )
//                                                        .edges(edge -> edge
//
//                                                                        .node(node -> node
//
//
//                                                                                        .totalPrice()
//                                                                                        .processedAt()
//                                                                                        .orderNumber()
//                                                                                        .totalPrice()
//                                                                                        .email()
//                                                                                        .processedAt()
//                                                                                        .totalShippingPrice()
//                                                                                        .subtotalPrice()
//                                                                                        .shippingAddress(address -> address
//                                                                                                .address1()
//                                                                                                .address2()
//                                                                                                .city()
//                                                                                                .country()
//                                                                                                .firstName()
//                                                                                                .lastName())
//                                                                                        .lineItems(args -> args.first(10), lineItemsArguments -> lineItemsArguments
//                                                                                                        .edges(orderLineItemEdgeQuery -> orderLineItemEdgeQuery
//                                                                                                                        .node(orderLineItemQuery -> orderLineItemQuery
//                                                                                                                                        .quantity()
//                                                                                                                                        .customAttributes(attributeQuery -> attributeQuery.key().value())
//                                                                                                                                        .variant(productVariantQuery -> productVariantQuery
//                                                                                                                                                        .title()
//                                                                                                                                                        .price()
//                                                                                                                                                        .sku()
//                                                                                                                                                        .weight()
//                                                                                                                                                        .weightUnit()
//                                                                                                                                                        .image(Storefront.ImageQuery::src)
//                                                                                                                                                        .product(Storefront.ProductQuery::title
//                                                                                                                                                        )
////                                                                                        .tags()
////                                                                                        .images(image->image
////                                                                                        .edges(imageedge->imageedge
////                                                                                        .node(imagenode->imagenode
////                                                                                        .src()
////                                                                                        .id())))
//
//                                                                                                                                        )
//                                                                                                                        )
//                                                                                                        )
//                                                                                        )
//                                                                        )
//                                                        )
//                                        )
//                        )
//        );
//        QueryGraphCall call = graphClient.queryGraph(query);
//
//        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
//            @Override
//            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
////
//                if (response.data().getCustomer().getOrders() != null) {
//
//                    productStringPageCursor.clear();
//                    Log.e("pagincursur", " " + productCursor);
//                    boolean hasNextProductPage = response.data().getCustomer().getOrders().getPageInfo().getHasNextPage().booleanValue();
//                    Log.e("hasNextProductPage", " " + hasNextProductPage);
//
//                    for (Storefront.OrderEdge order : response.data().getCustomer().getOrders().getEdges()) {
//                        if (hasNextProductPage) {
//                            for (int i = 0; i < order.getNode().getLineItems().getEdges().size(); i++) {
//                                productPageCursor = order.getCursor().toString();
//
//                                productStringPageCursor.add(productPageCursor);
//                            }
//                            i = 1;
//                        }
//
//                        OrderModel orderModel = new OrderModel();
//                        orderModel.setOrderd(order.getNode());
//                        orderModel.setLineitemsize(order.getNode().getLineItems().getEdges().size());
//                        orderModelArrayList.add(orderModel);
//                    }
//                }
//
//                Log.e("orderModelArrayList", String.valueOf(orderModelArrayList.size()));
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        adapter.notifyDataSetChanged();
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(@NonNull GraphError error) {
//                Log.e("TAG", "Failed to execute query", error);
//            }
//        });
//
//
//    }


}
