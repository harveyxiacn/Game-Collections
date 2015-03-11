package com.itgarage.harvey.gamecollections.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
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
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncClientManager;
import com.itgarage.harvey.gamecollections.amazon_web_services.CognitoSyncGames;
import com.itgarage.harvey.gamecollections.models.User;
import com.itgarage.harvey.gamecollections.utils.NetworkStatus;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity implements Session.StatusCallback, ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {


    SharedPreferences preferences;
    LoginButton facebookLoginButton;
    Button appStart, connectMobileInternet, connectWIFIInternet;
    TextView welcomeTv;
    static final String TAG = "LoginActivity";
    private UiLifecycleHelper uiHelper;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN_GOOGLE = 0;
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;


    SignInButton googleLoginButton;
    Button googleLogoutButton;
    public String googleToken;
    
    static final String USERNAME_KEY = "username";
    static final String PROFILE_ID_KEY = "profileId";
    static final String IS_FB_OR_GOOGLE = "is fb or google";
    static final String LOGIN_ACTIVITY_KEY = "login activity";

    private static final int DIALOG_PLAY_SERVICES_ERROR = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
        editor.apply();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.itgarage.harvey.gamecollections",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // AWS client
        CognitoSyncClientManager.init(this);
        // Google
        mGoogleApiClient = buildGoogleApiClient();
        googleLoginButton = (SignInButton) findViewById(R.id.btn_google_login);
        googleLoginButton.setOnClickListener(this);
        googleLogoutButton = (Button) findViewById(R.id.btn_google_logout);
        googleLogoutButton.setOnClickListener(this);
        // common
        welcomeTv = (TextView) findViewById(R.id.welcomeTextView);
        appStart = (Button) findViewById(R.id.btn_app_start);
        // Start app button
        appStart.setOnClickListener(this);
        connectMobileInternet = (Button) findViewById(R.id.btn_connect_mobile);
        connectMobileInternet.setOnClickListener(this);
        connectWIFIInternet = (Button) findViewById(R.id.btn_connect_wifi);
        connectWIFIInternet.setOnClickListener(this);
        // Facebook
        uiHelper = new UiLifecycleHelper(this, this);
        uiHelper.onCreate(savedInstanceState);
        facebookLoginButton = (LoginButton) findViewById(R.id.btn_facebook_login);
        facebookLoginButton.setOnClickListener(this);
        // update UIs
        String fb_or_google = preferences.getString(IS_FB_OR_GOOGLE, "");
        //Log.i(TAG, fb_or_google);
        if(!mGoogleApiClient.isConnected()){
            fb_or_google = "";
        }
        if(!NetworkStatus.isNetworkAvailable(this)){
            facebookLoginButton.setVisibility(View.GONE);
            googleLoginButton.setVisibility(View.GONE);
            welcomeTv.setText("You don't connect to Internet, please check network, quit and restart the app again or use offline mode.");
            appStart.setText("Use Offline Mode");
            appStart.setVisibility(View.VISIBLE);
            connectMobileInternet.setVisibility(View.VISIBLE);
            connectWIFIInternet.setVisibility(View.VISIBLE);
        }else {
            updateButtonsVisibilityOnCreateOrResume(fb_or_google);
            connectMobileInternet.setVisibility(View.GONE);
            connectWIFIInternet.setVisibility(View.GONE);
        }
        // Start app button
        appStart.setOnClickListener(this);

    }

    private void updateButtonsVisibilityOnCreateOrResume(String fb_or_google) {
        appStart.setText("App start");
        switch (fb_or_google) {
            case "facebook":
                final Session session = Session
                        .openActiveSessionFromCache(this);
                if (session != null) {
                    setFacebookSession(session);
                }
                googleLoginButton.setVisibility(View.GONE);
                googleLogoutButton.setVisibility(View.GONE);
                appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorFb));
                welcomeTv.setText("Welcome " + preferences.getString(USERNAME_KEY, ""));
                break;
            case "google":
                facebookLoginButton.setVisibility(View.GONE);
                googleLoginButton.setVisibility(View.GONE);
                googleLogoutButton.setVisibility(View.VISIBLE);
                appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorGp));
                welcomeTv.setText("Welcome " + preferences.getString(USERNAME_KEY, ""));
                break;
            case "":
                facebookLoginButton.setVisibility(View.VISIBLE);
                googleLoginButton.setVisibility(View.VISIBLE);
                googleLogoutButton.setVisibility(View.GONE);
                appStart.setVisibility(View.GONE);
                welcomeTv.setText("You logged out");
                break;
        }
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                responseCode, data);
        uiHelper.onActivityResult(requestCode, responseCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
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
                        User currentUser = new User(user.getName(), user.getId(), "facebook");
                        signedInUIsUpdate(currentUser);
                    }
                }
            }).executeAsync();
        } else if (session.isClosed()) {
            //Log.i(TAG, "facebook session is closed");
            signedOutUIsUpdate("facebook");
        }
    }

    private void setFacebookSession(Session session) {
        Log.i(TAG, "facebook token: " + session.getAccessToken());
        CognitoSyncClientManager.addLogins("graph.facebook.com",
                session.getAccessToken());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        //Log.i(TAG, "onResume " + preferences.getString(IS_FB_OR_GOOGLE, ""));
        //updateButtonsVisibilityOnCreateOrResume(preferences.getString(IS_FB_OR_GOOGLE, ""));
        if(NetworkStatus.isNetworkAvailable(this)){
            connectMobileInternet.setVisibility(View.GONE);
            connectWIFIInternet.setVisibility(View.GONE);
            updateButtonsVisibilityOnCreateOrResume(preferences.getString(IS_FB_OR_GOOGLE, ""));
        }
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
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        if (lastClickTime <= 0) {
            Toast.makeText(this, getString(R.string.back_button_click_toast_text), Toast.LENGTH_SHORT).show();
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            if (currentClickTime - lastClickTime < 2000) {
                finish();
            } else {
                Toast.makeText(this, getString(R.string.back_button_click_toast_text), Toast.LENGTH_SHORT).show();
                lastClickTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Called when our Activity successfully connects to Google Play services.
     */
    @Override
    public void onConnected(Bundle bundle) {
        // Indicate that the sign in process is complete.
        Log.i(TAG, "onConnected");
        mSignInClicked = false;
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String imageUrl = currentUser.getImage().getUrl();
        imageUrl = imageUrl.substring(0,
                imageUrl.length() - 2)
                + PROFILE_PIC_SIZE;
        User user = new User(currentUser.getDisplayName(), imageUrl, "google");
        signedInUIsUpdate(user);
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        GoogleTokenTask googleTokenTask = new GoogleTokenTask(this);
        googleTokenTask.execute();
    }

    private class GoogleTokenTask extends AsyncTask<Void, Void, Void>{
        Activity activity;

        private GoogleTokenTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AccountManager accountManager = AccountManager.get(activity);
            Account[] accounts = accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            try {
                googleToken = GoogleAuthUtil.getToken(getApplicationContext(), accounts[0].name,
                        "audience:server:client_id:457905976296-1fllhsb87geles3bqli2sueakrp4nbqc.apps.googleusercontent.com");
            } catch (IOException | GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Log.i(TAG, "googleToken "+googleToken);
            CognitoSyncClientManager.addLogins("accounts.google.com", googleToken);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        signedOutUIsUpdate("");
    }

    /**
     * Called when our Activity could not connect to Google Play services.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    private void onSignedOut() {
        signedOutUIsUpdate("google");
    }

    private void signedInUIsUpdate(User user) {
        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switch (user.getFb_or_google()) {
            case "facebook":
                googleLoginButton.setVisibility(View.GONE);
                appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorFb));
                welcomeTv.setText("Welcome " + user.getUsername());
                /*Toast.makeText(LoginActivity.this,
                        "Login facebook", Toast.LENGTH_SHORT)
                        .show();*/
                editor.putString(USERNAME_KEY, user.getUsername());
                editor.putString(PROFILE_ID_KEY, user.getProfileID());
                editor.putString(IS_FB_OR_GOOGLE, user.getFb_or_google());
                editor.apply();
                break;
            case "google":
                googleLoginButton.setVisibility(View.GONE);
                facebookLoginButton.setVisibility(View.GONE);
                googleLogoutButton.setVisibility(View.VISIBLE);
                appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorGp));
                welcomeTv.setText("Welcome " + user.getUsername());

                /*Toast.makeText(LoginActivity.this,
                        "Login google", Toast.LENGTH_SHORT)
                        .show();*/
                preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString(USERNAME_KEY, user.getUsername());
                editor.putString(PROFILE_ID_KEY, user.getProfileID());
                editor.putString(IS_FB_OR_GOOGLE, user.getFb_or_google());
                editor.apply();
                break;
        }
        Log.i(TAG, String.valueOf(preferences.getBoolean(LOGIN_ACTIVITY_KEY, false)));
        if(preferences.getBoolean(LOGIN_ACTIVITY_KEY, false)) {
            CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
            cognitoSyncGames.refreshDatasetMetadata(this);
            editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
            editor.apply();
        }
    }

    private void signedOutUIsUpdate(String fb_or_google) {
        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        googleLoginButton.setVisibility(View.VISIBLE);
        facebookLoginButton.setVisibility(View.VISIBLE);
        googleLogoutButton.setVisibility(View.GONE);
        appStart.setVisibility(View.GONE);
        /*Toast.makeText(LoginActivity.this,
                "You logged out. " + fb_or_google, Toast.LENGTH_SHORT)
                .show();*/
        welcomeTv.setText("You are not logged in.");

        editor.putString(USERNAME_KEY, "");
        editor.putString(PROFILE_ID_KEY, "");
        editor.putString(IS_FB_OR_GOOGLE, "");
        editor.apply();
    }

    /**
     * Starts an appropriate intent for user interaction to resolve the current
     * error preventing the user from being signed in.
     */
    protected void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN_GOOGLE);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            // We only process button clicks when GoogleApiClient is not transitioning
            // between connected and not connected.
            SharedPreferences.Editor editor = preferences.edit();
            switch (v.getId()) {
                case R.id.btn_google_login:
                    if(NetworkStatus.isNetworkAvailable(this)) {
                        // Signin button clicked
                        editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
                        editor.apply();
                        signInWithGplus();
                    }else {
                        Toast.makeText(this, "You are offline. Please check network.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_google_logout:
                    // Signout button clicked
                    signOutFromGplus();
                    break;
                case R.id.btn_app_start:
                    if(NetworkStatus.isNetworkAvailable(this)) {
                        // Sync the game data
                        CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
                        cognitoSyncGames.refreshDatasetMetadata(this);
                    }else {
                        Intent intent = new Intent(this, NaviDrawerActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.btn_facebook_login:
                    if(NetworkStatus.isNetworkAvailable(this)) {
                        editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
                        editor.apply();
                    }else {
                        Toast.makeText(this, "You are offline. Please check network.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_connect_mobile:
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    break;
                case R.id.btn_connect_wifi:
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    break;
            }
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "signout google");
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();
            signedOutUIsUpdate("google");
        }
    }

    @Override
    public void finish() {
        super.finish();
        signOutFromGplus();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
        editor.apply();
    }
}
