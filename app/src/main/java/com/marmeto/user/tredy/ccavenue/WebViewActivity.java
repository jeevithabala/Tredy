package com.marmeto.user.tredy.ccavenue;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.bag.PayUMoneyActivity;
import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.bag.cartdatabase.DBHelper;
import com.marmeto.user.tredy.bag.OrderDetailModel;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.util.Config;
import com.marmeto.user.tredy.util.Internet;
import com.marmeto.user.tredy.util.SharedPreference;
import com.marmeto.user.tredy.util.VolleySingleton;
import com.marmeto.user.tredy.utility.AvenuesParams;
import com.marmeto.user.tredy.utility.Constants;
import com.marmeto.user.tredy.utility.LoadingDialog;
import com.marmeto.user.tredy.utility.RSAUtility;
import com.marmeto.user.tredy.utility.ServiceUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class WebViewActivity extends AppCompatActivity implements Communicator {

    WebView myBrowser;
    WebSettings webSettings;
    private BroadcastReceiver mIntentReceiver;
    String bankUrl = "";
    FragmentManager manager;
    ActionDialog actionDialog = new ActionDialog();
    Timer timer = new Timer();
    TimerTask timerTask;
    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    public int loadCounter = 0;
    DBHelper db;
    List<AddToCart_Model> cartlist = new ArrayList<>();
    Intent mainIntent;
    String html, encVal, transaction_id;
    int MyDeviceAPI;
    String emailstring, totalamount, firstname = "", lastname = "", bfirstname = "", blastname = "", address1 = "", city = "", state = "", country = "", zip = "", phone = "", b_address1 = "", b_city = "", b_state = "", b_country = "", b_zip = "", product_varientid, product_qty, discounted_price, discount_coupon;
    String finalhtml = " ", b_phone = "";
    int costtotal = 0;
    private int buynow = 0;
    OrderDetailModel model;
    int ordercount = 0;
    private RequestQueue mRequestQueue;

    /**
     * Async task class to get json by making HTTP call
     */
    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                if (!ServiceUtility.chkNull(vResponse).equals("")
                        && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                    StringBuffer vEncVal = new StringBuffer("");
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                    encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            LoadingDialog.cancelLoading();
            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    try {
                        // process the html source code to get final status of transaction
                        Log.v("Logs", "-------------- Process HTML : " + html);
                        String status = null;
                        if (html.indexOf("Failure") != -1) {
                            status = "Transaction Declined!";
                        } else if (html.indexOf("Success") != -1) {
                            status = "Transaction Successful!";
                            finalhtml = html;
                            if (ordercount == 0) {
                                getData();
                            }
                        } else if (html.indexOf("Aborted") != -1) {
                            status = "Transaction Cancelled!";
                        } else {
                            status = "Status Not Known!";
                        }
//                        setResult(203);
//                        finish();
//                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
//                        if (!status.equals("Transaction Successful!")) {
//                            Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("transStatus", status);
//                            startActivity(intent);
//                            finish();
//

                        if (!status.equals("Transaction Successful!")) {
                            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(WebViewActivity.this, Navigation.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("message", "m");
                            startActivity(i);
//                            finish();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("Logs", "-------------- Error : " + e);
                    }
                }
            }

            //final WebView webview = (WebView) findViewById(R.id.webView);
            //myBrowser.getSettings().setJavaScriptEnabled(true);
            myBrowser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            myBrowser.setWebViewClient(new WebViewClient() {
                /*@Override
                public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                    bankUrl = url;
                    return false;
                }*/


                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    bankUrl = url;
                    return false;
                }

                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    bankUrl = url;
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(myBrowser, url);
                    LoadingDialog.cancelLoading();
                    if (url.indexOf("/ccavResponseHandler.php") != -1) {
                        myBrowser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }

                    // calling load Waiting for otp fragment
                    if (loadCounter < 1) {
                        if (MyDeviceAPI >= 19) {
                            loadCitiBankAuthenticateOption(url);
                            loadWaitingFragment(url);
                        }
                    }
                    bankUrl = url;
                }


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");
                }
            });

            try {
                String postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), "UTF-8") + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID), "UTF-8") + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8") + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL), "UTF-8") + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.CANCEL_URL), "UTF-8") + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8")
                        + "&" + AvenuesParams.BILLING_NAME + "=" + URLEncoder.encode(model.getFirstname(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_ADDRESS + "=" + URLEncoder.encode(model.getAddress1(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_CITY + "=" + URLEncoder.encode(model.getCity(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_STATE + "=" + URLEncoder.encode(model.getState(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_ZIP + "=" + URLEncoder.encode(model.getZip(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_COUNTRY + "=" + URLEncoder.encode(model.getCountry(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_EMAIL + "=" + URLEncoder.encode(model.getEmailstring(), "UTF-8")
                        + "&" + AvenuesParams.BILLING_MOBILENUMBER + "=" + URLEncoder.encode(model.getS_mobile(), "UTF-8")

                        + "&" + AvenuesParams.DELIVERY_NAME + "=" + URLEncoder.encode(model.getBfirstname(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_ADDRESS + "=" + URLEncoder.encode(model.getB_address1(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_CITY + "=" + URLEncoder.encode(model.getB_city(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_STATE + "=" + URLEncoder.encode(model.getB_state(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_ZIP + "=" + URLEncoder.encode(model.getB_zip(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_COUNTRY + "=" + URLEncoder.encode(model.getB_country(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_EMAIL + "=" + URLEncoder.encode(model.getB_email(), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_MOBILENUMBER + "=" + URLEncoder.encode(model.getB_mobile(), "UTF-8");
//
//                + "&" + AvenuesParams.BILLING_NAME + "=" + URLEncoder.encode(model.getBfirstname(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_ADDRESS + "=" + URLEncoder.encode(model.getB_address1(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_CITY + "=" + URLEncoder.encode(model.getB_city(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_STATE + "=" + URLEncoder.encode(model.getB_state(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_ZIP + "=" + URLEncoder.encode(model.getB_zip(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_COUNTRY + "=" + URLEncoder.encode(model.getB_country(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_EMAIL + "=" + URLEncoder.encode(model.getB_email(), "UTF-8")
//                        + "&" + AvenuesParams.BILLING_MOBILENUMBER + "=" + URLEncoder.encode(model.getB_mobile(), "UTF-8")


                myBrowser.postUrl(Constants.TRANS_URL, postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        mainIntent = getIntent();
        manager = getFragmentManager();

        Bundle bundle = mainIntent.getExtras();

        model = (OrderDetailModel) bundle.getSerializable("value");

        myBrowser = (WebView) findViewById(R.id.webView);
        webSettings = myBrowser.getSettings();
        webSettings.setJavaScriptEnabled(true);

        MyDeviceAPI = Build.VERSION.SDK_INT;
        //get rsa key method
        get_RSA_key(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
        getpreviousData();
    }

    // Method to start Timer for 30 sec. delay
    public void startTimer() {
        try {
            //set a new Timer
            if (timer == null) {
                timer = new Timer();
            }
            //initialize the TimerTask's job
            initializeTimerTask();

            //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
            timer.schedule(timerTask, 30000, 30000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to Initialize Task
    public void initializeTimerTask() {
        try {
            timerTask = new TimerTask() {
                public void run() {

                    //use a handler to run a toast that shows the current timestamp
                    handler.post(new Runnable() {
                        public void run() {
                        /*int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), "I M Called ..", duration);
                        toast.show();*/
                            loadActionDialog();
                        }
                    });
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to stop timer
    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void loadCitiBankAuthenticateOption(String url) {
        if (url.contains("https://www.citibank.co.in/acspage/cap_nsapi.so")) {
            CityBankFragment citiFrag = new CityBankFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, citiFrag, "CitiBankAuthFrag");
            transaction.commit();
            loadCounter++;
        }
    }

    public void removeCitiBankAuthOption() {
        CityBankFragment cityFrag = (CityBankFragment) manager.findFragmentByTag("CitiBankAuthFrag");
        FragmentTransaction transaction = manager.beginTransaction();
        if (cityFrag != null) {
            transaction.remove(cityFrag);
            transaction.commit();
        }
    }

    // Method to load Waiting for OTP fragment
    public void loadWaitingFragment(String url) {

        // SBI Debit Card
        if (url.contains("https://acs.onlinesbi.com/sbi/")) {
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }

        // Kotak Bank Visa Debit card
        else if (url.contains("https://cardsecurity.enstage.com/ACSWeb/")) {
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // For SBI and All its Asscocites Net Banking
        else if (url.contains("https://merchant.onlinesbi.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbi.com/merchant/resendsmsotp.htm") || url.contains("https://m.onlinesbi.com/mmerchant/smsenablehighsecurity.htm")
                || url.contains("https://merchant.onlinesbh.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbh.com/merchant/resendsmsotp.htm")
                || url.contains("https://merchant.sbbjonline.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.sbbjonline.com/merchant/resendsmsotp.htm")
                || url.contains("https://merchant.onlinesbm.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbm.com/merchant/resendsmsotp.htm")
                || url.contains("https://merchant.onlinesbp.com/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.onlinesbp.com/merchant/resendsmsotp.htm")
                || url.contains("https://merchant.sbtonline.in/merchant/smsenablehighsecurity.htm") || url.contains("https://merchant.sbtonline.in/merchant/resendsmsotp.htm")) {
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }

        // For ICICI Credit Card
        else if (url.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer")) {
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // City bank Debit card
        else if (url.equals("cityBankAuthPage")) {
            removeCitiBankAuthOption();
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // HDFC Debit Card and Credit Card
        else if (url.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth")) {
            //removeCitiBankAuthOption();
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }
        // For SBI  Visa credit Card
        else if (url.contains("https://secure4.arcot.com/acspage/cap")) {
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        }

        // For Kotak Bank Visa Credit Card
        else if (url.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer")) {
            OtpFragment waitingFragment = new OtpFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.otp_frame, waitingFragment, "OTPWaitingFrag");
            transaction.commit();
            startTimer();
        } else {
            removeWaitingFragment();
            removeApprovalFragment();
            stopTimerTask();
        }

    }

    // Method to remove Waiting fragment
    public void removeWaitingFragment() {
        OtpFragment waitingFragment = (OtpFragment) manager.findFragmentByTag("OTPWaitingFrag");
        if (waitingFragment != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(waitingFragment);
            transaction.commit();
        } else {
            // DO nothing
            //Toast.makeText(this," --test-- ",Toast.LENGTH_SHORT).show();
        }
    }

    // Method to load Approve Otp Fragment
    public void loadApproveOTP(String otpText, String senderNo) {
        try {
            Integer vTemp = Integer.parseInt(otpText);

            if (bankUrl.contains("https://acs.onlinesbi.com/sbi/") && senderNo.contains("SBI") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For Kotak bank Debit Card
            else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/") && senderNo.contains("KOTAK") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // for SBI Net Banking
            else if ((((bankUrl.contains("https://merchant.onlinesbi.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbi.com/merchant/resendsmsotp.htm") || bankUrl.contains("https://m.onlinesbi.com/mmerchant/smsenablehighsecurity.htm")) && senderNo.contains("SBI"))
                    || ((bankUrl.contains("https://merchant.onlinesbh.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbh.com/merchant/resendsmsotp.htm")) && senderNo.contains("SBH"))
                    || ((bankUrl.contains("https://merchant.sbbjonline.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.sbbjonline.com/merchant/resendsmsotp.htm")) && senderNo.contains("SBBJ"))
                    || ((bankUrl.contains("https://merchant.onlinesbm.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbm.com/merchant/resendsmsotp.htm")) && senderNo.contains("SBM"))
                    || ((bankUrl.contains("https://merchant.onlinesbp.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbp.com/merchant/resendsmsotp.htm")) && senderNo.contains("SBP"))
                    || ((bankUrl.contains("https://merchant.sbtonline.in/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.sbtonline.in/merchant/resendsmsotp.htm")) && senderNo.contains("SBT"))) && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For ICICI Visa Credit Card
            else if (bankUrl.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer") && senderNo.contains("ICICI") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For ICICI Debit card
            else if (bankUrl.contains("https://acs.icicibank.com/acspage/cap?") && senderNo.contains("ICICI") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For CITI bank Debit card
            else if (bankUrl.contains("https://www.citibank.co.in/acspage/cap_nsapi.so") && senderNo.contains("CITI") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For HDFC bank debit card and Credit Card
            else if (bankUrl.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth") && senderNo.contains("HDFC") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For HDFC Netbanking
            else if (bankUrl.contains("https://netbanking.hdfcbank.com/netbanking/entry") && senderNo.contains("HDFC") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            }
            // For SBI Visa credit Card
            else if (bankUrl.contains("https://secure4.arcot.com/acspage/cap") && senderNo.contains("SBI") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            } else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer") && senderNo.contains("KOTAK") && (otpText.length() == 6 || otpText.length() == 8)) {
                removeWaitingFragment();
                stopTimerTask();
                ApproveOTPFragment approveFragment = new ApproveOTPFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.otp_frame, approveFragment, "OTPApproveFrag");
                transaction.commit();
                approveFragment.setOtpText(otpText);
            } else {
                removeApprovalFragment();
                stopTimerTask();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeApprovalFragment() {
        ApproveOTPFragment approveOTPFragment = (ApproveOTPFragment) manager.findFragmentByTag("OTPApproveFrag");
        if (approveOTPFragment != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(approveOTPFragment);
            transaction.commit();
        }
    }

    public void loadActionDialog() {

        try {
            actionDialog.show(getFragmentManager(), "ActionDialog");
            stopTimerTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    //removeWaitingFragment();
                    removeApprovalFragment();
                    ///////////////////////////////////////
                    String msgText = intent.getStringExtra("get_otp");
                    String otp = msgText.split("\\|")[0];
                    String senderNo = msgText.split("\\|")[1];
                    if (MyDeviceAPI >= 19) {
                        loadApproveOTP(otp, senderNo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Exception :" + e, Toast.LENGTH_SHORT).show();
                }
            }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mIntentReceiver);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    // On click of Approve button
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void respond(String otpText) {

        String data = otpText;
        try {
            // For SBI and all the associates
            if (bankUrl.contains("https://acs.onlinesbi.com/sbi/")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('otp').value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For Kotak Bank Debit card
            else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('txtOtp').value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For SBI Visa credit card
            else if (bankUrl.contains("https://secure4.arcot.com/acspage/cap")) {
                myBrowser.evaluateJavascript("javascript:document.getElementsByName('pin1')[0].value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For SBI and associates banks Net Banking
            else if (bankUrl.contains("https://merchant.onlinesbi.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbi.com/merchant/resendsmsotp.htm") || bankUrl.contains("https://m.onlinesbi.com/mmerchant/smsenablehighsecurity.htm")
                    || bankUrl.contains("https://merchant.onlinesbh.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbh.com/merchant/resendsmsotp.htm")
                    || bankUrl.contains("https://merchant.sbbjonline.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.sbbjonline.com/merchant/resendsmsotp.htm")
                    || bankUrl.contains("https://merchant.onlinesbm.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbm.com/merchant/resendsmsotp.htm")
                    || bankUrl.contains("https://merchant.onlinesbp.com/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.onlinesbp.com/merchant/resendsmsotp.htm")
                    || bankUrl.contains("https://merchant.sbtonline.in/merchant/smsenablehighsecurity.htm") || bankUrl.contains("https://merchant.sbtonline.in/merchant/resendsmsotp.htm")) {
                myBrowser.evaluateJavascript("javascript:document.getElementsByName('securityPassword')[0].value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For ICICI credit card
            else if (bankUrl.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('txtAutoOtp').value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For ICICI bank Debit card
            else if (bankUrl.contains("https://acs.icicibank.com/acspage/cap?")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('txtAutoOtp').value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For Citi Bank debit card
            else if (bankUrl.contains("https://www.citibank.co.in/acspage/cap_nsapi.so")) {
                myBrowser.evaluateJavascript("javascript:document.getElementsByName('otp')[0].value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For HDFC Debit card and Credit card
            else if (bankUrl.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('txtOtpPassword').value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // HDFC Net Banking
            else if (bankUrl.contains("https://netbanking.hdfcbank.com/netbanking/entry")) {
                myBrowser.evaluateJavascript("javascript:document.getElementsByName('fldOtpToken')[0].value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // For Kotak Band visa Credit Card
            else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('otpValue').value = '" + otpText + "'", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            // for CITI Bank Authenticate with option selection
            if (data.equals("password")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('uid_tb_r').click();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
            if (data.equals("smsOtp")) {
                myBrowser.evaluateJavascript("javascript:document.getElementById('otp_tb_r').click();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
                loadWaitingFragment("cityBankAuthPage");
            }
            loadCounter++;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void actionSelected(String data) {
        try {
            if (data.equals("ResendOTP")) {
                stopTimerTask();
                removeWaitingFragment();
                if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank")) {
                    myBrowser.evaluateJavascript("javascript:reSendOtp();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                // For HDFC Credit and Debit Card
                else if (bankUrl.contains("https://netsafe.hdfcbank.com/ACSWeb/jsp/dynamicAuth.jsp?transType=payerAuth")) {
                    myBrowser.evaluateJavascript("javascript:generateOTP();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                // SBI Visa Credit Card
                else if (bankUrl.contains("https://secure4.arcot.com/acspage/cap")) {
                    myBrowser.evaluateJavascript("javascript:OnSubmitHandlerResend();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                // For Kotak Visa Credit Card
                else if (bankUrl.contains("https://cardsecurity.enstage.com/ACSWeb/EnrollWeb/KotakBank/server/OtpServer")) {
                    myBrowser.evaluateJavascript("javascript:doSendOTP();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                // For ICICI Credit Card
                else if (bankUrl.contains("https://www.3dsecure.icicibank.com/ACSWeb/EnrollWeb/ICICIBank/server/OtpServer")) {
                    myBrowser.evaluateJavascript("javascript:resend_otp();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                } else {
                    myBrowser.evaluateJavascript("javascript:resendOTP();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                //loadCounter=0;
            } else if (data.equals("EnterOTPManually")) {
                stopTimerTask();
                removeWaitingFragment();
            } else if (data.equals("Cancel")) {
                stopTimerTask();
                removeWaitingFragment();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Action not available for this Payment Option !", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void get_RSA_key(final String ac, final String od) {
        LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, mainIntent.getStringExtra(AvenuesParams.RSA_KEY_URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(WebViewActivity.this,response,Toast.LENGTH_LONG).show();
                        LoadingDialog.cancelLoading();
                        vResponse = response;
                        if (vResponse.contains("!ERROR!")) {

                            show_alert(vResponse);
                        } else {
                            new RenderView().execute();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.cancelLoading();
                        Toast.makeText(WebViewActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AvenuesParams.ACCESS_CODE, ac);
                params.put(AvenuesParams.ORDER_ID, od);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    String vResponse;


    public void show_alert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                WebViewActivity.this).create();


        alertDialog.setTitle("Error!!!");
        if (msg.contains("\n"))
            msg = msg.replaceAll("\\\n", "");

        alertDialog.setMessage(msg);


        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });


        alertDialog.show();
    }

//    public RequestQueue getRequestQueue() {
//        // lazy initialize the request queue, the queue instance will be
//        // created when it is accessed for the first time
//        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        }
//
//        return mRequestQueue;
//    }

    public void getData() {

//        try {
        String[] separated = finalhtml.split("<td>");
        transaction_id = separated[6];
        String[] separated1 = transaction_id.split("</td>");
        transaction_id = separated1[0];
//        }catch (Exception e){
//            transaction_id="";
//        }

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        OrderDetailModel detail = (OrderDetailModel) bundle.getSerializable("value");
//        OrderDetailModel detail=new OrderDetailModel();
        emailstring = detail.getEmailstring();
        Log.e("email", " " + emailstring);
        totalamount = detail.getTotalamount();
        firstname = detail.getFirstname();
        lastname = detail.getLastname();
        bfirstname = detail.getBfirstname();
        blastname = detail.getBlastname();
        address1 = detail.getLastname();
        city = detail.getCity();
        state = detail.getState();
        country = detail.getCountry();
        zip = detail.getZip();
        phone = detail.getPhone();
        b_address1 = detail.getB_address1();
        b_city = detail.getCity();
        b_state = detail.getB_state();
        b_country = detail.getB_country();
        b_zip = detail.getB_zip();
        product_qty = detail.getQty();
        product_varientid = detail.getVarientid();
        discounted_price = detail.getDiscounted_price();
        discount_coupon = detail.getDiscount_coupon();
        if (discount_coupon == null) {
            discounted_price = "";
            discount_coupon = "";
        }
        if (product_varientid == null) {
            product_varientid = " ";
        }
        b_phone = detail.getB_mobile();
        db = new DBHelper(this);
        cartlist = db.getCartList();
        postOrder();
    }

    public void postOrder() {
        costtotal = Integer.parseInt(totalamount.trim());
        try {
            if (ordercount == 0) {
//                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", emailstring);
                jsonBody.put("financial_status", "paid");


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

                notes.put("name", "ccavenue");
                notes.put("value", transaction_id);

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
                billingaddress.put("phone", b_phone);
                billingaddress.put("city", b_city);
                billingaddress.put("province", b_state);
                billingaddress.put("country", b_country);
                billingaddress.put("zip", b_zip);
                jsonBody.put("billing_address", billingaddress);

                JSONArray cost = new JSONArray();
                JSONObject costobject = new JSONObject();

                String kind_transaction = "sale";

                costobject.put("kind", kind_transaction);
                costobject.put("status", "success");
                costobject.put("amount", costtotal);
                costobject.put("gateway", "ccavenue");

                cost.put(costobject);
                jsonBody.put("transactions", cost);


                Log.d("check JSON", jsonBody.toString());


                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, com.marmeto.user.tredy.util.Constants.postcreateorder, new Response.Listener<String>() {
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
                                    String dynamicKey = (String) keys.next();
                                    Log.d("Dynamic Key", "" + dynamicKey);
                                    if (dynamicKey.equals("order")) {
                                        JSONObject order = obj.getJSONObject("order");
                                        String orderid = order.getString("id");

                                    }
                                }
//                                if (buynow != 1) {
                                db.deleteCart(getApplicationContext());
//                                }
//                            Intent i = new Intent(WebViewActivity.this, Navigation.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            i.putExtra("message", "Transaction Successful!");
//                            startActivity(i);
                                Dialog("Your Order Placed Successfully");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

                };
                if (ordercount == 0) {
                    ordercount++;
//                    getRequestQueue().add(stringRequest);
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Dialog(String poptext) {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(WebViewActivity.this, R.style.AlertDialogStyle);
        builder.setTitle("Success");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        Intent i = new Intent(WebViewActivity.this, Navigation.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("message", "m");
                        startActivity(i);
                        finish();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.white);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getApplicationContext() != null) {
            if (Internet.isConnected(getApplicationContext())) {
                abandandCheckout();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getApplicationContext() != null) {
            if (Internet.isConnected(getApplicationContext())) {
                abandandCheckout();
            }
        }
    }

    public void abandandCheckout() {


        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        String orderId = randomNum.toString();
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
//                    items.put("variant_id", "5823671107611");
                    items.put("product_id", product_varientid.trim());
                    items.put("quantity", quantity);
                    line_items.put(items);
                    jsonBody.put("line_items", line_items);
                }
            } else {
                JSONObject items = new JSONObject();

//                items.put("variant_id", "5823671107611");
                items.put("product_id", product_varientid.trim());
                items.put("quantity", product_qty);
                line_items.put(items);
                jsonBody.put("line_items", line_items);

            }


//            JSONArray note = new JSONArray();
//            JSONObject notes = new JSONObject();
//
//            notes.put("name", "paypal");
//            notes.put("value", "78233011");
//
//            note.put(notes);
//            jsonBody.put("note_attributes", note);


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
            billingaddress.put("phone", b_phone);
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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, com.marmeto.user.tredy.util.Constants.createabandoned, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        String msg = obj.getString("msg");
                        Log.e("msg", " " + msg);


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

    public void getpreviousData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        OrderDetailModel detail = (OrderDetailModel) bundle.getSerializable("value");
//        OrderDetailModel detail=new OrderDetailModel();
        emailstring = detail.getEmailstring();
        Log.e("email", " " + emailstring);
        totalamount = detail.getTotalamount();
        firstname = detail.getFirstname();
        lastname = detail.getLastname();
        bfirstname = detail.getBfirstname();
        blastname = detail.getBlastname();
        address1 = detail.getLastname();
        city = detail.getCity();
        state = detail.getState();
        country = detail.getCountry();
        zip = detail.getZip();
        phone = detail.getPhone();
        b_address1 = detail.getB_address1();
        b_city = detail.getCity();
        b_state = detail.getB_state();
        b_country = detail.getB_country();
        b_zip = detail.getB_zip();
        product_qty = detail.getQty();
        product_varientid = detail.getVarientid();
        if (product_varientid == null) {
            product_varientid = " ";
        }
        db = new DBHelper(this);
        cartlist = db.getCartList();
    }

}
