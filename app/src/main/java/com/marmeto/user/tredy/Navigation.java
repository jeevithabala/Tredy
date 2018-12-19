package com.marmeto.user.tredy;

import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.marmeto.user.tredy.account.MyAccount;
import com.marmeto.user.tredy.bag.Bag;
import com.marmeto.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.marmeto.user.tredy.bag.cartdatabase.DBHelper;
import com.marmeto.user.tredy.category.Categories;
import com.marmeto.user.tredy.category.CategoryProduct;
import com.marmeto.user.tredy.ccavenue.WebViewActivity;
import com.marmeto.user.tredy.foryou.ForYou;
import com.marmeto.user.tredy.callback.AddRemoveCartItem;
import com.marmeto.user.tredy.callback.OnFilterDataCallBack;
import com.marmeto.user.tredy.foryou.allcollection.AllCollectionModel;
import com.marmeto.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.marmeto.user.tredy.foryou.newarrival.NewArrivalModel;
import com.marmeto.user.tredy.foryou.topselling.TopSellingModel;
import com.marmeto.user.tredy.foryou.viewmodel.ForyouInterface;
import com.marmeto.user.tredy.login.LoginActiviy;
import com.marmeto.user.tredy.networkCheck.NetworkSchedulerService;
import com.marmeto.user.tredy.notification.NotificationsListFragment;
import com.marmeto.user.tredy.search.Search;
import com.marmeto.user.tredy.util.Constants;
import com.marmeto.user.tredy.util.Internet;
import com.marmeto.user.tredy.util.SharedPreference;
import com.marmeto.user.tredy.utility.Converter;
import com.marmeto.user.tredy.whislist.Whislist;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddRemoveCartItem, GoogleApiClient.OnConnectionFailedListener, OnFilterDataCallBack, ForyouInterface, ForceUpdateChecker.OnUpdateNeededListener {

    FragmentManager fragmentManager;
    private int cart_count = 0;
    public static int noti_counnt = 0;
    DBHelper db;
    private List<AddToCart_Model> cartList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    Toolbar toolbar;
    Boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();


    }

    public void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob();
        }
        fragmentManager = getSupportFragmentManager();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        db = new DBHelper(getApplicationContext());
        cartList = db.getCartList();
//        cart_count = cartList.size();
        cart_count = 0;
        for (int i = 0; i < cartList.size(); i++) {
            cartList.get(i).getQty();
            cart_count = cart_count + cartList.get(i).getQty();
        }

        if (Internet.isConnected(this)) {
            getNotiCount();
        } else {
            Toast.makeText(getApplicationContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String actionFragment = intent.getStringExtra("action_fragment");
            Log.e("action_fragment3", "" + actionFragment);
            if (actionFragment != null) {
                if (actionFragment.equals("Notification"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_container, new NotificationsListFragment()).addToBackStack(null).commit();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);

        if (getIntent() != null) {
            String message = getIntent().getStringExtra("message");
            if (message != null && message.trim().length() > 0) {
                SharedPreference.saveData("update", "true", getApplicationContext());
                if (message.contains("Transaction Successful!")) {
                    message = "Your Order Placed Successfully";
                }
                Dialog(message);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.bag);
        menuItem.setIcon(Converter.convertLayoutToImage(Navigation.this, cart_count, R.drawable.ic_shopping_bag));

        MenuItem notificationcount = menu.findItem(R.id.action_notificaton);
        notificationcount.setIcon(Converter.convertLayoutToImage1(Navigation.this, noti_counnt, R.drawable.ic_notifications_black_24dp));

//        MenuItem searchItem = menu.findItem(R.id.searchBar);
//
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setQueryHint("Search Product");
//        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
//        searchView.setIconified(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        } else
        if (id == R.id.bag) {
            Bag bag = new Bag();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            transaction1.replace(R.id.home_container, bag, "Bag");
            if (fragmentManager.findFragmentByTag("Bag") == null) {
                transaction1.addToBackStack("Bag");
                transaction1.commit();
            } else {
                transaction1.commit();
            }
            return true;
        } else if (id == R.id.action_notificaton) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof NotificationsListFragment) {

            } else {
                NotificationsListFragment notificationsListFragment = new NotificationsListFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction1.replace(R.id.home_container, notificationsListFragment, "notification");
                if (fragmentManager.findFragmentByTag("notification") == null) {
                    transaction1.addToBackStack("notification");
                    transaction1.commit();
                } else {
                    transaction1.commit();
                }
            }
            return true;
        } else if (id == R.id.searchBar) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Search) {

            } else {
                Search search = new Search();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction1.replace(R.id.home_container, search, "search");
                if (fragmentManager.findFragmentByTag("search") == null) {
                    transaction1.addToBackStack("search");
                    transaction1.commit();
                } else {
                    transaction1.commit();
                }
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.for_you) {
            Fragment fragment = new ForYou();
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof ForYou) {

            } else {
                FragmentTransaction transactioncal = getSupportFragmentManager().beginTransaction();
                transactioncal.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transactioncal.replace(R.id.home_container, fragment, "ForYou");
                transactioncal.commit();
            }
        } else if (id == R.id.category) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Categories) {

            } else {
                Categories categories = new Categories();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.home_container, categories, "Categories");
                if (fragmentManager.findFragmentByTag("Categories") == null) {
                    transaction.addToBackStack("Categories");
                    transaction.commit();
                } else {
                    transaction.commit();
                }

            }
        } else if (id == R.id.wishlist) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Whislist) {

            } else {
                Whislist whislist = new Whislist();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction wtransaction = getSupportFragmentManager().beginTransaction();
                wtransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                wtransaction.replace(R.id.home_container, whislist, "wishlist");
                if (fragmentManager.findFragmentByTag("wishlist") == null) {
                    wtransaction.addToBackStack("wishlist");
                    wtransaction.commit();
                } else {
                    wtransaction.commit();
                }

            }
        } else if (id == R.id.account) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof MyAccount) {

            } else {
                MyAccount myAccount = new MyAccount();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction2.replace(R.id.home_container, myAccount, "account");
                if (fragmentManager.findFragmentByTag("account") == null) {
                    transaction2.addToBackStack("account");
                    transaction2.commit();
                } else {
                    transaction2.commit();
                }

            }

        } else if (id == R.id.aboutus) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Aboutus) {

            } else {
                Aboutus aboutus = new Aboutus();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction2.replace(R.id.home_container, aboutus, "aboutus");
                if (fragmentManager.findFragmentByTag("aboutus") == null) {
                    transaction2.addToBackStack("aboutus");
                    transaction2.commit();
                } else {
                    transaction2.commit();
                }

            }

        } else if (id == R.id.contactus) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof ContactUs) {

            } else {
                ContactUs contactUs = new ContactUs();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction2.replace(R.id.home_container, contactUs, "contactUs");
                if (fragmentManager.findFragmentByTag("contactUs") == null) {
                    transaction2.addToBackStack("contactUs");
                    transaction2.commit();
                } else {
                    transaction2.commit();
                }

            }
        } else if (id == R.id.logout) {
            noDialog();

//        } else if (id == R.id.share) {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            String shareBodyText = "Share the app to your loved ones";
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject/Title");
//            intent.putExtra(Intent.EXTRA_TEXT, shareBodyText + "\n" + "https://play.google.com/store/apps/details?id=" + "");
//            startActivity(Intent.createChooser(intent, "Share app via"));

        } else {
            Fragment fragment = new ForYou();
            FragmentTransaction transactioncal = getSupportFragmentManager().beginTransaction();
            transactioncal.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            transactioncal.replace(R.id.home_container, fragment, "ForYou");
            transactioncal.addToBackStack("ForYou");
            transactioncal.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void AddCartItem() {
        cartList.clear();
        cartList = db.getCartList();
        cart_count = 0;
        for (int i = 0; i < cartList.size(); i++) {
            cartList.get(i).getQty();
            cart_count = cart_count + cartList.get(i).getQty();
        }
        Log.e("countt", String.valueOf(cart_count));
//        cart_count = cartList.size();
        invalidateOptionsMenu();
    }

    @Override
    public void RemoveCartItem() {
        cartList.clear();
        cartList = db.getCartList();
        cart_count = 0;
        for (int i = 0; i < cartList.size(); i++) {
            cartList.get(i).getQty();
            cart_count = cart_count + cartList.get(i).getQty();
        }
//        cart_count = cartList.size();
        Log.e("countt", String.valueOf(cart_count));
        invalidateOptionsMenu();
    }


    public void noDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        disconnectFromFacebook();
                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        }
                        SharedPreference.saveData("login", "", Navigation.this);
                        SharedPreference.saveData("accesstoken", "", getApplicationContext());
                        startActivity(new Intent(Navigation.this, LoginActiviy.class));
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        } else {

            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();

                }
            }).executeAsync();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void refreshActivity() {

        Toast.makeText(this, "Main ACtivity", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onFilterValueSelectCallBack(String minprice, String maxprice, String sortby, String collectionid, ArrayList<String> selectedFilterLists, String CollectionName) {
        CategoryProduct categoryProduct = (CategoryProduct) getSupportFragmentManager().findFragmentByTag("categoryproduct");
        categoryProduct.getFilterData(minprice, maxprice, sortby, collectionid, selectedFilterLists, CollectionName);
//
//        if (categoryProduct == null) {
//            categoryProduct = new CategoryProduct();
//        }
//
//        FragmentTransaction ft = fragmentManager.beginTransaction().replace(R.id.home_container, categoryProduct, "categoryproduct");
//        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//        ft.addToBackStack("ForYou");
//        ft.commit();

    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }


    public void getNotiCount() {
        String customerid = SharedPreference.getData("customerid", this);
        String minusdatet = getCalculatedDate("MM/dd/yyyy", -10);


        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.unreadcount + customerid.trim() + "?from=" + minusdatet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            Log.e("response", response);
                            String count = obj.getString("count");
                            noti_counnt = Integer.parseInt(count);
                            invalidateOptionsMenu();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    @Override
    public void allcollection(ArrayList<AllCollectionModel> allCollectionModelArrayList) {

    }

    @Override
    public void collectionlist(ArrayList<TopSellingModel> topSellingModelArrayList, ArrayList<NewArrivalModel> newArrivalModelArrayList) {

    }

    @Override
    public void bannerlist(ArrayList<String> bannerlist) {

    }

    @Override
    public void grocerylist(ArrayList<GroceryHomeModel> arrayList) {

    }

    @Override
    public void getcount(int count) {
        noti_counnt = count;
        invalidateOptionsMenu();
    }


    @Override
    public void onUpdateNeeded(final String updateUrl) {
       String update= SharedPreference.getData("update",getApplicationContext());
       if(update.equals("false")) {
           SharedPreference.saveData("update", "true", getApplicationContext());
           AlertDialog dialog = new AlertDialog.Builder(this)
                   .setTitle("New version available")
                   .setMessage("Please, update app to new version to continue reposting.")
                   .setPositiveButton("Update",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   redirectStore(updateUrl);
                               }
                           }).setNegativeButton("No, thanks",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           }).create();
           dialog.show();
       }
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void Dialog(String poptext) {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Navigation.this, R.style.AlertDialogStyle);
        builder.setTitle("Status");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getIntent().removeExtra("message");
                        dialog.dismiss();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        alert.getWindow().setBackgroundDrawableResource(android.R.color.white);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreference.saveData("update", "false", getApplicationContext());
    }
}
