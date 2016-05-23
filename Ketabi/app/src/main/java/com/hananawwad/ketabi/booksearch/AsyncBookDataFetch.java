package com.hananawwad.ketabi.booksearch;

import android.os.AsyncTask;

import com.hananawwad.ketabi.BuildConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.Cleanup;
import lombok.Setter;

/**
 * @author hananawwad
 */
public class AsyncBookDataFetch extends AsyncTask<String, Void, String>{

    private static final int CONNECTION_TIMEOUT = 10000;

    @Setter
    private TaskCompleteEvent taskCompleteEvent;

    public interface TaskCompleteEvent{
        void onTaskComplete(String result);
        void onTaskFailed();
    }

    @Override
    protected String doInBackground(String... params) {

        if (params == null || params.length == 0){

            return "";
        }

        String url = params[0];

        try {
            return makeConnectionAndFetchResult(url);

        } catch (Exception e){

            return "";
        }
    }

    @Override
    protected void onPostExecute(String result){

        if(result.equals("")){
            if(taskCompleteEvent != null){
                taskCompleteEvent.onTaskFailed();
            }
            return;
        }

        if(taskCompleteEvent != null){
            taskCompleteEvent.onTaskComplete(result);
        }
    }

    private String makeConnectionAndFetchResult(String url) throws Exception{

        @Cleanup("disconnect")HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(CONNECTION_TIMEOUT);

        urlConnection.setRequestProperty("key", BuildConfig.GOOGLE_BOOK_KEY);

        if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
            return "";
        }

        @Cleanup InputStream inputStream = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String inputStr;
        StringBuilder responseStrBuilder = new StringBuilder();
        while ((inputStr = bufferedReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return responseStrBuilder.toString();
    }
}
