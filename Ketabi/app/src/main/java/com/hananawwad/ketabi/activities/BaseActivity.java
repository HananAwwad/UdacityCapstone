package com.hananawwad.ketabi.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.alertDialogs.AlertMessageDialog;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.PermissionUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;


public abstract class BaseActivity extends AppCompatActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;


    @Getter
    private boolean isDrawerOpen;


    @Setter
    public int activityId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            confirmAllPermissions();
        }


        isDrawerOpen = false;
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });


        activityId = -1;
    }



    public void setContentViewAndId(int layoutId, int activityId) {


        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_frame_layout);
        layoutInflater.inflate(layoutId, frameLayout, true);
        this.activityId = activityId;
    }


    public void toggleDrawer(View v) {
        if (isDrawerOpen) {
            closeDrawer();
        } else {
            openDrawer();
        }
        isDrawerOpen = !isDrawerOpen;
    }

    private void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }


    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    public void homeClicked(View v) {
        handleItemClicked(Constants.homeDrawerItemCode);
    }


    public void uploadedBooksClicked(View v) {
        handleItemClicked(Constants.uploadedBooksDrawerItemCode);
    }

    public void connectionsClicked(View v) {
        handleItemClicked(Constants.connectionsDrawerItemCode);
    }



    public void handleItemClicked(int drawerItemCode) {

        if (activityId == Constants.homeActivity) {
            toggleDrawer(null);
            if (drawerItemCode != Constants.homeDrawerItemCode) {
                startCorrespondingActivity(drawerItemCode);
            }
        } else {
            if (activityId == drawerItemCode) {
                toggleDrawer(null);
            } else if (drawerItemCode == Constants.homeDrawerItemCode) {
                finish();
            } else {
                startCorrespondingActivity(drawerItemCode);
                finish();
            }
        }
    }

    /**
     * Start the activity corresponding to drawer item code given
     *
     * @param drawerItemCode it is the code given to each drawer item
     */
    public void startCorrespondingActivity(int drawerItemCode) {

        Intent intent = null;
        switch (drawerItemCode) {
            case Constants.homeDrawerItemCode:
                intent = new Intent(this, MainScreenActivity.class);
                break;
            case Constants.uploadedBooksDrawerItemCode:
                intent = new Intent(this, UploadedBookActivity.class);
                break;
            case Constants.connectionsDrawerItemCode:
                intent = new Intent(this, ConnectionsActivity.class);
                break;

        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    public void navigationDrawerHeaderClicked(View v) {
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen) {
            toggleDrawer(null);
        } else {
            super.onBackPressed();
        }
    }


    private void confirmAllPermissions() {

        List<String> deniedPermissions = PermissionUtil.deniedPermissions(
                this,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
        );

        if (deniedPermissions.isEmpty()) {
            return;
        }


        ActivityCompat.requestPermissions(this, deniedPermissions.toArray(new String[deniedPermissions.size()]), Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS) {
            if (!PermissionUtil.confirmGrantedPermissions(grantResults)) {
                needPermissionsDialog();
            }
        }
    }

    public void needPermissionsDialog() {

        AlertMessageDialog alertMessageDialog = new AlertMessageDialog(
                this,
                getString(R.string.permissions_denied),
                getString(R.string.go_to_app_settings),
                getString(R.string.cancel),
                getString(R.string.app_settings)
        );


        alertMessageDialog.show();

        alertMessageDialog.setOnAlertButtonClicked(new AlertMessageDialog.OnAlertButtonClicked() {
            @Override
            public void onLeftButtonClicked(View v) {
                finish();
            }

            @Override
            public void onRightButtonClicked(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(getString(R.string.package_string), getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

                finish();
            }
        });
    }


}
