package com.example.user.trendy.NetworkCheck;

import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.example.user.trendy.ForYou.ViewModel.ForYouViewModel;
import com.example.user.trendy.ForYou.ViewModel.ForyouInterface;
import com.example.user.trendy.Interface.OnNetworkCheckCallBack;
import com.example.user.trendy.Navigation;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

        private static final String TAG = NetworkSchedulerService.class.getSimpleName();

        private ConnectivityReceiver mConnectivityReceiver;

        @Override
        public void onCreate() {
                super.onCreate();
                Log.i(TAG, "Service created");
                mConnectivityReceiver = new ConnectivityReceiver(this);
        }

        @Override
        public void onDestroy() {
                super.onDestroy();
                Log.i(TAG, "Service destroyed");
        }

        /**
         * When the app's MainActivity is created, it starts this service. This is so that the
         * activity and this service can communicate back and forth. See "setUiCallback()"
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
                Log.i(TAG, "onStartCommand");
                return START_NOT_STICKY;
        }


        @Override
        public boolean onStartJob(JobParameters params) {
                Log.i(TAG, "onStartJob" + mConnectivityReceiver);
                registerReceiver(mConnectivityReceiver, new IntentFilter(Constants.CONNECTIVITY_ACTION));
                return true;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
                Log.i(TAG, "onStopJob");
                unregisterReceiver(mConnectivityReceiver);
                return true;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {


                if (!MyApplication.isInterestingActivityVisible()) {
                        String message = isConnected ? "Internet Is Now Connected" : "Please Make Sure Internet Is Connected";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        ForYouViewModel forYouViewModel=new ForYouViewModel(getApplicationContext());
                }

        }
}