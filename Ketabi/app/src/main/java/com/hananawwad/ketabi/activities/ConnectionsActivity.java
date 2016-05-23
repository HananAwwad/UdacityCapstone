package com.hananawwad.ketabi.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.adapter.ConnectionsAdapter;
import com.hananawwad.ketabi.alertDialogs.AlertMessageDialog;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.firebase.RPNFirebaseHelper;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * @author hananawwad
 */
public class ConnectionsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView agentNameTextView;

    TextView phoneNumberTextView;

    ListView connectionsListView;

    ConnectionsAdapter connectionsAdapter;

    List<String> rpnData;

    TextView loadingTextView;

    TextView nothingFoundTextView;

    RelativeLayout mainRelativeLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewAndId(R.layout.activity_connections, Constants.connectionsActivity);

        loadingTextView = (TextView)findViewById(R.id.loading_text_view);
        nothingFoundTextView = (TextView)findViewById(R.id.nothing_found_text_view);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout_connections);
        connectionsListView = (ListView) findViewById(R.id.connections_list_view);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_number_text_view);
        agentNameTextView = (TextView)findViewById(R.id.agent_name_text_view);
        agentNameTextView.setText(KetabiApplication.getInstance().getUserName());
        phoneNumberTextView.setText(KetabiApplication.getInstance().getPhoneNumber());

        if (NetworkUtil.isNetworkConnected(this)) {
            loadingTextView.setVisibility(View.VISIBLE);
            nothingFoundTextView.setVisibility(View.GONE);
            getLoaderManager().initLoader(1, null, this);
        } else {
            Snackbar.make(mainRelativeLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
        }
    }


    public void syncClicked(View v) {
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        v.startAnimation(rotation);

        getLoaderManager().restartLoader(1, null, this);
    }

    public void shareClicked(View v) {
        sendShareIntent();
    }


    public void showShareAlert() {

        AlertMessageDialog alertMessageDialog = new AlertMessageDialog(
                this,
                getString(R.string.invite_people),
                getString(R.string.want_to_invite),
                getString(R.string.no),
                getString(R.string.yes)
        );


        alertMessageDialog.show();


        alertMessageDialog.setOnAlertButtonClicked(new AlertMessageDialog.OnAlertButtonClicked() {
            @Override
            public void onLeftButtonClicked(View v) {
            }

            @Override
            public void onRightButtonClicked(View v) {
                sendShareIntent();
            }
        });
    }

    public void sendShareIntent() {

        Uri imageUri = Uri.parse(getString(R.string.android_resource_link) + getPackageName() + getString(R.string.drawable_directory) + getString(R.string.ketabi_icon));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_love_ketabi));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType(getString(R.string.image_type_jpeg));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_friends_using)));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + getString(R.string.asc)
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (data.getCount() == 0) {
            nothingFoundTextView.setVisibility(View.VISIBLE);
            loadingTextView.setVisibility(View.GONE);
        } else {
            loadingTextView.setVisibility(View.GONE);
            nothingFoundTextView.setVisibility(View.GONE);
        }
        getDataFromFirebase(data);

        ImageView syncImageView = (ImageView) findViewById(R.id.syncButton);
        syncImageView.clearAnimation();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void getDataFromFirebase(final Cursor data) {

        RPNFirebaseHelper rpnFirebaseHelper = new RPNFirebaseHelper();
        rpnFirebaseHelper.fetchListOfRPN();
        rpnFirebaseHelper.setRpnEvents(new RPNFirebaseHelper.RPNEvents() {
            @Override
            public void onRPNFetched(List<String> rpnList, FirebaseError firebaseError) {
                if (firebaseError == null) {
                    rpnData = new ArrayList<>(rpnList);
                }

                connectionsAdapter = new ConnectionsAdapter(ConnectionsActivity.this, data, R.layout.connections_list_view_item, rpnData);

                connectionsAdapter.setConnectionItemClickListener(new ConnectionsAdapter.ConnectionItemClickListener() {
                    @Override
                    public void onItemClicked(View v) {
                        showShareAlert();
                    }
                });


                connectionsListView.setAdapter(connectionsAdapter);
            }
        });
    }
}
