package com.example.user.trendy;

import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.user.trendy.Account.MyAccount;
import com.example.user.trendy.Bag.Bag;
import com.example.user.trendy.Bag.Db.AddToCart_Model;
import com.example.user.trendy.Bag.Db.DBHelper;
import com.example.user.trendy.Category.Categories;
import com.example.user.trendy.Category.CategoryProduct;
import com.example.user.trendy.ForYou.ForYou;
import com.example.user.trendy.Interface.AddRemoveCartItem;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Interface.OnFilterDataCallBack;
import com.example.user.trendy.Interface.OnNetworkCheckCallBack;
import com.example.user.trendy.Login.LoginActiviy;
import com.example.user.trendy.NetworkCheck.NetworkSchedulerService;
import com.example.user.trendy.Search.Search;
import com.example.user.trendy.Util.SharedPreference;
import com.example.user.trendy.Whislist.Whislist;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;


public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddRemoveCartItem, GoogleApiClient.OnConnectionFailedListener, OnFilterDataCallBack {

    FragmentManager fragmentManager;
    private int cart_count = 0;
    DBHelper db;
    private List<AddToCart_Model> cartList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

    }

    public void init() {

        scheduleJob();
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.bag);
        menuItem.setIcon(Converter.convertLayoutToImage(Navigation.this, cart_count, R.drawable.ic_shopping_bag));

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

                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction1.replace(R.id.home_container, new Bag(), "Bag");
                transaction1.addToBackStack("ForYou");
                transaction1.commit();
            return true;
        } else if (id == R.id.searchBar) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Whislist) {
            }
            else {
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction1.replace(R.id.home_container, new Search(), "search");
                transaction1.addToBackStack("ForYou");
                transaction1.commit();
                return true;
            }
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
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.home_container, new Categories(), "Categories");
                transaction.addToBackStack("ForYou");
                transaction.commit();
            }
        } else if (id == R.id.wishlist) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Whislist) {

            } else {
                FragmentTransaction wtransaction = getSupportFragmentManager().beginTransaction();
                wtransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                wtransaction.replace(R.id.home_container, new Whislist(), "whislist");
                wtransaction.addToBackStack("ForYou");
                wtransaction.commit();
            }
        } else if (id == R.id.account) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof MyAccount) {

            } else {
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction2.replace(R.id.home_container, new MyAccount(), "account");
                transaction2.addToBackStack("ForYou");
                transaction2.commit();
            }
        } else if (id == R.id.logout) {
            noDialog();

        } else if (id == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "Share the app to your loved ones";
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject/Title");
            intent.putExtra(Intent.EXTRA_TEXT, shareBodyText + "\n" + "https://play.google.com/store/apps/details?id=" + "");
            startActivity(Intent.createChooser(intent, "Share app via"));

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
                        startActivity(new Intent(Navigation.this, LoginActiviy.class));

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
}

