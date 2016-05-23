package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.util.Constants;

public class MainScreenActivity extends BaseActivity {


    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        super.setContentViewAndId(R.layout.activity_main_screen, Constants.homeActivity);

        ((TextView)findViewById(R.id.agent_name_text_view)).setText(KetabiApplication.getInstance().getUserName());

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    public void findBooksClicked(View v){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();

                            Intent intent = new Intent(MainScreenActivity.this, SearchBookActivity.class);
                            startActivity(intent);
                        }
                    });
                }else {

                    Intent intent = new Intent(MainScreenActivity.this, SearchBookActivity.class);
                    startActivity(intent);
                }
            }
        }, 500);
    }

    public void uploadBooksClicked(View v){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainScreenActivity.this, UploadBookActivity.class);
                Pair<View, String> pair1 = Pair.create(findViewById(R.id.upload_books_colored_circular_view), getString(R.string.upper_half));
                Pair<View, String> pair2 = Pair.create(findViewById(R.id.upload_books_white_circular_view), getString(R.string.lower_half));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainScreenActivity.this, pair1, pair2);
                startActivity(intent, options.toBundle());
            }
        }, 500);
    }

}
