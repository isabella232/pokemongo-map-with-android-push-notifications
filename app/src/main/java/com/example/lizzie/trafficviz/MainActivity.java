package com.example.lizzie.trafficviz;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;


import java.util.Arrays;

public class MainActivity extends Activity {

    WebView webView;
    //TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
//    SessionConfiguration config = new SessionConfiguration.Builder()
//            .setClientId("fE9Sab2S8kEwyCYYF8Bdw7yXPF1NTx1g")
//            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
//            .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
//            .build();

}
