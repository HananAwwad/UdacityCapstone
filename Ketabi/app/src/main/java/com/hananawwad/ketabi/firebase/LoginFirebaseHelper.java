package com.hananawwad.ketabi.firebase;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hananawwad.ketabi.BuildConfig;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.models.UserModel;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;

import java.util.HashMap;
import java.util.Map;

import hugo.weaving.DebugLog;
import lombok.Setter;

/**
 * @author hananawwad
 */
public class LoginFirebaseHelper {

    @Setter
    LoginEvents loginEvents;
    Firebase firebase;

    public LoginFirebaseHelper(){
        firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);
    }

    @DebugLog
    public void checkAuthentication(){

        firebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {

                if (loginEvents == null) {
                    return;
                }
                if (authData != null) {
                    loginEvents.onUserAuthCheck(true, authData.getUid());
                } else {
                    loginEvents.onUserAuthCheck(false, "");
                }
            }
        });
    }

    @DebugLog
    public void createNewUser(UserModel userModel, String password){

        firebase.createUser(userModel.getEmailId(), password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                if (loginEvents != null) {
                    loginEvents.onUserCreated(true, null);
                }
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                if (loginEvents != null) {
                    loginEvents.onUserCreated(false, firebaseError);
                }
            }
        });
    }

    /** Logs in user, also updates user's information to firebase database **/
    @DebugLog
    public void firstLogIn(final UserModel userModel, final String password){

        /** Log in user **/
        firebase.authWithPassword(userModel.getEmailId(), password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

                KetabiApplication.getInstance().setUid(authData.getUid());

                Map<String, String> userDataMap = new HashMap<>();
                userDataMap.put(Constants.NAME, userModel.getUserName());
                userDataMap.put(Constants.PHONENUMBER, userModel.getPhoneNumber());
                firebase.child(Constants.USERs).child(authData.getUid()).setValue(userDataMap);

                Map<String, String> phoneNumberDataMap = new HashMap<>();

                String email = userModel.getEmailId();
                email = email.replaceAll("[.]", "_dot_");
                email = email.replaceAll("[@]", "_at_the_rate_");
                phoneNumberDataMap.put(email, password);
                firebase.child(Constants.RPNs).child(userModel.getPhoneNumber()).setValue(phoneNumberDataMap);
                if (loginEvents != null) {
                    loginEvents.onFirstTimeUserLogIn(true, null);
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (loginEvents != null) {
                    loginEvents.onFirstTimeUserLogIn(false, firebaseError);
                }
            }
        });
    }

    @DebugLog
    public void existingLogIn(String email, String password){

        firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(final AuthData authData) {
                if(loginEvents != null){
                    loginEvents.onExistingUserLogIn(true, authData.getUid(), null);
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (loginEvents != null) {
                    loginEvents.onExistingUserLogIn(false, "", firebaseError);
                }
            }
        });

    }

    @DebugLog
    public void getNameForExistingUser(String uid){
        final Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.USERs + "/" + uid);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (loginEvents != null) {
                    loginEvents.onUserNameFetchedFromExistingUser((String) dataSnapshot.child(Constants.NAME).getValue());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                FLog.d(this, firebaseError.toString());
            }
        });
    }


    @DebugLog
    public void checkWhetherPhoneNumberAlreadyExists(final String phoneNumber){

        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.RPNs);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(loginEvents == null){
                    return;
                }
                if(dataSnapshot.hasChild(phoneNumber)){
                    HashMap<String, String> emailPassMap = (HashMap < String, String >)dataSnapshot.child(phoneNumber).getValue();
                    for(Map.Entry<String,String> entry : emailPassMap.entrySet()){
                        String email = entry.getKey();
                        email = email.replaceAll("_dot_",".");
                        email = email.replaceAll("_at_the_rate_", "@");
                        loginEvents.onExistingUser(true, email, entry.getValue());
                    }
                } else {
                    loginEvents.onExistingUser(false, "", "");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                loginEvents.onExistingUser(false, "", "");
            }
        });
    }

    public interface LoginEvents{
        void onUserAuthCheck(boolean isLoggedIn, String uid);
        void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error);
        void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error);
        void onExistingUser(boolean isExisting, String email, String password);
        void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String uid ,FirebaseError error);
        void onUserNameFetchedFromExistingUser(String userName);
        void onUserUploadedBookIdFetchedFromExistingUser(String bId);
    }


    public void setLoginEvents(LoginEvents loginEvents) {
        this.loginEvents = loginEvents;
    }
}
