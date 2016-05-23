package com.hananawwad.ketabi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hananawwad.ketabi.models.BookCondition;
import com.hananawwad.ketabi.models.BookImageType;
import com.hananawwad.ketabi.models.SearchedBook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author hananawwad
 */
public class BookUtil {

    public static String preferredImageLink(HashMap<BookImageType, String> bookImageTypeStringHashMap){

        String imageUrl = "";

        for(Map.Entry<BookImageType,String> entry : bookImageTypeStringHashMap.entrySet()){
            if(entry.getKey().equals(BookImageType.THUMBNAIL)){
                imageUrl = entry.getValue();
                break;
            } else if(entry.getValue() != null && entry.getValue().length() != 0){
                imageUrl = entry.getValue();
                break;
            }
        }

        return imageUrl;
    }

    public static ArrayList<SearchedBook> sterilizeSearchedBooks(ArrayList<SearchedBook> searchedBookArrayList){

        Set<SearchedBook> uniqueSearchedBooks = new HashSet<>();
        for(SearchedBook searchedBook : searchedBookArrayList){
            if(performValidationOnSearchedBook(searchedBook)){
                uniqueSearchedBooks.add(searchedBook);
            }
        }

        return new ArrayList<>(uniqueSearchedBooks);
    }

    /**
     * A Searched Book entry must have atleast below mentioned entries :
     * Title, Authors and IndustryIdentifier.
     *
     * This method validates these entries.
     *
     * @param searchedBook the book to be validated
     * @return boolean value denoting validation's success.
     */
    private static boolean performValidationOnSearchedBook(SearchedBook searchedBook){

        if(searchedBook == null){
            return false;
        }
        if(searchedBook.getTitle() == null
                || searchedBook.getTitle().length() == 0){
            return false;
        }
        if(searchedBook.getAuthors() == null
                || searchedBook.getAuthors().size() == 0
                || searchedBook.getAuthors().get(0) == null
                || searchedBook.getAuthors().get(0).length() == 0){
            return false;
        }
        if(searchedBook.getIndustryIdentifier() == null
                || searchedBook.getIndustryIdentifier().length() == 0){
            return false;
        }

        return true;
    }


    /**
     * Converts bitmap image to byte array
     *
     * @param bitmap image
     * @return byte array got from image bitmap
     */
    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        if(bitmap == null){
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    /**
     * Converts byte array to image bitmap
     *
     * @param bytes byte array
     * @return bitmap image
     */
    public static Bitmap convertByteArrayToBitmap(byte[] bytes){
        if(bytes == null || bytes.length == 0){
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Static Map used to map each book condition with a corresponding string value.
     */
    private static final Map<String, BookCondition> BOOK_CONDITION_MAP =
            Collections.unmodifiableMap(new HashMap<String, BookCondition>() {{
                put("POOR", BookCondition.POOR);
                put("LOOSE_BINDING", BookCondition.LOOSE_BINDING);
                put("BINDING", BookCondition.BINDING_COPY);
                put("FAIR", BookCondition.FAIR);
                put("FINE", BookCondition.FINE);
                put("GOOD", BookCondition.GOOD);
                put("NEW", BookCondition.NEW);
            }});

    /**
     * This method returns corresponding string value for each book condition entry.
     * It uses a static unmodifiable hash map.
     *
     * @param bookCondition {@link BookCondition} describes the condition of uploaded book.
     * @return String value for the corresponding book condition.
     */
    public static String getStringForBookConditionEnum(BookCondition bookCondition){
        for (Map.Entry<String,BookCondition> entry: BOOK_CONDITION_MAP.entrySet()) {
            if(entry.getValue().equals(bookCondition)){
                return entry.getKey();
            }
        }
        return null;
    }


    /**
     * This method returns the corresponding book condition for the string specified.
     * It uses a static unmodifiable hash map.
     *
     * @param condition string value denoting the book condition
     * @return {@link BookCondition} describes the condition of the uploaded book
     */
    public static BookCondition getBookConditionEnumFromString(String condition){
        for (Map.Entry<String,BookCondition> entry: BOOK_CONDITION_MAP.entrySet()) {
            if(entry.getKey().equals(condition)){
                return entry.getValue();
            }
        }
        return null;
    }
}
