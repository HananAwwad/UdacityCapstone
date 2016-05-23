package com.hananawwad.ketabi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hananawwad.ketabi.firebase.ACRAFirebaseHelper;
import com.hananawwad.ketabi.preferences.SharedPreference;
import com.hananawwad.ketabi.util.NetworkUtil;

import java.util.Map;

/**
 * @author hananawwad
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(NetworkUtil.isNetworkConnected(context)) {
            SharedPreference sharedPreference = new SharedPreference(context);
            ACRAFirebaseHelper acraFirebaseHelper = new ACRAFirebaseHelper(context);
            Map<String, String> acraMap = sharedPreference.loadAcraMap();
            if(acraMap != null && acraMap.size() > 0){
                acraFirebaseHelper.sendAcraMap(acraMap);
            }

        }
    }
}
