package com.tredy.user.tredy.ccavenue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tredy.user.tredy.bag.OrderDetailModel;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.utility.AvenuesParams;
import com.tredy.user.tredy.utility.ServiceUtility;

import java.util.ArrayList;


public class InitialActivity extends AppCompatActivity {

    private EditText   amount, orderId;
    String accessCode,merchantId,currency,rsaKeyUrl,redirectUrl,cancelUrl;
    String emailstring, totalamount, firstname = "", lastname = "", bfirstname = "", blastname = "", address1 = "", city = "", state = "", country = "", zip = "", phone = "", b_address1 = "", b_city = "", b_state = "", b_country = "", b_zip = "", product_varientid, product_qty, discounted_price, discount_coupon;
   String s_mobile,b_mobile,b_email;
    private ArrayList<OrderDetailModel> orderDetailModelArrayList=new ArrayList<>();


    private void init(){
        accessCode ="AVML80FJ99AW34LMWA";
        merchantId = "139259";
        orderId  = (EditText) findViewById(R.id.orderId);
        currency = "INR";
        amount = (EditText) findViewById(R.id.amount);
        rsaKeyUrl = "http://52.66.204.219/GetRSA.php";
        redirectUrl = "http://52.66.204.219/ccavResponseHandler.php";
        cancelUrl ="http://52.66.204.219/ccavResponseHandler.php";

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        init();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        OrderDetailModel detail = (OrderDetailModel) bundle.getSerializable("value");

        emailstring = detail.getEmailstring();
        Log.e("email", " "+emailstring);
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
        discounted_price=detail.getDiscounted_price();
        discount_coupon=detail.getDiscount_coupon();
        s_mobile=detail.getS_mobile();
        b_mobile=detail.getB_mobile();
        b_email=detail.getB_email();

        if (discount_coupon == null) {
            discounted_price = "";
            discount_coupon = "";
        }
        product_varientid = detail.getVarientid();
        if (product_varientid == null) {
            product_varientid = " ";
        }
        amount.setText(totalamount);

    }


    public void onClick(View view) {
        OrderDetailModel orderDetailModel=new OrderDetailModel(emailstring,totalamount,firstname,lastname,bfirstname,blastname,address1,city,state,country,zip,phone,b_address1,b_city,b_state,b_country,b_zip, product_varientid,product_qty,discounted_price,discount_coupon,s_mobile,b_mobile,b_email);
        orderDetailModelArrayList.add(orderDetailModel);

        String vAccessCode = ServiceUtility.chkNull(accessCode).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchantId).toString().trim();
        String vCurrency = ServiceUtility.chkNull(currency).toString().trim();
        String vAmount = ServiceUtility.chkNull(amount.getText()).toString().trim();
        if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
            Intent intent = new Intent(this,WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amount.getText()).toString().trim());

            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl).toString().trim());
            Bundle bundle = new Bundle();
            bundle.putSerializable("value", orderDetailModelArrayList.get(0));
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
//            showToast("All parameters are mandatory.");
            Config.Dialog("All parameters are mandatory.", InitialActivity.this);

        }
    }



    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //generating order number
        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId.setText(randomNum.toString());
    }
}
