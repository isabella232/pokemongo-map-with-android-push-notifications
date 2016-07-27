package com.example.lizzie.trafficviz;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.LocationServices;
import com.pubnub.api.Pubnub;

import java.io.IOException;
import java.io.InputStream;

//pokemon, inject JS into localhost page

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
    String serverApiKey = "AIzaSyBJ9ZBdNbfZ5n2PGySR_TOjlLwXTgRrxno";
    //PubNub lat-long: 37.783556, -122.399234
    String senderID = "209687934533";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView tvLatLong;
    private static final String TAG = "RegIntentService";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView1);

        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setDomStorageEnabled(true); //stackoverflow
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setDisplayZoomControls(false);

        //pokemon api additions w/ ngrok
        webSetting.setAllowUniversalAccessFromFileURLs(true);


        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.addJavascriptInterface(new WebAppInterface(this));
        webView.setWebViewClient(new lizzieBrowser());
        String idkMan = Uri.parse("http://c7581073.ngrok.io").toString(); //url will change each time ngrok changes, runs
        webView.loadUrl(idkMan);

        tvLatLong = (TextView) findViewById(R.id.tvLatLong);

        tvLatLong.setGravity(Gravity.BOTTOM);

        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "not connected...", Toast.LENGTH_SHORT).show();
        }

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch(IOException e){
            Toast.makeText(this, "error in instanceID", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                tvLatLong.setText("Latitude: " + String.valueOf(mLastLocation.getLatitude()) + "Longitude: " + String.valueOf(mLastLocation.getLongitude()));

            }
        } catch(SecurityException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "Latitude: "+ String.valueOf(mLastLocation.getLatitude()) + "Longitude: " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private class lizzieBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //view.loadUrl(url); //
            //return super.shouldOverrideUrlLoading(view, url);
            return false; //pokemon
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            injectScriptFile(view, "index.js");
            view.loadUrl("javascript:setTimeout(test(), 500");
        }

        private void injectScriptFile(WebView view, String scriptFile) {
            InputStream input;
            try {
                input = getAssets().open(scriptFile);
                byte[] buffer = new byte[input.available()];
                input.read(buffer);
                input.close();

                String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
                view.loadUrl("javascript:(function() {" +
                        "var parent= document.getElementByTagName('head'.item(0);" +
                        "var script = document.createElement('script');" +
                        "script.type = 'text/javascript';" +
                        //tell browser to BASE64-decode string into script
                        "script.innerHTML = window.atob('" + encoded + "');" +
                        //"script.innerHTML = decodeURIComponent(escape(window(atob('"+ encoded + "')));" +
                        "parent.appendChild(script)" +
                        "})()");
            } catch (IOException e) {
                //TODO auto-generated catch block
                e.printStackTrace();
            }

        }

    }


}






