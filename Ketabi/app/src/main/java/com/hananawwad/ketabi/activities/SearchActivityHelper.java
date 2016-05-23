package com.hananawwad.ketabi.activities;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.hananawwad.ketabi.R;
import com.hananawwad.ketabi.adapter.BaseAdapter;
import com.hananawwad.ketabi.adapter.SearchedBookAdapter;
import com.hananawwad.ketabi.booksearch.BookSearchHandler;
import com.hananawwad.ketabi.models.BookSearchFailure;
import com.hananawwad.ketabi.models.DotsState;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.NetworkUtil;

import java.util.ArrayList;

import lombok.Setter;

/**
 * @author hananawwad
 */
public class SearchActivityHelper {

    @Setter
    private ItemClickEvent itemClickEvent;
    private Context context;
    private SearchedBookAdapter searchedBookAdapter;
    private BookSearchHandler bookSearchHandler;

    public SearchActivityHelper(Context context){
        this.context = context;
        bookSearchHandler = new BookSearchHandler();
        bookSearchHandler.setBookSearchEvents(new BookSearchListener());
    }

    public void linkRecyclerViewAndAdapter(RecyclerView recyclerView) {
        searchedBookAdapter = new SearchedBookAdapter(R.layout.search_recycler_view_item, new ArrayList<SearchedBook>(),context, true, R.layout.search_recycler_view_footer_item);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(searchedBookAdapter);

        searchedBookAdapter.setMoreDataRequired(new SearchedBookAdapter.MoreDataRequired() {
            @Override
            public void onDataDemanded() {
                if(bookSearchHandler != null){
                    bookSearchHandler.fetchNextBatch();
                }
            }
        });

        searchedBookAdapter.setRecyclerViewAdapterEvents(new BaseAdapter.RecyclerViewAdapterEvents() {
            @Override
            public void onItemClick(int position, View v) {
                if (itemClickEvent != null) {
                    ImageView transitionImageView = (ImageView)v.findViewById(R.id.search_result_book_image_view);
                    transitionImageView.setTransitionName(context.getString(R.string.common_image_transition_view));
                    itemClickEvent.onItemClicked(transitionImageView, searchedBookAdapter.getItemAtPosition(position));
                }
            }
        });

        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "");
    }

    public void handleEmptyQueryEvent(){

        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "");

        if(searchedBookAdapter != null) {
            searchedBookAdapter.deleteAllItems();
        }
    }

    public void handleNewQueryEvent(String query){

        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.START, "");

        if(searchedBookAdapter != null) {
            searchedBookAdapter.deleteAllItems();
        }

        if(bookSearchHandler != null) {
            bookSearchHandler.initiateQuery(query);
        }
    }

    public void handleSearchQueryFailedEvent(String failureMessage){
        searchedBookAdapter.updateFooter(View.VISIBLE, View.VISIBLE, View.GONE, DotsState.STOP, failureMessage);

    }

    public void retrySearching(){
        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.START, "");
        if(bookSearchHandler != null) {
            bookSearchHandler.retryFetchBatch();
        }
    }

    class BookSearchListener implements BookSearchHandler.BookSearchEvents {

        @Override
        public void onBookSearchFailed(BookSearchFailure bookSearchFailure) {

            if(context != null) {
                String message = "";
                /** Check for internet connection **/
                if(!NetworkUtil.isNetworkConnected(context)) {
                    message = context.getString(R.string.seems_like_no_internet_connection);
                }
                handleSearchQueryFailedEvent(message);
            }
        }

        @Override
        public void onBookSearchCompleted(boolean searchHasCompleted, ArrayList<SearchedBook> searchedBookArrayList) {

            if(searchedBookArrayList != null && searchedBookArrayList.size() > 0){
                searchedBookAdapter.addMultipleItems(BookUtil.sterilizeSearchedBooks(searchedBookArrayList));
            }

            if(searchHasCompleted && context != null){

                if((searchedBookArrayList == null || searchedBookArrayList.size() == 0) && searchedBookAdapter.getItemCount() == 1){
                    searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, context.getResources().getString(R.string.nothing_found_for_the_given_keyword));
                } else {
                    searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "");
                }
            }
        }
    }

    public interface ItemClickEvent{
        void onItemClicked(ImageView transitionImageView, SearchedBook searchedBook);
    }


    public void setItemClickEvent(ItemClickEvent itemClickEvent) {
        this.itemClickEvent = itemClickEvent;
    }
}
