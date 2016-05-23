package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    /**
     * Back pressed by user, stop the camera and
     * set result as failure.
     */
    @Override
    public void onBackPressed(){

        setResult(Constants.RESULT_CANCELLED);


        mScannerView.stopCamera();
        super.onBackPressed();
    }

    @Override
    public void handleResult(Result rawResult) {

        FLog.d(ScannerActivity.this, rawResult.getText());
        FLog.d(ScannerActivity.this, rawResult.getBarcodeFormat().toString());

        mScannerView.stopCamera();


        if(rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13)) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.ISBN_KEY, rawResult.getText());
            setResult(Constants.RESULT_OK, returnIntent);
        } else {
            setResult(Constants.RESULT_FAILURE);
        }

        finish();
    }
}
