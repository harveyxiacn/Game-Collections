package com.itgarage.harvey.gamecollections.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class holds function to check if the phone is online or offline.
 */
public class NetworkStatus {
    /**
     * Check if the network is available.
     * @param context Current context.
     * @return true - the phone is online. false - the phone is offline.
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if(infos!=null){
                for (NetworkInfo info : infos) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
