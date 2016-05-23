package com.hananawwad.ketabi.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.hananawwad.ketabi.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hananawwad
 */
public class SharedPreference {

    private Context context;

    public SharedPreference(Context context){
        this.context = context.getApplicationContext();
    }

    public void saveAcraMap(Map<String,String> inputMap){

        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            if(editor != null) {
                editor.remove(Constants.acraMap).commit();
                editor.putString(Constants.acraMap, jsonString);
                editor.commit();
            }
        }
    }

    public Map<String,String> loadAcraMap(){

        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(Constants.acraMap, (new JSONObject()).toString());
                if(jsonString != null && jsonString.length() >0) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = (String)(jsonObject.get(key));
                        outputMap.put(key, value);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

        return outputMap;
    }

    public void removeAcraMap(){
        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            SharedPreferences.Editor editor = pSharedPref.edit();
            if(editor!=null) {
                editor.remove(Constants.acraMap);
                editor.commit();
            }
        }
    }

    public void saveFeedback(String feedback){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor!=null) {
                editor.remove(Constants.feedbackMap).commit();
                editor.putString(Constants.feedbackMap, feedback);
                editor.commit();
            }
        }
    }

    public String loadFeedback(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.feedbackMap, null);
        } else {
            return null;
        }
    }

    public void removeFeedback(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor!=null) {
                editor.remove(Constants.feedbackMap);
                editor.commit();
            }
        }
    }

    public void saveUserName(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.usernamePrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.username).commit();
                editor.putString(Constants.username, name);
                editor.commit();
            }
        }
    }

    public String getUserName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.usernamePrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.username, null);
        } else {
            return null;
        }
    }


    public void savePassword(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.passwordPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.passwordName).commit();
                editor.putString(Constants.passwordName, name);
                editor.commit();
            }
        }
    }

    public String loadPassword(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.passwordPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.passwordName, null);
        } else {
            return null;
        }
    }

    public void saveEmail(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.emailPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.emailName).commit();
                editor.putString(Constants.emailName, name);
                editor.commit();
            }
        }
    }

    public String loadEmail(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.emailPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.emailName, null);
        } else {
            return null;
        }
    }


    public void saveUid(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.uidPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.uidName).commit();
                editor.putString(Constants.uidName, name);
                editor.commit();
            }
        }
    }

    public String loadUid(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.uidPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.uidName, null);
        } else {
            return null;
        }
    }


    public void savePhoneNumber(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.phoneNumberPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.phoneNumberName).commit();
                editor.putString(Constants.phoneNumberName, name);
                editor.commit();
            }
        }
    }

    public String loadPhoneNumber(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.phoneNumberPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.phoneNumberName, null);
        } else {
            return null;
        }
    }
}
