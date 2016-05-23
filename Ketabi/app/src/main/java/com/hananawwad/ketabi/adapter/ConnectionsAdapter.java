package com.hananawwad.ketabi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.hananawwad.ketabi.R;

import java.util.List;

import lombok.Setter;

/**
 * @author hananawwad
 */
public class ConnectionsAdapter extends android.widget.BaseAdapter {

    private Context context;
    private Cursor cursor;
    private int inflateLayoutId;
    private LayoutInflater layoutInflater;
    List<String> rpnData;
    @Setter
    ConnectionItemClickListener connectionItemClickListener;

    public ConnectionsAdapter(Context context, Cursor cursor, int inflateLayoutId, List<String> rpnData){
        this.context = context;
        this.cursor = cursor;
        this.inflateLayoutId = inflateLayoutId;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.rpnData = rpnData;
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

    class ConnectionAdapterHolder {


        CircularImageView circularImageView;


        TextView nameTextView;

        TextView phoneNumberTextView;

        TextView usingAppTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        ConnectionAdapterHolder connectionAdapterHolder;

        cursor.moveToPosition(position);

        if(view == null){
            view = layoutInflater.inflate(inflateLayoutId, parent, false);
            connectionAdapterHolder = new ConnectionAdapterHolder();
            connectionAdapterHolder.circularImageView = (CircularImageView) view.findViewById(R.id.contact_photo);
            connectionAdapterHolder.nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            connectionAdapterHolder.phoneNumberTextView = (TextView) view.findViewById(R.id.phone_number);
            connectionAdapterHolder.usingAppTextView = (TextView) view.findViewById(R.id.using_app_text_view);
            view.setTag(connectionAdapterHolder);
        } else {
            connectionAdapterHolder = (ConnectionAdapterHolder) view.getTag();
        }

        try {
            connectionAdapterHolder.circularImageView.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                            context.getContentResolver(),
                            Uri.parse(cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI)))
                    )
            );
        } catch (Exception e){

            connectionAdapterHolder.circularImageView.setImageResource(R.drawable.contact);
        }

        connectionAdapterHolder.phoneNumberTextView.setText(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));

        connectionAdapterHolder.nameTextView.setText(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));

        String phoneNumber = connectionAdapterHolder.phoneNumberTextView.getText().toString();
        if(rpnData.contains(phoneNumber)
                || (phoneNumber.length() >0 && rpnData.contains(phoneNumber.substring(3)))){
            connectionAdapterHolder.usingAppTextView.setVisibility(View.VISIBLE);
        } else {
            connectionAdapterHolder.usingAppTextView.setVisibility(View.GONE);
        }

        if(connectionItemClickListener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectionItemClickListener.onItemClicked(v);
                }
            });
        }

        return view;
    }


    public interface ConnectionItemClickListener{
        void onItemClicked(View v);
    }

    public void setConnectionItemClickListener(ConnectionItemClickListener connectionItemClickListener) {
        this.connectionItemClickListener = connectionItemClickListener;
    }
}
