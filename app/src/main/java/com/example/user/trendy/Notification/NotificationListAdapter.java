package com.example.user.trendy.Notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.user.trendy.R;
import com.example.user.trendy.Search.SearchModel;
import com.example.user.trendy.Util.Constants;
import com.example.user.trendy.Util.Internet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 4;
    private static final int TYPE_ITEM = 5;
    private List<NotificationListSet> customerlist;
    private Spinner spinner;
    String c_id;
    Context context;
    ArrayAdapter<CharSequence> spinnerAdapter;
    String image;
    private FragmentManager fragmentManager;
    SimpleDateFormat simpleDateFormatinput = new SimpleDateFormat("yyyy-mm-dd");
    SimpleDateFormat simpleDateFormatoutput = new SimpleDateFormat("dd-mm-yyyy");
    private boolean loading;


    static class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView title, year, genre;


        TextView title, iconText, date, name, time;
        ImageView imgProfile;
        LinearLayout notification;


        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            title = (TextView) view.findViewById(R.id.title);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            //  iconText = (TextView) view.findViewById(R.id.icon_text);
            date = (TextView) view.findViewById(R.id.icon_star);
            notification = (LinearLayout) view.findViewById(R.id.notificationp);
            time = (TextView) view.findViewById(R.id.time);


        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public View view;

        FooterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            itemView.setVisibility(View.GONE);
        }
    }

    public NotificationListAdapter(List<NotificationListSet> customerlist, Context context) {
        this.customerlist = customerlist;
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("ulhijio","hui");
        if (viewType == TYPE_ITEM) {
            Log.e("item_view", "came");
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notificationlistadapter, parent, false);
            return new ViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            Log.e("footer_view", "came");
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_list_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            return null;
        }


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final String id = customerlist.get(position).getPid();
            final String pnew = customerlist.get(position).getPnew();
            Log.e("pnew", " "+pnew);

            final String title = customerlist.get(position).getTitle();

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.title.setText(customerlist.get(position).getTitle());
            if (pnew.equals("null")) {
//                viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                viewHolder.name.setTypeface(null, Typeface.NORMAL);
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                viewHolder.title.setTypeface(null, Typeface.NORMAL);
                viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                viewHolder.date.setTypeface(null, Typeface.NORMAL);
            } else {
                viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextnuread));
                viewHolder.name.setTypeface(null, Typeface.BOLD);
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextnuread));
                viewHolder.title.setTypeface(null, Typeface.BOLD);
                viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextnuread));
                viewHolder.date.setTypeface(null, Typeface.BOLD);
            }

            if (!pnew.equals("null"))

            {
                if (Internet.isConnected(context)) {
                    registperp(id);
                } else {
                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }


            }

            viewHolder.notification.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {
                    viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                    viewHolder.name.setTypeface(null, Typeface.NORMAL);
                    viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                    viewHolder.title.setTypeface(null, Typeface.NORMAL);
                    viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                    viewHolder.date.setTypeface(null, Typeface.NORMAL);

                    final String id = customerlist.get(position).getPid();
                    final String pnew = customerlist.get(position).getPnew();
                    final String title = customerlist.get(position).getTitle();


                    Intent i=new Intent(context,NotificationDataFragment.class);
                    i.putExtra("id", id);
                    i.putExtra("pnew", pnew);
                    i.putExtra("title", title);
                    context.startActivity(i);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("id", id);
//                    bundle.putString("pnew", pnew);
//                    bundle.putString("pread", pread);
//                    bundle.putString("name", name);
//                    bundle.putString("title", title);
//                    bundle.putString("date", finalDate_string);
//                    bundle.putString("dec", dec);
//                    bundle.putString("image", imagestring);
//                    //set Fragmentclass Arguments
//                    NotificationDataFragment fragobj = new NotificationDataFragment();
//                    fragobj.setArguments(bundle);
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                    transaction.replace(R.id.notification_container, fragobj, "HealthHistoryFragment");
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                Intent i=new Intent(context,NotificationDataFragment.class);
//                i.putExtra("id", id);
//                i.putExtra("pnew", pnew);
//                i.putExtra("pread", pread);
//                i.putExtra("name", name);
//                i.putExtra("title", title);
//                i.putExtra("date", date);
//                i.putExtra("dec", dec);
//                context.startActivity(i);
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            Log.e("footer_bind_view", "came");
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        }


    }

    private void registperp(String s) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
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
    public int getItemCount() {
        return customerlist.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("item_view_type", "came");
        Log.e("item_position", String.valueOf(position));
        Log.e("list_size", String.valueOf(customerlist.size()));
        if (position == customerlist.size()) {
            Log.e("footer_view_type", "came");
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

}



