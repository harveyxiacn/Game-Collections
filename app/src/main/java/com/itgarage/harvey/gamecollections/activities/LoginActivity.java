package com.itgarage.harvey.gamecollections.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

/**
 * Activity for user to choose login with Facebook or Google+.
 *
 * After successfully login, navigate the app to NaviDrawerActivity for use.
 *
 * If the phone is offline from Internet, just start NaviDrawerActivity for use and sync data next connect time.
 */
public class LoginActivity extends Activity implements Session.StatusCallback, ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {
    /**
     * Shared preferences for load and save preferences.
     */
    SharedPreferences preferences;
    /**
     * Login button for Facebook login and logout.
     */
    LoginButton facebookLoginButton;
    /**
     * Button for start the app, navigate to NaviDrawerActivity.
     */
    Button appStart;
    /**
     * TextView for display welcome text with user name or logout.
     */
    //TextView welcomeTv;
    /**
     * Debug Tag for use logging debug output to LogCat.
     */
    static final String TAG = "LoginActivity";
    /**
     * UI lifecycle helper from Facebook API.
     */
    private UiLifecycleHelper uiHelper;

    /**
     * Request code used to invoke sign in user interactions.
     */
    private static final int RC_SIGN_IN_GOOGLE = 0;
    /**
     * Google Profile image size set in pixels.
     */
    private static final int PROFILE_PIC_SIZE = 400;
    /**
     * Client used to interact with Google APIs.
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    /**
     * A flag indicating that the sign in button is clicked.
     */
    private boolean mSignInClicked;
    /**
     * Connection result from Google play service connect.
     */
    private ConnectionResult mConnectionResult;
    /**
     * Login button for Google+ login.
     */
    SignInButton googleLoginButton;
    /**
     * Button for Google+ logout.
     */
    Button googleLogoutButton;
    /**
     * Token from Google+ login.
     */
    public String googleToken;
    /**
     * Shared preferences key for user name.
     */
    static final String USERNAME_KEY = "username";
    /**
     * Shared preferences key for Facebook profile ID or Google+ profile image URL.
     */
    static final String PROFILE_ID_KEY = "profileId";
    /**
     * Shared preferences key for indicating is login with Facebook or Google+ or empty for no login.
     */
    static final String IS_FB_OR_GOOGLE = "is fb or google";
    /**
     * Shared preferences key for indicate back to login activity from NaviDrawerActivity or not.
     * true - Exit app from LoginActivity, start NaviDrawerActivity when app start if Logged in with Facebook,
     * or Login button is clicked, start NaviDrawerActivity when successfully login.
     * false - back from NaviDrawerActivity, not going to start again until App start button is clicked.
     */
    static final String LOGIN_ACTIVITY_KEY = "login activity";
    /**
     * Shared preferences key for indicate if login Google+ before exit the app or not.
     */
    static final String IS_SIGN_OUT_GOOGLE_ON_FINISH = "sign out google";
    /**
     * A flag indicates in on creating login activity or not.
     */
    boolean isOnCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (!NetworkStatus.isNetworkAvailable(this)) {
            // If the phone is offline from Internet, start the offline mode
            //Log.i(TAG, "offline start navi");
            startActivity(new Intent(this, NaviDrawerActivity.class));
            finish();
        }
        //Log.i(TAG, "onCreate");
        editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
        editor.putBoolean(NaviDrawerActivity.NAVI_ACTIVITY_KEY, false);
        editor.putBoolean(IS_SIGN_OUT_GOOGLE_ON_FINISH, false);
        editor.apply();
        // print facebook hash key for calling facebook native app.
        /*try {
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
        }*/
        if(NetworkStatus.isNetworkAvailable(this)) {
            // AWS client
            CognitoSyncClientManager.init(this);
            //Log.i(TAG, "CognitoSyncClientManager init");
        }
        // Google stuffs
        mGoogleApiClient = buildGoogleApiClient();
        if(NetworkStatus.isNetworkAvailable(this)) {
            mGoogleApiClient.connect();
        }
        googleLoginButton = (SignInButton) findViewById(R.id.btn_google_login);
        googleLoginButton.setOnClickListener(this);
        googleLogoutButton = (Button) findViewById(R.id.btn_google_logout);
        googleLogoutButton.setOnClickListener(this);
        // common stuffs
        //welcomeTv = (TextView) findViewById(R.id.welcomeTextView);
        appStart = (Button) findViewById(R.id.btn_app_start);
        appStart.setOnClickListener(this);
        // Facebook stuffs
        uiHelper = new UiLifecycleHelper(this, this);
        uiHelper.onCreate(savedInstanceState);
        facebookLoginButton = (LoginButton) findViewById(R.id.btn_facebook_login);
        facebookLoginButton.setOnClickListener(this);
        // update UIs by no login or login with facebook or google+
        String fb_or_google = preferences.getString(IS_FB_OR_GOOGLE, "");
        //Log.i(TAG, fb_or_google);
        if (!mGoogleApiClient.isConnected()) {
            fb_or_google = "";
        }
        isOnCreate = true;
        updateButtonsVisibilityOnCreateOrResume(fb_or_google);
    }

    /**
     * Update buttons visibility on activity create or resume.
     *
     * @param fb_or_google Param for deciding operation.
     */
    private void updateButtonsVisibilityOnCreateOrResume(String fb_or_google) {
        appStart.setText("App start");
        switch (fb_or_google) {
            case "facebook":// login with Facebook
                if(googleLoginButton.getVisibility()!=View.GONE)
                    googleLoginButton.setVisibility(View.GONE);
                if(googleLogoutButton.getVisibility()!=View.GONE)
                    googleLogoutButton.setVisibility(View.GONE);
                if(appStart.getVisibility()!=View.VISIBLE)
                    appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorFb));
                //welcomeTv.setText("Welcome " + preferences.getString(USERNAME_KEY, ""));
                break;
            case "google":// login with Google+
                if(facebookLoginButton.getVisibility()!=View.GONE)
                    facebookLoginButton.setVisibility(View.GONE);
                if(googleLoginButton.getVisibility()!=View.GONE)
                    googleLoginButton.setVisibility(View.GONE);
                if(googleLogoutButton.getVisibility()!=View.VISIBLE)
                    googleLogoutButton.setVisibility(View.VISIBLE);
                if(appStart.getVisibility()!=View.VISIBLE)
                    appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorGp));
                //welcomeTv.setText("Welcome " + preferences.getString(USERNAME_KEY, ""));
                if(isOnCreate&&preferences.getBoolean(IS_SIGN_OUT_GOOGLE_ON_FINISH, false)){
                    // if it is creating login activity and login with Google+ before last exit.
                    // sign in Google+.
                    signInWithGplus();
                }
                break;
            case "":// no login
                if(facebookLoginButton.getVisibility()!=View.VISIBLE)
                    facebookLoginButton.setVisibility(View.VISIBLE);
                if(googleLoginButton.getVisibility()!=View.VISIBLE)
                    googleLoginButton.setVisibility(View.VISIBLE);
                if(googleLogoutButton.getVisibility()!=View.GONE)
                    googleLogoutButton.setVisibility(View.GONE);
                if(appStart.getVisibility()!=View.GONE)
                    appStart.setVisibility(View.GONE);
                //welcomeTv.setText("You logged out");
                break;
        }
    }

    /**
     * Build google api client before use it.
     *
     * @return Built google api client, ready to use.
     */
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
        // sign in with Google+
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
                        // get username and profile id of facebook
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

    /**
     * Set Facebook token to AWS cognito sync client.
     *
     * @param session Facebook session.
     */
    private void setFacebookSession(Session session) {
        //Log.i(TAG, "facebook token: " + session.getAccessToken());
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
        isOnCreate = false;
        updateButtonsVisibilityOnCreateOrResume(preferences.getString(IS_FB_OR_GOOGLE, ""));
        if(preferences.getBoolean(NaviDrawerActivity.NAVI_ACTIVITY_KEY, false)){
            //Log.i(TAG, "Sync Local game");
            // sync local games
            CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
            cognitoSyncGames.openDataset();
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

    /**
     * Save last click time to calculate how many seconds passed between twice back button pressed.
     */
    private long lastClickTime = 0;

    /**
     * Override onBackPressed to let user quit the app by press back button by twice in 2 seconds.
     */
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
        //Log.i(TAG, "onConnected");
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

    /**
     * Async task to get google token if use google+ login.
     */
    private class GoogleTokenTask extends AsyncTask<Void, Void, Void> {
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

    /**
     * Update buttons' visibility and save shared preferences.
     *
     * @param user User should be saved to shared preferences.
     */
    private void signedInUIsUpdate(User user) {
        //Log.i(TAG, "signedInUIsUpdate");
        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switch (user.getFb_or_google()) {
            case "facebook":
                googleLoginButton.setVisibility(View.GONE);
                appStart.setVisibility(View.VISIBLE);
                appStart.setBackgroundColor(getResources().getColor(R.color.ColorFb));
                //welcomeTv.setText("Welcome " + user.getUsername());
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
                //welcomeTv.setText("Welcome " + user.getUsername());

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
        if (preferences.getBoolean(LOGIN_ACTIVITY_KEY, false)) {
            //Log.i(TAG, "navi "+String.valueOf(preferences.getBoolean(NaviDrawerActivity.NAVI_ACTIVITY_KEY, false)));
            //Log.i(TAG, "login "+String.valueOf(preferences.getBoolean(LOGIN_ACTIVITY_KEY, false)));
            CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
            cognitoSyncGames.refreshDatasetMetadata();
            editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
            editor.putBoolean(NaviDrawerActivity.NAVI_ACTIVITY_KEY, false);
            editor.apply();
        }
    }

    /**
     * Update buttons' visibility and shared preferences.
     *
     * @param fb_or_google Operation flag.
     */
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
        //welcomeTv.setText("You are not logged in.");
        editor.putString(IS_FB_OR_GOOGLE, "");
        editor.putString(USERNAME_KEY, "");
        editor.putString(PROFILE_ID_KEY, "");
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
                    // Signin button clicked
                    editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
                    editor.apply();
                    signInWithGplus();
                    break;
                case R.id.btn_google_logout:
                    // Signout button clicked
                    signOutFromGplus();
                    break;
                case R.id.btn_app_start:
                    // Sync the game data
                    CognitoSyncGames cognitoSyncGames = new CognitoSyncGames(this);
                    cognitoSyncGames.refreshDatasetMetadata();
                    break;
                case R.id.btn_facebook_login:
                    editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
                    editor.apply();
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
            //Log.i(TAG, "signout google");
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();
            signedOutUIsUpdate("google");
        }
    }

    @Override
    public void finish() {
        // If logged in with Google+, save IS_SIGN_OUT_GOOGLE_ON_FINISH as true
        // The app will login with Google+ when the app start next time
        if(preferences.getString(IS_FB_OR_GOOGLE, "").equals("google")) {
            preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_SIGN_OUT_GOOGLE_ON_FINISH, true);
            editor.apply();
            signOutFromGplus();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGIN_ACTIVITY_KEY, true);
        editor.apply();
        super.finish();
    }
}
