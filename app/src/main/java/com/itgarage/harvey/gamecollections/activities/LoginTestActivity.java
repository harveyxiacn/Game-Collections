package com.itgarage.harvey.gamecollections.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncClientManager;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncGames;

public class LoginTestActivity extends ActionBarActivity implements Session.StatusCallback{
    Toolbar toolbar;
    private static final String TAG = "LoginTestActivity";

    private static final String[] APP_SCOPES = {
            "profile"
    };
    private Button btnLoginFacebook, btnWipedata, btnLogoutFacebook;

    SharedPreferences preferences;

    TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        /**
         * Initializes the sync client. This must be call before you can use it.
         */
        CognitoSyncClientManager.init(this);

        btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Facebook Login
                Session.openActiveSession(LoginTestActivity.this, true,
                        LoginTestActivity.this);
            }
        });

        btnLogoutFacebook = (Button) findViewById(R.id.btnLogoutFacebook);

        final Session session = Session
                .openActiveSessionFromCache(LoginTestActivity.this);
        if (session != null) {
            setFacebookSession(session);
        }

        btnLogoutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LoginTestActivity.this)
                        .setTitle("Logout facebook?")
                        .setMessage(
                                "This will log off your current session. ")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // clear facebook login status
                                        if (session != null) {
                                            session.closeAndClearTokenInformation();
                                        }
                                        btnLoginFacebook
                                                .setVisibility(View.VISIBLE);
                                        btnLogoutFacebook.setVisibility(View.GONE);
                                        /*if (mAuthManager != null) {
                                            mAuthManager
                                                    .clearAuthorizationState(null);
                                        }
                                        btnLoginLWA.setVisibility(View.VISIBLE);*/
                                        // wipe data
                                        /*CognitoSyncManager client = CognitoSyncClientManager.getInstance();
                                        Dataset dataset = client.openOrCreateDataset("games");
                                        dataset.delete();*/


                                        // Wipe shared preferences
                                        /*AmazonSharedPreferencesWrapper.wipe(PreferenceManager
                                                .getDefaultSharedPreferences(LoginTestActivity.this));*/

                                        TextView title = (TextView) findViewById(R.id.tvTitle);
                                        title.setText("Login");

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("username", "");
                                        editor.putString("profileId", "");
                                        editor.apply();
                                    }

                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                    }
                                }).show();
            }
        });

        btnWipedata = (Button) findViewById(R.id.btnWipedata);
        btnWipedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LoginTestActivity.this)
                        .setTitle("Wipe data?")
                        .setMessage(
                                "This will log off your current session and wipe all user data. "
                                        + "Any data not synchronized will be lost.")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // clear facebook login status
                                        if (session != null) {
                                            session.closeAndClearTokenInformation();
                                        }
                                        btnLoginFacebook
                                                .setVisibility(View.VISIBLE);
                                        /*if (mAuthManager != null) {
                                            mAuthManager
                                                    .clearAuthorizationState(null);
                                        }
                                        btnLoginLWA.setVisibility(View.VISIBLE);*/
                                        // wipe data
                                        CognitoSyncClientManager.getInstance()
                                                .wipeData();

                                        TextView title = (TextView) findViewById(R.id.tvTitle);
                                        title.setText("Login");

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("username", "");
                                        editor.putString("profileId", "");
                                        editor.apply();
                                    }

                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                    }
                                }).show();
            }
        });

        findViewById(R.id.btnListDatasets).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginTestActivity.this,
                                ListDatasetsActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.btnSyncGames).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(LoginTestActivity.this);
                cognitoSyncGames.refreshDatasetMetadata();
            }
        });

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        String username = preferences.getString("username", "");
        if(!username.equals("")){
            tvTitle.setText(username);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
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
                        Toast.makeText(LoginTestActivity.this,
                                "Hello " + user.getName(), Toast.LENGTH_LONG)
                                .show();
                        TextView title = (TextView) findViewById(R.id.tvTitle);
                        title.setText("Hello " + user.getName());
                        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", user.getName());
                        editor.putString("profileId", user.getId());
                        editor.apply();
                    }
                }
            }).executeAsync();
        }
    }
    private void setFacebookSession(Session session) {
        Log.i(TAG, "facebook token: " + session.getAccessToken());
        CognitoSyncClientManager.addLogins("graph.facebook.com",
                session.getAccessToken());
        btnLoginFacebook.setVisibility(View.GONE);
        btnLogoutFacebook.setVisibility(View.VISIBLE);
    }
}
