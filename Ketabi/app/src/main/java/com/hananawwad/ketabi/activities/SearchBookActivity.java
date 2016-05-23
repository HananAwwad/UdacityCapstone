package com.hananawwad.ketabi.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.BundleUtil;
import com.hananawwad.ketabi.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;

/**
 *
 * @author hananawwad
 */
public class SearchBookActivity extends AppCompatActivity {

    @Bind(R.id.search_result_recycler_view_search_book)
    RecyclerView searchBookRecyclerView;

    @Bind(R.id.search_edit_text_search_book)
    EditText searchBookEditText;


    SearchActivityHelper searchActivityHelper;

    @Getter
    String searchedQuery;


    private Handler handler = new Handler();


    String isbn;

    boolean isFromUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ButterKnife.bind(this);

        if(searchActivityHelper == null) {
            searchActivityHelper = new SearchActivityHelper(this);
            searchActivityHelper.linkRecyclerViewAndAdapter(searchBookRecyclerView);

        }

        searchActivityHelper.setItemClickEvent(new RecyclerViewItemClick());

        searchBookEditText.addTextChangedListener(new SearchTextWatcher());

        isFromUpload = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.IS_UPLOAD, false);
        isbn = BundleUtil.getStringFromBundle(savedInstanceState, getIntent().getExtras(), Constants.ISBN_KEY, "");
        if(isFromUpload){
            searchBookEditText.setText(isbn);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(searchBookRecyclerView != null && searchBookRecyclerView.getLayoutManager() != null) {
            outState.putParcelable(Constants.SAVED_LAYOUT_MANAGER_KEY,
                    searchBookRecyclerView.getLayoutManager().onSaveInstanceState());
        }
        outState.putBoolean(Constants.IS_UPLOAD, isFromUpload);
        outState.putString(Constants.ISBN_KEY, isbn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null && searchBookRecyclerView != null && searchBookRecyclerView.getLayoutManager() != null){
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(Constants.SAVED_LAYOUT_MANAGER_KEY);
            searchBookRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class RecyclerViewItemClick implements SearchActivityHelper.ItemClickEvent{

        @Override
        public void onItemClicked(ImageView transitionImageView, SearchedBook searchedBook) {

            Intent intent = new Intent(SearchBookActivity.this, BookDetailActivity.class);
            intent.putExtra(Constants.SEARCHED_BOOK_KEY, searchedBook);
            intent.putExtra(Constants.IS_UPLOAD, isFromUpload);
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) transitionImageView.getDrawable());
            if(bitmapDrawable == null){
            } else {
                intent.putExtra(Constants.BOOK_IMAGE_KEY, BookUtil.convertBitmapToByteArray(bitmapDrawable.getBitmap()));
            }
            Pair<View, String> pair1 = Pair.create((View)transitionImageView, getString(R.string.common_image_transition_view));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchBookActivity.this, pair1);
            startActivityForResult(intent, 5, options.toBundle());
        }
    }

    class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {

            handler.removeCallbacks(mFilterTask);

            if(s != null && s.length() == 0){
                if(searchActivityHelper != null){
                    searchActivityHelper.handleEmptyQueryEvent();
                }
                searchedQuery = "";
                return;
            }

            handler.postDelayed(mFilterTask, 1000);
        }
    }


    public void retryClicked(View v){
        if(searchActivityHelper != null){
            searchActivityHelper.retrySearching();
        }
    }

    Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            if(searchBookEditText != null){
                String text = searchBookEditText.getText().toString();
                searchActivityHelper.handleNewQueryEvent(text);
                searchedQuery = text;
            }
        }
    };

    @Override
    protected void onDestroy(){
        handler.removeCallbacks(mFilterTask);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if(requestCode == 5 && resultCode == Constants.CLOSE_ACTIVITY){
            finish();
        }
    }


}
