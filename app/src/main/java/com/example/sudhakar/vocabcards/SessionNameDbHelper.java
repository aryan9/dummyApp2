package com.example.sudhakar.vocabcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by sudhakar on 18/2/17.
 */

public class SessionNameDbHelper extends SQLiteOpenHelper {
    
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SessionNameContract.FeedEntry.TABLE_NAME + " (" +
                    SessionNameContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    SessionNameContract.FeedEntry.COLUMN_NAME_SESSION + " TEXT," +
                    SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SessionNameContract.FeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SessionName.db";

    public SessionNameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

        /*
        Initialize this dB with one row by default.
         */
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        ContentValues values = new ContentValues();
        values.put(SessionNameContract.FeedEntry.COLUMN_NAME_SESSION, "Default Session");
        values.put(SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME, ts.toString());
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(SessionNameContract.FeedEntry.TABLE_NAME, null, values);
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
