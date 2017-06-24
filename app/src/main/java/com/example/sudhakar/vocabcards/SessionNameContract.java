package com.example.sudhakar.vocabcards;

import android.provider.BaseColumns;

/**
 * Created by sudhakar on 15/4/17.
 */

public final class SessionNameContract {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.    private FeedReaderContract() {}
    private SessionNameContract() {
    }

    ;

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Session_Names";
        public static final String COLUMN_NAME_SESSION = "session";
        public static final String COLUMN_NAME_LASTTIME = "lastTime";
    }
}