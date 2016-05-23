package com.hananawwad.ketabi.firebase;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hananawwad.ketabi.BuildConfig;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.preferences.SharedPreference;

import java.util.Date;

/**
 * @author hananawwad
 */
public class FeedbackFirebaseHelper {

    private Context context;
    private SharedPreference sharedPreference;

    public FeedbackFirebaseHelper(Context context){
        this.context = context;
        sharedPreference = new SharedPreference(context);
    }
    public void sendFeedback(final String feedback){
        final Firebase myFirebaseRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);
        Firebase connectedRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + ".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    Date date = new Date(System.currentTimeMillis());
                    myFirebaseRef.child("Feedback " + date).setValue(feedback, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                sharedPreference.saveFeedback(feedback);
                            } else {
                                sharedPreference.removeFeedback();

                            }
                        }
                    });

                } else {
                    sharedPreference.saveFeedback(feedback);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                sharedPreference.saveFeedback(feedback);
            }
        });

    }

}
