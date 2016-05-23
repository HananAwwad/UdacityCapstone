package com.hananawwad.ketabi.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * @author hananawwad
 */
public class NetworkUtil {

    public static boolean isNetworkConnected(Context context){
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
