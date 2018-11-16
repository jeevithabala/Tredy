package com.example.user.trendy.Notification;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.Constants;
import com.example.user.trendy.Util.Internet;
import com.example.user.trendy.Util.SharedPreference;
import com.example.user.trendy.Util.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NotificationDataFragment extends AppCompatActivity {
    //implements FragmentManager.OnBackStackChangedListener
    String title, dec = "", date="", name, id, pnew, pread;
    ImageView imageView;
    TextView textView, pdtitle;
    TextView pdate, pd_time;
    private FragmentManager fragmentManager;
    TextView pdec;
    String userid;
    private Toolbar toolbar;
    String image = "";
    ImageView imagenot;
    SimpleDateFormat simpleDateFormatinput = new SimpleDateFormat("yyyy-mm-dd");
    SimpleDateFormat simpleDateFormatoutput = new SimpleDateFormat("dd-mm-yyyy");

    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.notificationdata, container, false);


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationdata);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        pnew = intent.getStringExtra("pnew");
//        pread = intent.getStringExtra("pread");
//        name = intent.getStringExtra("name");
        title = intent.getStringExtra("title");
//        dec = intent.getStringExtra("dec");
//        date = intent.getStringExtra("date");
//        image = intent.getStringExtra("image");


//        id = getArguments().getString("id");
//        pnew = getArguments().getString("pnew");
//        pread = getArguments().getString("pread");
//        name = getArguments().getString("name");
//        title = getArguments().getString("title");
//        dec = getArguments().getString("dec");
//        date = getArguments().getString("date");
//        image = getArguments().getString("image");
//        Log.e("imageabc", "" + image);
//        imagenot = findViewById(R.id.imagenot);
//        if (image != null) {
//            if (image.trim().length() != 0) {
//                Picasso.with(this)
//                        .load(image)
//                        .noFade()
//                        .into(imagenot);
//            }
//        }


        pdtitle = (TextView) findViewById(R.id.pd_title);
        pdtitle.setText(title);
//        pdate = (TextView) findViewById(R.id.pd_date);
//        pd_time = (TextView) findViewById(R.id.pd_time);
//        // pdate.setText(date);
//        pdec = (TextView) findViewById(R.id.residencial_desc_text);
//        if (dec != null) {
//            pdec.setText(dec);
//        }

//        String[] split = date.split(" ");
//        String dately = split[0];
//        String time = split[1];
//
//
//        if (date.length() != 0) {
//            Date date1 = null;  // <---  yyyy-mm-dd
//            try {
//                date1 = simpleDateFormatinput.parse(dately);
//                dately = simpleDateFormatoutput.format(date1);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        pdate.setText(dately);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        ParsePosition pos = new ParsePosition(0);
//        long then = formatter.parse(date, pos).getTime();
//        long now = new Date().getTime();
//
//        long seconds = (now - then) / 1000;
//        long minutes = seconds / 60;
//        long hours = minutes / 60;
//        long days = hours / 24;
//
//        String friendly = null;
//        long num = 0;
//        if (days > 0) {
//            num = days;
//            friendly = days + " day";
//        } else if (hours > 0) {
//            num = hours;
//            friendly = hours + " hour";
//        } else if (minutes > 0) {
//            num = minutes;
//            friendly = minutes + " minute";
//        } else {
//            num = seconds;
//            friendly = seconds + " second";
//        }
//        if (num > 1) {
//            friendly += "s";
//        }
//        String createdAt = friendly + " ago";
//        Log.e("TotalTime>>", "abc" + createdAt);
//        pd_time.setText("Updated " + createdAt);
        if (!pnew.equals("null")) {

            if (Internet.isConnected(getApplicationContext())) {
                registperp(id);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void registperp(String s) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constants.readnotification + s,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            Log.e("response", response);


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
        }
    }

}
