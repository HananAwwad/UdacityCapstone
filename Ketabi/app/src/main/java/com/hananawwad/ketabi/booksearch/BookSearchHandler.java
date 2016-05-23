package com.hananawwad.ketabi.booksearch;

import com.hananawwad.ketabi.models.BookSearchFailure;
import com.hananawwad.ketabi.models.SearchedBook;

import java.util.ArrayList;

import lombok.Setter;

/**
 * @author hananawwad
 */
public class BookSearchHandler {

    private BookSearchRequestHandler bookSearchRequestHandler;
    private BookSearchResponseHandler bookSearchResponseHandler;
    private BookSearchRequestListener bookSearchRequestListener;
    private BookSearchResponseListener bookSearchResponseListener;
    private String query;
    private boolean searchHasCompleted;

    @Setter
    private BookSearchEvents bookSearchEvents;

    /** Initialize objects in constructor **/
    public BookSearchHandler(){
        searchHasCompleted = false;
        init();
    }

    private void init(){
        bookSearchRequestHandler = new BookSearchRequestHandler();
        bookSearchRequestListener = new BookSearchRequestListener();
        bookSearchRequestHandler.setBookSearchRequestEvents(bookSearchRequestListener);
        bookSearchResponseHandler = new BookSearchResponseHandler();
        bookSearchResponseListener = new BookSearchResponseListener();
        bookSearchResponseHandler.setBookSearchResponseEvents(bookSearchResponseListener);
    }

    public void initiateQuery(String query){

        clean();
        init();

        this.query = query;
        searchHasCompleted = false;

        bookSearchRequestHandler.fetchBookData(query);
    }

    public void fetchNextBatch(){

        if(!searchHasCompleted) {
            bookSearchRequestHandler.setStartIndex(bookSearchRequestHandler.getStartIndex() + bookSearchRequestHandler.getMaxResults());

            bookSearchRequestHandler.fetchBookData(query);
        }
    }

    public void retryFetchBatch(){

        bookSearchRequestHandler.fetchBookData(query);
    }


    private class BookSearchResponseListener implements BookSearchResponseHandler.BookSearchResponseEvents {

        @Override
        public void onBookSearchResponseSuccessfullyParsed(ArrayList<SearchedBook> searchedBookArrayList) {

            if(searchedBookArrayList == null || searchedBookArrayList.size() == 0){
                searchHasCompleted = true;

                if(bookSearchEvents != null){
                    bookSearchEvents.onBookSearchCompleted(true, new ArrayList<SearchedBook>());
                }
            }
            else {
                if(bookSearchEvents != null){
                    bookSearchEvents.onBookSearchCompleted(false, searchedBookArrayList);
                }
            }
        }

        @Override
        public void onBookSearchResponseParsedFailed() {

            if(bookSearchEvents != null){
                bookSearchEvents.onBookSearchFailed(BookSearchFailure.RESPONSE_FAILURE);
            }
        }

        @Override
        public void onBookSearchResponseItemsBodyEmpty() {
            searchHasCompleted = true;

            if(bookSearchEvents != null){
                bookSearchEvents.onBookSearchCompleted(true, new ArrayList<SearchedBook>());
            }
        }
    }

    /** Listener for book search request events **/
    private class BookSearchRequestListener implements BookSearchRequestHandler.BookSearchRequestEvents {

        @Override
        public void onBookSearchRequestCompleted(String data) {
            bookSearchResponseHandler.handleBookSearchResponse(data);
        }

        @Override
        public void onBookSearchRequestFailed() {
            if(bookSearchEvents != null){
                bookSearchEvents.onBookSearchFailed(BookSearchFailure.REQUEST_FAILURE);
            }
        }
    }

    public interface BookSearchEvents{
        void onBookSearchFailed(BookSearchFailure bookSearchFailure);
        void onBookSearchCompleted(boolean searchHasCompleted, ArrayList<SearchedBook> searchedBookArrayList);
    }

    private void clean(){
        bookSearchRequestHandler = null;
        bookSearchRequestListener = null;
        bookSearchResponseHandler = null;
        bookSearchResponseListener = null;
    }
}
