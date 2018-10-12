package com.example.user.trendy.CcAvenue;

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
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.R;
import com.example.user.trendy.Utility.AvenuesParams;
import com.example.user.trendy.Utility.Constants;
import com.example.user.trendy.Utility.LoadingDialog;
import com.example.user.trendy.Utility.RSAUtility;
import com.example.user.trendy.Utility.ServiceUtility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
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

    Intent mainIntent;
    String html, encVal;
    int MyDeviceAPI;

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
                        } else if (html.indexOf("Aborted") != -1) {
                            status = "Transaction Cancelled!";
                        } else {
                            status = "Status Not Known!";
                        }
                        //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                        intent.putExtra("transStatus", status);
                        startActivity(intent);
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
                String postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), "UTF-8") + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID), "UTF-8") + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8") + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL), "UTF-8") + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.CANCEL_URL), "UTF-8") + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8");
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

        myBrowser = (WebView) findViewById(R.id.webView);
        webSettings = myBrowser.getSettings();
        webSettings.setJavaScriptEnabled(true);

        MyDeviceAPI = Build.VERSION.SDK_INT;
        //get rsa key method
        get_RSA_key(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
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
Log.e("inside","came");
                        if (vResponse.contains("!ERROR!")) {
                            Log.e("inside1","came");

                            show_alert(vResponse);
                        } else {
                            Log.e("inside2","came");
                            new RenderView().execute();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.cancelLoading();
                        Log.e("inside3","came");
                        Log.e("message",error.toString());
                        Toast.makeText(WebViewActivity.this,error.toString(), Toast.LENGTH_LONG).show();
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

}
