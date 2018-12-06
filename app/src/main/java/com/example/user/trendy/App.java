package com.example.user.trendy;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());


        registerActivityLifecycleCallbacks(new SplashScreenHelper());

    }
}
