package com.example.user.trendy;

import android.content.Intent;
import android.os.Bundle;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.user.trendy.Account.MyAccount;
import com.example.user.trendy.Bag.Bag;
import com.example.user.trendy.Category.Categories;
import com.example.user.trendy.ForYou.ForYou;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.Login.LoginActiviy;
import com.example.user.trendy.Util.SharedPreference;
import com.example.user.trendy.Whislist.Whislist;


public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    private int cart_count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.bag);
        menuItem.setIcon(Converter.convertLayoutToImage(Navigation.this,cart_count,R.drawable.ic_shopping_bag));
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

            }else {
                FragmentTransaction transactioncal = getSupportFragmentManager().beginTransaction();
                transactioncal.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transactioncal.replace(R.id.home_container, fragment, "ForYou");
                transactioncal.commit();
            }
        } else if (id == R.id.category) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Categories) {

            }else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.home_container, new Categories(), "Categories");
                transaction.addToBackStack("ForYou");
                transaction.commit();
            }
        } else if (id == R.id.wishlist) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof Whislist) {

            }else {
                FragmentTransaction wtransaction = getSupportFragmentManager().beginTransaction();
                wtransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                wtransaction.replace(R.id.home_container, new Whislist(), "whislist");
                wtransaction.addToBackStack("ForYou");
                wtransaction.commit();
            }
        } else if (id == R.id.account) {
            if (fragmentManager.findFragmentById(R.id.home_container) instanceof MyAccount) {

            }else {
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction2.replace(R.id.home_container, new MyAccount(), "account");
                transaction2.addToBackStack("ForYou");
                transaction2.commit();
            }
        } else if (id == R.id.logout) {
            SharedPreference.saveData("login", "", Navigation.this);
            startActivity(new Intent(Navigation.this, LoginActiviy.class));

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


}

