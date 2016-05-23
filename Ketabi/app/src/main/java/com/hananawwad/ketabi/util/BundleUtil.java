package com.hananawwad.ketabi.util;

import android.os.Bundle;

import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.models.UserModel;

/**
 * @author hananawwad
 */
public class BundleUtil {

    public static String getStringFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, String defaultValue){

        String returnValue = null;
        if(intentExtras != null){
            returnValue = (String) intentExtras.getString(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (String) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    public static int getIntFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, int defaultValue){

        Integer returnValue = null;
        if(intentExtras != null){
            returnValue = (Integer) intentExtras.getInt(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (Integer) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    public static boolean getBooleanFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, boolean defaultValue){

        Boolean returnValue = null;
        if(intentExtras != null){
            returnValue = (boolean) intentExtras.getBoolean(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (boolean) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    /** Returns int value from initialized bundle **/
    public static Long getLongFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, Long defaultValue){

        Long returnValue = null;
        if(intentExtras != null){
            returnValue = (Long) intentExtras.getLong(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (Long) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    /** Get UserModel model object from bundle instance **/
    public static UserModel getUserDataFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, UserModel defaultValue){

        UserModel userModel = null;
        if (savedInstanceState != null) {
            userModel = (UserModel) savedInstanceState.getParcelable(token);
        }
        if(userModel != null){
            return userModel;
        }
        if(intentExtras != null){
            userModel = (UserModel) intentExtras.getParcelable(token);
        }
        if(userModel != null){
            return userModel;
        }
        userModel = defaultValue;
        return userModel;
    }

    public static SearchedBook getSearchedBookFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, SearchedBook defaultValue){

        SearchedBook searchedBook = null;
        if (savedInstanceState != null) {
            searchedBook = (SearchedBook) savedInstanceState.getParcelable(token);
        }
        if(searchedBook != null){
            return searchedBook;
        }
        if(intentExtras != null){
            searchedBook = (SearchedBook) intentExtras.getParcelable(token);
        }
        if(searchedBook != null){
            return searchedBook;
        }
        searchedBook = defaultValue;
        return searchedBook;
    }

    public static UploadedBook getUploadedBookFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, UploadedBook defaultValue){

        UploadedBook uploadedBook = null;
        if (savedInstanceState != null) {
            uploadedBook = (UploadedBook) savedInstanceState.getParcelable(token);
        }
        if(uploadedBook != null){
            return uploadedBook;
        }
        if(intentExtras != null){
            uploadedBook = (UploadedBook) intentExtras.getParcelable(token);
        }
        if(uploadedBook != null){
            return uploadedBook;
        }
        uploadedBook = defaultValue;
        return uploadedBook;
    }

    public static byte[] getByteArrayFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, byte[] defaultValue){

        byte[] returnValue = null;
        if(intentExtras != null){
            returnValue = (byte[]) intentExtras.getByteArray(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (byte[]) savedInstanceState.getByteArray(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
        
    }
}
