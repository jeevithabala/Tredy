package com.tredy.user.tredy.bag;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tredy.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.tredy.user.tredy.bag.cartdatabase.DBHelper;
import com.tredy.user.tredy.ccavenue.WebViewActivity;
import com.tredy.user.tredy.login.Validationemail;
import com.tredy.user.tredy.login.Validationmobile;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.util.Constants;
import com.tredy.user.tredy.util.SharedPreference;
import com.tredy.user.tredy.util.VolleySingleton;
import com.tredy.user.tredy.utility.AvenuesParams;
import com.tredy.user.tredy.utility.ServiceUtility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PayUMoneyActivity extends AppCompatActivity implements View.OnClickListener, DiscountAdapter.Discountinterface {

    EditText emailedit, mobile, amountedit;
    LinearLayout paynowbtn, recycler_layout;
    Button btnsubmit1, btncancel;
    RadioButton btnradonline, btnradcod;
    String emailstring, totalamount, firstname = "", lastname = "", bfirstname = "", blastname = "", address1 = "", city = "", state = "", country = "", zip = "", phone = "", b_address1 = "", b_city = "", b_state = "", b_country = "", b_zip = "";
    String s_mobile = "", b_mobile = "", b_email = "";
    TextView textcoupon,txtpayamount, t_pay, discount_price, apply_coupon;
    LinearLayout discount_layout;
    int cod = 0;
    private String dynamicKey = "", remove_cod = "";
    DBHelper db;
    List<AddToCart_Model> cartlist = new ArrayList<>();
    String product_varientid = "", product_qty = "", totalcost = "", tag = "";
    RecyclerView discount_recycler;
    private RequestQueue mRequestQueue;
    ArrayList<DiscountModel> discountlist = new ArrayList<>();
    DiscountAdapter discountAdapter;
    TextView view_coupon;
    String accessToken;
    private ProgressDialog progressDialog;
    ArrayList<OrderDetailModel> orderDetailModelArrayList = new ArrayList<>();
    private String orderId, discounted_price, discount_coupon;
    String accessCode, merchantId, currency, rsaKeyUrl, redirectUrl, cancelUrl;
    int buynow = 0, ordercount = 0;
    ArrayList<String> allPinList = new ArrayList<>();
    String isCODAvilable = " ", tcost="";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_umoney);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle("Processing");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        SharedPreference.saveData("update", "true", getApplicationContext());
        accessToken = SharedPreference.getData("accesstoken", PayUMoneyActivity.this);

//        GraphClient graphClient = GraphClient.builder(PayUMoneyActivity.this)
//                .shopDomain(BuildConfig.SHOP_DOMAIN)
//                .accessToken(BuildConfig.API_KEY)
//                .httpCache(new File(PayUMoneyActivity.this.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
//                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
//                .build();


        db = new DBHelper(this);

        cartlist = db.getCartList();
//        totalcost = SharedPreference.getData("total", getApplicationContext());
        discount_recycler = findViewById(R.id.discount_recycler);
        recycler_layout = findViewById(R.id.recycler_layout);
        view_coupon = findViewById(R.id.view_coupon);
        textcoupon=findViewById(R.id.textcoupon);

        if (getIntent() != null) {
            firstname = getIntent().getStringExtra("firstname");
            lastname = getIntent().getStringExtra("lastname");
            emailstring = getIntent().getStringExtra("email");
            address1 = getIntent().getStringExtra("s_area");
            city = getIntent().getStringExtra("s_city");
            state = getIntent().getStringExtra("s_state");
            country = getIntent().getStringExtra("s_country");
            zip = getIntent().getStringExtra("s_pincode");

            bfirstname = getIntent().getStringExtra("bfirstname");
            blastname = getIntent().getStringExtra("blastname");
            b_address1 = getIntent().getStringExtra("b_area");
            b_city = getIntent().getStringExtra("b_city");
            b_state = getIntent().getStringExtra("b_state");
            b_country = getIntent().getStringExtra("b_country");
            b_zip = getIntent().getStringExtra("b_pincode");
            remove_cod = getIntent().getStringExtra("remove_cod");
            product_varientid = getIntent().getStringExtra("product_varientid");
            product_qty = getIntent().getStringExtra("product_qty");
            totalcost = getIntent().getStringExtra("totalcost");
            tag = getIntent().getStringExtra("tag");
            s_mobile = getIntent().getStringExtra("s_mobile");
            b_mobile = getIntent().getStringExtra("b_mobile");
            b_email = getIntent().getStringExtra("b_email");

            if (product_varientid != null) {
                if (product_varientid.trim().length() != 0) {
                    byte[] tmp2 = Base64.decode(product_varientid, Base64.DEFAULT);
                    String val2 = new String(tmp2);
                    String[] str = val2.split("/");
                    product_varientid = str[4];
                }
            }


        }
        Log.e("remove_cod", " " + remove_cod);


        getAllPinList();

//        Toast.makeText(this, totalcost, Toast.LENGTH_SHORT).show();

        totalamount = totalcost;
        tcost=totalcost;
        if (totalamount != null) {
            String[] separated = totalamount.split(" ");
            totalamount = separated[1];
        }


        emailedit = findViewById(R.id.payuemail);
        mobile = findViewById(R.id.payumobile);
        amountedit = findViewById(R.id.payuamount);
//        apply_discount = findViewById(R.id.apply_discount);
        discount_price = findViewById(R.id.discount_price);
        t_pay = findViewById(R.id.t_pay);
        discount_layout = findViewById(R.id.discount_layout);
        apply_coupon = findViewById(R.id.apply_coupon);
        paynowbtn = findViewById(R.id.paynowbtn);
        paynowbtn.setOnClickListener(this);
        view_coupon.setOnClickListener(this);

        emailedit.setText(emailstring);
        mobile.setText(s_mobile);
        amountedit.setText(getResources().getString(R.string.Rs) + " " + totalcost);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        discount_recycler.setLayoutManager(layoutManager1);
        discount_recycler.setItemAnimator(new DefaultItemAnimator());


        discountAdapter = new DiscountAdapter(getApplicationContext(), discountlist, this);
        discount_recycler.setAdapter(discountAdapter);
        if (Config.isNetworkAvailable(getApplicationContext())) {
            getDiscount();
        } else {
            Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }


        discountAdapter.notifyDataSetChanged();


//        if (accessToken != null) {
//            getEmailId();
//        }

    }

    private void init() {
        accessCode = "AVML80FJ99AW34LMWA";
        merchantId = "139259";
        currency = "INR";
//        amount = (EditText) findViewById(R.id.amount);
        rsaKeyUrl = "http://52.66.204.219/GetRSA.php";
        redirectUrl = "http://52.66.204.219/ccavResponseHandler.php";
        cancelUrl = "http://52.66.204.219/ccavResponseHandler.php";

        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId = randomNum.toString();
//
//        String vAccessCode = ServiceUtility.chkNull(accessCode).toString().trim();
//        String vMerchantId = ServiceUtility.chkNull(merchantId).toString().trim();
//        String vCurrency = ServiceUtility.chkNull(currency).toString().trim();
//        String vAmount = ServiceUtility.chkNull(totalcost).toString().trim();
//        if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode).toString().trim());
        intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId).toString().trim());
        intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId).toString().trim());
        intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency).toString().trim());
        intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(totalcost).toString().trim());

        intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl).toString().trim());
        intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl).toString().trim());
        intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl).toString().trim());
        Bundle bundle = new Bundle();
        bundle.putSerializable("value", orderDetailModelArrayList.get(0));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.online:
                btnradonline.setChecked(true);
                btnradcod.setChecked(false);
                break;

            case R.id.cod:
                btnradcod.setChecked(true);
                btnradonline.setChecked(false);
                break;
            case R.id.paynowbtn:
                phone = mobile.getText().toString();
                if (!Validationemail.isEmailAddress(emailedit, true)) {
//                    Toast.makeText(PayUMoneyActivity.this, "Please enter your valid email", Toast.LENGTH_SHORT).show();
                    Config.Dialog("Please enter your valid email", PayUMoneyActivity.this);
                } else if (phone.trim().length() == 0) {
                    Config.Dialog("Please enter your phone number", PayUMoneyActivity.this);

//                    Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else if (!Validationmobile.isPhoneNumber(mobile, true)) {
                    Config.Dialog("Please enter your valid phone number", PayUMoneyActivity.this);

//                    Toast.makeText(getApplicationContext(), "Please enter your valid phone number", Toast.LENGTH_SHORT).show();

                } else {

//                    showCustomDialog1(checkPinCode(zip));

                    showCustomDialog1(isCODAvilable);

                }
                break;

            case R.id.view_coupon:
                recycler_layout.setVisibility(View.VISIBLE);
                break;


        }
    }

    private void getAllPinList() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.Check_COD_API,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    JSONArray jsonArray = response.getJSONArray("values");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
//                        Log.d("array" + i, jsonArray1.get(0).toString());
                        allPinList.add(jsonArray1.get(0).toString());


                    }
                    if (zip != null && zip.trim().length() > 0) {
                        checkPinCode(zip);
                    }else {
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(PayUMoneyActivity.this);
        requestQueue.add(jsonObjectRequest);


    }

    private boolean checkPinCode(String pin) {
//        Log.e("allPinList", String.valueOf(allPinList.size()));
//        Log.e("iscod", isCODAvilable);
        progressDialog.dismiss();

        if (allPinList.contains(pin.trim())) {
            isCODAvilable = "cod";
            return true;
        }


        return false;
    }

    protected void showCustomDialog1(String isCODAvilable) {
        // TODO Auto-generated method stub


        final Dialog dialog = new Dialog(PayUMoneyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.paybywalletorbankdata);

        btnsubmit1 = dialog.findViewById(R.id.res_pay_submit);
        btncancel = dialog.findViewById(R.id.res_pay_cancel);
        txtpayamount = dialog.findViewById(R.id.pay_amount);
        btnradonline = dialog.findViewById(R.id.online);
        btnradcod = dialog.findViewById(R.id.cod);
//        btnradcod.setVisibility(View.GONE);
        double cost = Double.parseDouble(totalcost.trim());
//        Log.e("remove_cod", " " + remove_cod);
//        Log.e("isCODAvilable", " " + isCODAvilable);
//        if (remove_cod.trim().length() == 0||isCODAvilable.trim().equals("cod")) {

        if (remove_cod.trim().equals("remove_cod")) {
            btnradcod.setVisibility(View.GONE);
        } else if (isCODAvilable.trim().equals("cod")) {
            btnradcod.setVisibility(View.VISIBLE);
        } else {
            btnradcod.setVisibility(View.GONE);
        }

        if (cost == 0.00) {
            btnradonline.setVisibility(View.GONE);
        } else {
            btnradonline.setVisibility(View.VISIBLE);
        }

//        if(isCODAvilable)
//            btnradcod.setVisibility(View.VISIBLE);
//        else
//            btnradcod.setVisibility(View.GONE);

        txtpayamount.setText(totalcost);
        btnradonline.setOnClickListener(this);
        btnradcod.setOnClickListener(this);

        dialog.setCanceledOnTouchOutside(false);

        btnsubmit1.setOnClickListener(view -> {
            Log.e("dismiss", "dialog dismiss");

            if (btnradonline.isChecked() || btnradcod.isChecked()) {
                dialog.dismiss();
                if (btnradonline.isChecked()) {
                    cod = 0;

                    OrderDetailModel orderDetailModel = new OrderDetailModel(emailstring, totalcost, firstname, lastname, bfirstname, blastname, address1, city, state, country, zip, phone, b_address1, b_city, b_state, b_country, b_zip, product_varientid, product_qty, discounted_price, discount_coupon, s_mobile, b_mobile, b_email);
                    orderDetailModelArrayList.add(orderDetailModel);
                    if (Config.isNetworkAvailable(getApplicationContext())) {
                        init();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                    }
//                        Intent i = new Intent(getApplicationContext(), InitialActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("value", orderDetailModelArrayList.get(0));
//                        i.putExtras(bundle);
//                        startActivity(i);
                } else {
                    if (discount_coupon == null) {
                        discounted_price = "";
                        discount_coupon = "";
                    }
                    cod = 1;
                    if (Config.isNetworkAvailable(getApplicationContext())) {
                        noDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                    }

                }
            } else {
                Config.Dialog("Select the payment method", PayUMoneyActivity.this);

//                    Toast.makeText(PayUMoneyActivity.this, "Select the payment method", Toast.LENGTH_SHORT).show();
            }


        });


        btncancel.setOnClickListener(view -> {
            // progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(PayUMoneyActivity.this);
            builder.setMessage("Are you sure you want to cancel?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog1, id) -> {
                        dialog.dismiss();
                        abandandCheckout();
                    })
                    .setNegativeButton("No", (dialog1, id) -> dialog1.cancel())
                    .show();


//                if (getApplicationContext() != null) {
//                    finish();
//                    startActivity(new Intent(PayUMoneyActivity.this, PayUMoneyActivity.class));
//                }
        });
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }

    public void postOrder() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        phone = mobile.getText().toString().trim();
        double costtotal = Double.parseDouble(totalcost.trim());

        try {
            if (ordercount == 0) {
//                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", emailstring);
                jsonBody.put("financial_status", "pending");
                jsonBody.put("gateway", "Cash on Delivery (COD)");


                JSONArray line_items = new JSONArray();
                if (product_varientid.trim().length() == 0) {
                    for (int i = 0; i < cartlist.size(); i++) {
                        JSONObject items = new JSONObject();

                        product_varientid = cartlist.get(i).getProduct_varient_id();
                        byte[] tmp2 = Base64.decode(product_varientid, Base64.DEFAULT);
                        String val2 = new String(tmp2);
                        String[] str = val2.split("/");
                        product_varientid = str[4];

                        Integer quantity = cartlist.get(i).getQty();
//                    items.put("variant_id", "5823671107611");
                        items.put("variant_id", product_varientid.trim());
                        items.put("quantity", quantity);
                        line_items.put(items);
                        jsonBody.put("line_items", line_items);
                    }
                } else {
                    JSONObject items = new JSONObject();
                    buynow = 1;
//                items.put("variant_id", "5823671107611");
                    items.put("variant_id", product_varientid.trim());
                    items.put("quantity", product_qty);
                    line_items.put(items);
                    jsonBody.put("line_items", line_items);

                }
                if (discount_coupon.trim().length() != 0) {
                    JSONArray note1 = new JSONArray();
                    JSONObject notes1 = new JSONObject();

                    notes1.put("code", discount_coupon);
                    notes1.put("amount", discounted_price);
                    notes1.put("type", "fixed_amount");

                    note1.put(notes1);
                    jsonBody.put("discount_codes", note1);
                }

                JSONArray note = new JSONArray();
                JSONObject notes = new JSONObject();

                notes.put("name", "Cash on Delivery");
                notes.put("value", "");

                note.put(notes);
                jsonBody.put("note_attributes", note);


                JSONObject shipping = new JSONObject();
                shipping.put("first_name", firstname);
                shipping.put("last_name", lastname);
                shipping.put("address1", address1);
                shipping.put("phone", phone);
                shipping.put("city", city);
                shipping.put("province", state);
                shipping.put("country", country);
                shipping.put("zip", zip);
                jsonBody.put("shipping_address", shipping);


                JSONObject billingaddress = new JSONObject();
                billingaddress.put("first_name", bfirstname);
                billingaddress.put("last_name", blastname);
                billingaddress.put("address1", b_address1);
                billingaddress.put("phone", b_mobile);
                billingaddress.put("city", b_city);
                billingaddress.put("province", b_state);
                billingaddress.put("country", b_country);
                billingaddress.put("zip", b_zip);
                jsonBody.put("billing_address", billingaddress);

                JSONArray cost = new JSONArray();
                JSONObject costobject = new JSONObject();
                String kind_transaction;
                if (cod == 1) {
                    kind_transaction = "cod";
                } else {
                    kind_transaction = "online";
                }

                costobject.put("kind", kind_transaction);
                costobject.put("status", "success");
                costobject.put("amount", costtotal);

                cost.put(costobject);
                jsonBody.put("transactions", cost);


                Log.d("check JSON", jsonBody.toString());


                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.postcreateorder, response -> {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        String msg = obj.getString("msg");

                        if (msg.equals("success")) {
                            Iterator keys = obj.keys();

                            while (keys.hasNext()) {
                                dynamicKey = (String) keys.next();
                                Log.d("Dynamic Key", "" + dynamicKey);
                                if (dynamicKey.equals("order")) {
                                    JSONObject order = obj.getJSONObject("order");
                                    String orderid = order.getString("id");

                                }
                            }
                            progressDialog.dismiss();
//                            if (buynow != 1) {
                            db.deleteCart(getApplicationContext());
//                            }
                            Dialog("Your Order Placed Successfully");

//                            Toast.makeText(PayUMoneyActivity.this, "Your Order Placed Sucessfully", Toast.LENGTH_SHORT).show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    progressDialog.dismiss();
                    Log.e("VOLLEY", " " + error.toString());
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
//                        return requestBody == null;
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        //TODO if you want to use the status code for any other purpose like to handle 401, 403, 404
//                        String statusCode = String.valueOf(response.statusCode);
                        //Handling logic
                        return super.parseNetworkResponse(response);
                    }
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
                };
                if (ordercount == 0) {
                    ordercount++;
//                    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
//                    getRequestQueue().add(stringRequest);
//                    requestQueue.add(stringRequest);
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getDiscount() {
        discountlist.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(PayUMoneyActivity.this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.getDiscount,
                response -> {
            Log.e("response",response);
                    try {

                        JSONObject obj = new JSONObject(response);

                        JSONArray jsonarray = obj.getJSONArray("discounts");
                        if (jsonarray.length() == 0) {
                            recycler_layout.setVisibility(View.GONE);
                        } else {
                            recycler_layout.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonarray.length(); i++) {
                            double cal = 0.0, amount = 0.0, value1 = 0.0;

                            JSONObject collectionobject = jsonarray.getJSONObject(i);

                            DiscountModel discountModel = new DiscountModel();
                            String active=collectionobject.getString("active");
                            if(active.trim().equals("true")){
                                String discountname = collectionobject.getString("title");
                                String value = collectionobject.getString("value");
                                String percent = collectionobject.getString("value_type");
                                String discount_type = collectionobject.getString("discount_type");
                                String discount_value=collectionobject.getString("discount_value");
                                String[] str = value.split("-");
                                value = str[1];
                                Log.e("value",value);
                                Log.e("totalcost",totalcost);
                                if (percent.trim().equals("percentage")) {
                                    int discountvalue = Integer.parseInt(value.trim());
                                    cal = Double.parseDouble(value.trim()) / 100;
                                    amount = Double.parseDouble(totalcost) * cal;
                                    value1 = Double.parseDouble(totalcost) - amount;
                                    Log.e("amouunt", String.valueOf(amount));

                                    discountModel.setValue(String.valueOf(Math.round(amount)));
                                    discountModel.setValuetype(String.valueOf(value1));
                                    discountModel.setPercent(value +" "+"%");
                                } else {
                                    value1 = Double.parseDouble(totalcost) - Double.parseDouble(value.trim());
//                                Log.e("amouunt", String.valueOf(amount));

//                                discountModel.setValue(String.valueOf(Math.round(amount)));
                                    discountModel.setValue(value);
                                    discountModel.setValuetype(String.valueOf(value1));
                                    discountModel.setPercent("Rs. "+value );


                                }
                                discountModel.setDiscountvalue(discount_value);
                                discountModel.setDiscount_type(discount_type);
                                discountModel.setTitle(discountname);

                                discountlist.add(discountModel);
                            }
                            discountAdapter.notifyDataSetChanged();


                        }


//
//


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    recycler_layout.setVisibility(View.GONE);
                }) {

            @Override
            protected void deliverResponse(String response) {
                Log.e("ree", " " + response);
                super.deliverResponse(response);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.e("reen", " " + response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);


    }


    @SuppressLint("SetTextI18n")
    @Override
    public void discountValue(String discounted_amount, String coupon, String totalamount, String discount_type,String discountvalue) {
String totalcost1=tcost;
        if (discounted_amount.trim().length() != 0) {

            discount_coupon = coupon;
            double amount;

//            String val2 = discounted_amount;
//            String[] str = val2.split("-");
//            discounted_amount = str[1];
            discounted_price = discounted_amount;
            amount = Double.parseDouble(discounted_amount);
            Log.e("amount", String.valueOf(discounted_amount));
//            totalcost = totalamount;
            Log.e("totalcost", totalamount);
            if (Double.parseDouble(totalcost) >= amount) {
                discount_layout.setVisibility(View.VISIBLE);
//                Double a = (double) Math.round(Double.parseDouble(totalcost) - amount);
                Double a = (double) Math.round(Double.parseDouble(totalamount));

                switch (discount_type) {
                    case "greater":
                        if (Double.parseDouble(totalcost1) > Math.round(Double.parseDouble(discountvalue))) {
                            totalcost = String.valueOf(a);
                            t_pay.setText(getResources().getString(R.string.Rs) + " " + totalcost);
                            discount_price.setText(getResources().getString(R.string.Rs) + " " + discounted_amount);
                            apply_coupon.setText("Your Applied Coupon Code is : " + coupon);
                            recycler_layout.setVisibility(View.GONE);
                            view_coupon.setText(R.string.view);
                            view_coupon.setVisibility(View.VISIBLE);
                            textcoupon.setVisibility(View.GONE);
                            discount_layout.setVisibility(View.VISIBLE);

                        } else {
                            totalcost=totalcost1;
                            discount_coupon="";
                            discounted_price="";
                            textcoupon.setVisibility(View.VISIBLE);
                            textcoupon.setText("This coupon is available only for more than Rs."+discountvalue+". Please add more items in your cart ");
                            discount_layout.setVisibility(View.GONE);
                        }
                        break;
                    case "equal":
                        if (Double.parseDouble(totalcost1) == Math.round(Double.parseDouble(discountvalue))) {
                            totalcost = String.valueOf(a);
                            t_pay.setText(getResources().getString(R.string.Rs) + " " + totalcost);
                            discount_price.setText(getResources().getString(R.string.Rs) + " " + discounted_amount);
                            apply_coupon.setText("Your Applied Coupon Code is : " + coupon);
                            recycler_layout.setVisibility(View.GONE);
                            view_coupon.setText(R.string.view);
                            view_coupon.setVisibility(View.VISIBLE);
                            textcoupon.setVisibility(View.GONE);
                            discount_layout.setVisibility(View.VISIBLE);

                        } else {
                            totalcost=totalcost1;
                            discount_coupon="";
                            discounted_price="";
                            textcoupon.setText("This coupon is available only for equal to Rs."+discountvalue);
                            textcoupon.setVisibility(View.VISIBLE);
                            discount_layout.setVisibility(View.GONE);


                        }

                        break;
                    case "less":
                        if (Double.parseDouble(totalcost1.trim()) <  Math.round(Double.parseDouble(discountvalue.trim()))) {
                            totalcost = String.valueOf(a);
                            t_pay.setText(getResources().getString(R.string.Rs) + " " + totalcost);
                            discount_price.setText(getResources().getString(R.string.Rs) + " " + discounted_amount);
                            apply_coupon.setText("Your Applied Coupon Code is : " + coupon);
                            recycler_layout.setVisibility(View.GONE);
                            view_coupon.setText(R.string.view);
                            view_coupon.setVisibility(View.VISIBLE);
                            textcoupon.setVisibility(View.GONE);
                            discount_layout.setVisibility(View.VISIBLE);

                        } else {
                            totalcost=totalcost1;
                            discount_coupon="";
                            discounted_price="";
                            textcoupon.setText("This coupon is available only for less than Rs."+discountvalue);
                            textcoupon.setVisibility(View.VISIBLE);
                            discount_layout.setVisibility(View.GONE);

                        }
                        break;
                }




//                totalcost = String.valueOf(a);
//                t_pay.setText(getResources().getString(R.string.Rs) + " " + totalcost);
//                discount_price.setText(getResources().getString(R.string.Rs) + " " + discounted_amount);
//                apply_coupon.setText("Your Applied Coupon Code is : " + coupon);
//                recycler_layout.setVisibility(View.GONE);
//                view_coupon.setText(R.string.view);
//                view_coupon.setVisibility(View.VISIBLE);
            } else {
                discount_layout.setVisibility(View.VISIBLE);
//                int a = Integer.parseInt(totalcost) - amount;
                discount_price.setText(getResources().getString(R.string.Rs) + " " + totalcost);
                totalcost = String.valueOf(0);
                t_pay.setText(getResources().getString(R.string.Rs) + " 0");
                apply_coupon.setText("Your Applied Coupon Code is : " + coupon);
                recycler_layout.setVisibility(View.GONE);
                view_coupon.setText(R.string.view);
                view_coupon.setVisibility(View.VISIBLE);

            }
        }
    }

//    public void getEmailId() {
//        Storefront.QueryRootQuery query = Storefront.query(root -> root
//                .customer(accessToken, customer -> customer
//                        .firstName()
//                        .lastName()
//                        .email()
//                        .phone()
//                        .displayName()
//                        .id()
//                )
//        );
//
//        QueryGraphCall call = graphClient.queryGraph(query);
//
//        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
//            @Override
//            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
//                if (response.data() != null && response.data().getCustomer() != null) {
//
//                    phone = response.data().getCustomer().getPhone();
//
//                    PayUMoneyActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (phone != null) {
//                                if (phone.length() != 0) {
//                                    if (phone.contains("+91")) {
//                                        phone = phone.substring(3, 13);
//                                        mobile.setText(phone);
//                                    } else {
//                                        mobile.setText(phone);
//                                    }
//                                }
//
//                            } else {
//                                phone = "";
//                                mobile.setText(phone);
//                            }
//
//                        }
//                    });
//
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull GraphError error) {
////                Log.e("TAG", "Failed to execute query", Objects.requireNonNull(error));
//            }
//        });
//
//    }

    public void abandandCheckout() {
        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId = randomNum.toString();
        phone = mobile.getText().toString().trim();
        double costtotal = Double.parseDouble(totalcost.trim());
        String customerid = SharedPreference.getData("customerid", getApplicationContext());

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", emailstring);
            jsonBody.put("id", orderId);
            jsonBody.put("total_price", costtotal);


            JSONArray line_items = new JSONArray();
            if (product_varientid.trim().length() == 0) {
                for (int i = 0; i < cartlist.size(); i++) {
                    JSONObject items = new JSONObject();

                    product_varientid = cartlist.get(i).getProduct_varient_id();
                    byte[] tmp2 = Base64.decode(product_varientid, Base64.DEFAULT);
                    String val2 = new String(tmp2);
                    String[] str = val2.split("/");
                    product_varientid = str[4];

                    Integer quantity = cartlist.get(i).getQty();
                    Log.e("quantity", " " + String.valueOf(quantity));
//                    items.put("variant_id", "5823671107611");
                    items.put("product_id", product_varientid.trim());
                    items.put("quantity", quantity);
                    items.put("title", cartlist.get(i).getProduct_name());
                    line_items.put(items);
                    jsonBody.put("line_items", line_items);
                }
            } else {
                JSONObject items = new JSONObject();
                Log.e("quantity1", product_qty);
//                items.put("variant_id", "5823671107611");
                items.put("product_id", product_varientid.trim());
                items.put("quantity", product_qty);
                line_items.put(items);
                jsonBody.put("line_items", line_items);

            }

            JSONObject shipping = new JSONObject();
            shipping.put("first_name", firstname);
            shipping.put("last_name", lastname);
            shipping.put("address1", address1);
            shipping.put("phone", phone);
            shipping.put("city", city);
            shipping.put("province", state);
            shipping.put("country", country);
            shipping.put("zip", zip);
            jsonBody.put("shipping_address", shipping);


            JSONObject billingaddress = new JSONObject();
            billingaddress.put("first_name", blastname);
            billingaddress.put("last_name", blastname);
            billingaddress.put("address1", b_address1);
            billingaddress.put("phone", b_mobile);
            billingaddress.put("city", b_city);
            billingaddress.put("province", b_state);
            billingaddress.put("country", b_country);
            billingaddress.put("zip", b_zip);
            jsonBody.put("billing_address", billingaddress);


            JSONObject customer = new JSONObject();
            customer.put("id", customerid);
            customer.put("first_name", firstname);
            customer.put("last_name", lastname);
            customer.put("email", emailstring);
            customer.put("phone", phone);
            customer.put("city", city);
            JSONObject default_address = new JSONObject();
            default_address.put("first_name", firstname);
            default_address.put("last_name", lastname);
            default_address.put("address1", address1);
            default_address.put("phone", phone);
            default_address.put("city", city);
            default_address.put("country", country);
            default_address.put("zip", zip);
            customer.put("default_address", default_address);

            jsonBody.put("customer", customer);


            Log.d("check JSON", jsonBody.toString());


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.createabandoned, response -> {
                Log.i("VOLLEY", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    Log.e("msg", " " + msg);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.e("VOLLEY", error.toString())) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//                        return requestBody == null;
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    //TODO if you want to use the status code for any other purpose like to handle 401, 403, 404
//                    String statusCode = String.valueOf(response.statusCode);
                    //Handling logic
                    return super.parseNetworkResponse(response);
                }
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void noDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (ordercount == 0) {
                        postOrder();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to pay by COD?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void Dialog(String poptext) {


        AlertDialog.Builder builder = new AlertDialog.Builder(PayUMoneyActivity.this, R.style.AlertDialogStyle);
        builder.setTitle("Success");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {

                    dialog.dismiss();

                    Intent i = new Intent(PayUMoneyActivity.this, Navigation.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                });
        AlertDialog alert = builder.create();
        alert.show();

        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.white);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreference.saveData("update", "true", getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        abandandCheckout();
    }
}
