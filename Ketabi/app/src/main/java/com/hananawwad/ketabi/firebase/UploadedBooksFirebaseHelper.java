package com.hananawwad.ketabi.firebase;

import android.text.TextUtils;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hananawwad.ketabi.BuildConfig;
import com.hananawwad.ketabi.application.KetabiApplication;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.models.UploadedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

/**
 * @author hananawwad
 */
public class UploadedBooksFirebaseHelper {

    @Setter
    UploadedBookFirebaseEvents uploadedBookFirebaseEvents;


    public void uploadBook(final UploadedBook uploadedBook){

        final Firebase firebase  = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String title = uploadedBook.getSearchedBook().getTitle();
                final String subTitle = uploadedBook.getSearchedBook().getSubTitle();
                final String authors = TextUtils.join(",", uploadedBook.getSearchedBook().getAuthors());
                final String publishers = uploadedBook.getSearchedBook().getPublisher();
                final String publishingDate = uploadedBook.getSearchedBook().getPublishedDate();
                final String description = uploadedBook.getSearchedBook().getDescription();
                final String industryIdentifier = uploadedBook.getSearchedBook().getIndustryIdentifier();
                final String thumbnailLink = uploadedBook.getSearchedBook().getThumbnailLink();
                final String condition  = BookUtil.getStringForBookConditionEnum(uploadedBook.getCondition());
                final String conditionDescription = uploadedBook.getConditionDescription();
                final String uploadedTimeStamp = uploadedBook.getUploadTimestamp();

                HashMap<String, String> map = new HashMap<String, String>() {{
                    put("subtitle",subTitle);
                    put("authors", authors);
                    put("publisher", publishers);
                    put("publishingDate", publishingDate);
                    put("description", description);
                    put("industryIdentifier", industryIdentifier);
                    put("thumbnailLink", thumbnailLink);
                    put("condition", condition);
                    put("conditionDescription", conditionDescription);
                    put("uploadedTimestamp", uploadedTimeStamp);
                }};


                firebase.child(KetabiApplication.getInstance().getUid()).child(title).setValue(map);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                FLog.d(this, firebaseError.toString());
            }
        });
    }


    public void deleteBook(UploadedBook uploadedBook){
        final Firebase firebase  = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs);
        firebase.child(KetabiApplication.getInstance().getUid()).child(uploadedBook.getSearchedBook().getTitle()).setValue(null);
    }

    public void fetchAllUploadedBooks(){
        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs + "/" + KetabiApplication.getInstance().getUid());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<UploadedBook> uploadedBooklist = new ArrayList<UploadedBook>();
                HashMap<String, ?> map = (HashMap < String,?>)dataSnapshot.getValue();
                for (Map.Entry<String, ?> bookDataEntry : map.entrySet()) {
                    String bookTitle = bookDataEntry.getKey();
                    HashMap<String, String> bookDataMap = (HashMap<String,String>)bookDataEntry.getValue();
                    UploadedBook uploadedBook = new UploadedBook.UploadedBookBuilder()
                            .addCondition(BookUtil.getBookConditionEnumFromString(bookDataMap.get("condition")))
                            .addBookImage(null)
                            .addConditionDescription(bookDataMap.get("conditionDescription"))
                            .addUploadTimestamp("uploadedTimestamp")
                            .addSearchedBook(new SearchedBook.SearchedBookBuilder()
                                            .addTitle(bookTitle)
                                            .addAuthors(new ArrayList<String>(Arrays.asList(bookDataMap.get("authors").split(" , "))))
                                            .addSubtitle(bookDataMap.get("subtitle"))
                                            .addPublisher(bookDataMap.get("publisher"))
                                            .addPublishedDate(bookDataMap.get("publishingDate"))
                                            .addDescription(bookDataMap.get("description"))
                                            .addIndustryIdentifier(bookDataMap.get("industryIdentifier"))
                                            .addThumbnailLink(bookDataMap.get("thumbnailLink"))
                                            .build()
                            ).build();
                    uploadedBooklist.add(uploadedBook);
                }
                if(uploadedBookFirebaseEvents != null){
                    uploadedBookFirebaseEvents.onFetchAllUploadedBooks(uploadedBooklist);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public interface UploadedBookFirebaseEvents{
        void onFetchAllUploadedBooks(ArrayList<UploadedBook> uploadedBookArrayList);
    }
}
