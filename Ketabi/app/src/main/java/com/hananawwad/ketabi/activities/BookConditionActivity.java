package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.models.BookCondition;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.BundleUtil;
import com.hananawwad.ketabi.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * @author hananawwad
 */
public class BookConditionActivity extends AppCompatActivity {


    @Bind(R.id.seek_bar_book_condition)
    SeekBar seekBar;


    @Bind(R.id.condition_detail_text_view)
    TextView conditionalDetailTextView;


    @Bind(R.id.text_view_book_condition)
    TextView conditionalNameTextView;

    UploadedBook uploadedBook;

    Bitmap bookImage;

    boolean updateRequired;

    int bookId;

    @Bind(R.id.image_view_book_condition)
    ImageView bookConditionImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_condition);

        ButterKnife.bind(this);


        uploadedBook = BundleUtil.getUploadedBookFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPLOADED_BOOK_KEY, null);
        bookImage = BookUtil.convertByteArrayToBitmap(uploadedBook.getBookImage());
        updateRequired = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPDATE_REQUIRED, false);
        bookId = BundleUtil.getIntFromBundle(savedInstanceState, getIntent().getExtras(), Constants.BOOK_ID, -1);


        if(uploadedBook.getCondition() != null){
            BookCondition bookCondition = uploadedBook.getCondition();
            seekBar.setProgress(getProgressFromBookCondition(bookCondition));
        }

        setConditionalDetails(seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                setConditionalDetails(progressValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(Constants.UPLOADED_BOOK_KEY, uploadedBook);
        savedInstanceState.putBoolean(Constants.UPDATE_REQUIRED, updateRequired);
        savedInstanceState.putInt(Constants.BOOK_ID, bookId);
    }

    public void tickClicked(View v){
        Intent intent = new Intent(this, ConfirmUploadActivity.class);
        UploadedBook uploadedBookData = new UploadedBook.UploadedBookBuilder()
                .addSearchedBook(uploadedBook.getSearchedBook())
                .addCondition(getBookCondition(seekBar.getProgress()))
                .addBookImage(BookUtil.convertBitmapToByteArray(bookImage))
                .build();
        intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBookData);
        intent.putExtra(Constants.UPDATE_REQUIRED, updateRequired);
        intent.putExtra(Constants.BOOK_ID, bookId);
        startActivityForResult(intent, 7);
    }


    private BookCondition getBookCondition(int progress){
        switch (progress){
            case 0 :
                return BookCondition.POOR;
            case 1 :
                return BookCondition.LOOSE_BINDING;
            case 2 :
                return BookCondition.BINDING_COPY;
            case 3 :
                return BookCondition.FAIR;
            case 4 :
                return BookCondition.GOOD;
            case 5 :
                return BookCondition.FINE;
            case 6 :
                return BookCondition.NEW;
        }
        return null;
    }

    private int getProgressFromBookCondition(BookCondition bookCondition){
        switch (bookCondition){
            case POOR:
                return 0;
            case LOOSE_BINDING:
                return 1;
            case BINDING_COPY:
                return 2;
            case FAIR:
                return 3;
            case GOOD:
                return 4;
            case FINE:
                return 5;
            case NEW :
                return 6;
        }
        return -1;
    }


    private void setConditionalDetails(int progress){
        BookCondition condition = getBookCondition(progress);
        if(condition == null){
            return;
        }
        switch (condition){
            case  POOR :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.poor_book_condition));
                conditionalNameTextView.setText(R.string.poor);
                conditionalDetailTextView.setText(R.string.poor_description);
                break;
            case LOOSE_BINDING :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.loose_binding));
                conditionalNameTextView.setText(R.string.losse_binding);
                conditionalDetailTextView.setText(R.string.loose_binding_description);
                break;
            case BINDING_COPY :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.binding_copy));
                conditionalNameTextView.setText(R.string.binding_copy);
                conditionalDetailTextView.setText(R.string.binding_copy_description);
                break;
            case FAIR:
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.fair_book_condition));
                conditionalNameTextView.setText(R.string.fair);
                conditionalDetailTextView.setText(R.string.fair_description);
                break;
            case GOOD:
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.good_book_condition));
                conditionalNameTextView.setText(R.string.good);
                conditionalDetailTextView.setText(R.string.good_description);
                break;
            case FINE :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.fine_book_condition));
                conditionalNameTextView.setText(R.string.fine);
                conditionalDetailTextView.setText(R.string.fine_description);
                break;
            case NEW :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.new_book_condition));
                conditionalNameTextView.setText(R.string.new_string);
                conditionalDetailTextView.setText(R.string.new_description);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if(requestCode == 7 && resultCode == Constants.CLOSE_ACTIVITY){
            setResult(Constants.CLOSE_ACTIVITY);
            finish();
        }
    }

    public void backClicked(View v){
        super.onBackPressed();
    }
}
