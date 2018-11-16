package com.example.user.trendy.Util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by GyanPrakash on 9/21/2017.
 */

public class Internet{

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
