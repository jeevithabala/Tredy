package com.example.user.trendy;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new SplashScreenHelper());

    }
}
