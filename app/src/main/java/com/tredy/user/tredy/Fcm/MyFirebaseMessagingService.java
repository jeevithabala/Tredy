package com.tredy.user.tredy.Fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tredy.user.tredy.Navigation;
import com.tredy.user.tredy.notification.NotificationsListFragment;
import com.tredy.user.tredy.R;
import com.tredy.user.tredy.util.Config;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseIDService";
    private static final String CHANNEL_ID = "100";
    private NotificationUtils notificationUtils;
    //private String imgUrl="http://192.168.0.6/push_notification_demo/img/";

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        Log.e(TAG, "From: " + remoteMessage.getFrom());
    //Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

//        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "From: " + remoteMessage.getFrom());
//
//            String title, message, img_url;
//
//            title = remoteMessage.getData().get("title");
//            message = remoteMessage.getData().get("message");
//            img_url = remoteMessage.getData().get("img_url");
//
//            Log.e("title", title);
//            Log.e("message", message);
//            Log.e("img_url", img_url);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                // Create the NotificationChannel, but only on API 26+ because
//                // the NotificationChannel class is new and not in the support library
//                CharSequence name = getString(R.string.channel_name);
//                String description = getString(R.string.channel_description);
//                int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
//                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//                channel.setDescription(description);
//                // Register the channel with the system
//                NotificationManager notificationManager = (NotificationManager) getSystemService(
//                        NOTIFICATION_SERVICE);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//            builder.setContentTitle(title);
//            builder.setContentText(message);
//            builder.setContentIntent(pendingIntent);
//            builder.setSound(soundUri);
//            builder.setSmallIcon(R.mipmap.ic_launcher);
//
////            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(0, builder.build());
//
//
//        }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            Log.e("Notification_channel","came");
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

            Intent countNotification = new Intent(Config.COUNT_NOTIFICATION);
            countNotification.putExtra("message", remoteMessage.getData().toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(countNotification);
            handleNotification(remoteMessage, remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("not_active7","came");
            handleNotification(remoteMessage, remoteMessage.getData().toString());
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        }
    }


    private void handleNotification(RemoteMessage remoteMessage, String message) {

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            if (NotificationsListFragment.active) {
//            if (active=="true") {
                Log.e("not_active1","came");
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }else {
                Log.e("not_active2","came");
                try {
                    Log.e("not_active3","came");
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    JSONObject json = new JSONObject(remoteMessage.getData());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e("not_active4","came");
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }


        } else {
            // If the app is in background, firebase itself handles the notification for notification payload
            try {
                Log.e("not_active5","came");
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e("not_active6","came");
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
//          JSONObject data = json.getJSONObject("data");

            String title = json.getString("title");
            String message = json.getString("message");
//          boolean isBackground = json.getBoolean("is_background");
            String imageName = json.getString("image");
            String imageUrl = imageName;
//          String timestamp = json.getString("timestamp");
//          JSONObject payload = json.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
//          Log.e(TAG, "isBackground: " + isBackground);
//          Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
//          Log.e(TAG, "timestamp: " + timestamp);


//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            } else {
            // app is in background, show the notification in notification tray
            Log.e("not_active8","came");
            Intent resultIntent = new Intent(getApplicationContext(), Navigation.class);
            Log.e("not_active9","came");
            resultIntent.putExtra("message", message);
            Log.e("not_active10","came");
            resultIntent.putExtra("action_fragment", "Notification");
            Log.e("not_active11","came");

            // check for image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//                } else {
            // image is present, show notification with image
            showNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent, imageUrl);
            Log.e("not_active12","came");
//                }
//            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, Intent intent, String imageUrl) {
        Log.e("method1", "came");
        Log.e("intent_extra1", ""+intent.getExtras().getString("action_fragment")+intent.getExtras().getString("message"));
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);

    }


    private class loadBitmapAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap image = loadBitmap(strings[0]);
            Log.e("bitmap", String.valueOf(image));
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

    }

    public Bitmap loadBitmap(String url) {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }
}

