package com.example.user.trendy;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.user.trendy.Bag.Bag;
import com.example.user.trendy.Category.Categories;
import com.example.user.trendy.ForYou.ForYou;
import com.example.user.trendy.Interface.CartController;
import com.example.user.trendy.NetworkCheck.NetworkSchedulerService;

public class MainActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_you);
    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected( MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_you:
                    Fragment fragment = new ForYou();
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction transactioncal = getSupportFragmentManager().beginTransaction();
                    transactioncal.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transactioncal.replace(R.id.home_container, fragment, "ForYou");
                    if(fragmentManager.findFragmentByTag("ForYou")==null)
                    {
                        transactioncal.addToBackStack("ForYou");
                        transactioncal.commit();
                    }
                    else
                    {
                        transactioncal.commit();
                    }
                    return true;

                case R.id.navigation_categories:
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction.replace(R.id.home_container, new Categories(), "Categories");
                    if(fragmentManager1.findFragmentByTag("Categories")==null)
                    {
                        transaction.addToBackStack("Categories");
                        transaction.commit();
                    }
                    else
                    {
                        transaction.commit();
                    }


                    return true;
                case R.id.navigation_bag:
                    FragmentManager fragmentManager2=getSupportFragmentManager();
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction1.replace(R.id.home_container, new Bag(), "Bag");
                    if(fragmentManager2.findFragmentByTag("Bag")==null)
                    {
                        transaction1.addToBackStack("Bag");
                        transaction1.commit();
                    }
                    else
                    {
                        transaction1.commit();
                    }


                    return true;
                default:
                    Fragment fragment1 = new ForYou();
                    FragmentTransaction transactioncl = getSupportFragmentManager().beginTransaction();
                    transactioncl.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transactioncl.replace(R.id.home_container, fragment1, "ForYou");
                    transactioncl.commit();
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finishAffinity();
    }





}
