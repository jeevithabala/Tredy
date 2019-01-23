package com.marmeto.user.tredy.bag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.bag.cartdatabase.DBHelper;
import com.marmeto.user.tredy.BuildConfig;
import com.marmeto.user.tredy.callback.CartController;
import com.marmeto.user.tredy.callback.CommanCartControler;
import com.marmeto.user.tredy.login.Validationemail;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.login.Validationmobile;
import com.marmeto.user.tredy.util.Config;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ShippingAddress extends Fragment implements TextWatcher {
    String s_mobile, emailstring, firstnamestring, lastnamestring, bfirstnamestring, blastnamestring, area, state, city, country, check_ship_bill = "", s_pincode, s_area, s_state, s_city, s_country, include_state = "", exclude_state = "";
    String b_mobile, b_email, b_area, b_city, b_state, b_country, b_pincode;
    EditText email, first_name, last_name, mobilenumber, shipping_door_street_input, shipping_pin_input, shipping_city_input, shipping_state_input, shipping_country_input;
    EditText bfirst_name, blast_name, billing_email, b_mobilenumber, billing_door_street_input, billing_city, billing_state, billing_country, billing_pin;
    CheckBox same;
    ArrayList<String> citylist = new ArrayList<>();
    LinearLayout payment_section;
    LinearLayout layout_same, layout_placing;
    private GraphClient graphClient;
    private List<AddToCart_Model> cartList = new ArrayList<>();
    TextView placing, placing1;
    ArrayList<String> getInclude = new ArrayList<>();
    ArrayList<String> productlist = new ArrayList<>();
    CartController cartController;
    CommanCartControler commanCartControler;
    String block = "false", product_qty = "", totalcost = "";
    private String product_varientid = "";
    private String tag;
    private String remove_cod = "";
    int placing_checkin = 0, product_view = 0;
    String accessToken;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String check = getArguments().getString("collection");
            if (check != null) {
                if (check.equals("productview")) {
                    product_view = 1;
                    product_varientid = getArguments().getString("product_varientid");
                    product_qty = getArguments().getString("product_qty");
                    totalcost = getArguments().getString("totalcost");
                    //                Toast.makeText(getActivity(), totalcost+" ----- "+product_qty, Toast.LENGTH_SHORT).show();
                    tag = getArguments().getString("tag");
                    if (tag != null && tag.trim().toLowerCase().contains("remove_cod")) {
                        remove_cod = "remove_cod";
                    }
                } else {
                    totalcost = getArguments().getString("totalcost");
                }
            }
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shippingaddress, container, false);


        init();


        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cartController = new CartController(getActivity());
        commanCartControler = cartController;

        accessToken = SharedPreference.getData("accesstoken", Objects.requireNonNull(getActivity()));

        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        if (Internet.isConnected(getActivity())) {
            getlatestCheckouot();
        } else {
            Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
        }

        if (accessToken != null) {
            if (Internet.isConnected(getActivity())) {
                getEmailId();
            } else {
                Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
            }

        }


        shipping_pin_input.addTextChangedListener(this);
        billing_pin.addTextChangedListener(this);

        same.setOnClickListener(view -> {
            if (same.isChecked()) {
//                    s_area = shipping_door_street_input.getText().toString();
//                    s_city = shipping_city_input.getText().toString();
//                    s_state = shipping_state_input.getText().toString();
//                    s_country = shipping_country_input.getText().toString();
//                    s_pincode = shipping_pin_input.getText().toString();
//
//                    b_area = s_area;
//                    b_city = s_city;
//                    b_state = s_state;
//                    b_country = s_country;
//                    b_pincode = s_pincode;

//
//
//                    billing_door_street_input.setText(b_area);
//                    billing_city.setText(b_city);
//                    billing_state.setText(b_state);
//                    billing_country.setText(b_country);
//                    billing_pin.setText(b_pincode);

                bfirstnamestring = firstnamestring;
                blastnamestring = "";
                b_area = "";
                b_city = "";
                b_state = "";
                b_country = "";
                b_pincode = "";
                b_mobile = "";
                b_email = "";

                billing_door_street_input.setText("");
                billing_city.setText("");
                billing_state.setText("");
                billing_country.setText("");
                billing_pin.setText("");
                bfirst_name.setText("");
                blast_name.setText("");
                b_mobilenumber.setText("");
                billing_email.setText("");

                layout_same.setVisibility(View.GONE);
            } else {
//                    b_area = "";
//                    b_city = "";
//                    b_state = "";
//                    b_country = "";
//                    b_pincode = "";
//                    billing_door_street_input.setText(b_area);
//                    billing_city.setText(b_city);
//                    billing_state.setText(b_state);
//                    billing_country.setText(b_country);
//                    billing_pin.setText(b_pincode);

                layout_same.setVisibility(View.VISIBLE);
            }
        });

        payment_section.setOnClickListener(view -> {
            if (Internet.isConnected(Objects.requireNonNull(getActivity()))) {
                if (same.isChecked()) {
                    bfirstnamestring = firstnamestring;
                    blastnamestring = "";
                    b_area = "";
                    b_city = "";
                    b_state = "";
                    b_country = "";
                    b_pincode = "";
                    b_mobile = "";
                    b_email = "";
                    layout_same.setVisibility(View.GONE);
                }
                s_pincode = shipping_pin_input.getText().toString().trim();
                s_area = shipping_door_street_input.getText().toString().trim();
                s_state = shipping_state_input.getText().toString().trim();
                s_city = shipping_city_input.getText().toString().trim();
                s_country = shipping_country_input.getText().toString().trim();
                s_mobile = mobilenumber.getText().toString();
                emailstring = email.getText().toString().trim();

                b_pincode = billing_pin.getText().toString().trim();
                b_area = billing_door_street_input.getText().toString().trim();
                b_state = billing_state.getText().toString().trim();
                b_city = billing_city.getText().toString().trim();
                b_country = billing_country.getText().toString().trim();
                firstnamestring = first_name.getText().toString().trim();
                lastnamestring = last_name.getText().toString().trim();
                bfirstnamestring = bfirst_name.getText().toString().trim();
                blastnamestring = blast_name.getText().toString().trim();
                b_email = billing_email.getText().toString().trim();
                b_mobile = b_mobilenumber.getText().toString().trim();

                if (b_mobile.trim().length() != 0) {
                    if (b_mobile.contains("+91")) {
                        b_mobile = b_mobile.substring(3, 13);
                    }
                    b_mobilenumber.setText(b_mobile);
                }
                if (s_mobile.trim().length() != 0) {
                    if (s_mobile.contains("+91")) {
                        s_mobile = s_mobile.substring(3, 13);
                    }
                    mobilenumber.setText(s_mobile);
                }

                if (s_pincode.trim().length() == 0) {
                    Config.Dialog("Please enter your shipping address pin-code", getActivity());
                } else if (emailstring.trim().length() == 0) {
                    Config.Dialog("Please enter your email", getActivity());
                } else if (!Validationemail.isEmailAddress(email, true)) {
                    Config.Dialog("Please enter your valid email", getActivity());
                } else if (s_mobile.trim().length() == 0) {
                    Config.Dialog("Please enter your shipping phone number", getActivity());
                } else if (!Validationmobile.isPhoneNumber(mobilenumber, true)) {
                    Config.Dialog("Please enter your valid shipping phone number", getActivity());
                } else if (s_pincode.trim().length() < 6) {
                    Config.Dialog("Please enter your valid pin-code", getActivity());
                } else if (firstnamestring.trim().length() == 0) {
                    Config.Dialog("Please enter your shipping address first name", getActivity());
                } else if (lastnamestring.trim().length() == 0) {
                    Config.Dialog("Please enter your shipping address last name", getActivity());
                } else if (s_area.trim().length() == 0) {
                    Config.Dialog("Please enter your shipping address door number & area", getActivity());
                } else if (s_state.trim().length() == 0) {
                    Config.Dialog("Please enter your valid shipping address pin-code", getActivity());
                } else if (!same.isChecked()) {
                    if (b_pincode.trim().length() == 0) {
                        Config.Dialog("Please enter your billing address pin-code", getActivity());
                    } else if (b_pincode.trim().length() < 0) {
                        Config.Dialog("Please enter your valid billing address pin-code", getActivity());
                    } else if (b_mobile.trim().length() == 0) {
                        Config.Dialog("Please enter your billing phone number", getActivity());
                    } else if (!Validationmobile.isPhoneNumber(b_mobilenumber, true)) {
                        Config.Dialog("Please enter your valid billing phone number", getActivity());
                    } else if (bfirstnamestring.trim().length() == 0) {
                        Config.Dialog("Please enter your billing address first name", getActivity());
                    } else if (blastnamestring.trim().length() == 0) {
                        Config.Dialog("Please enter your billing address last name", getActivity());
                    } else if (b_area.trim().length() == 0) {
                        Config.Dialog("Please enter your billing address door number & area", getActivity());
                    } else if (b_email.trim().length() == 0) {
                        Config.Dialog("Please enter your  billing email", getActivity());
                    } else if (!Validationemail.isEmailAddress(billing_email, true)) {
                        Config.Dialog("Please enter your valid billing email", getActivity());

                    } else {
                        if (block.equals("false")) {
                            Intent intent = new Intent(getActivity(), PayUMoneyActivity.class);
                            intent.putExtra("firstname", firstnamestring);
                            intent.putExtra("lastname", lastnamestring);
                            intent.putExtra("email", emailstring);
                            intent.putExtra("s_area", s_area);
                            intent.putExtra("s_city", s_city);
                            intent.putExtra("s_state", s_state);
                            intent.putExtra("s_country", s_country);
                            intent.putExtra("s_pincode", s_pincode);
                            intent.putExtra("s_mobile", s_mobile);

//
                            intent.putExtra("bfirstname", bfirstnamestring);
                            intent.putExtra("blastname", blastnamestring);
                            intent.putExtra("b_pincode", b_pincode);
                            intent.putExtra("b_area", b_area);
                            intent.putExtra("b_state", b_state);
                            intent.putExtra("b_city", b_city);
                            intent.putExtra("b_country", b_country);
                            intent.putExtra("remove_cod", remove_cod);
                            intent.putExtra("product_varientid", product_varientid);
                            intent.putExtra("product_qty", product_qty);
                            intent.putExtra("totalcost", " " + totalcost);
                            intent.putExtra("tag", tag);
                            intent.putExtra("b_mobile", b_mobile);
                            intent.putExtra("b_email", b_email);
                            startActivity(intent);
                        } else {
                            payment_section.setEnabled(false);
                            layout_placing.findFocus();
                        }
                    }

                } else {
                    if (block.equals("false")) {
                        Intent intent = new Intent(getActivity(), PayUMoneyActivity.class);
                        intent.putExtra("firstname", firstnamestring);
                        intent.putExtra("lastname", lastnamestring);
                        intent.putExtra("email", emailstring);
                        intent.putExtra("s_area", s_area);
                        intent.putExtra("s_city", s_city);
                        intent.putExtra("s_state", s_state);
                        intent.putExtra("s_country", s_country);
                        intent.putExtra("s_pincode", s_pincode);
                        intent.putExtra("s_mobile", s_mobile);

//
                        intent.putExtra("bfirstname", bfirstnamestring);
                        intent.putExtra("blastname", blastnamestring);
                        intent.putExtra("b_pincode", b_pincode);
                        intent.putExtra("b_area", b_area);
                        intent.putExtra("b_state", b_state);
                        intent.putExtra("b_city", b_city);
                        intent.putExtra("b_country", b_country);
                        intent.putExtra("remove_cod", remove_cod);
                        intent.putExtra("product_varientid", product_varientid);
                        intent.putExtra("product_qty", product_qty);
                        intent.putExtra("totalcost", " " + totalcost);
                        intent.putExtra("tag", tag);
                        intent.putExtra("b_mobile", b_mobile);
                        intent.putExtra("b_email", b_email);
                        startActivity(intent);
                    } else {
                        payment_section.setEnabled(false);
                        layout_placing.findFocus();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        layout_placing.setOnClickListener(view -> {
            if (placing_checkin == 0) {
                Fragment bag = new Bag();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("nonshipping", productlist);
                bundle.putString("state", state);
                bag.setArguments(bundle);
                FragmentTransaction transaction1;
                if (getFragmentManager() != null) {
                    transaction1 = getFragmentManager().beginTransaction();
                    transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction1.replace(R.id.home_container, bag, "Bag");
                    if (getFragmentManager().findFragmentByTag("Bag") == null) {
                        transaction1.addToBackStack("Bag");
                        transaction1.commit();
                    } else {
                        transaction1.commit();
                    }
                }

            }
        });
    }

    public void init() {
        email = view.findViewById(R.id.email);
        billing_email = view.findViewById(R.id.billing_email);

        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        bfirst_name = view.findViewById(R.id.b_first_name);
        blast_name = view.findViewById(R.id.b_last_name);

        mobilenumber = view.findViewById(R.id.mobilenumber);
        b_mobilenumber = view.findViewById(R.id.billing_mobilenumber);

        shipping_door_street_input = view.findViewById(R.id.shipping_door_street_input);
        shipping_pin_input = view.findViewById(R.id.shipping_pin_input);
        shipping_city_input = view.findViewById(R.id.shipping_city_input);
        shipping_state_input = view.findViewById(R.id.shipping_state_input);
        shipping_country_input = view.findViewById(R.id.shipping_country_input);

        billing_door_street_input = view.findViewById(R.id.billing_door_street_input);
        billing_city = view.findViewById(R.id.billing_city);
        billing_state = view.findViewById(R.id.billing_state);
        billing_country = view.findViewById(R.id.billing_country);
        billing_pin = view.findViewById(R.id.billing_pin);
        placing = view.findViewById(R.id.placing);
        placing1 = view.findViewById(R.id.placing1);
        layout_placing = view.findViewById(R.id.layout_placing);

        payment_section = view.findViewById(R.id.payment_section);
        same = view.findViewById(R.id.same_address);
        layout_same = view.findViewById(R.id.layout_same);

    }


    private void getAddress(String pincode) {
//        citylist.clear();
        area = "";
        city = "";
        state = "";
        country = "";
//        RequestQueue mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.shippingaddressfetch + pincode,
                response -> {
                    try {
                        Log.e("response", response);

                        JSONObject obj = new JSONObject(response);
                        Log.e("response1", response);
                        String status = obj.getString("Status");
                        if (status.equals("Success")) {
                            JSONArray jsonarray = obj.getJSONArray("PostOffice");
                            Log.e("jsonarray", String.valueOf(jsonarray));

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject object = jsonarray.getJSONObject(i);
                                area = object.getString("Name");
                                city = object.getString("District");
                                state = object.getString("State");
                                country = object.getString("Country");
//                                citylist.add(city);

                            }
                        }
                        if (check_ship_bill.trim().equals("shipping")) {
                            Log.e("city", "" + city);
                            shipping_city_input.setText(city);
                            shipping_state_input.setText(state);
                            shipping_country_input.setText(country);
                        } else {
                            Log.e("city", "" + city);
                            billing_city.setText(city);
                            billing_state.setText(state);
                            billing_country.setText(country);
                        }
                        getdataDB(state);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                }) {

        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

//        stringRequest.setTag("categories_page");
//        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
//
//        int socketTimeout = 10000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(policy);
//        mRequestQueue.add(stringRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Objects.requireNonNull(((Navigation) getActivity()).getSupportActionBar()).setTitle("Shipping");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence1, int i, int i1, int i2) {
        if (charSequence1 != null) {
            if (charSequence1.hashCode() == shipping_pin_input.getText().hashCode()) {
              String  s_pincode = shipping_pin_input.getText().toString().trim();
                if (s_pincode.trim().length() == 0) {
                    shipping_city_input.setText("");
                    shipping_state_input.setText("");
                    shipping_country_input.setText("");
                } else {
                    check_ship_bill = "shipping";
                    if (Internet.isConnected(Objects.requireNonNull(getActivity()))) {
                        getAddress(s_pincode.trim());
                    } else {
                        Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (charSequence1.hashCode() == billing_pin.getText().hashCode()) {
               String b_pincode = billing_pin.getText().toString();
                if (b_pincode.trim().length() == 0) {
                    billing_city.setText("");
                    billing_state.setText("");
                    billing_country.setText("");
                } else {
                    check_ship_bill = "billing";
                    if (Internet.isConnected(Objects.requireNonNull(getActivity()))) {
                        getAddress(b_pincode.trim());
                    } else {
                        Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            }


        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null) {
            if (editable.hashCode() == shipping_pin_input.getText().hashCode()) {
                s_pincode = shipping_pin_input.getText().toString().trim();
                if (s_pincode.trim().length() == 0) {
                    shipping_city_input.setText("");
                    shipping_state_input.setText("");
                    shipping_country_input.setText("");
                } else {
                    check_ship_bill = "shipping";
                    if (Internet.isConnected(Objects.requireNonNull(getActivity()))) {
                        getAddress(s_pincode.trim());
                    } else {
                        Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }


                }
            } else if (editable.hashCode() == billing_pin.getText().hashCode()) {
                b_pincode = billing_pin.getText().toString().trim();
                if (b_pincode.trim().length() == 0) {
                    billing_city.setText("");
                    billing_state.setText("");
                    billing_country.setText("");
                } else {
                    check_ship_bill = "billing";
                    if (Internet.isConnected(Objects.requireNonNull(getActivity()))) {
                        getAddress(b_pincode.trim());
                    } else {
                        Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            }


        }

    }

    @SuppressLint("SetTextI18n")
    public void getdataDB(String state) {
//        if (!check.trim().equals("productview")) {
        placing_checkin = 0;
        include_state = "";
        exclude_state = "";
        block = "false";
        productlist.clear();
        cartList.clear();
        if (getActivity() != null) {
            DBHelper db = new DBHelper(getActivity());
            cartList = db.getCartList();
        }

        if (cartList.size() > 0) {
            for (int i = 0; i < cartList.size(); i++) {
                String tag = cartList.get(i).getTag();
                Log.e("tag", "" + cartList.get(i).getTag());
                if (tag.trim().toLowerCase().contains("remove_cod")) {
                    remove_cod = "remove_cod";
                }
//                String tagcheck = "EXCLUDES:" + state;
                String exclude = "EXCLUDES";
                String include = "INCLUDES";
//                String includecheck = "INCLUDES:" + state;
                if (tag.toLowerCase().contains(exclude.toLowerCase())) {

                    getInclude.clear();
                    productlist.add(cartList.get(i).getProduct_varient_id().trim());


                    String[] items1 = tag.split(",");
                    getInclude.addAll(Arrays.asList(items1));
//                    Log.d("getsize", String.valueOf(getInclude.size()));
                    for (int j = 0; j < getInclude.size(); j++) {
                        if (getInclude.get(j).toLowerCase().contains(exclude.toLowerCase())) {
                            String[] item = getInclude.get(j).split(":");
                            exclude_state = item[1];
                            Log.d("exclude_state", exclude_state);
                        }
//                        Log.d("state", state.trim().toLowerCase());
//                        Log.d("exclude", " " + exclude_state.trim().toLowerCase());
                        String excludespace = exclude_state.replace(" ", "");
                        String statespace = state.replace(" ", "");
//                if (tag.toLowerCase().contains(tagcheck.toLowerCase())) {
                        if (excludespace.trim().toLowerCase().contains(statespace.trim().toLowerCase())) {
                            block = "true";
                            commanCartControler.UpdateShipping(cartList.get(i).getProduct_varient_id().trim(), "false");
                            layout_placing.setVisibility(View.VISIBLE);
                            //placing.setText("Few of the products in your cart cannot be shipped to your given " + state + ".");
                            placing1.setText(getResources().getText(R.string.link));
                        } else {
                            Log.e("tag", "not there");
                            layout_placing.setVisibility(View.GONE);
                        }
                    }

                }
                if (tag.toLowerCase().contains(include.toLowerCase())) {

//                productlist.add(cartList.get(i).getProduct_varient_id());
                    getInclude.clear();
                    productlist.add(cartList.get(i).getProduct_varient_id().trim());

                    String[] items = tag.split(",");
                    getInclude.addAll(Arrays.asList(items));
                    for (int j = 0; j < getInclude.size(); j++) {
                        if (getInclude.get(j).toLowerCase().contains(include.toLowerCase())) {
                            String[] item = getInclude.get(j).split(":");
                            include_state = item[1];
                            Log.d("include_state", include_state);
                        }

                    }
                    String excludespace = include_state.replace(" ", "");
                    String statespace = state.replace(" ", "");

//                    if (tag.toLowerCase().contains(includecheck.toLowerCase())) {
                    if (excludespace.trim().toLowerCase().contains(statespace.trim().toLowerCase())) {
                        layout_placing.setVisibility(View.GONE);

                    } else {
                        block = "true";
                        commanCartControler.UpdateShipping(cartList.get(i).getProduct_varient_id().trim(), "false");
//                    cartList.get(i).setShip("false");
                        layout_placing.setVisibility(View.VISIBLE);
                        placing.setText("Few of the products in your cart cannot be shipped to your given " + state + "." + " Few Products can be Shipped only in" + " " + include_state + ".");
                        placing1.setText(R.string.link);
                    }

                }


            }
        }
//        }
//        else {
//            placing_checkin = 1;
//            include_state = "";
//            exclude_state = "";
//            String exclude = "EXCLUDES";
//            String include = "INCLUDES";
//            block = "false";
//            if (tag.toLowerCase().contains(exclude.toLowerCase())) {
//
//                getInclude.clear();
//
//                String[] items1 = tag.split(",");
//                for (String item : items1) {
//                    getInclude.add(item);
//                }
//                Log.d("getsize", String.valueOf(getInclude.size()));
//                for (int j = 0; j < getInclude.size(); j++) {
//                    if (getInclude.get(j).toLowerCase().contains(exclude.toLowerCase())) {
//                        String[] item = getInclude.get(j).split(":");
//                        exclude_state = item[1];
//                        Log.d("exclude_state", exclude_state);
//                    }
//                    Log.d("state", state.trim().toLowerCase());
//                    Log.d("exclude", " " + exclude_state.trim().toLowerCase());
//                    String excludespace = exclude_state.replace(" ", "");
//                    String statespace = state.replace(" ", "");
////                if (tag.toLowerCase().contains(tagcheck.toLowerCase())) {
//                    if (excludespace.trim().toLowerCase().contains(statespace.trim().toLowerCase())) {
//                        block = "true";
//                        layout_placing.setVisibility(View.VISIBLE);
//                        //placing.setText("Few of the products in your cart cannot be shipped to your given " + state + ".");
//                        placing1.setText(getResources().getText(R.string.link));
//                    } else {
//                        Log.e("tag", "not there");
//                        layout_placing.setVisibility(View.GONE);
//                    }
//                }
//
//            }
//
//            if (tag.toLowerCase().contains(include.toLowerCase())) {
//
//                getInclude.clear();
//                String[] items = tag.split(",");
//                for (String item : items) {
//                    getInclude.add(item);
//                }
//                for (int j = 0; j < getInclude.size(); j++) {
//                    if (getInclude.get(j).toLowerCase().contains(include.toLowerCase())) {
//                        String[] item = getInclude.get(j).split(":");
//                        include_state = item[1];
//                        Log.d("include_state", include_state);
//                    }
//
//                }
//                String excludespace = include_state.replace(" ", "");
//                String statespace = state.replace(" ", "");
//
//                if (excludespace.trim().toLowerCase().contains(statespace.trim().toLowerCase())) {
//                    layout_placing.setVisibility(View.GONE);
//
//                } else {
//                    block = "true";
//                    layout_placing.setVisibility(View.VISIBLE);
//                    placing.setText("Few of the products in your cart cannot be shipped to your given " + state + "." + " Few Products can be Shipped only in" + " " + include_state + ".");
//                }
//            }
//        }

    }

    public void getEmailId() {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .firstName()
                        .lastName()
                        .email()
                        .phone()
                        .displayName()
                        .id()
                        .defaultAddress(Storefront.MailingAddressQuery::zip)
                )
        );

        QueryGraphCall call = graphClient.queryGraph(query);

        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if (response.data() != null && response.data().getCustomer() != null) {

                    firstnamestring = response.data().getCustomer().getFirstName();
                    lastnamestring = response.data().getCustomer().getLastName();
                    emailstring = response.data().getCustomer().getEmail();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            email.setText(emailstring);
                            first_name.setText(firstnamestring);
                            last_name.setText(lastnamestring);
                            if (response.data().getCustomer().getPhone() != null) {
                                mobilenumber.setText(response.data().getCustomer().getPhone());
                            }
                        });

                    }
                    if (response.data() != null && response.data().getCustomer().getDefaultAddress() != null) {
                        String pincode = response.data().getCustomer().getDefaultAddress().getZip();
//                        String address1 = response.data().getCustomer().getDefaultAddress().getFormattedArea();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                shipping_pin_input.setText(pincode);
//                            shipping_door_street_input.setText(address1);
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
            }
        });
    }

    public void getlatestCheckouot() {
        String customerid = SharedPreference.getData("customerid", Objects.requireNonNull(getActivity()));
        String id = "customer_id=" + customerid;

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.latestcheckout + id.trim(),
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);
                        Log.e("response", response);
                        JSONObject object = obj.getJSONObject("customer");
                        JSONObject object1 = object.getJSONObject("default_address");
                        String address1 = object1.getString("address1");
                        String pincode = object1.getString("zip");
                        String phone = object1.getString("phone");
                        Log.e("pincodeeee", " " + pincode);
                        if (pincode != null) {
                            if (pincode.trim().length() != 0) {
                                if (address1 != null) {
                                    shipping_door_street_input.setText(address1);

                                }
                                shipping_pin_input.setText(pincode);
                                s_pincode=shipping_pin_input.getText().toString();
                            }
                        }
                        if (phone != null) {
                            if (phone.trim().length() != 0) {
                                mobilenumber.setText(phone);
                            }
                        }

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



}
