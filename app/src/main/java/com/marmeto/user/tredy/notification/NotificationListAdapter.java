package com.marmeto.user.tredy.notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.marmeto.user.tredy.Navigation;
import com.marmeto.user.tredy.R;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.Internet;
import com.marmeto.user.tredy.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.marmeto.user.tredy.notification.NotificationsListFragment.getCalculatedDate;

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
    noticount noticount;


    static class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView title, year, genre;


        TextView title, iconText, date, name, read;
        ImageView imgProfile;
        LinearLayout notification;


        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
//            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            //  iconText = (TextView) view.findViewById(R.id.icon_text);
            date = (TextView) view.findViewById(R.id.icon_star);
            notification = (LinearLayout) view.findViewById(R.id.notificationp);
            read = (TextView) view.findViewById(R.id.read);


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

    public NotificationListAdapter(List<NotificationListSet> customerlist, Context context, noticount noticount) {
        this.customerlist = customerlist;
        this.context = context;
        this.noticount=noticount;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("ulhijio", "hui");
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
            Log.e("pnew", " " + pnew);

            final String title = customerlist.get(position).getTitle();

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.title.setText(customerlist.get(position).getTitle());
            if (!pnew.equals("null")) {
//                viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                viewHolder.name.setTypeface(null, Typeface.NORMAL);
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                viewHolder.title.setTypeface(null, Typeface.NORMAL);
                viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                viewHolder.date.setTypeface(null, Typeface.NORMAL);
            } else {
//                viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextnuread));
//                viewHolder.name.setTypeface(null, Typeface.BOLD);
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextnuread));
                viewHolder.title.setTypeface(null, Typeface.BOLD);
                viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextnuread));
                viewHolder.date.setTypeface(null, Typeface.BOLD);
            }

//            if (!pnew.equals("null"))
//
//            {
//                if (Internet.isConnected(context)) {
//                    registperp(id);
//                } else {
//                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }

            viewHolder.notification.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {
//                    viewHolder.name.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
//                    viewHolder.name.setTypeface(null, Typeface.NORMAL);
                    viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                    viewHolder.title.setTypeface(null, Typeface.NORMAL);
                    viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                    viewHolder.date.setTypeface(null, Typeface.NORMAL);

                    final String id = customerlist.get(position).getPid();
                    final String pnew = customerlist.get(position).getPnew();
                    final String title = customerlist.get(position).getTitle();
                    String orderid = customerlist.get(position).getOrderid();


                    Intent i = new Intent(context, NotificationDataFragment.class);
                    i.putExtra("id", id);
                    i.putExtra("pnew", pnew);
                    i.putExtra("title", title);
                    i.putExtra("orderid", orderid);
                    context.startActivity(i);

//
                }
            });

            viewHolder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pnew.equals("null")) {
                        if (Internet.isConnected(context)) {
                            registperp(id);
                            viewHolder.title.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                            viewHolder.title.setTypeface(null, Typeface.NORMAL);
                            viewHolder.date.setTextColor(context.getResources().getColor(R.color.ntificationtextread));
                            viewHolder.date.setTypeface(null, Typeface.NORMAL);
                            noticount.noticountchange(id);
                        } else {
                            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
//
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
                response -> {
                },
                error -> {
//                        progressDialog.dismiss();
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

    public interface noticount {
        void noticountchange(String id);
    }

}



