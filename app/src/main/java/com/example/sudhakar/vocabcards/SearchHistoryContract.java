package com.example.sudhakar.vocabcards;

import android.provider.BaseColumns;

/**
 * Created by sudhakar on 28/12/16.
 */

public final class SearchHistoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.    private FeedReaderContract() {}
    private SearchHistoryContract() {};

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "SearchHistory";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_REMARKS = "remarks";
    }
}

