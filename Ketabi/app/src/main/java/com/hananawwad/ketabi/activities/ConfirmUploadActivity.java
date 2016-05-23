package com.hananawwad.ketabi.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.db.UploadedBookProvider;
import com.hananawwad.ketabi.db.UploadedBookTable;
import com.hananawwad.ketabi.firebase.UploadedBooksFirebaseHelper;
import com.hananawwad.ketabi.models.BookCondition;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.BundleUtil;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author hananawwad
 */
public class ConfirmUploadActivity extends AppCompatActivity {


    @Bind(R.id.book_preview_image_view)
    ImageView bookPreviewImageView;


    @Bind(R.id.preview_title_text_view)
    TextView previewTitleTextView;


    @Bind(R.id.preview_author_text_view)
    TextView previewAuthorTextView;

    @Bind(R.id.preview_description_text_view)
    TextView previewDescriptionTextView;


    @Bind(R.id.preview_publishing_details_text_view)
    TextView previewPublishingDetailsTextView;


    @Bind(R.id.book_condition_text_view)
    TextView conditionalNameTextView;

    @Bind(R.id.book_condition_details_text_view)
    TextView conditionalDetailTextView;


    @Bind(R.id.preview_timestamp_text_view)
    TextView previewTimestampTextView;

    @Bind(R.id.additional_details_app_compat_edit_text)
    TextView additionalDetailsAppCompatEditText;


    UploadedBook uploadedBook;

    Bitmap bookImage;


    boolean updateRequired;

    int bookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_upload);
        ButterKnife.bind(this);

        uploadedBook = BundleUtil.getUploadedBookFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPLOADED_BOOK_KEY, null);
        bookImage = BookUtil.convertByteArrayToBitmap(uploadedBook.getBookImage());
        updateRequired = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPDATE_REQUIRED, false);
        bookId = BundleUtil.getIntFromBundle(savedInstanceState, getIntent().getExtras(), Constants.BOOK_ID, -1);
        SearchedBook searchedBook = uploadedBook.getSearchedBook();


        setTitle(searchedBook.getTitle(), searchedBook.getSubTitle());
        setAuthors(searchedBook.getAuthors());
        setDescription(searchedBook.getDescription());
        setPublishingDetails(searchedBook.getPublisher(), searchedBook.getPublishedDate());
        BookCondition bookCondition = uploadedBook.getCondition();
        setConditionalDetails(bookCondition);


        DateFormat df = new SimpleDateFormat(getString(R.string.date_format));
        String date = df.format(Calendar.getInstance().getTime());
        previewTimestampTextView.setText(date);


        if (bookImage == null && searchedBook.getThumbnailLink().length() != 0) {
            Picasso.with(this).load(searchedBook.getThumbnailLink()).into(target);
        } else {
            bookPreviewImageView.setImageBitmap(bookImage);
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bookImage = bitmap;
            bookPreviewImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.UPLOADED_BOOK_KEY, uploadedBook);
        outState.putBoolean(Constants.UPDATE_REQUIRED, updateRequired);
        outState.putInt(Constants.BOOK_ID, bookId);
    }


    public void backClicked(View v) {
        super.onBackPressed();
    }

    public void tickClicked(View v) {

        /** Prepares {@link UploadedBook} domain object **/
        UploadedBook uploadedBookData = new UploadedBook.UploadedBookBuilder()
                .addSearchedBook(uploadedBook.getSearchedBook())
                .addCondition(uploadedBook.getCondition())
                .addBookImage(BookUtil.convertBitmapToByteArray(bookImage))
                .addConditionDescription(additionalDetailsAppCompatEditText.getText().toString())
                .addUploadTimestamp(previewTimestampTextView.getText().toString())
                .build();

        if (updateRequired) {


            int rowsAffected = getContentResolver().update(UploadedBookProvider.CONTENT_URI, UploadedBookTable.getContentValues(uploadedBookData), UploadedBookTable.KEY_ID + " = " + bookId, null);

            FLog.d(this, String.valueOf(rowsAffected));

        } else {


            Uri result = getContentResolver().insert(UploadedBookProvider.CONTENT_URI, UploadedBookTable.getContentValues(uploadedBookData));

            FLog.d(this, uploadedBookData.toString());
            FLog.d(this, String.valueOf(result));
        }


        UploadedBooksFirebaseHelper uploadedBooksFirebaseHelper = new UploadedBooksFirebaseHelper();
        uploadedBooksFirebaseHelper.uploadBook(uploadedBookData);



        setResult(Constants.CLOSE_ACTIVITY);
        finish();
    }


    public void setTitle(String title, String subTitle) {
        if (subTitle == null
                || subTitle.length() == 0) {
            previewTitleTextView.setText(title);
        } else {
            previewTitleTextView.setText(String.format("%s:%s", title, subTitle));
        }
    }


    public void setAuthors(ArrayList<String> authors) {
        if (authors == null
                || authors.size() == 0) {
            previewAuthorTextView.setText("");
        } else {
            previewAuthorTextView.setText(getString(R.string.by) + TextUtils.join(",", authors));
        }
    }


    public void setDescription(String description) {
        if (description == null
                || description.length() == 0) {
            previewDescriptionTextView.setText(R.string.no_description_found);
        } else {
            previewDescriptionTextView.setText(description);
        }
    }

    public void setPublishingDetails(String publisher, String publishingDate) {
        if (publisher == null
                || publisher.length() == 0) {
            previewPublishingDetailsTextView.setText("");
        } else {
            if (publishingDate == null
                    || publishingDate.length() == 0) {
                previewPublishingDetailsTextView.setText(publisher);
            } else {
                previewPublishingDetailsTextView.setText(String.format("%s - %s", publisher, publishingDate));
            }
        }
    }


    private void setConditionalDetails(BookCondition condition) {
        if (condition == null) {
            return;
        }
        switch (condition) {
            case POOR:
                conditionalNameTextView.setText(R.string.poor);
                conditionalDetailTextView.setText(R.string.poor_description);
                break;
            case LOOSE_BINDING:
                conditionalNameTextView.setText(R.string.losse_binding);
                conditionalDetailTextView.setText(R.string.loose_binding_description);
                break;
            case BINDING_COPY:
                conditionalNameTextView.setText(R.string.binding_copy);
                conditionalDetailTextView.setText(R.string.binding_copy_description);
                break;
            case FAIR:
                conditionalNameTextView.setText(R.string.fair);
                conditionalDetailTextView.setText(R.string.fair_description);
                break;
            case GOOD:
                conditionalNameTextView.setText(R.string.good);
                conditionalDetailTextView.setText(R.string.good_description);
                break;
            case FINE:
                conditionalNameTextView.setText(R.string.fine);
                conditionalDetailTextView.setText(R.string.fine_description);
                break;
            case NEW:
                conditionalNameTextView.setText(R.string.new_string);
                conditionalDetailTextView.setText(R.string.new_description);
                break;
        }
    }

    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
