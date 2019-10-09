package com.tredy.user.tredy.networkCheck;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tredy.user.tredy.Navigation;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static boolean isInterestingActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register to be notified of activity state changes
        registerActivityLifecycleCallbacks(this);
    }

    public static boolean isInterestingActivityVisible() {
        return isInterestingActivityVisible;
    }


    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof Navigation) {
            isInterestingActivityVisible = true;
        }
    }


    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof Navigation) {
            isInterestingActivityVisible = false;
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
