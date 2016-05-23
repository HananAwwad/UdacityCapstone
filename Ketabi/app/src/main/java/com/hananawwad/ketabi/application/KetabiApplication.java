package com.hananawwad.ketabi.application;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.firebase.client.Firebase;
import com.hananawwad.ketabi.BuildConfig;
import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.acra.ACRAReportSender;
import com.hananawwad.ketabi.preferences.SharedPreference;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;

/**
 * @author hananawwad
 */
@ReportsCrashes(
        formUri = "",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast// optional. displays a Toast message when the user accepts to send a report.
)
public class KetabiApplication extends Application {

    private static KetabiApplication mInstance;
    private  String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String uId;

    @Override
    public void onCreate() {

        /** Initialize ACRA error reports sending library **/
        try {
            ACRA.init(this);
            ACRAReportSender yourSender = new ACRAReportSender();
            ACRA.getErrorReporter().setReportSender(yourSender);
        } catch (Throwable ignored){ /** Nothing can be done at such early stage **/}

        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());
        mInstance = this;

        /** Initialize firebase **/
        Firebase.setAndroidContext(this);


    }

    /** Get the application instance **/
    public static synchronized KetabiApplication getInstance() {
        return mInstance;
    }




    @DebugLog
    public String getUserName(){
        if(username == null || username.length() == 0){
            SharedPreference sharedPreference = new SharedPreference(this);
            username = sharedPreference.getUserName();
        }
        return username;
    }

    /** Sets the agent name **/
    @DebugLog
    public void setUserName(String agentName){
        this.username = agentName;

        /** Save agent name permanently **/
        SharedPreference sharedPreference = new SharedPreference(this);
        sharedPreference.saveUserName(agentName);
    }


    /**************************************************************************************
     ********************************** Email  ********************************************
     **************************************************************************************/

    /** Get the email **/
    @DebugLog
    public String getEmail(){
        if(email == null || email.length() == 0){
            SharedPreference sharedPreference = new SharedPreference(this);
            email = sharedPreference.loadEmail();
        }
        return email;
    }

    /** Sets the email **/
    @DebugLog
    public void setEmail(String email){
        this.email = email;

        /** Save email permanently **/
        SharedPreference sharedPreference = new SharedPreference(this);
        sharedPreference.saveEmail(email);
    }

    /**************************************************************************************
     ********************************** Password ******************************************
     **************************************************************************************/

    /** Get the password **/
    @DebugLog
    public String getPassword(){
        if(password == null || password.length() == 0){
            SharedPreference sharedPreference = new SharedPreference(this);
            password = sharedPreference.loadPassword();
        }
        return password;
    }

    /** Sets the password **/
    @DebugLog
    public void setPassword(String password){
        this.password = password;

        /** Save password permanently **/
        SharedPreference sharedPreference = new SharedPreference(this);
        sharedPreference.savePassword(password);
    }

    /**************************************************************************************
     ********************************** Phone Number **************************************
     **************************************************************************************/

    /** Get the phoneNumber **/
    @DebugLog
    public String getPhoneNumber(){
        if(phoneNumber == null || phoneNumber.length() == 0){
            SharedPreference sharedPreference = new SharedPreference(this);
            phoneNumber = sharedPreference.loadPhoneNumber();
        }
        return phoneNumber;
    }

    /** Sets the phoneNumber **/
    @DebugLog
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;

        /** Save phoneNumber name permanently **/
        SharedPreference sharedPreference = new SharedPreference(this);
        sharedPreference.savePhoneNumber(phoneNumber);
    }

    /**************************************************************************************
     ********************************** Uid ***********************************************
     **************************************************************************************/

    /** Get the uId **/
    @DebugLog
    public String getUid(){
        if(uId == null || uId.length() == 0){
            SharedPreference sharedPreference = new SharedPreference(this);
            uId = sharedPreference.loadUid();
        }
        return uId;
    }

    /** Sets the uId **/
    @DebugLog
    public void setUid(String uId){
        this.uId = uId;

        /** Save uId name permanently **/
        SharedPreference sharedPreference = new SharedPreference(this);
        sharedPreference.saveUid(uId);
    }
}
