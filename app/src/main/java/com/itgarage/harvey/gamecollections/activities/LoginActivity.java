package com.itgarage.harvey.gamecollections.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncClientManager;

import java.util.Arrays;

public class LoginActivity extends Activity implements Session.StatusCallback{


    SharedPreferences preferences;
    LoginButton facebookLoginButton;
    Button appStart;
    TextView welcomeTv;
    static final String TAG = "LoginActivity";
    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, this);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        CognitoSyncClientManager.init(this);

        welcomeTv = (TextView) findViewById(R.id.welcomeTextView);
        appStart = (Button) findViewById(R.id.btn_app_start);

        facebookLoginButton = (LoginButton) findViewById(R.id.btn_facebook_login);
        facebookLoginButton.setReadPermissions(Arrays.asList("email"));
        facebookLoginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    welcomeTv.setText("Hello " + user.getName());
                } else {
                    welcomeTv.setText("You are not logged in.");
                }
            }
        });

        final Session session = Session
                .openActiveSessionFromCache(LoginActivity.this);
        if (session != null) {
            setFacebookSession(session);
        }

        appStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NaviDrawerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void call(Session session, SessionState sessionState, Exception e) {
        if (session.isOpened()) {
            setFacebookSession(session);
            // make request to the /me API
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Toast.makeText(LoginActivity.this,
                                "Hello " + user.getName(), Toast.LENGTH_SHORT)
                                .show();
                        /*TextView title = (TextView) findViewById(R.id.welcomeTextView);
                        title.setText("Hello " + user.getName());*/
                        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", user.getName());
                        editor.putString("profileId", user.getId());
                        editor.apply();
                    }
                }
            }).executeAsync();
        }else if(session.isClosed()){
            Log.i(TAG, "facebook session is closed");
            Toast.makeText(LoginActivity.this,
                    "You logged out.", Toast.LENGTH_SHORT)
                    .show();
            preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", "");
            editor.putString("profileId", "");
            editor.apply();
            appStart.setVisibility(View.GONE);
        }
    }

    private void setFacebookSession(Session session) {
        Log.i(TAG, "facebook token: " + session.getAccessToken());
        CognitoSyncClientManager.addLogins("graph.facebook.com",
                session.getAccessToken());
        appStart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }
    private long lastClickTime = 0;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(lastClickTime<=0){
            Toast.makeText(this, getString(R.string.back_button_click_toast_text), Toast.LENGTH_SHORT).show();
            lastClickTime = System.currentTimeMillis();
        }else {
            long currentClickTime = System.currentTimeMillis();
            if(currentClickTime-lastClickTime<2000){
                finish();
            }else {
                Toast.makeText(this, getString(R.string.back_button_click_toast_text), Toast.LENGTH_SHORT).show();
                lastClickTime = System.currentTimeMillis();
            }
        }
    }
}
