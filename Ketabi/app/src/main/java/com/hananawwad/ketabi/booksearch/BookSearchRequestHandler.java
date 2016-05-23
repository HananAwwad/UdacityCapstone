package com.hananawwad.ketabi.booksearch;

import android.support.annotation.NonNull;

import com.hananawwad.ketabi.util.FLog;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hananawwad
 */
public class BookSearchRequestHandler {

    @Setter @Getter
    private int startIndex;

    @Getter
    private int maxResults;

    @Setter
    private BookSearchRequestEvents bookSearchRequestEvents;

    public BookSearchRequestHandler(){
        this.startIndex = 0;
        this.maxResults = 10;
    }

    public void fetchBookData(@NonNull String query){

        String url = getRequestUrl(query);

        if (url.equals("")) {

            if(bookSearchRequestEvents != null){
                bookSearchRequestEvents.onBookSearchRequestFailed();
            }
            return;
        }

        FLog.i(this, url);

        AsyncBookDataFetch asyncBookDataFetch = new AsyncBookDataFetch();
        asyncBookDataFetch.setTaskCompleteEvent(new AsyncBookDataFetch.TaskCompleteEvent() {

            @Override
            public void onTaskComplete(String result) {

                if(bookSearchRequestEvents != null){
                    bookSearchRequestEvents.onBookSearchRequestCompleted(result);
                }
            }

            @Override
            public void onTaskFailed(){
                if(bookSearchRequestEvents != null){
                    bookSearchRequestEvents.onBookSearchRequestFailed();
                }
            }

        });
        asyncBookDataFetch.execute(url);
    }

    private String getRequestUrl(@NonNull String query){

        BookSearchUrl bookSearchUrl;
        try {
            bookSearchUrl = new BookSearchUrl.BookSearchUrlBuilder()
                    .addSearchQuery(query)
                    .addStartIndex(startIndex)
                    .addMaxResults(maxResults)
                    .build();
        } catch (Exception e){
            return "";
        }
        return bookSearchUrl.getGoogleBookSearchApiUrl();
    }

    public interface BookSearchRequestEvents{
        void onBookSearchRequestCompleted(String data);
        void onBookSearchRequestFailed();
    }
}
