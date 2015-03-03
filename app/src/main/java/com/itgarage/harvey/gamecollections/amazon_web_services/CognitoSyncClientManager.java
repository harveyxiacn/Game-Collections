package com.itgarage.harvey.gamecollections.amazon_web_services;


import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.auth.IdentityChangedListener;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;

import java.util.HashMap;
import java.util.Map;

public class CognitoSyncClientManager implements IdentityChangedListener {

    private static final String TAG = "CognitoSyncClientManage";

    /**
     * Enter here the Identity Pool associated with your app and the AWS
     * region where it belongs. Get this information from the AWS console.
     */

    private static final String IDENTITY_POOL_ID = CognitoPool.POOL_ID;
    private static final Regions REGION = Regions.US_EAST_1;

    private static CognitoSyncManager syncClient;
    protected static CognitoCachingCredentialsProvider credentialsProvider = null;
    protected static AWSAbstractCognitoIdentityProvider developerIdentityProvider;

    /**
     * Set this flag to true for using developer authenticated identities
     */
    private static boolean useDeveloperAuthenticatedIdentities = false;


    /**
     * Initializes the Cognito Identity and Sync clients. This must be called before getInstance().
     *
     * @param context a context of the app
     */
    public static void init(Context context) {

        if (syncClient != null) return;

        /*
         * For using developer authenticated identities make sure you set the
         * flag to true and configure all the constants in the
         * DeveloperAuthenticationProvider class.
         */
        /*useDeveloperAuthenticatedIdentities = useDeveloperAuthenticatedIdentities
                && DeveloperAuthenticationProvider.isDeveloperAuthenticatedAppConfigured();

        if (useDeveloperAuthenticatedIdentities) {
            developerIdentityProvider = new DeveloperAuthenticationProvider(
                    null, IDENTITY_POOL_ID, context, Regions.US_EAST_1);
            credentialsProvider = new CognitoCachingCredentialsProvider(context, developerIdentityProvider,
                    REGION);
            Log.i(TAG, "Using developer authenticated identities");
        } else {*/
            credentialsProvider = new CognitoCachingCredentialsProvider(context, IDENTITY_POOL_ID,
                    REGION);
            //Log.i(TAG, "Developer authenticated identities is not configured");
        //}

        syncClient = new CognitoSyncManager(context, REGION, credentialsProvider);
    }

    /**
     * Sets the login so that you can use authorized identity. This requires a
     * network request, so you should call it in a background thread.
     *
     * @param providerName the name of the external identity provider
     * @param token openId token
     */
    public static void addLogins(String providerName, String token) {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }

        Map<String, String> logins = credentialsProvider.getLogins();
        if (logins == null) {
            logins = new HashMap<String, String>();
        }
        logins.put(providerName, token);
        Log.i(TAG, logins.toString());
        credentialsProvider.withLogins(logins);
    }

    /**
     * Gets the singleton instance of the CognitoClient. init() must be called
     * prior to this.
     *
     * @return an instance of CognitoClient
     */
    public static CognitoSyncManager getInstance() {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }
        return syncClient;
    }

    /**
     * Returns a credentials provider object
     *
     * @return
     */
    public CognitoCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    @Override
    public void identityChanged(String oldIdentityId, String newIdentityId) {

    }
}