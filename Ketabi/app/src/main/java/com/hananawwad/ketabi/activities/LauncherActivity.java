package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.FirebaseError;
import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.firebase.LoginFirebaseHelper;

/**
 *
 * @author hananawwad
 */
public class LauncherActivity extends AppCompatActivity {


    private static final long ANIMATED_LOGIN_STARTOFFSET = 3500;

    private Handler loginHandler;

    private Runnable loginRunnable;

    private LoginEventListener loginEventListener;

    private static boolean isDestroyed;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        setupAnimatedLogo();
        isDestroyed = false;
    }


    /**
     * Initializes animated logo fragment for {@link android.graphics.DashPathEffect} animation.
     * It uses svg glyph data to fetch {@link android.graphics.Path} object, which helps
     * render logo text on screen.
     */
    public void setupAnimatedLogo(){

        loginHandler = new Handler();
        loginRunnable = new Runnable() {
            @Override
            public void run() {

                /** Check user login **/
                LoginFirebaseHelper loginFirebaseHelper = new LoginFirebaseHelper();
                loginFirebaseHelper.checkAuthentication();
                loginEventListener = new LoginEventListener(loginFirebaseHelper);
                loginFirebaseHelper.setLoginEvents(loginEventListener);
            }
        };
        loginHandler.postDelayed(loginRunnable, ANIMATED_LOGIN_STARTOFFSET);
    }


    /** Listening login events **/
    class LoginEventListener implements LoginFirebaseHelper.LoginEvents{
        
        private LoginFirebaseHelper loginFirebaseHelper;
        
        public LoginEventListener(LoginFirebaseHelper loginFirebaseHelper){
            this.loginFirebaseHelper = loginFirebaseHelper;    
        }

        @Override
        public void onUserAuthCheck(boolean isLoggedIn, String uid) {
            
            if(isLoggedIn){

                /** Already logged in **/

                if(!isDestroyed) {

                    /** Start the main Activity **/
                    Intent intent = new Intent(LauncherActivity.this, MainScreenActivity.class);
                    LauncherActivity.this.startActivity(intent);
                }
                LauncherActivity.this.finish();
                
            } else {

                /** Not logged in **/

                String email = KetabiApplication.getInstance().getEmail();
                String password = KetabiApplication.getInstance().getPassword();
                if(email == null 
                        || password == null
                        || email.length() == 0
                        || password.length() == 0){
                    
                    /** Its first login **/
                    Intent intent = new Intent(LauncherActivity.this, PhoneNumberVerificationActivity.class);
                    LauncherActivity.this.startActivity(intent);
                    LauncherActivity.this.finish();

                } else {

                    /** There has been a token expiry **/
                    loginFirebaseHelper.existingLogIn(email, password);
                }
            }
        }

        @Override
        public void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error) {

        }

        @Override
        public void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error) {

        }

        @Override
        public void onExistingUser(boolean isExisting, String email, String password) {

        }

        @Override
        public void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String uid, FirebaseError error) {

            if(isSuccessfullyLoggedIn){


                /** Start the main Activity **/
                Intent intent = new Intent(LauncherActivity.this, MainScreenActivity.class);
                LauncherActivity.this.startActivity(intent);
                LauncherActivity.this.finish();

            } else {


                /** Its first login **/
                Intent intent = new Intent(LauncherActivity.this, PhoneNumberVerificationActivity.class);
                LauncherActivity.this.startActivity(intent);
                LauncherActivity.this.finish();
            }
        }

        @Override
        public void onUserNameFetchedFromExistingUser(String userName) {

        }

        @Override
        public void onUserUploadedBookIdFetchedFromExistingUser(String bId) {

        }
    }

    public void onDestroy(){
        loginHandler.removeCallbacks(loginRunnable);
        loginEventListener = null;
        isDestroyed = true;
        super.onDestroy();
    }
}
