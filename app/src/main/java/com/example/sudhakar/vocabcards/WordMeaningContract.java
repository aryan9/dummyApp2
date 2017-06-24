package com.example.sudhakar.vocabcards;

import android.provider.BaseColumns;

/**
 * Created by sudhakar on 28/12/16.
 */

public final class WordMeaningContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.    private FeedReaderContract() {}
    private WordMeaningContract() {};

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "WordMeanings";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_JSON = "json";
        public static final String COLUMN_NAME_LASTSEARCHED = "lastSearched";
        public static final String COLUMN_NAME_SEARCHCOUNT = "searchCount";
        public static final String COLUMN_NAME_REVISECOUNT = "reviseCount";
        public static final String COLUMN_NAME_SESSION = "session";
    }
}

