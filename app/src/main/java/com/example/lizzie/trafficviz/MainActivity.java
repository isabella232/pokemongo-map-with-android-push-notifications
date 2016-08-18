package com.example.lizzie.trafficviz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;


import com.google.android.gms.common.api.Scope;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

public class MainActivity extends Activity {

    WebView webView;
    //TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button backButton;

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
        backButton = (Button) findViewById(R.id.screenTransition2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent2 = new Intent(getApplicationContext(), twitter_login.class);
                startActivity(moveIntent2);
            }
        });
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
