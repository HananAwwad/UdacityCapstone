package com.hananawwad.ketabi.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.activities.BookConditionActivity;
import com.hananawwad.ketabi.db.UploadedBookProvider;
import com.hananawwad.ketabi.db.UploadedBookTable;
import com.hananawwad.ketabi.firebase.UploadedBooksFirebaseHelper;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author hananawwad
 */
public class UploadedBookAdapter extends android.widget.BaseAdapter {

    private Context context;
    private Cursor cursor;
    private int inflateLayoutId;
    private LayoutInflater layoutInflater;
    
    public UploadedBookAdapter(Context context, Cursor cursor, int inflateLayoutId){
        this.context = context;
        this.cursor = cursor;
        this.inflateLayoutId = inflateLayoutId;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /** Holder class for this adapter **/
    class UploadedBookAdapterHolder {
        ImageView bookImageView;
        TextView bookTitleView;
        TextView bookAuthorsView;
        TextView bookIsbnView;
        TextView bookConditionView;
        TextView bookUploadTimestamp;
        LinearLayout editDeleteLayout;
        RelativeLayout editRelativeLayout;
        RelativeLayout deleteRelativeLayout;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        UploadedBookAdapterHolder uploadedBookAdapterHolder;


        cursor.moveToPosition(position);

        if(view == null){
            view = layoutInflater.inflate(inflateLayoutId, parent, false);
            uploadedBookAdapterHolder = new UploadedBookAdapterHolder();
            uploadedBookAdapterHolder.bookImageView = (ImageView) view.findViewById(R.id.uploaded_book_book_image_view);
            uploadedBookAdapterHolder.bookTitleView = (TextView) view.findViewById(R.id.uploaded_book_book_title);
            uploadedBookAdapterHolder.bookAuthorsView = (TextView) view.findViewById(R.id.uploaded_book_book_authors);
            uploadedBookAdapterHolder.bookIsbnView = (TextView) view.findViewById(R.id.uploaded_book_book_isbn);
            uploadedBookAdapterHolder.bookConditionView = (TextView) view.findViewById(R.id.book_condition_);
            uploadedBookAdapterHolder.bookUploadTimestamp = (TextView) view.findViewById(R.id.upload_timestamp_);
            uploadedBookAdapterHolder.editDeleteLayout = (LinearLayout) view.findViewById(R.id.edit_linear_layout);
            uploadedBookAdapterHolder.editRelativeLayout = (RelativeLayout) view.findViewById(R.id.modify_relative_layout);
            uploadedBookAdapterHolder.deleteRelativeLayout = (RelativeLayout) view.findViewById(R.id.delete_relative_layout);
            view.setTag(uploadedBookAdapterHolder);
        } else {
            uploadedBookAdapterHolder = (UploadedBookAdapterHolder) view.getTag();
        }

        Bitmap image = BookUtil.convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_IMAGE)));
        if(image != null){
            uploadedBookAdapterHolder.bookImageView.setImageBitmap(image);
        }
        uploadedBookAdapterHolder.bookTitleView.setText(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_TITLE)));
        uploadedBookAdapterHolder.bookAuthorsView.setText(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_AUTHORS)));
        uploadedBookAdapterHolder.bookIsbnView.setText(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER)));
        uploadedBookAdapterHolder.bookConditionView.setText(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION)));
        uploadedBookAdapterHolder.bookUploadTimestamp.setText(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP)));

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
        uploadedBook.setId(cursor.getInt(cursor.getColumnIndex(UploadedBookTable.KEY_ID)));


        view.setOnClickListener(new OnClickListenerForUploadedItem(uploadedBook, uploadedBookAdapterHolder));

        return view;
    }

    class OnClickListenerForUploadedItem implements View.OnClickListener{

        final UploadedBook uploadedBook;
        final UploadedBookAdapterHolder uploadedBookAdapterHolder;

        public OnClickListenerForUploadedItem(final UploadedBook uploadedBook, final UploadedBookAdapterHolder uploadedBookAdapterHolder){
            this.uploadedBook = uploadedBook;
            this.uploadedBookAdapterHolder = uploadedBookAdapterHolder;
            uploadedBookAdapterHolder.editRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, BookConditionActivity.class);
                    intent.putExtra(Constants.UPDATE_REQUIRED, true);
                    intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBook);
                    intent.putExtra(Constants.BOOK_ID, uploadedBook.getId());
                    context.startActivity(intent);

                    uploadedBookAdapterHolder.editDeleteLayout.setVisibility(View.GONE);
                }
            });
            uploadedBookAdapterHolder.deleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int rowsDeleted = context.getContentResolver().delete(UploadedBookProvider.CONTENT_URI, UploadedBookTable.KEY_ID + " = " + uploadedBook.getId(), null);

                    FLog.d(this, String.valueOf(rowsDeleted));

                    UploadedBooksFirebaseHelper uploadedBooksFirebaseHelper = new UploadedBooksFirebaseHelper();
                    uploadedBooksFirebaseHelper.deleteBook(uploadedBook);


                    uploadedBookAdapterHolder.editDeleteLayout.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if(uploadedBookAdapterHolder.editDeleteLayout.getVisibility() == View.VISIBLE){
                uploadedBookAdapterHolder.editDeleteLayout.setVisibility(View.GONE);
            } else {
                uploadedBookAdapterHolder.editDeleteLayout.setVisibility(View.VISIBLE);
            }

        }
    }

}
