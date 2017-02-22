package com.example.sudhakar.vocabcards;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sudhakar on 18/2/17.
 */

public class SearchHistoryDbHelper extends SQLiteOpenHelper {
    
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SearchHistoryContract.FeedEntry.TABLE_NAME + " (" +
                    SearchHistoryContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    SearchHistoryContract.FeedEntry.COLUMN_NAME_WORD + " TEXT," +
                    SearchHistoryContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " TEXT)" +
                    SearchHistoryContract.FeedEntry.COLUMN_NAME_REMARKS + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SearchHistoryContract.FeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SearchHistory.db";

    public SearchHistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
