package com.tredy.user.tredy.category.productDetail;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tredy.user.tredy.Tawk;
import com.tredy.user.tredy.bag.Bag;
import com.tredy.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.tredy.user.tredy.BuildConfig;
import com.tredy.user.tredy.category.model.ProductModel;
import com.tredy.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.tredy.user.tredy.foryou.newarrival.NewArrivalModel;
import com.tredy.user.tredy.foryou.topcollection.TopCollectionModel;
import com.tredy.user.tredy.foryou.topselling.TopSellingModel;
import com.tredy.user.tredy.groceries.GroceryModel;
import com.tredy.user.tredy.callback.CartController;
import com.tredy.user.tredy.callback.CommanCartControler;
import com.tredy.user.tredy.callback.ProductClickInterface;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.whislist.AddWhislistModel;
import com.tredy.user.tredy.databinding.ProductViewBinding;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProductView extends Fragment implements ProductClickInterface {
    SelectItemModel itemModel = new SelectItemModel();
    RecyclerView recyclerView;
    ArrayList<Storefront.Image> itemsList = new ArrayList<>();
    ProductViewBinding productViewBinding;
    TextView product_price, sku, veg_text;
    RadioButton rbn;
    LinearLayout veg, eggless, fatfree;
    EditText count;
    String mHtmlString, product = "";
    private String id;
    private GraphClient graphClient;
    View view;
    Button bag_button, buy;
    CartController cartController;
    CommanCartControler commanCartControler;
//    String selectedweight = "";
    int selectedID = 0;
    RadioGroup radioGroup;
    String no_of_count;
    private ImageView veg_image;
    private FloatingActionButton chat_button;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        productViewBinding = DataBindingUtil.inflate(inflater, R.layout.product_view, container, false);
        view = productViewBinding.getRoot();


        //  final View view = inflater.inflate(R.layout.product_view, container, false);
        cartController = new CartController(getActivity());
        commanCartControler = cartController;
        chat_button=view.findViewById(R.id.chat_button);

        veg = view.findViewById(R.id.veg);
        eggless = view.findViewById(R.id.eggless);
        fatfree = view.findViewById(R.id.fatfree);
        count = view.findViewById(R.id.count);
        graphClient = GraphClient.builder(Objects.requireNonNull(getActivity()))
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();
        bag_button = view.findViewById(R.id.bag_button);
        buy = view.findViewById(R.id.buy);
        product_price = view.findViewById(R.id.product_price);
        sku = view.findViewById(R.id.sku);
        radioGroup = view.findViewById(R.id.radiogroup);
        veg_image = view.findViewById(R.id.veg_image);
        veg_text = view.findViewById(R.id.veg_text);

        // desc=view.findViewById(R.id.desc);
//        count.addTextChangedListener(this);
//        productViewBinding.setCount(new CountModel());


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tawk tawk = new Tawk();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction1 = null;
                if (getFragmentManager() != null) {
                    transaction1 = getFragmentManager().beginTransaction();
                    transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction1.add(R.id.home_container, tawk, "tawk");
                    if (fragmentManager.findFragmentByTag("tawk") == null) {
                        transaction1.addToBackStack("tawk");
                        transaction1.commit();
                    } else {
                        transaction1.commit();
                    }
                }

            }
        });


        if (getArguments() != null) {
            product = getArguments().getString("category");
        }
        if (product != null) {
            switch (product.trim()) {
                case "topselling": {
                    TopSellingModel detail = (TopSellingModel) getArguments().getSerializable("category_id");
                    if (detail != null) {
                        id = detail.getProduct_ID();
                    }
                    break;
                }
                case "topcollection": {
                    TopCollectionModel detail = (TopCollectionModel) getArguments().getSerializable("category_id");
                    if (detail != null) {
                        id = detail.getProduct_ID().trim();
                    }
                    break;
                }
                case "newarrival": {
                    NewArrivalModel detail = (NewArrivalModel) getArguments().getSerializable("category_id");
                    if (detail != null) {
                        id = detail.getProduct_ID().trim();
                    }
                    break;
                }
                case "ca_adapter":
                    id = getArguments().getString("product_id");
                    //            String text = "gid://shopify/Product/" + id.trim();
                    //
                    //            String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
                    //            Log.e("coverted", converted.trim());
                    //            Log.e("id", id);
                    //            getProductVariantID(converted.trim());
                    break;
                case "grocery": {
                    GroceryModel detail = (GroceryModel) getArguments().getSerializable("category_id");
                    if (detail != null) {
                        id = detail.getProduct().getId().toString();
                    }
                    break;
                }
                case "groceryhome": {
                    GroceryHomeModel detail = (GroceryHomeModel) getArguments().getSerializable("category_id");
                    if (detail != null) {
                        id = detail.getProduct().getId().toString();
                    }
                    break;
                }
                case "bag": {
                    AddToCart_Model model = (AddToCart_Model) getArguments().getSerializable("category_id");

                    if (model != null) {
                        id = model.getProduct_id();
                    }
                    break;
                }
                case "wishlist": {
                    AddWhislistModel model = (AddWhislistModel) getArguments().getSerializable("category_id");

                    if (model != null) {
                        id = model.getProduct_id();
                    }
                    break;
                }
                case "search":
                    id = getArguments().getString("product_id");
                    break;
                default: {
                    ProductModel detail = (ProductModel) getArguments().getSerializable("category_id");
                    //            itemModel.setProduct(detail.getProduct());
                    if (detail != null) {
                        id = detail.getProduct_ID();
                    }
                    //            itemModel = new SelectItemModel(detail);
                    //            productViewBinding.setProductview(itemModel);
                    //            Log.e("title", detail.getProduct().getTitle());
                    //            Log.e("description", detail.getProduct().getDescription());
                    //            Log.e("descriptionhtml", "" + detail.getProduct().getDescriptionHtml().toString());
                    //            mHtmlString = detail.getProduct().getDescriptionHtml().toString();
                    break;
                }
            }
        }
        if (product != null) {
            if (product.trim().equals("grocery") || product.trim().equals("bag") || product.trim().equals("wishlist") || product.trim().equals("groceryhome") || product.trim().equals("topselling") || product.trim().equals("newarrival")) {
                if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                    getProductVariantID(id.trim());
                } else {
                    Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }
            } else {
                String text = "gid://shopify/Product/" + id.trim();

                String converted = Base64.encodeToString(text.trim().getBytes(), Base64.DEFAULT).trim();
                if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                    getProductVariantID(converted.trim());
                } else {
                    Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                }
            }
        }


//        buy.setOnClickListener(view -> {
//            no_of_count = count.getText().toString();
//            if (no_of_count.isEmpty()) {
//
//                Toast.makeText(getActivity(), "Please Enter Quantity.", Toast.LENGTH_SHORT).show();
//            } else {
//                if (Integer.parseInt(no_of_count) <= 100) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("collection", "productview");
//                    bundle.putString("productid", itemModel.getProductid());
//                    bundle.putString("product_varientid", String.valueOf(itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getId()));
//                    bundle.putString("product_qty", no_of_count);
//                    bundle.putString("totalcost", String.valueOf(itemModel.getCost()));
//                    bundle.putString("tag", String.valueOf(itemModel.getProduct().getTags()));
//
////                        if (product.trim().equals("grocery") || product.trim().equals("groceryhome") || product.trim().equals("bag") || product.trim().equals("wishlist")) {
////                            byte[] tmp2 = Base64.decode(id, Base64.DEFAULT);
////                            String val2 = new String(tmp2);
////                            String[] str = val2.split("/");
////                            Log.d("str value", str[4]);
////                            commanCartControler.AddToCartGrocery(id.trim(), selectedID, Integer.parseInt(no_of_count));
//////                    Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
////                        } else {
////                            String text = "gid://shopify/Product/" + id.trim();
////                            String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
////                            Log.e("coverted", converted.trim());
////                            Log.e("id", id);
////                            commanCartControler.AddToCartGrocery(converted.trim(), selectedID, Integer.parseInt(no_of_count));
//////                    Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
////                        }
//
//                    Fragment fragment = new ShippingAddress();
//                    fragment.setArguments(bundle);
//                    FragmentTransaction ft;
//                    if (getFragmentManager() != null) {
//                        ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "fragment");
//                        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                        ft.commit();
//                    }
//
//                } else {
//                    dialog("Entered Quantity should be less than 100");
////                        Toast.makeText(getActivity(), "Entered Quantity should be less than 100", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        buy.setOnClickListener(view -> {
            if (Config.isNetworkAvailable(getActivity())) {
            no_of_count = count.getText().toString();
            if (no_of_count.isEmpty()) {
                dialog("Please Enter Quantity.");

//                    Toast.makeText(getActivity(), "Please Enter Quantity.", Toast.LENGTH_SHORT).show();
            } else {
                if (Integer.parseInt(no_of_count) <= 100) {
                    if (product.trim().equals("grocery") || product.trim().equals("groceryhome") || product.trim().equals("bag") || product.trim().equals("wishlist") || product.trim().equals("topselling") || product.trim().equals("newarrival")) {
                        no_of_count = count.getText().toString();
//                        byte[] tmp2 = Base64.decode(id, Base64.DEFAULT);
//                        String val2 = new String(tmp2);
//                        String[] str = val2.split("/");
                        commanCartControler.AddToCartGrocery(id.trim(), selectedID, Integer.parseInt(no_of_count));
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Fragment fragment = new Bag();
                            FragmentTransaction ft;
                            if (getFragmentManager() != null) {
                                ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "bag");
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.commitAllowingStateLoss();
                            }
                        }, 2000);

                    } else {
                        String text = "gid://shopify/Product/" + id.trim();
                        String converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
                        no_of_count = count.getText().toString();
                        commanCartControler.AddToCartGrocery(converted.trim(), selectedID, Integer.parseInt(no_of_count));

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Fragment fragment = new Bag();
                            FragmentTransaction ft;
                            if (getFragmentManager() != null) {
                                ft = getFragmentManager().beginTransaction().replace(R.id.home_container, fragment, "bag");
                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                                ft.commitAllowingStateLoss();
                            }
                        }, 2000);
                    }
                } else {
                    dialog("Entered Quantity should be less than 100");
//                        Toast.makeText(getActivity(), "Entered Quantity should be less than 100", Toast.LENGTH_SHORT).show();
                }
            }
            } else {
                Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
            }
        });


        bag_button.setOnClickListener(view -> {
            if (Config.isNetworkAvailable(getActivity())) {
                no_of_count = count.getText().toString();
                if (no_of_count.isEmpty()) {
                    dialog("Please Enter Quantity.");

//                    Toast.makeText(getActivity(), "Please Enter Quantity.", Toast.LENGTH_SHORT).show();
                } else {
                    if (Integer.parseInt(no_of_count) <= 100) {
                        if (product.trim().equals("grocery") || product.trim().equals("groceryhome") || product.trim().equals("bag") || product.trim().equals("wishlist") || product.trim().equals("topselling") || product.trim().equals("newarrival")) {
                            no_of_count = count.getText().toString();
//                        byte[] tmp2 = Base64.decode(id, Base64.DEFAULT);
//                        String val2 = new String(tmp2);
//                        String[] str = val2.split("/");
                            commanCartControler.AddToCartGrocery(id.trim(), selectedID, Integer.parseInt(no_of_count));
                            Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            String text = "gid://shopify/Product/" + id.trim();
                            String converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
                            no_of_count = count.getText().toString();
                            commanCartControler.AddToCartGrocery(converted.trim(), selectedID, Integer.parseInt(no_of_count));
                            Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog("Entered Quantity should be less than 100");
//                        Toast.makeText(getActivity(), "Entered Quantity should be less than 100", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void getProductVariantID(String productID) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(productID), nodeQuery -> nodeQuery
                        .onProduct(productQuery -> productQuery
                                .title()
                                .description()
                                .descriptionHtml()
                                .tags()
                                .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                        .edges(imageEdgeQuery -> imageEdgeQuery
                                                .node(Storefront.ImageQuery::src
                                                )
                                        )
                                )
                                .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                        .edges(variantEdgeQuery -> variantEdgeQuery

                                                .node(productVariantQuery -> productVariantQuery
                                                        .sku()
                                                        .price()
                                                        .title()
                                                        .image(Storefront.ImageQuery::src)
                                                        .weight()
                                                        .weightUnit()
                                                        .selectedOptions(ar -> ar.value()
                                                                .name())
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
                    if (response.data().getNode() != null) {
                        Storefront.Product product = (Storefront.Product) response.data().getNode();
//                    Log.e("titit", product.getTitle());
                        itemModel.setProduct(product);
                        itemModel.setWeightname(product.getVariants().getEdges().get(0).getNode().getSelectedOptions().get(0).getName());
                        productViewBinding.setProductview(itemModel);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                getData();
                                progressDialog.dismiss();
                            });
                        }

                    } else {
                        Objects.requireNonNull(getActivity()).runOnUiThread(progressDialog::dismiss);
                    }
                } else {
                    Objects.requireNonNull(getActivity()).runOnUiThread(progressDialog::dismiss);

                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(progressDialog::dismiss);
                }

//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }


        });


    }

    @SuppressLint("SetTextI18n")
    public void getData() {
        if (itemModel != null) {

            mHtmlString = itemModel.getProduct().getDescriptionHtml();
            itemModel.setPrice(selectedID);


            WebView webView = view.findViewById(R.id.webView);
//            webView.clearCache(true);
//            String s="<head><meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/></head>";
            webView.loadDataWithBaseURL(null, mHtmlString, "text/html", "utf-8", null);
            final WebSettings webSettings = webView.getSettings();
            webSettings.setDefaultFontSize(14);
            webView.setBackgroundColor(Color.TRANSPARENT);


            if (itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getSku() != null) {
                sku.setText(itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getSku());
            } else {
                sku.setText("");
            }
            product_price.setText(getResources().getString(R.string.Rs) + " " + itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getPrice().toString());

            recyclerView = view.findViewById(R.id.product_view_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setFocusable(true);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);


            RecyclerView.Adapter adapter = new ImageAdapter(getActivity(), itemsList);

            recyclerView.setAdapter(adapter);

            for (int i = 0; i < itemModel.getProduct().getImages().getEdges().size(); i++) {
                itemsList.add(itemModel.getProduct().getImages().getEdges().get(i).getNode());
            }

            adapter.notifyDataSetChanged();
            if (itemModel.getProduct().getTags() != null && itemModel.getProduct().getTags().size() > 0) {
                ArrayList<String> arrayList = new ArrayList<>(itemModel.getProduct().getTags());

                if (arrayList.contains("Non Veg") || arrayList.contains("Filter Type Non-Veg")) {
                    veg.setVisibility(View.VISIBLE);
                    veg_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_non_veg));
                    veg_text.setText("Non Veg");
                } else if (arrayList.contains("eggless") || arrayList.contains("egg less")) {
                    eggless.setVisibility(View.VISIBLE);
                } else if (arrayList.contains("fatfree") || arrayList.contains("fat free")) {
                    fatfree.setVisibility(View.VISIBLE);
                } else if (arrayList.contains("veg")|| arrayList.contains("Filter Type Veg")||arrayList.contains("Veg")) {
                    veg.setVisibility(View.VISIBLE);
                } else {
                    veg.setVisibility(View.INVISIBLE);
                }
            }
//            if (itemModel.getProduct().getTags() != null && itemModel.getProduct().getTags().size() > 0) {
//                String product_tag = itemModel.getProduct().getTags().get(0);
//                StringTokenizer st = new StringTokenizer(product_tag, ","); //pass comma as delimeter
//
//                while (st.hasMoreTokens()) {
//                    String token = st.nextToken();
//                    switch (token.trim().toLowerCase()) {
//                        case "veg":
//                            veg.setVisibility(View.VISIBLE);
//                            break;
//                        case "eggless":
//                        case "egg less":
//                            eggless.setVisibility(View.VISIBLE);
//
//                            break;
//                        case "fatfree":
//                        case "fat free":
//                            fatfree.setVisibility(View.VISIBLE);
//                            break;
//                        default:
//                            veg.setVisibility(View.VISIBLE);
//                            break;
//                    }
//                }
//
//                Log.e("product_tag", "" + product_tag);
//            }
            ArrayList<Integer> arrayList = new ArrayList<>();
            try {
                for (int i = 0; i < itemModel.getProduct().getVariants().getEdges().size(); i++) {
                    for (int j = 0; j < itemModel.getProduct().getVariants().getEdges().get(i).getNode().getSelectedOptions().size(); j++) {
                        arrayList.add(Integer.valueOf(itemModel.getProduct().getVariants().getEdges().get(i).getNode().getSelectedOptions().get(j).getValue()));
                    }
                }
                selectedID = arrayList.indexOf(Collections.min(arrayList));
            } catch (Exception e) {
                selectedID=0;
            }


            for (int i = 0; i < itemModel.getProduct().getVariants().getEdges().size(); i++) {
                rbn = new RadioButton(getActivity());
                rbn.setId(i);

//                String weightunit = itemModel.getProduct().getVariants().getEdges().get(0).getNode().getWeightUnit().toString();
//                selectedweight = itemModel.getProduct().getVariants().getEdges().get(0).getNode().getWeight().toString() + " " + weightunit;
//                if (weightunit.trim().equals("GRAMS")) {
//                    weightunit = "g";
//                }

                for (int j = 0; j < itemModel.getProduct().getVariants().getEdges().get(i).getNode().getSelectedOptions().size(); j++) {
                    if (!itemModel.getProduct().getVariants().getEdges().get(i).getNode().getSelectedOptions().get(j).getValue().trim().equals("0") && itemModel.getProduct().getVariants().getEdges().get(i).getNode().getSelectedOptions().get(j).getValue() != null) {
//                    rbn.setText(itemModel.getProduct().getVariants().getEdges().get(i).getNode().getWeight().toString() + " " + weightunit);
                        rbn.setText(itemModel.getProduct().getVariants().getEdges().get(i).getNode().getSelectedOptions().get(j).getValue());
                        rbn.setTag(itemModel.getProduct().getVariants().getEdges().get(i));
                        rbn.setTextColor(Color.BLACK);
                        rbn.setBackgroundResource(R.drawable.radio_button_bg);
                        rbn.setPadding(20, 5, 20, 5);
                        rbn.setGravity(Gravity.CENTER_VERTICAL);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 5, 10, 5);
                        rbn.setLayoutParams(params);

                        if (rbn.getParent() != null)
                            ((ViewGroup) rbn.getParent()).removeView(rbn);

                        productViewBinding.radiogroup.addView(rbn);

//                        String finalWeightunit = weightunit;
                        productViewBinding.radiogroup.setOnCheckedChangeListener((radioGroup, i1) -> {

                            selectedID = productViewBinding.radiogroup.getCheckedRadioButtonId();
                            rbn.setTextColor(Color.BLACK);
                            rbn = view.findViewById(selectedID);
//                            Log.e("selected id", String.valueOf(selectedID));
//                            Log.e("selected rdn id", itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getWeight().toString());
//                            Log.e("child count", String.valueOf(productViewBinding.radiogroup.getChildCount()));
//                            selectedweight = itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getWeight().toString() + " " + finalWeightunit;
                            product_price.setText(getResources().getString(R.string.Rs) + " " + itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getPrice().toString());

                            itemModel.setPrice(selectedID);
                            itemModel.setProductid(String.valueOf(itemModel.getProduct().getVariants().getEdges().get(selectedID).getNode().getId()));


                            for (int j1 = 0; j1 < productViewBinding.radiogroup.getChildCount(); j1++) {
                                if (j1 == selectedID) {
                                    rbn.setTextColor(Color.WHITE);
                                }
                            }
                        });
                    }
                }
                if (i == itemModel.getProduct().getVariants().getEdges().size() - 1) {
                    radioGroup.check(selectedID);
                }

            }
        }

    }

    @Override
    public void clickProduct(String productid) {
        id = productid;
        String text = "gid://shopify/Product/" + id.trim();

        String converted = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            getProductVariantID(converted.trim());
        } else {
            Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnclickPlus(String productid) {

    }

    @Override
    public void OnclickWhislilst(String productid) {

    }

    public void dialog(String poptext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
//            builder.setTitle("Success");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.white);
//            alert.getWindow().setBackgroundDrawableResource(android.R.color.white)
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Product");
    }

//    public static <T extends Comparable<T>> int findMinIndex(final List<T> xs) {
//        int minIndex;
//        if (xs.isEmpty()) {
//            minIndex = -1;
//        } else {
//            final ListIterator<T> itr = xs.listIterator();
//            T min = itr.next(); // first element as the current minimum
//            minIndex = itr.previousIndex();
//            while (itr.hasNext()) {
//                final T curr = itr.next();
//                if (curr.compareTo(min) < 0) {
//                    min = curr;
//                    minIndex = itr.previousIndex();
//                }
//            }
//        }
//        return minIndex;
//    }

}