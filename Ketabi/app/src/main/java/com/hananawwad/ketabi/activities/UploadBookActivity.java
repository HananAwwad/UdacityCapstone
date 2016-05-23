package com.hananawwad.ketabi.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.util.BundleUtil;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;
import com.hananawwad.ketabi.util.HardwareUtil;
import com.hananawwad.ketabi.util.PermissionUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author hananawwad
 */
public class UploadBookActivity extends AppCompatActivity {

    @Bind(R.id.isbn_edit_text)
    AppCompatEditText isbnEditText;

    @Bind(R.id.error_text_view)
    TextView errorTextView;


    @Bind(R.id.upload_book_main_relative_layout)
    RelativeLayout mainRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
        ButterKnife.bind(this);
    }

    public void tickClicked(View v) {
        if (validateIsbn()) {
            callSearchBookWithISBN(isbnEditText.getText().toString());
        }
    }


    public void backClicked(View v) {
        super.onBackPressed();
    }


    public void scanClicked(View v) {

        if (!HardwareUtil.isCameraAvailable(this)) {
            Snackbar.make(mainRelativeLayout, R.string.no_camera_detected, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            confirmCameraPermission();
        } else {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, Constants.ISBN_REQUEST);
        }
    }


    public void confirmCameraPermission() {

        List<String> deniedPermissions = PermissionUtil.deniedPermissions(
                this,
                Manifest.permission.CAMERA
        );

        if (deniedPermissions.isEmpty()) {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, Constants.ISBN_REQUEST);
            return;
        }

        ActivityCompat.requestPermissions(this, deniedPermissions.toArray(new String[deniedPermissions.size()]), Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS) {
            if (!PermissionUtil.confirmGrantedPermissions(grantResults)) {
                Snackbar.make(mainRelativeLayout, R.string.camera_permission_denied, Snackbar.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, Constants.ISBN_REQUEST);
            }
        }
    }

    private boolean validateIsbn() {
        String isbn = isbnEditText.getText().toString();
        if (!(isbn.length() == 13)) {
            errorTextView.setText(getResources().getString(R.string.enter_valid_isbn));
            requestFocus(isbnEditText);
            return false;
        } else {
            errorTextView.setText("");
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ISBN_REQUEST) {

            if (resultCode == Constants.RESULT_OK) {
                String isbn = BundleUtil.getStringFromBundle(null, data.getExtras(), Constants.ISBN_KEY, "");
                FLog.d(this, isbn);
                callSearchBookWithISBN(isbn);

            } else if (resultCode == Constants.RESULT_FAILURE) {
                Snackbar.make(mainRelativeLayout, R.string.not_a_book_isbn_scanned, Snackbar.LENGTH_LONG).show();

            }
        }
    }


    private void callSearchBookWithISBN(String isbn) {
        Intent intent = new Intent(UploadBookActivity.this, SearchBookActivity.class);
        intent.putExtra(Constants.ISBN_KEY, isbn);
        intent.putExtra(Constants.IS_UPLOAD, true);
        startActivity(intent);
        finish();
    }
}
