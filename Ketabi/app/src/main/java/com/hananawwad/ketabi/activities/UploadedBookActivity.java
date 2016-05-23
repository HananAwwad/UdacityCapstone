package com.hananawwad.ketabi.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.adapter.UploadedBookAdapter;
import com.hananawwad.ketabi.db.UploadedBookProvider;
import com.hananawwad.ketabi.db.UploadedBookTable;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.NetworkUtil;

import butterknife.ButterKnife;

/**
 * @author hananawwad
 */
public class UploadedBookActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    ListView uploadedBookListView;

    UploadedBookAdapter uploadedBookAdapter;


    TextView loadingTextView;
    TextView nothingFoundTextView;
    RelativeLayout mainRelativeLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewAndId(R.layout.activity_uploaded_book, Constants.uploadedBooksActivity);
        ButterKnife.bind(this);

        uploadedBookListView = (ListView)findViewById(R.id.uploaded_book_list_view);
        loadingTextView = (TextView) findViewById(R.id.loading_text_view);
        nothingFoundTextView = (TextView)findViewById(R.id.nothing_found_text_view);
         mainRelativeLayout= (RelativeLayout)findViewById(R.id.root_container_relative_layout_uploaded_book);

        if (!NetworkUtil.isNetworkConnected(this)) {
            Snackbar.make(mainRelativeLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();
        } else {

            loadingTextView.setVisibility(View.VISIBLE);
            nothingFoundTextView.setVisibility(View.GONE);
            getLoaderManager().initLoader(1, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                UploadedBookProvider.CONTENT_URI,
                null,
                null,
                null,
                UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + getString(R.string.desc)
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            nothingFoundTextView.setVisibility(View.VISIBLE);
            loadingTextView.setVisibility(View.GONE);
        } else {
            nothingFoundTextView.setVisibility(View.GONE);
            loadingTextView.setVisibility(View.GONE);
        }
        data.moveToFirst();
        uploadedBookAdapter = new UploadedBookAdapter(this, data, R.layout.uploaded_book_list_view_item);
        uploadedBookListView.setAdapter(uploadedBookAdapter);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
