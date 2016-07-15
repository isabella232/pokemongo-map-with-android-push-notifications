package com.example.lizzie.trafficviz;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.Uri;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.pubnub.api.*;
import org.json.*;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    Pubnub pn = new Pubnub("demo", "demo");
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView tvLatLong;

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

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.addJavascriptInterface(new WebAppInterface(this));
        webView.setWebViewClient(new lizzieBrowser());
        //webView.setWebChromeClient(new WebChromeClient());
        String idkMan = Uri.parse("file:///android_asset/eon.html").toString();
        webView.loadUrl(idkMan);

        tvLatLong = (TextView) findViewById(R.id.tvLatLong);

        buildGoogleApiClient();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        else {
            Toast.makeText(this, "not connected...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            tvLatLong.setText("Latitude: "+ String.valueOf(mLastLocation.getLatitude()) + "Longitude: " + String.valueOf(mLastLocation.getLongitude()));
        }
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
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
            //return true;
        }
    }
}


