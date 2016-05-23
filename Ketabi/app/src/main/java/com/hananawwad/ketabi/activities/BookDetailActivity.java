package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.BundleUtil;
import com.hananawwad.ketabi.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  * @author hananawwad
 */
public class BookDetailActivity extends AppCompatActivity {

    @Bind(R.id.title_text_view)
    TextView titleTextView;

    @Bind(R.id.title_text_view_1)
    TextView fullTitleTextView;

    @Bind(R.id.authors_text_view)
    TextView authorsTextView;

    @Bind(R.id.authors_card_view)
    CardView authorsCardView;

    @Bind(R.id.description_text_view)
    TextView descriptionTextView;

    @Bind(R.id.description_card_view)
    CardView descriptionCardView;

    @Bind(R.id.publishing_details_text_view)
    TextView publishingDetailsTextView;

    @Bind(R.id.publisher_card_view)
    CardView publishingCardView;

    @Bind(R.id.yes_text_view)
    TextView yesTextView;

    @Bind(R.id.no_text_view)
    TextView noTextView;

    @Bind(R.id.sharing_card_view)
    CardView sharingCardView;

    @Bind(R.id.toolbar_title_text_view)
    TextView toolbarTitle;

    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;

    @Bind(R.id.book_image_view)
    ImageView bookImageView;
    SearchedBook searchedBook;
    Boolean isFromUpload;
    Bitmap bookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        ButterKnife.bind(this);

        searchedBook = BundleUtil.getSearchedBookFromBundle(savedInstanceState, getIntent().getExtras(), Constants.SEARCHED_BOOK_KEY, null);
        isFromUpload = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.IS_UPLOAD, false);
        bookImage = BookUtil.convertByteArrayToBitmap(BundleUtil.getByteArrayFromBundle(savedInstanceState, getIntent().getExtras(), Constants.BOOK_IMAGE_KEY, null));

        if(bookImage == null && searchedBook.getThumbnailLink().length() != 0){
            Picasso.with(this).load(searchedBook.getThumbnailLink()).into(target);
        } else {
            bookImageView.setImageBitmap(bookImage);
        }


        setTitle(searchedBook.getTitle(), searchedBook.getSubTitle());
        setAuthors(searchedBook.getAuthors());
        setDescription(searchedBook.getDescription());
        setPublishingDetails(searchedBook.getPublisher(), searchedBook.getPublishedDate());

        yesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, BookConditionActivity.class);
                UploadedBook uploadedBook = new UploadedBook.UploadedBookBuilder()
                        .addSearchedBook(searchedBook)
                        .addBookImage(BookUtil.convertBitmapToByteArray(bookImage))
                        .build();
                intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBook);
                startActivityForResult(intent, 6);
            }
        });



        noTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation slideAnimation = AnimationUtils.loadAnimation(BookDetailActivity.this, R.anim.slide_right_to_left);
                slideAnimation.setFillAfter(true);
                sharingCardView.startAnimation(slideAnimation);
                slideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sharingCardView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                /**
                 * For a fixed scroll range, display toolbar text title, else hide toolbar text.
                 */
                if (scrollRange + verticalOffset >= -1*getResources().getDimension(R.dimen.density_56dp) && scrollRange + verticalOffset <= getResources().getDimension(R.dimen.density_56dp)) {

                    setToolbarTitle(searchedBook.getTitle(), searchedBook.getSubTitle());
                    isShow = true;
                } else if(isShow) {


                    setToolbarTitle("","");
                    isShow = false;
                }
            }
        });
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bookImage = bitmap;
            bookImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(Constants.SEARCHED_BOOK_KEY, searchedBook);
        savedInstanceState.putBoolean(Constants.IS_UPLOAD, isFromUpload);
        savedInstanceState.putByteArray(Constants.BOOK_IMAGE_KEY, BookUtil.convertBitmapToByteArray(bookImage));
    }


    public void setTitle(String title, String subTitle){
        if(subTitle == null
                || subTitle.length() == 0){
            titleTextView.setText(title);
            fullTitleTextView.setText(title);
        } else {
            titleTextView.setText(String.format("%s:%s", title, subTitle));
            fullTitleTextView.setText(String.format("%s:%s", title, subTitle));
        }
    }



    public void setToolbarTitle(String title, String subTitle){
        if(title.length() == 0
                && subTitle.length() == 0){
            toolbarTitle.setText("");
        }
        if(subTitle.length() == 0){
            toolbarTitle.setText(title);
        } else {
            toolbarTitle.setText(String.format("%s:%s", title, subTitle));
        }
    }


    /**
     * Set authors for the book
     *
     * @param authors of the book
     */
    public void setAuthors(ArrayList<String> authors) {
        if (authors == null
                || authors.size() == 0) {
            authorsCardView.setVisibility(View.GONE);
        } else {
            authorsTextView.setText(TextUtils.join(",", authors));
        }
    }


    public void setDescription(String description) {
        if (description == null
                || description.length() == 0) {
            descriptionCardView.setVisibility(View.GONE);
        } else {
            descriptionTextView.setText(description);
        }
    }

    void setPublishingDetails(String publisher, String publishingDate) {
        if (publisher == null
                || publisher.length() == 0) {
            publishingCardView.setVisibility(View.GONE);
        } else {
            if(publishingDate == null
                    || publishingDate.length() == 0){
                publishingDetailsTextView.setText(publisher);
            } else {
                publishingDetailsTextView.setText(String.format("%s - %s", publisher, publishingDate));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if(requestCode == 6 && resultCode == Constants.CLOSE_ACTIVITY){
            setResult(Constants.CLOSE_ACTIVITY);
            finish();
        }
    }


    public void backClicked(View v){
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
