package com.example.user.trendy.Bag;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.Bag.Db.AddToCart_Model;
import com.example.user.trendy.Bag.Db.DBHelper;
import com.example.user.trendy.Login.Validationemail;
import com.example.user.trendy.Login.Validationmobile;
import com.example.user.trendy.MainActivity;
import com.example.user.trendy.Payu_Utility.AppEnvironment;
import com.example.user.trendy.Payu_Utility.AppPreference;
import com.example.user.trendy.Payu_Utility.MyApplication;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.Constants;
import com.example.user.trendy.Util.SharedPreference;
import com.google.gson.JsonObject;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PayUMoneyActivity extends AppCompatActivity implements View.OnClickListener, DiscountAdapter.Discountinterface {

    EditText emailedit, mobile, amountedit, discount;
    LinearLayout paynowbtn,recycler_layout;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    Button btnsubmit1, btncancel;
    RadioButton btnradonline, btnradcod;
    String emailstring, totalamount, coupon, firstname = "", lastname = "", address1 = "", city = "", state = "", country = "", zip = "", phone = "", b_address1 = "", b_city = "", b_state = "", b_country = "", b_zip = "";
    TextView txtpayamount, t_pay, discount_price,apply_coupon;
    CardView apply_discount;
    LinearLayout discount_layout;
    int i = 0, cod = 0;
    private String dynamicKey = "", remove_cod = "";
    DBHelper db;
    List<AddToCart_Model> cartlist = new ArrayList<>();
    String product_varientid = "", product_qty = "", totalcost = "", tag = "";
    private String kind_transaction = "";
    private String product_varientid1 = "";
    RecyclerView discount_recycler;
    private RequestQueue mRequestQueue;
    ArrayList<DiscountModel> discountlist = new ArrayList<>();
    private JsonArrayRequest request;
    DiscountAdapter discountAdapter;
    TextView view_coupon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_umoney);

        db = new DBHelper(this);

        cartlist = db.getCartList();
        totalcost = SharedPreference.getData("total", getApplicationContext());
        phone = SharedPreference.getData("mobile", getApplicationContext());
        emailstring = SharedPreference.getData("email", getApplicationContext());
        discount_recycler = findViewById(R.id.discount_recycler);
        recycler_layout=findViewById(R.id.recycler_layout);
        view_coupon=findViewById(R.id.view_coupon);

        if (getIntent() != null) {
            firstname = getIntent().getStringExtra("firstname");
            lastname = getIntent().getStringExtra("lastname");
            emailstring = getIntent().getStringExtra("email");
            address1 = getIntent().getStringExtra("s_area");
            city = getIntent().getStringExtra("s_city");
            state = getIntent().getStringExtra("s_state");
            country = getIntent().getStringExtra("s_country");
            zip = getIntent().getStringExtra("s_pincode");

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
            if (product_varientid != null) {
                if (product_varientid.trim().length() != 0) {
                    byte[] tmp2 = Base64.decode(product_varientid, Base64.DEFAULT);
                    String val2 = new String(tmp2);
                    String[] str = val2.split("/");
                    product_varientid = str[4];
                }
            }


        }
        totalamount=totalcost;
        emailedit = (EditText) findViewById(R.id.payuemail);
        mobile = (EditText) findViewById(R.id.payumobile);
        amountedit = (EditText) findViewById(R.id.payuamount);
//        apply_discount = findViewById(R.id.apply_discount);
        discount_price = findViewById(R.id.discount_price);
        t_pay = findViewById(R.id.t_pay);
        discount_layout = findViewById(R.id.discount_layout);
        apply_coupon=findViewById(R.id.apply_coupon);
        paynowbtn = (LinearLayout) findViewById(R.id.paynowbtn);
        paynowbtn.setOnClickListener(this);
      view_coupon.setOnClickListener(this);

        emailedit.setText(emailstring);
        amountedit.setText(totalcost);
        if (phone.trim().length() != 0) {
            mobile.setText(phone);
        }


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        discount_recycler.setLayoutManager(layoutManager1);
        discount_recycler.setItemAnimator(new DefaultItemAnimator());


        discountAdapter = new DiscountAdapter(getApplicationContext(), discountlist, this);
        discount_recycler.setAdapter(discountAdapter);
        getDiscount();

        discountAdapter.notifyDataSetChanged();

    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");
        AppEnvironment appEnvironment = ((MyApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

//        Logger.LogError("hashsequence",stringBuilder.toString());
        String hash = hashCal("SHA-512", stringBuilder.toString());
      /*  AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        //String hash = hashCal(stringBuilder.toString());*/
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
    }

    private void launchPayUMoneyFlow() {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        // payUmoneyConfig.setDoneButtonText(((EditText) findViewById(R.id.status_page_et)).getText().toString());

        //Use this to set your custom title for the activity
        //payUmoneyConfig.setPayUmoneyActivityTitle(((EditText) findViewById(R.id.activity_title_et)).getText().toString());

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {
            amount = Double.parseDouble(amountedit.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        String phone = mobile.getText().toString();
        String productName = "product name";
        String firstName = "marcony";
        String email = emailedit.getText().toString();
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((MyApplication) getApplication()).getAppEnvironment();
        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            /* generateHashFromServer(mPaymentParams);*/

            /**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PayUMoneyActivity.this, AppPreference.selectedTheme, false);
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PayUMoneyActivity.this, R.style.AppTheme_default, false);
            }

        } catch (Exception e) {
            // some exception occurred
            Log.e("Message", e.getStackTrace().toString());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            paynowbtn.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    postOrder();

                } else {
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d("PAYU", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d("PAYU", "Both objects are null!");
            }
        }
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

                phone = mobile.getText().toString().trim();
                phone=phone.substring(3);
                mobile.setText(phone);
                if (!Validationemail.isEmailAddress(emailedit, true)) {
                    Toast.makeText(PayUMoneyActivity.this, "Please enter your valid email", Toast.LENGTH_SHORT).show();

                } else if (phone.trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else if (!Validationmobile.isPhoneNumber(mobile, true)) {
                    Toast.makeText(getApplicationContext(), "Please enter your valid phone number", Toast.LENGTH_SHORT).show();

                } else {
                    showCustomDialog1();

                }
                break;
            case R.id.view_coupon:
                recycler_layout.setVisibility(View.VISIBLE);
                break;


        }
    }

    private void discount() {
        if (i == 0) {
            coupon = discount.getText().toString();
            if (coupon.trim().length() != 0) {
                if (Integer.parseInt(totalamount) > 50) {
                    int a = Integer.parseInt(totalamount) - 50;
                    totalamount = String.valueOf(a);

                    t_pay.setText(totalamount);
                    discount_price.setText("50");
                    discount_layout.setVisibility(View.VISIBLE);
                }
            }
            i = 1;
        }


    }

    protected void showCustomDialog1() {
        // TODO Auto-generated method stub

        final Dialog dialog = new Dialog(PayUMoneyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.paybywalletorbankdata);

        btnsubmit1 = (Button) dialog.findViewById(R.id.res_pay_submit);
        btncancel = (Button) dialog.findViewById(R.id.res_pay_cancel);
        txtpayamount = (TextView) dialog.findViewById(R.id.pay_amount);
        btnradonline = (RadioButton) dialog.findViewById(R.id.online);
        btnradcod = (RadioButton) dialog.findViewById(R.id.cod);
        if (remove_cod.trim().length() != 0) {
            btnradcod.setVisibility(View.GONE);
        } else {
            btnradcod.setVisibility(View.VISIBLE);
        }

        txtpayamount.setText(totalamount);
        btnradonline.setOnClickListener(this);
        btnradcod.setOnClickListener(this);

        dialog.setCanceledOnTouchOutside(true);

        btnsubmit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("dismiss", "dialog dismiss");

                if (btnradonline.isChecked() || btnradcod.isChecked()) {
                    dialog.dismiss();
                    if (btnradonline.isChecked()) {
                        cod = 0;
                        launchPayUMoneyFlow();
                    } else {
                        cod = 1;
                        postOrder();
//                        postCheck();

//cms
                    }
                } else {
                    Toast.makeText(PayUMoneyActivity.this, "Select the payment method", Toast.LENGTH_SHORT).show();
                }


            }
        });


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progressDialog.dismiss();
                dialog.dismiss();
                if (getApplicationContext() != null) {
                    finish();
                    startActivity(new Intent(PayUMoneyActivity.this, PayUMoneyActivity.class));
                }
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }

    public void postOrder() {
        phone = mobile.getText().toString().trim();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", emailstring);
            jsonBody.put("financial_status", "pending");


            JSONArray line_items = new JSONArray();
            JSONObject items = new JSONObject();
            if (product_varientid.trim().length() == 0) {
                for (int i = 0; i < cartlist.size(); i++) {

                    product_varientid = cartlist.get(i).getProduct_varient_id();
                    byte[] tmp2 = Base64.decode(product_varientid, Base64.DEFAULT);
                    String val2 = new String(tmp2);
                    String[] str = val2.split("/");
                    product_varientid = str[4];

                    Integer quantity = cartlist.get(i).getQty();
                    items.put("variant_id", "5823671107611");
//                    items.put("variant_id", product_varientid.trim());
                    items.put("quantity", quantity);
                    line_items.put(items);
                    jsonBody.put("line_items", line_items);
                }
            } else {

                items.put("variant_id", "5823671107611");
//                items.put("variant_id", product_varientid.trim());
                items.put("quantity", product_qty);
                line_items.put(items);
                jsonBody.put("line_items", line_items);

            }


            JSONArray note = new JSONArray();
            JSONObject notes = new JSONObject();

            notes.put("name", "paypal");
            notes.put("value", "78233011");

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
            billingaddress.put("first_name", firstname);
            billingaddress.put("last_name", lastname);
            billingaddress.put("address1", b_address1);
            billingaddress.put("phone", phone);
            billingaddress.put("city", b_city);
            billingaddress.put("province", b_state);
            billingaddress.put("country", b_country);
            billingaddress.put("zip", b_zip);
            jsonBody.put("billing_address", billingaddress);

            JSONArray cost = new JSONArray();
            JSONObject costobject = new JSONObject();
            if (cod == 1) {
                kind_transaction = "cod";
            } else {
                kind_transaction = "online";
            }
            costobject.put("kind", kind_transaction);
            costobject.put("status", "success");
            costobject.put("amount", totalcost);

            cost.put(costobject);
            jsonBody.put("transactions", cost);


            Log.d("check JSON", jsonBody.toString());


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.postcreateorder, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        String msg = obj.getString("msg");

                        Log.e("msg", "" + msg);
                        if (msg.equals("success")) {
                            Iterator keys = obj.keys();
                            Log.e("Keys", "" + String.valueOf(keys));

                            while (keys.hasNext()) {
                                dynamicKey = (String) keys.next();
                                Log.d("Dynamic Key", "" + dynamicKey);
                                if (dynamicKey.equals("order")) {
                                    JSONObject order = obj.getJSONObject("order");
                                    String orderid = order.getString("id");
                                    Log.e("orderid", orderid);

                                }
                            }
                            Toast.makeText(PayUMoneyActivity.this, "Your Order Placed Sucessfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PayUMoneyActivity.this, MainActivity.class);
                            startActivity(i);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
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
                    String statusCode = String.valueOf(response.statusCode);
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

    public void postCheck() {
        phone = mobile.getText().toString().trim();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonBody1 = new JSONObject();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", emailstring);
            jsonBody.put("financial_status", "pending");


            JSONArray line_items = new JSONArray();
            JSONObject items = new JSONObject();
            if (product_varientid.trim().length() == 0) {
                for (int i = 0; i < cartlist.size(); i++) {

                    product_varientid = cartlist.get(i).getProduct_varient_id();
                    byte[] tmp2 = Base64.decode(product_varientid, Base64.DEFAULT);
                    String val2 = new String(tmp2);
                    String[] str = val2.split("/");
                    product_varientid = str[4];

                    Integer quantity = cartlist.get(i).getQty();

                    items.put("variant_id", product_varientid.trim());
                    items.put("quantity", quantity);
                    line_items.put(items);
                    jsonBody.put("line_items", line_items);
                }
            } else {


                items.put("variant_id", product_varientid.trim());
                items.put("quantity", product_qty);
                line_items.put(items);
                jsonBody.put("line_items", line_items);

            }

            jsonBody1.put("order", jsonBody);

            Log.d("check JSON", jsonBody1.toString());


            final String requestBody = jsonBody1.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.postch, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        String msg = obj.getString("msg");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
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
                    String statusCode = String.valueOf(response.statusCode);
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

    private void getDiscount() {
        discountlist.clear();
        mRequestQueue = Volley.newRequestQueue(PayUMoneyActivity.this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.getDiscount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("response", " " + response);

                            JSONObject obj = new JSONObject(response);
                            Log.e("response1", response);

                            JSONArray jsonarray = obj.getJSONArray("discounts");
                            Log.e("jsonarray", String.valueOf(jsonarray));

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject collectionobject = jsonarray.getJSONObject(i);

                                DiscountModel discountModel = new DiscountModel();
                                String discountname = collectionobject.getString("title");
                                String value = collectionobject.getString("value");
                                discountModel.setTitle(discountname);
                                discountModel.setValue(value);


                                Log.e("discountname", discountname);
                                Log.e("value", value);
                                discountlist.add(discountModel);
                            }


                            discountAdapter.notifyDataSetChanged();
//
//


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.getMessage());

                    }
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


    @Override
    public void discountValue(String discounted_amount, String coupon) {
        if (discounted_amount.trim().length() != 0) {
            int amount = Integer.parseInt(discounted_amount);

            String val2 = new String(discounted_amount);
            String[] str = val2.split("-");
            discounted_amount = str[1];
            Log.e("amount", String.valueOf(discounted_amount));
            totalcost=totalamount;
            if (Integer.parseInt(totalcost) > amount) {
                discount_layout.setVisibility(View.VISIBLE);
                int a = Integer.parseInt(totalcost) + amount;
                totalcost = String.valueOf(a);

                t_pay.setText(totalcost);
                discount_price.setText(discounted_amount);
                apply_coupon.setText("Your Applied Coupon Code is : " + coupon);
                recycler_layout.setVisibility(View.GONE);
                view_coupon.setText(R.string.view);
                view_coupon.setVisibility(View.VISIBLE);

            }
        }
    }
}
