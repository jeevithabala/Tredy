package com.tredy.user.tredy.notification;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.Tawk;
import com.tredy.user.tredy.util.Config;
import com.tredy.user.tredy.util.Constants;
import com.tredy.user.tredy.util.Internet;
import com.tredy.user.tredy.util.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.support.v7.widget.RecyclerView.*;


public class NotificationsListFragment extends Fragment implements NotificationListAdapter.noticount {
    List<NotificationListSet> actorsList = new ArrayList<NotificationListSet>();
    private RecyclerView recyclerView;
    NotificationListAdapter adapter;
    public static boolean active = false;
    TextView noti_text, read_all;
    private ProgressDialog progressDoalog;
    private FloatingActionButton chat_button;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notificationlist, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        read_all = view.findViewById(R.id.read_all);
        noti_text = view.findViewById(R.id.noti_text);
        chat_button=view.findViewById(R.id.chat_button);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(((Navigation) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Notification");
        progressDoalog = new ProgressDialog(getActivity());

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


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotificationListAdapter(actorsList, getActivity(), this,getFragmentManager());
        recyclerView.setAdapter(adapter);
        if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            progressDoalog.setMessage("loading....");
            progressDoalog.setTitle("Processing");
            progressDoalog.setCanceledOnTouchOutside(false);
            progressDoalog.show();
            NotificationCountstatuslist();
            getNotiCount();
        } else {
            Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }

        read_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDoalog.setMessage("loading....");
                progressDoalog.setTitle("Processing");
                progressDoalog.setCanceledOnTouchOutside(false);
                progressDoalog.show();
                if (actorsList.size() > 0) {
                    if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                        for (int i = 0; i < actorsList.size(); i++) {
                            if (actorsList.get(i).getPnew().equals("null")) {
                                registperp(actorsList.get(i).getPid());
                            }
                        }
                        getNotiCount();
                    } else {
                        progressDoalog.dismiss();
                        Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDoalog.dismiss();
                }
            }
        });

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("notification_recieved", "came");
            if (getActivity() != null) {
                if (Internet.isConnected(getActivity())) {
                    actorsList.clear();
                    NotificationCountstatuslist();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.PUSH_NOTIFICATION);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).
                registerReceiver(broadcastReceiver, filter);

        if (Config.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            getNotiCount();
        } else {
            Toast.makeText(getActivity(), "Please Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPause() {
        active = false;
        super.onPause();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

        }

    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }


    private void NotificationCountstatuslist() {
        if (getActivity() != null) {

            String token = SharedPreference.getData("customerid", getActivity());
            Log.e("customer_id", " " + token);

            String minusdatet = getCalculatedDate("MM/dd/yyyy", -10);

            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.getallnotification + token.trim() + "?from=" + minusdatet,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
//                            paginationProgress.setVisibility(View.GONE);
//                            progressBar.setVisibility(View.GONE);
                                JSONArray array = new JSONArray(response);
                                Log.e("response", response);

                                actorsList.clear();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object1 = array.getJSONObject(i);
                                    NotificationListSet actor = new NotificationListSet();
                                    String id = object1.getString("_id");
                                    JSONObject object = object1.getJSONObject("notification");

                                    actor.setTitle(object.getString("title"));
                                    actor.setPnew(object1.getString("read_at"));
                                    if (object.has("order_name") && !object.isNull("order_name")) {
                                        actor.setOrderid(object.getString("order_name"));
                                    } else {
                                        // Avoid this user.
                                    }
                                    if(object.has("checkout_id")&&!object.isNull("checkout_id")){
                                        actor.setCheckout_id(object.getString("checkout_id"));
                                    }else {
//                                        actor.setCheckout_id(object.getString(""));
                                    }
                                    actor.setPid(id);
                                    actorsList.add(actor);

                                }
                                if (actorsList.size() == 0) {
                                    noti_text.setVisibility(VISIBLE);
                                } else {
                                    noti_text.setVisibility(GONE);
                                }
                                adapter.notifyDataSetChanged();
                                if (progressDoalog != null && progressDoalog.isShowing()) {
                                    progressDoalog.dismiss();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (progressDoalog != null && progressDoalog.isShowing()) {
                                progressDoalog.dismiss();
                            }
                        }
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


    public void getNotiCount() {
        String customerid = SharedPreference.getData("customerid", Objects.requireNonNull(getActivity()));
        String minusdatet = getCalculatedDate("MM/dd/yyyy", -10);


        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.unreadcount + customerid.trim() + "?from=" + minusdatet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            Log.e("response", response);
                            String count = obj.getString("count");
                            NotificationCountstatuslist();
                            Navigation.noti_counnt = Integer.parseInt(count);
                            if (getActivity() != null) {
                                getActivity().invalidateOptionsMenu();
                            }

                        } catch (JSONException e) {
                            if (progressDoalog != null && progressDoalog.isShowing()) {
                                progressDoalog.dismiss();
                            }
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDoalog != null && progressDoalog.isShowing()) {
                            progressDoalog.dismiss();
                        }
                    }
                }) {

        };
        stringRequest.setTag("noti");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);


    }

    private void registperp(String s) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constants.readnotification + s,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            if (progressDoalog != null && progressDoalog.isShowing()) {
                                progressDoalog.dismiss();
                            }

                        } catch (JSONException e) {
                            if (progressDoalog != null && progressDoalog.isShowing()) {
                                progressDoalog.dismiss();
                            }
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDoalog != null && progressDoalog.isShowing()) {
                            progressDoalog.dismiss();
                        }
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
    public void noticountchange(String id) {
        getNotiCount();
    }
}
