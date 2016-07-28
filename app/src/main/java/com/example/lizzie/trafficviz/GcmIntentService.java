package com.example.lizzie.trafficviz;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;

import org.json.JSONException;
import org.json.JSONObject;
//import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.io.IOException;
import java.security.Policy;

import com.google.android.gms.iid.InstanceID;


/**
 * Created by lizzie on 7/26/16.
 */

public class GcmIntentService extends IntentService {

    private class asyncTaskClass extends GcmIntentService {

    }

    String regId = "pikachu";
    String SENDER_ID = "209687934533";
    String maybe_sender_id = "AIzaSyCGbXUSDAyPeow9QR7JJHNGvp6frpWoy3M";
    String TAG = "hello, world";
    GoogleCloudMessaging gcm;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    Context context;
    String CHANNEL = "pikaChannel";
    String PROPERTY_REG_ID = "oi";

    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch(Exception e) {
            System.out.println(e);
        }
        Bundle extras = intent.getExtras();
        gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            sendNotification("Received: " + extras.toString());
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("PubNub GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        int NOTIFICATION_ID = 1;
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

//which device should receive notifications?

    private void register() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            try {
                regId = getRegistrationId(context);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (regId.isEmpty()) {
                registerInBackground();
            } else {
                Toast.makeText(this, "Registration ID already exists: " + regId, Toast.LENGTH_SHORT).show(); //makeToast() ??
            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog((Activity) context, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                Log.e(TAG, "device not supported"); //finish() ?
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

    public String getRegistrationToken() throws IOException {

        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

        return token;
    }

    private void registerInBackground() {
        AsyncTask<Integer, Void, String> opTask = new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    //regId = gcm.register(SENDER_ID);
                    regId = getRegistrationToken(); //register is deprecated :(
                    msg = "Device registered, registration ID: " + regId;

                    sendRegistrationId(regId);

                    storeRegistrationId(context, regId);
                    Log.i(TAG, msg);
                } catch (Exception ex) { //try
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, msg);
                } //catch
                return msg;
            }
        };
        opTask.execute(null, null, null); //async task
    }

    //PubNub instance
    private final Pubnub pb = new Pubnub("pub-c-cfd86269-b02b-4004-97a6-92ca5f582fce", "sub-c-e3dc294e-4568-11e6-bfbb-02ee2ddab7fe");

    private void sendRegistrationId(String regID) {
        pb.enablePushNotificationsOnChannel(
                CHANNEL,
                regID
        );
    }

    private void storeRegistrationId(Context context, String regId) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }

    public void sendNotification() {
        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject jso = new JSONObject();
        try {
            jso.put("GCMSays", "hi");
        } catch (JSONException e) {
        }
        gcmMessage.setData(jso);

        // Create PnMessage
        Callback publishCallback = new Callback() { 
            public void successCallback(String channel, Object response) { 
                System.out.println(response); 
            }  

            public void errorCallback(String channel, PubnubError error) { 
                System.out.println(error);
            } 
        };

         PnMessage message = new PnMessage(pb, CHANNEL, publishCallback , gcmMessage);


          //message.put("b");

          try {
                 message.publish();
             } catch (PubnubException e) {
            Toast.makeText(this, "PubNubException: " + e, Toast.LENGTH_SHORT).show();
        } 

    }

    public static Callback callback = new Callback() {
        String TAG = "hello, world";
        @Override
        public void successCallback(String channel, Object message) {
            Log.i(TAG, "Success on Channel " + channel + " : " + message);
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.i(TAG, "Error On Channel " + channel + " : " + error);
        }
    };

    private void unregister()  {
        AsyncTask<String, Void, Object> opTask = new AsyncTask<String, Void, Object>(){
            @Override
            protected Object doInBackground(String... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    // Unregister from GCM
                    gcm.unregister();

                    // Remove Registration ID from memory
                    removeRegistrationId(context);

                    // Disable Push Notification
                    pb.disablePushNotificationsOnChannel(CHANNEL, regId);

                } catch (Exception e) {
                }
                return null;
            }
        }.execute(null, null, null);
    }

    private void removeRegistrationId(Context context) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.apply();
    }
}




