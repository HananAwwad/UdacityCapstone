package com.hananawwad.ketabi.booksearch;

import com.hananawwad.ketabi.models.BookImageType;
import com.hananawwad.ketabi.models.SearchedBook;
import com.hananawwad.ketabi.util.BookUtil;
import com.hananawwad.ketabi.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Setter;

/**
 * @author hananawwad
 */
public class BookSearchResponseHandler {

    private ArrayList<SearchedBook> searchedBookArrayList;

    @Setter
    private BookSearchResponseEvents bookSearchResponseEvents;

    public void handleBookSearchResponse(String responseString) {

        JSONArray jsonArray = null;
        try {

            jsonArray = getItemsJSONArrayFromString(responseString);

        } catch (Exception e) {
            if (bookSearchResponseEvents != null) {
                bookSearchResponseEvents.onBookSearchResponseParsedFailed();
            }
            return;
        }

        if (jsonArray == null || jsonArray.length() == 0) {
            if (bookSearchResponseEvents != null) {
                bookSearchResponseEvents.onBookSearchResponseItemsBodyEmpty();
            }
            return;
        }
        try {
            parseResponseItems(jsonArray);
            bookSearchResponseEvents.onBookSearchResponseSuccessfullyParsed(searchedBookArrayList);

        } catch (Exception e) {
            if (bookSearchResponseEvents != null) {
                bookSearchResponseEvents.onBookSearchResponseParsedFailed();
            }
        }
    }

    private void parseResponseItems(JSONArray jsonArray) throws Exception {

        searchedBookArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            SearchedBook searchedBook = parseSingleItemAndPrepareSearchedBook(jsonArray.getJSONObject(i));
            if (searchedBook != null) {
                searchedBookArrayList.add(searchedBook);
            }
        }
    }

    /**
     * Fetches items array present in json string
     **/
    private JSONArray getItemsJSONArrayFromString(String jsonString) throws Exception {

        JSONObject jsonObject = new JSONObject(jsonString);
        if (jsonObject.has(Constants.BOOK_API_RESPONSE_ITEMS)) {
            return (JSONArray) jsonObject.get(Constants.BOOK_API_RESPONSE_ITEMS);
        } else {
            return null;
        }
    }


    public SearchedBook parseSingleItemAndPrepareSearchedBook(JSONObject jsonObject) throws Exception {

        String kindForResult;
        if (jsonObject.has(Constants.BOOK_API_RESPONSE_KIND)) {
            kindForResult = jsonObject.getString(Constants.BOOK_API_RESPONSE_KIND);
        } else {
            return null;
        }

        if (!kindForResult.equals("books#volume")) {
            return null;
        }

        JSONObject volumeInfo;
        if (jsonObject.has(Constants.BOOK_API_RESPONSE_VOLUME_INFO)) {
            volumeInfo = (JSONObject) jsonObject.getJSONObject(Constants.BOOK_API_RESPONSE_VOLUME_INFO);
        } else {
            return null;
        }

        String title;
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_TITLE)) {
            title = volumeInfo.getString(Constants.BOOK_API_RESPONSE_TITLE);
        } else {
            return null;
        }

        String subtitle = "";
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_SUBTITLE)) {
            subtitle = volumeInfo.getString(Constants.BOOK_API_RESPONSE_SUBTITLE);
        }

        JSONArray authors;
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_AUTHORS)) {
            authors = volumeInfo.getJSONArray(Constants.BOOK_API_RESPONSE_AUTHORS);
        } else {
            return null;
        }
        ArrayList<String> authorsList = new ArrayList<>();
        for (int j = 0; j < authors.length(); j++) {
            String author = authors.getString(j);
            authorsList.add(author);
        }

        String publisher = "";
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_PUBLISHER)) {
            publisher = volumeInfo.getString(Constants.BOOK_API_RESPONSE_PUBLISHER);
        }

        String publishedDate = "";
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_PUBLISHED_DATE)) {
            publishedDate = volumeInfo.getString(Constants.BOOK_API_RESPONSE_PUBLISHED_DATE);
        }

        String description = "";
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_DESCRIPTION)) {
            description = volumeInfo.getString(Constants.BOOK_API_RESPONSE_DESCRIPTION);
        }

        String industryIdentifier = "";
        JSONArray identifiersJSONArray = null;
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER)) {
            identifiersJSONArray = volumeInfo.getJSONArray(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER);
        }
        for (int j = 0; identifiersJSONArray != null && j < identifiersJSONArray.length(); j++) {
            JSONObject identifierJsonObject = identifiersJSONArray.getJSONObject(j);

            String type = "";
            if (identifierJsonObject.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE)) {
                type = identifierJsonObject.getString(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE);
            }
            if (type.equals(Constants.BOOK_API_RESPONSE_ISBN_13)) {
                if (identifierJsonObject.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER)) {
                    industryIdentifier = identifierJsonObject.getString(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER);
                }
            }
        }

        JSONObject imageLinksJSONObject;
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_IMAGELINKS)) {
            imageLinksJSONObject = (JSONObject) volumeInfo.get(Constants.BOOK_API_RESPONSE_IMAGELINKS);
        } else {
            imageLinksJSONObject = new JSONObject();
        }
        HashMap<BookImageType, String> bookImageTypeStringHashMap = new HashMap<>();
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL)) {
            bookImageTypeStringHashMap.put(BookImageType.SMALL_THUMBNAIL,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL));
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL)) {
            bookImageTypeStringHashMap.put(BookImageType.THUMBNAIL,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL));
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALL)) {
            bookImageTypeStringHashMap.put(BookImageType.SMALL,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALL));
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_MEDIUM)) {
            bookImageTypeStringHashMap.put(BookImageType.MEDIUM,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_MEDIUM));
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_LARGE)) {
            bookImageTypeStringHashMap.put(BookImageType.LARGE,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_LARGE));
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_XLARGE)) {
            bookImageTypeStringHashMap.put(BookImageType.XLARGE,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_XLARGE));
        }

        String thumbnail = BookUtil.preferredImageLink(bookImageTypeStringHashMap);

        return new SearchedBook.SearchedBookBuilder()
                .addTitle(title)
                .addSubtitle(subtitle)
                .addDescription(description)
                .addAuthors(authorsList)
                .addIndustryIdentifier(industryIdentifier)
                .addPublishedDate(publishedDate)
                .addPublisher(publisher)
                .addThumbnailLink(thumbnail)
                .build();
    }

    public interface BookSearchResponseEvents {
        void onBookSearchResponseSuccessfullyParsed(ArrayList<SearchedBook> searchedBookArrayList);

        void onBookSearchResponseParsedFailed();

        void onBookSearchResponseItemsBodyEmpty();
    }

}
