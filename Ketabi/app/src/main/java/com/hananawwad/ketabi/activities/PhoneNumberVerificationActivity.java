package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.firebase.client.FirebaseError;
import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.firebase.FirebaseErrorHandler;
import com.hananawwad.ketabi.firebase.LoginFirebaseHelper;
import com.hananawwad.ketabi.firebase.UploadedBooksFirebaseHelper;
import com.hananawwad.ketabi.models.UserModel;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.tajchert.sample.DotsTextView;

/**
 * @author hananawwad
 */
public class PhoneNumberVerificationActivity extends AppCompatActivity {

    @Bind(R.id.loading_view)
    View loadingView;


    @Bind(R.id.card_view_1)
    View cardView;

    @Bind(R.id.loading_text_view)
    TextView loadingTextView;

    @Bind(R.id.dots_text_view)
    DotsTextView dotsTextView;

    FirebaseErrorHandler firebaseErrorHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);
        ButterKnife.bind(this);

        firebaseErrorHandler = new FirebaseErrorHandler(findViewById(R.id.main_container));

        /** Using twitter digits for phone number verification **/
        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setAuthTheme(R.style.CustomDigitsTheme);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {

                if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.length() < 10) {

                    showFailure();
                    return;
                }

                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.phone_number_verified) + phoneNumber);
                loadingView.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);

                if (phoneNumber.length() > 10) {
                    int startIndex = phoneNumber.length() - 10;
                    phoneNumber = phoneNumber.substring(startIndex);
                }
                LoginFirebaseHelper loginFirebaseHelper = new LoginFirebaseHelper();
                loginFirebaseHelper.checkWhetherPhoneNumberAlreadyExists(phoneNumber);
                loginFirebaseHelper.setLoginEvents(new LoginEventsListener(loginFirebaseHelper, phoneNumber));
            }

            @Override
            public void failure(DigitsException exception) {
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.sign_in_with_digits_failure) + exception);
                showFailure();
            }
        });

    }

    private void showFailure() {
        Snackbar.make(findViewById(R.id.main_container), R.string.verification_failed, Snackbar.LENGTH_LONG).show();
    }

    class LoginEventsListener implements LoginFirebaseHelper.LoginEvents {

        LoginFirebaseHelper loginFirebaseHelper;
        String phoneNumber;
        String email;
        String password;
        String uid;

        public LoginEventsListener(LoginFirebaseHelper loginFirebaseHelper, String phoneNumber) {
            this.phoneNumber = phoneNumber;
            this.loginFirebaseHelper = loginFirebaseHelper;
        }

        @Override
        public void onUserAuthCheck(boolean isLoggedIn, String uid) {
        }

        @Override
        public void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error) {
        }

        @Override
        public void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error) {
        }

        @Override
        public void onExistingUser(boolean isExisting, String email, String password) {
            if (isExisting) {
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.existing_phone_number));
                this.email = email;
                this.password = password;
                loginFirebaseHelper.existingLogIn(email, password);
            } else {
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.new_phone_number));
                Intent intent = new Intent(PhoneNumberVerificationActivity.this, LoginActivity.class);
                intent.putExtra(Constants.USER_DATA_KEY, new UserModel("", "", phoneNumber));
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String uid, FirebaseError error) {
            if (isSuccessfullyLoggedIn) {
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.successfully_logged_in_existing_phone_number));
                this.uid = uid;
                loginFirebaseHelper.getNameForExistingUser(uid);
            } else {
                FLog.e(PhoneNumberVerificationActivity.this, getString(R.string.could_not_log_in_existing_phon_number) + error.toString());
                Intent intent = new Intent(PhoneNumberVerificationActivity.this, LoginActivity.class);
                intent.putExtra(Constants.USER_DATA_KEY, new UserModel("", "", phoneNumber));
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onUserNameFetchedFromExistingUser(String userName) {

            KetabiApplication.getInstance().setUserName(userName);
            KetabiApplication.getInstance().setUid(uid);
            KetabiApplication.getInstance().setPhoneNumber(phoneNumber);
            KetabiApplication.getInstance().setEmail(email);
            KetabiApplication.getInstance().setPassword(password);

            dotsTextView.setVisibility(View.GONE);
            loadingTextView.setText(String.format(getString(R.string.welcome_back), userName));

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    UploadedBooksFirebaseHelper uploadedBooksFirebaseHelper = new UploadedBooksFirebaseHelper();
                    uploadedBooksFirebaseHelper.fetchAllUploadedBooks();

                    Intent intent = new Intent(PhoneNumberVerificationActivity.this, MainScreenActivity.class);
                    PhoneNumberVerificationActivity.this.startActivity(intent);
                    PhoneNumberVerificationActivity.this.finish();
                }
            }, 2500);
        }

        @Override
        public void onUserUploadedBookIdFetchedFromExistingUser(String bId) {
        }
    }
}
