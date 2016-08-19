package com.example.lizzie.trafficviz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;


import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivity;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.android.rides.RideRequestViewError;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.SessionConfiguration.Builder;
import com.uber.sdk.rides.client.error.ApiError;
import static com.uber.sdk.android.core.utils.Preconditions.checkNotNull;
import static com.uber.sdk.android.core.utils.Preconditions.checkState;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

public class MainActivity extends Activity implements RideRequestButtonCallback {
    private static final String dropoffAddress = "725 Folsom, San Francisco";
    //lat,long = 37.784141, -122.399266
    private static final double dropoffLat = 37.784141;
    private static final double dropoffLong = -122.399266;
    private static final String dropoffAka = "PubNub HQ";
    private static final String errLogTag = "Android Muni map with EON.js, Fabric, Uber APIs";
    private static final String pickupAddress = "1355 Market, San Francisco";
    //lat, long: 37.776994, -122.416384
    private static final double pickupLat = 37.776994;
    private static final double pickupLong = -122.416384;
    private static final String pickupAka = "Twitter HQ";
    private SessionConfiguration config;

    private static final String clientId = BuildConfig.CLIENT_ID;
    private static final String redirectUri = BuildConfig.REDIRECT_URI;
    private static final String serverToken = BuildConfig.SERVER_TOKEN;

    private RideRequestButton rideReqButton;
    private static final String twitterKey = "kdxvwh1nDAUZsPyaiuH70QLQr";
    private static final String twitterSecret = "4YYA7BPcH5DDw0bFmTdWqcs9gjDjhnsJfPTMjvhnmpCNDVpbMw";
    private Button composeTweetButton;

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get fabric data from intent
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String val = extras.getString("key");
        }

        config = new SessionConfiguration
                //.setRedirectUri(r)
        .Builder().setRedirectUri(redirectUri)
                .setClientId(clientId)
                .setServerToken(serverToken)
                .build();

        //validateConfiguration(config);
        ServerTokenSession sesh = new ServerTokenSession(config);

        RideParameters rideParams = new RideParameters.Builder()
                .setPickupLocation(pickupLat, pickupLong, pickupAka, pickupAddress)
                .setDropoffLocation(dropoffLat, dropoffLong, dropoffAka, dropoffAddress)
                .build();

        rideReqButton = (RideRequestButton) findViewById(R.id.uber_req_button);
        rideReqButton.setRideParameters(rideParams);
        rideReqButton.setSession(sesh);
        rideReqButton.setCallback(this);
        rideReqButton.loadRideInformation();

        RideParameters rideParametersCheapestProduct = new RideParameters.Builder()
                .setPickupLocation(pickupLat, pickupLong, pickupAka, pickupAddress)
                .setDropoffLocation(dropoffLat, dropoffLong, dropoffAka, dropoffAddress)
                .build();

        webView = (WebView) findViewById(R.id.webView1);
        webView.setInitialScale(1);
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/eon.html");

        //compose tweet button clicked
        composeTweetButton = (Button) findViewById(R.id.compTweetButt);
        composeTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet();
            }
        });

    }

    @Override
    public void onRideInformationLoaded() {
        Toast.makeText(this, "estimates have been refre$hed", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onError(ApiError apiErr) {
        Toast.makeText(this, apiErr.getClientErrors().get(0).getTitle(), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onError(Throwable throwable) {
        Log.e("eon yaaa", "error obtaining metadata", throwable);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (data.getSerializableExtra(RideRequestActivity.RIDE_REQUEST_ERROR) != null) {
            RideRequestViewError error = (RideRequestViewError) data.getSerializableExtra(RideRequestActivity
                    .RIDE_REQUEST_ERROR);
            Toast.makeText(MainActivity.this, "RideRequest error " + error.name(), Toast.LENGTH_SHORT).show();
            //Log.d(ERR_LOG_TAG, "Error occurred in the Ride Request Widget: " + error.toString().toLowerCase());
        }
    }

    protected void composeTweet() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitterKey, twitterSecret);
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());
        TweetComposer.Builder tweetBuilder = new TweetComposer.Builder(this).
                text("Checking Muni buses with PubNub's EON.js on my Android phone. May call an Uber, though");
        tweetBuilder.show();
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
