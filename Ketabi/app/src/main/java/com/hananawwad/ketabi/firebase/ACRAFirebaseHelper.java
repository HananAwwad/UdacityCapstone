package com.hananawwad.ketabi.firebase;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hananawwad.ketabi.BuildConfig;
import com.hananawwad.ketabi.preferences.SharedPreference;

import java.util.Date;
import java.util.Map;

/**
 * @author hananawwad
 */
public class ACRAFirebaseHelper {

    private Context context;
    private SharedPreference sharedPreference;

    public ACRAFirebaseHelper(Context context){
        this.context = context;
        sharedPreference = new SharedPreference(context);
    }

    /** Try to send ACRA Map **/
    public void sendAcraMap(final Map<String,String> acraMap){

        /** Firebase linked **/
        final Firebase myFirebaseRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);

        /** Need to know firebase connection presence **/
        Firebase connectedRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + ".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    Date date = new Date(System.currentTimeMillis());
                    myFirebaseRef.child("ACRA " + date).setValue(acraMap, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                sharedPreference.saveAcraMap(acraMap);
                            } else {
                                sharedPreference.removeAcraMap();

                            }
                        }
                    });

                } else {
                    sharedPreference.saveAcraMap(acraMap);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                sharedPreference.saveAcraMap(acraMap);
            }
        });

    }

}
