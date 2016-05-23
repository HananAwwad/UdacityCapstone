package com.hananawwad.ketabi.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.db.UploadedBookProvider;
import com.hananawwad.ketabi.db.UploadedBookTable;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.util.BookUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private int mCount;
    private List<UploadedBook> mWidgetItems = new ArrayList<UploadedBook>();
    private Context mContext;
    private int mAppWidgetId;
    private Cursor cursor;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        cursor = mContext.getContentResolver().query(UploadedBookProvider.CONTENT_URI, null, null, null, UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + " DESC");
        updateCount();
    }

    public void onCreate() {

        fetchDataAndUpdateList();
    }

    public void onDestroy() {

        mWidgetItems.clear();
    }

    public int getCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        if(position < mWidgetItems.size() && mWidgetItems.size() > 0) {

            Bitmap image = BookUtil.convertByteArrayToBitmap(mWidgetItems.get(position).getBookImage());
            if (image != null) {
                rv.setImageViewBitmap(R.id.uploaded_book_book_image_view, image);
            }
            rv.setTextViewText(R.id.uploaded_book_book_title, mWidgetItems.get(position).getSearchedBook().getTitle());
            rv.setTextViewText(R.id.uploaded_book_book_authors, TextUtils.join(",", mWidgetItems.get(position).getSearchedBook().getAuthors()));
            rv.setTextViewText(R.id.uploaded_book_book_isbn, mWidgetItems.get(position).getSearchedBook().getIndustryIdentifier());
            rv.setTextViewText(R.id.book_condition_, BookUtil.getStringForBookConditionEnum(mWidgetItems.get(position).getCondition()));
            rv.setTextViewText(R.id.upload_timestamp_, mWidgetItems.get(position).getUploadTimestamp());


            Bundle extras = new Bundle();
            extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(R.id.main_linear_layout, fillInIntent);

        }
        return rv;
    }

    public RemoteViews getLoadingView() {

        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {

        if(cursor != null){
            cursor.close();
        }
        cursor = mContext.getContentResolver().query(UploadedBookProvider.CONTENT_URI, null, null, null, UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + " DESC");

        updateCount();
        fetchDataAndUpdateList();

    }

    private void updateCount(){
        if(cursor == null || cursor.getCount() <1){
            mCount = 0;
        } else {
            mCount = cursor.getCount();
        }
    }

    private void fetchDataAndUpdateList(){

        if(mCount == 0){
            return;
        }

        mWidgetItems.clear();

        while(cursor.moveToNext()){


            UploadedBook uploadedBook = new UploadedBook.UploadedBookBuilder()
                    .addCondition(BookUtil.getBookConditionEnumFromString(
                                    cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION)))
                    )
                    .addBookImage(cursor.getBlob(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_IMAGE)))
                    .addConditionDescription(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION_DETAIL)))
                    .addUploadTimestamp(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP)))
                    .addSearchedBook(new SearchedBook.SearchedBookBuilder()
                                    .addTitle(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_TITLE)))
                                    .addAuthors(new ArrayList<String>(Arrays.asList((
                                                    cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_AUTHORS))).split(" , ")))
                                    )
                                    .addSubtitle(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_SUBTITLE)))
                                    .addPublisher(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHERS)))
                                    .addPublishedDate(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHED_DATE)))
                                    .addDescription(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_DESCRIPTION)))
                                    .addIndustryIdentifier(cursor.getString(
                                                    cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER))
                                    )
                                    .addThumbnailLink(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_THUMBNAIL)))
                                    .build()
                    ).build();

            mWidgetItems.add(uploadedBook);

        }
        cursor.close();
    }
}