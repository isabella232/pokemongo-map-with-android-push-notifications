//package com.example.lizzie.trafficviz;
//
///**
// * Created by lizzie on 7/8/16.
// */
//import android.content.Context;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.webkit.JavascriptInterface;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Toast;
//
//public class EonUI extends WebViewClient {
//    Context mContext;
//
//    /** Instantiate the interface and set the context */
//    EonUI(Context c) {
//        mContext = c;
//    }
//
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        view.loadUrl(url);
//        return true;
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        WebView eon = (WebView) findViewById(R.id.webview);
//        WebSettings webSettings = eon.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//    }
//
//    /** Show a toast from the web page */
//    @JavascriptInterface
//    public void showToast(String toast) {
//        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
//    }
//}
