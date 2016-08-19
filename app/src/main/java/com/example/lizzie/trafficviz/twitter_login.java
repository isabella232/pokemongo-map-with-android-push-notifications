package com.example.lizzie.trafficviz;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.Callback;
import io.fabric.sdk.android.Fabric;


/**
 * Created by lizzie on 8/9/16.
 */

public class twitter_login extends Activity {
    private TwitterLoginButton loginButton;
    private static final String twitterKey = "kdxvwh1nDAUZsPyaiuH70QLQr";
    private static final String twitterSecret = "4YYA7BPcH5DDw0bFmTdWqcs9gjDjhnsJfPTMjvhnmpCNDVpbMw";
    TwitterSession sesh;
    Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_login);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitterKey, twitterSecret);
        Fabric.with(this, new TwitterCore(authConfig), new TwitterCore(authConfig));
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                //Twitter.getInstance().core.getSessionManager().getActiveSession();
                sesh = result.data;
                String msg = "@" + sesh.getUserName() + " logged in! (#" + sesh.getUserId() + ")";
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                Intent dataToPass = new Intent(getApplicationContext(), MainActivity.class);
                dataToPass.putExtra("username", msg);
                startActivity(dataToPass);
                Intent moveIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(moveIntent);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
