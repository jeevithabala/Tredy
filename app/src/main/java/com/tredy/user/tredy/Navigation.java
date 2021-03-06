package com.tredy.user.tredy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.tredy.user.tredy.account.MyAccount;
import com.tredy.user.tredy.bag.Bag;
import com.tredy.user.tredy.bag.cartdatabase.AddToCart_Model;
import com.tredy.user.tredy.bag.cartdatabase.DBHelper;
import com.tredy.user.tredy.category.Categories;
import com.tredy.user.tredy.category.CategoryProduct;
import com.tredy.user.tredy.foryou.ForYou;
import com.tredy.user.tredy.callback.AddRemoveCartItem;
import com.tredy.user.tredy.callback.OnFilterDataCallBack;
import com.tredy.user.tredy.foryou.allcollection.AllCollectionModel;
import com.tredy.user.tredy.foryou.groceryhome.GroceryHomeModel;
import com.tredy.user.tredy.foryou.newarrival.NewArrivalModel;
import com.tredy.user.tredy.foryou.topselling.TopSellingModel;
import com.tredy.user.tredy.foryou.viewmodel.ForyouInterface;
import com.tredy.user.tredy.login.LoginActiviy;
import com.tredy.user.tredy.networkCheck.NetworkSchedulerService;
import com.tredy.user.tredy.notification.NotificationsListFragment;
import com.tredy.user.tredy.search.Search;
import com.tredy.user.tredy.util.Constants;
import com.tredy.user.tredy.util.FilterSharedPreference;
import com.tredy.user.tredy.util.Internet;
import com.tredy.user.tredy.util.SharedPreference;
import com.tredy.user.tredy.utility.Converter;
import com.tredy.user.tredy.whislist.Whislist;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddRemoveCartItem, GoogleApiClient.OnConnectionFailedListener, OnFilterDataCallBack, ForyouInterface, ForceUpdateChecker.OnUpdateNeededListener {

    FragmentManager fragmentManager;
    public static int cart_count = 0;
    public static int noti_counnt = 0;
    DBHelper db;
    private List<AddToCart_Model> cartList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    Toolbar toolbar;
    private GraphClient graphClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        toolbar = findViewById(R.id.toolbar);
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (inputMethodManager != null) {
//                    inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
//                }
//            }
//
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (inputMethodManager != null) {
//                    inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
//                }
//            }
//        };
//        drawer.setDrawerListener(actionBarDrawerToggle);
//        drawer.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();


            try {
                if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("ForYou")).isVisible()) {
                    toolbar.setTitle("Home");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("grocery")).isVisible()) {
                    toolbar.setTitle("Grocery");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("wishlist")).isVisible()) {
                    toolbar.setTitle("Wishlist");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("Categories")).isVisible()) {
                    toolbar.setTitle("Categories");
//                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("notification")).isVisible()) {
//                    toolbar.setTitle("Notification");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("account")).isVisible()) {
                    toolbar.setTitle("Account Details");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("aboutus")).isVisible()) {
                    toolbar.setTitle("About Us");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("Bag")).isVisible()) {
                    toolbar.setTitle("Cart");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("search")).isVisible()) {
                    toolbar.setTitle("Search");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("productview")).isVisible()) {
                    toolbar.setTitle("Product");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("categoryproduct")).isVisible()) {
                    toolbar.setTitle("Categories");
                } else if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("tawk")).isVisible()) {
                    toolbar.setTitle("Chat");
                }

            } catch (NullPointerException ignored) {
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        graphClient = GraphClient.builder(this)
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getApplicationContext().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();

        String customerid = SharedPreference.getData("customerid", this);
        if (customerid.trim().length() == 0) {
            if (Internet.isConnected(this)) {
                String accessToken = SharedPreference.getData("accesstoken", getApplicationContext());
                getCustomerId(accessToken);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Internet.isConnected(this)) {
                saveToken();
            } else {
                Toast.makeText(getApplicationContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
            }
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
        if (jobScheduler != null) {
            jobScheduler.schedule(myJob);
        }
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        try {
            stopService(new Intent(this, NetworkSchedulerService.class));
        } catch (Exception ignored) {
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        try {
            Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
            startService(startServiceIntent);
        } catch (Exception ignored) {
        }

        if (getIntent() != null) {
            SharedPreference.saveData("update", "true", getApplicationContext());
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
            transaction1.add(R.id.home_container, bag, "Bag");
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void AddCartItem() {
        cartList.clear();
        cartList = db.getCartList();
        cart_count = 0;
        for (int i = 0; i < cartList.size(); i++) {
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
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    disconnectFromFacebook();
                    if (mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    }
                    SharedPreference.saveData("login", "", Navigation.this);
                    SharedPreference.saveData("accesstoken", "", getApplicationContext());
                    SharedPreference.clearSession(getApplicationContext());
                    startActivity(new Intent(Navigation.this, LoginActiviy.class));
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
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

            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> LoginManager.getInstance().logOut()).executeAsync();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    public void refreshActivity() {
//
//        Toast.makeText(this, "Main ACtivity", Toast.LENGTH_SHORT).show();
//
//    }
//

    @Override
    public void onFilterValueSelectCallBack(String minprice, String maxprice, String sortby, String collectionid, ArrayList<String> selectedFilterLists, String CollectionName) {
        CategoryProduct categoryProduct = (CategoryProduct) getSupportFragmentManager().findFragmentByTag("categoryproduct");
        if (categoryProduct != null) {
            categoryProduct.getFilterData(minprice, maxprice, sortby, collectionid, selectedFilterLists, CollectionName);
        }
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
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);
                        Log.e("response", response);
                        String count = obj.getString("count");
                        noti_counnt = Integer.parseInt(count);
                        invalidateOptionsMenu();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
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
    public void collectionlist(ArrayList<NewArrivalModel> newArrivalModelArrayList) {

    }


    @Override
    public void bannerlist(ArrayList<String> bannerlist) {

    }

    @Override
    public void grocerylist(ArrayList<GroceryHomeModel> arrayList) {

    }

    @Override
    public void topselling1(ArrayList<TopSellingModel> topSellingModelArrayList) {

    }

    @Override
    public void getcount(int count) {
        noti_counnt = count;
        invalidateOptionsMenu();
    }


    @Override
    public void onUpdateNeeded(final String updateUrl) {
        String update = SharedPreference.getData("update", getApplicationContext());
        if (update.equals("false")) {
            SharedPreference.saveData("update", "true", getApplicationContext());
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle("New version available")
//                    .setMessage("Please, update app to new version to continue shopping.")
//                    .setPositiveButton("Update",
//                            (dialog12, which) -> redirectStore(updateUrl)).setNegativeButton("No, thanks",
//                            (dialog1, which) -> dialog1.dismiss()).create();
//            dialog.show();
            showCustomDialog(updateUrl);
        }
    }


    private void showCustomDialog(String dtext) {
        if (getApplicationContext() != null) {
            //before inflating the custom alert dialog layout, we will get the current activity viewgroup
            ViewGroup viewGroup = findViewById(android.R.id.content);

            //then we will inflate the custom alert dialog xml that we created
            View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customdialog, viewGroup, false);

            //Now we need an AlertDialog.Builder object
     AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView);

            //finally creating the alert dialog and displaying it
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
//            alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    // Prevent dialog close on back press button
//                    return keyCode == KeyEvent.KEYCODE_BACK;
//                }
//            });

            Button ok = dialogView.findViewById(R.id.buttonOk);
            TextView dialog_text = dialogView.findViewById(R.id.dialog_text);
//            dialog_text.setText(dtext);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    alertDialog.dismiss();
                    redirectStore(dtext);
                }
            });

//            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
//                @Override
//                public void onCancel(DialogInterface dialogInterface) {
//                    closeActivity();
//                }
//            });


            alertDialog.show();

        }

    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void closeActivity(){

        finish();

    }
    public void Dialog(String poptext) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Navigation.this, R.style.AlertDialogStyle);
        builder.setTitle("Status");
        builder.setMessage(poptext)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    getIntent().removeExtra("message");
                    dialog.dismiss();
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.white);

    }

    public void saveToken() {
        String token = FilterSharedPreference.getData("firebasetoken", getApplicationContext());
        if (token.trim().length() > 0) {


            String customerid = SharedPreference.getData("customerid", this);


            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("customer_id", customerid.trim());
                jsonBody.put("registration_token", token);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.savetoken, response -> {
                    Log.e("tokenresponse", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        getNotiCount();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("VOLLEY", " " + error.toString())) {
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
//                    String statusCode = String.valueOf(response.statusCode);
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
    }

    public void getCustomerId(String accessToken) {

        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .id()
                )
        );

        QueryGraphCall call = graphClient.queryGraph(query);

        call.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                if (response.data() != null && response.data().getCustomer() != null) {
                    String customerid = response.data().getCustomer().getId().toString();
                    byte[] data = Base64.decode(customerid, Base64.DEFAULT);
                    try {
                        customerid = new String(data, "UTF-8");
                        String[] separated = customerid.split("/");
                        customerid = separated[4]; // this will contain "Customer id"
                        Log.e("customer_id", " " + customerid);
                        SharedPreference.saveData("customerid", customerid, getApplicationContext());
                        if (getApplicationContext() != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveToken();
                                }
                            });
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e("TAG", "Failed to execute query", error);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreference.saveData("update", "false", getApplicationContext());
    }
}
