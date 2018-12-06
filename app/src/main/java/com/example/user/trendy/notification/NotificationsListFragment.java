package com.example.user.trendy.notification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.R;
import com.example.user.trendy.util.Config;
import com.example.user.trendy.util.Constants;
import com.example.user.trendy.util.Internet;
import com.example.user.trendy.util.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;


public class NotificationsListFragment extends Fragment  {
    List<NotificationListSet> actorsList= new ArrayList<NotificationListSet>();;
    private RecyclerView recyclerView;
    NotificationListAdapter adapter;
    String userid, familyid = "";
//    CustomSwipeRefreshLayout swipeLayout;
    LinearLayout notification_lp;
    LinearLayout progressBar;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    public static boolean active = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private boolean loading = false;
    private ArrayList<NotificationListSet> addedList = new ArrayList<>();
    private String nextPageUrl = "";
//    private ProgressBar paginationProgress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notificationlist, container, false);

//        paginationProgress = view.findViewById(R.id.pagination_progress_bar);
//        swipeLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
//        fragmentManager = getFragmentManager();


//        ((HomePage)getActivity()).setToolbar(toolbar, "Notification");
//        notification_lp = (LinearLayout) view.findViewById(R.id.notification_lp);
//        notification_lp.setVisibility(View.GONE);
//        progressBar = (LinearLayout) view.findViewById(R.id.linearProgressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);





//        swipeLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        actorsList.clear();
//                        Log.e("swipe_refresh", "came");
//                        nextPageUrl = "";
//                        NotificationCountstatuslist();
//
//                        //adapter.notifyDataSetChanged();
//                        swipeLayout.refreshComplete();
//
//                    }
//                }, 5000);
//                // do something here when it starts to refresh
//                // e.g. to request data from server
//            }
//        });

        //set RefreshCheckHandler (OPTIONAL)
//        swipeLayout.setRefreshCheckHandler(new CustomSwipeRefreshLayout.RefreshCheckHandler() {
//            @Override
//            public boolean canRefresh() {
//                // return false when you don't want to trigger refresh
//                // e.g. return false when network is disabled.
//                if (Internet.isConnected(getActivity())) {
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        });

//        if (getActivity() != null) {
//            if (Internet.isConnected(getActivity())) {
//                progressBar.setVisibility(View.VISIBLE);
//                actorsList.clear();
//                NotificationCountstatuslist();
//            } else {
//            }
//        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotificationListAdapter(actorsList,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        NotificationCountstatuslist();
    }


//    public void onRefresh() {
//        // TODO Auto-generated method stub
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                actorsList.clear();
//                NotificationCountstatuslist();
//                // adapter.notifyDataSetChanged();
//                swipeLayout.refreshComplete();
//
//            }
//        }, 5000);
//    }


//    public interface CustomSwipeRefreshHeadLayout {
//        void onStateChange(CustomSwipeRefreshLayout.State currentState, CustomSwipeRefreshLayout.State lastState);
//    }

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
//        if (getActivity() != null) {
//            if (Internet.isConnected(getActivity())) {
//                progressBar.setVisibility(View.VISIBLE);
//                actorsList.clear();
//                NotificationCountstatus();
//            } else {
//                noInternetLayout.setVisibility(View.VISIBLE);
//            }
//        }
        Log.e("resume","inside");
//        if (Internet.isConnected(getActivity())) {
//            NotificationCountstatuslist();
//        } else {
//            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
//        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.PUSH_NOTIFICATION);
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(broadcastReceiver, filter);
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
        actorsList.clear();
        String token = SharedPreference.getData("customerid", getActivity());

        String minusdatet=getCalculatedDate( "MM/dd/yyyy", -10);

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.getallnotification+token.trim()+"?from="+minusdatet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            paginationProgress.setVisibility(View.GONE);
//                            progressBar.setVisibility(View.GONE);
                            JSONArray array = new JSONArray(response);

                            Log.e("response", response);

                            Log.e("array", String.valueOf(array.length()));


                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                NotificationListSet actor = new NotificationListSet();
                                String id=object1.getString("_id");
                                Log.e("iddd", id);
                                JSONObject object=object1.getJSONObject("notification");

                                Log.e("iddd", " "+object.getString("title")+" "+object1.getString("read_at"));

                                actor.setTitle(object.getString("title"));
                                actor.setPnew(object1.getString("read_at"));

                                actor.setPid(id);
                                actorsList.add(actor);

                            }
                        adapter.notifyDataSetChanged();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        paginationProgress.setVisibility(View.GONE);
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


    private void addFixedItems() {
        Log.e("item_size", String.valueOf(actorsList.size()));
        for (int i = 0; i < actorsList.size(); i++) {
            if (i < 10) {
                addedList.add(actorsList.get(i));
            } else {
                break;
            }

        }
        progressBar.setVisibility(View.GONE);
        notification_lp.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }



    private ViewHolder getViewHolderObject() {
        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(actorsList.size() + 1);
        if (null != holder) {
            Log.e("holder_check", "came");
//            holder.itemView.setVisibility(View.VISIBLE);
            return holder;
        } else {
            return null;
        }

    }

    private void setFooterVisibility(String visibilityStatus) {

        ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(actorsList.size()));
        Log.e("holder_check3", "" + viewHolder);

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(actorsList.size() + 1);
                Log.e("holder_check1", "came");
                if (holder != null) {
                    Log.e("holder_check2", "came");
                    if (visibilityStatus.equals("VISIBLE")) {
                        holder.itemView.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemView.setVisibility(View.GONE);
                    }

                }
            }
        }, 50);

    }

}
