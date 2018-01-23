package com.example.sudhakar.vocabcards;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.SeekableByteChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sudhakar on 8/1/17.
 */

public class WordMeaningSearchHelper {

    private WordMeaningDbHelper WordMeaningDb;
    private SessionNameDbHelper SessionNameDb;
    private String jsonResponse;
    private String wordToSearch;
    private boolean wordFoundInDb, sessionFoundInDb;
    private ArrayList<String> allSessionNames;
    private ArrayList<String> wordFields;
    private Integer searchCount=0;
    private final int SEARCH_SESSION_NAME=0, GET_ALL_SESSION_NAMES=1;

    public boolean errorFlag;
    public String errorInfo;

    private class SessionNameDbWriterTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                // Gets the data repository in write mode
                SQLiteDatabase db = SessionNameDb.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                if(sessionFoundInDb){
                    values.put(SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME, params[1]);
                    // Filter results WHERE "title" = 'My Title'
                    String whereClause = SessionNameContract.FeedEntry.COLUMN_NAME_SESSION + " LIKE ?";
                        /*
                        TODO: Error Checking.
                         */
                    String[] whereArgs = {params[0]};

                    long rowId = db.update(SessionNameContract.FeedEntry.TABLE_NAME,
                            values,
                            whereClause,
                            whereArgs);
                }
                else {
                    values.put(SessionNameContract.FeedEntry.COLUMN_NAME_SESSION, params[0]);
                    values.put(SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME, params[1]);

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(SessionNameContract.FeedEntry.TABLE_NAME, null, values);
                }
                return "done";
            }
            catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error while writing in Session Name DB.";
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }


    private class SessionNameDbReaderTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                SQLiteDatabase db = SessionNameDb.getReadableDatabase();

                String sortOrder;
                List itemIds;
                List items;
                //String[] projection = new String[1];

                Cursor cursor;

                //
                // Proceed according to the intent of invoking this
                // async task.
                //
                switch(Integer.parseInt(params[0])){

                    /*
                    TODO: Implement error checking for string parameters
                     */
                    case SEARCH_SESSION_NAME:

                        // Define a projection that specifies which columns from the database
                        // you will actually use after this query.
                        String[] projection = {
                                SessionNameContract.FeedEntry._ID,
                                SessionNameContract.FeedEntry.COLUMN_NAME_SESSION,
                                SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME
                        };

                        // Filter results WHERE "title" = 'My Title'
                        String selection = SessionNameContract.FeedEntry.COLUMN_NAME_SESSION + " = ?";
                        /*
                        TODO: Error Checking.
                         */
                        String[] selectionArgs = {params[1]};

                        // How you want the results sorted in the resulting Cursor
                        sortOrder =
                                SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME + " ASC";

                        cursor = db.query(
                        SessionNameContract.FeedEntry.TABLE_NAME, // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                        );

                        itemIds = new ArrayList<>();
                        items = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            long itemId = cursor.getLong(
                                    cursor.getColumnIndexOrThrow(SessionNameContract.FeedEntry._ID));
                            /*
                            TODO: Error Checking
                             */
                            itemIds.add(itemId);
                            String item = cursor.getString(cursor.getColumnIndexOrThrow(SessionNameContract.FeedEntry.COLUMN_NAME_SESSION));
                            /*
                            TODO: Error Checking
                             */
                            items.add(item);
                        }

                        cursor.close();
                        db.close();

                        switch(items.size()){
                            /*
                            Session not found. Need to add.
                             */
                            case 0:
                                sessionFoundInDb = false;
                                break;
                            /*
                            Session found. Need to only update time_stamp field.
                             */
                            case 1:
                                sessionFoundInDb = true;
                                break;
                            /*
                            More than one matching session entries found. Potential Error.
                             */
                            default:
                                /*
                                TODO: Find when such a scenario may occur and how to mitigate it.
                                 */
                                errorFlag = true;
                                errorInfo = "Multiple entries found in SessionName DB";

                        }

                        break;

                    case GET_ALL_SESSION_NAMES:

                        // Define a projection that specifies which columns from the database
                        // you will actually use after this query.
                        String[] projection2 = {
                                SessionNameContract.FeedEntry._ID,
                                SessionNameContract.FeedEntry.COLUMN_NAME_SESSION,
                                SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME
                        };
                        // How you want the results sorted in the resulting Cursor
                        sortOrder = SessionNameContract.FeedEntry.COLUMN_NAME_LASTTIME + " DESC";
//                        cursor = db.rawQuery("select * from " + SessionNameContract.FeedEntry.TABLE_NAME + " order by " +
//                                sortOrder, null);
                        String limit = "5";
                        cursor = db.query(
                                SessionNameContract.FeedEntry.TABLE_NAME, // The table to query
                                projection2,                               // The columns to return
                                null,                                // The columns for the WHERE clause
                                null,                            // The values for the WHERE clause
                                null,                                     // don't group the rows
                                null,                                     // don't filter by row groups
                                sortOrder,                          // The sort order
                                limit                               // Total rows to fetch
                        );

                        itemIds = new ArrayList<>();
                        items = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            long itemId = cursor.getLong(
                                    cursor.getColumnIndexOrThrow(SessionNameContract.FeedEntry._ID));
                            /*
                            TODO: Error Checking
                             */
                            itemIds.add(itemId);
                            String item = cursor.getString(cursor.getColumnIndexOrThrow(SessionNameContract.FeedEntry.COLUMN_NAME_SESSION));
                            /*
                            TODO: Error Checking
                             */
                            items.add(item);
                      }
                        /*
                            TODO: Error Checking
                             */
                        allSessionNames = (ArrayList<String>) items;
                        cursor.close();
                        db.close();

                        break;
                    default:
                        break;
                }

                return null;

            } catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error in reading Session Name DB";
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private class DbWriterTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                //System.out.print(params[1]);
                // Gets the data repository in write mode
                SQLiteDatabase db = WordMeaningDb.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                if(wordFoundInDb) {
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_SEARCHCOUNT, Integer.toString(searchCount +1) );
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_LASTSEARCHED, params[2]);
                    // Filter results WHERE "title" = 'My Title'
                    String whereClause = WordMeaningContract.FeedEntry.COLUMN_NAME_WORD + " LIKE ?";
                        /*
                        TODO: Error Checking.
                         */
                    String[] whereArgs = {params[0]};

                    long rowId = db.update(WordMeaningContract.FeedEntry.TABLE_NAME,
                            values,
                            whereClause,
                            whereArgs);

                }else{
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_WORD, params[0]);
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_JSON, params[1]);
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_LASTSEARCHED, params[2]);
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_SEARCHCOUNT, "1");
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_REVISECOUNT, "0");
                    values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_SESSION, params[3]);


                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(WordMeaningContract.FeedEntry.TABLE_NAME, null, values);
                }

                return "done";
            }
            catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error while storing word in Word-Meaning DB.";
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    private class DbReaderTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {


            try {

                SQLiteDatabase db = WordMeaningDb.getReadableDatabase();

                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                String[] projection = {
                        WordMeaningContract.FeedEntry._ID,
                        WordMeaningContract.FeedEntry.COLUMN_NAME_WORD,
                        WordMeaningContract.FeedEntry.COLUMN_NAME_JSON,
                        WordMeaningContract.FeedEntry.COLUMN_NAME_SEARCHCOUNT,
//                        WordMeaningContract.FeedEntry.COLUMN_NAME_LASTSEARCHED,
//                        WordMeaningContract.FeedEntry.COLUMN_NAME_SESSION,
                };

                // Filter results WHERE "title" = 'My Title'
                String selection = WordMeaningContract.FeedEntry.COLUMN_NAME_WORD + " = ?";
                String[] selectionArgs = {params[0]};

                // How you want the results sorted in the resulting Cursor
                String sortOrder =
                        WordMeaningContract.FeedEntry.COLUMN_NAME_JSON + " DESC";

                Cursor cursor = db.query(
                        WordMeaningContract.FeedEntry.TABLE_NAME, // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );

//                List itemIds = new ArrayList<>();
                List items = new ArrayList<>();
                List fields = new ArrayList<>();

                while (cursor.moveToNext()) {
//                    long itemId = cursor.getLong(
//                            cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry._ID));
//                    itemIds.add(itemId);
                    String item = cursor.getString(cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry.COLUMN_NAME_JSON));
                    items.add(item);

                    item = cursor.getString(cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry.COLUMN_NAME_SEARCHCOUNT));
                    searchCount = Integer.parseInt(item);
//                    Log.d("IN DB WM",item);
//
//                    item = cursor.getString(cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry.COLUMN_NAME_LASTSEARCHED));
//                    Log.d("IN DB WM",item);
//
//                    item = cursor.getString(cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry.COLUMN_NAME_SESSION));
//                    Log.d("IN DB WM",item);
                }
                cursor.close();
                db.close();
                //return items.toString();
                //System.out.println(String.valueOf(items.size()));

                String retVal = null;
                switch(items.size()){
                    /*
                    word not found.
                     */
                    case 0:
                        wordFoundInDb = false;
                        retVal = null;
                        break;

                    /*
                    Word found. Return the word meaning json by extracting from the list.
                     */
                    case 1:
                        wordFoundInDb = true;
                        //wordFields = (ArrayList<String>) fields;
                        retVal = items.get(0).toString();
                        //jsonResponse = retVal;

                        //Log.d("DB READ",items.get(0).toString());
                        break;
                    /*
                    More than one matching word entries found. Potential Error.

                     */
                    default:
                        /*
                        TODO: Find when such a scenario may occur and how to mitigate it.
                         */
                        errorFlag = true;
                        errorInfo = "Multiple entries found in Word-Meaning DB";

                }
                return retVal;

            } catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error in reading Word-Meaning DB";
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            jsonResponse = result;
            //tvWordMeaning.setText(jsonResponse);
        }
    }


    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {


                //TODO: replace with your own app id and app key
                final String app_id = "3e9f90d3";
                final String app_key = "db1e09133940592fdadb852d838a9b5a";
                try {
                        URL url = new URL(params[0]);
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.setRequestProperty("app_id", app_id);
                        urlConnection.setRequestProperty("app_key", app_key);

                        // read the output from the server
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();

                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line + "\n");
                        }

                        return stringBuilder.toString();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    errorFlag = true;
                    errorInfo = "The word doesn't exist! Try again.";
                    return e.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorFlag = true;
                    errorInfo = "Error in looking up word on the server.";
                    return e.toString();
                }
            }

            @Override
            protected void onPostExecute (String result){
                super.onPostExecute(result);

                //System.out.println(result);
                jsonResponse = result;
                //String save = tvWordMeaning.getText().toString();
                //tvWordMeaning.setText(new String(save+jsonResponse));
            }

    }

    private String dictionaryEntries(String searchWord) {
        final String language = "en";
        final String word = searchWord;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }

    public WordMeaningSearchHelper(WordMeaningDbHelper db1, SessionNameDbHelper db2){
        WordMeaningDb = db1;
        SessionNameDb = db2;
        jsonResponse = null;
        wordFoundInDb = false;
        errorFlag = false;
        errorInfo = null;
    }

    public  WordMeaningSearchHelper(SessionNameDbHelper db){
        SessionNameDb = db;
        errorFlag = false;
        errorInfo = null;
    }

    public String getMeaning(String word, String session){
        try {
                /*
                Get the timestamp first.
                 */
                Date date = new Date();
                Timestamp searchTimestamp = new Timestamp(date.getTime());
//                Date date2 = new Date(ts.getTime());
//                Log.d("WordMeaningSearchHelper","" + ts.toString() + " | " + date2.toString());

                /*
                What's the word to search
                 */
                wordToSearch = word;

                /*
                Search the word first in the local database.
                 */
                DbReaderTask newReader =  new DbReaderTask();
                newReader.execute(word).get();
                while(isRunning(newReader));

                /*
                If word not found in the local database, search it on the internet
                and store the meaning for future references.
                 */
                if (!(errorFlag || wordFoundInDb)) {
                    CallbackTask newSearch = new CallbackTask();
                    newSearch.execute(dictionaryEntries(word)).get();
                    while(isRunning(newSearch));
                }

                /*
                Store or update the word and corresponding fields in WordMeaning dB
                 */
                if(!errorFlag && jsonResponse!=null) {
                    String[] pair = {word, jsonResponse, searchTimestamp.toString(),session};
                    DbWriterTask newWriter = new DbWriterTask();
                    newWriter.execute(pair).get();
                    while(isRunning(newWriter));
                }

                /*
                Read SessionName dB.
                Check in the dB if the session already exists.
                 */
                if(!errorFlag && !session.equals("")){
                    String[] pair = {Integer.toString(SEARCH_SESSION_NAME), session};
                    SessionNameDbReaderTask newSessionNameReader = new SessionNameDbReaderTask();
                    newSessionNameReader.execute(pair).get();
                    while(isRunning(newSessionNameReader));
                }

                /*
                Write to SessionName dB.
                If the session exists, just update the timestamp otherwise add a new entry.
                 */
                if(!errorFlag && !session.equals("")){
                    String[] pair = {session, searchTimestamp.toString()};
                    SessionNameDbWriterTask newSessionNameWriter = new SessionNameDbWriterTask();
                    newSessionNameWriter.execute(pair).get();
                    while(isRunning(newSessionNameWriter));
                }

            /*
            Got the meaning, now return it.
             */
            //System.out.println(jsonResponse);
            return jsonResponse;
        }

        catch (InterruptedException e){
            e.printStackTrace();
            return e.toString();
        }
        catch (ExecutionException e){
            e.printStackTrace();
            return e.toString();
        }
    }

    private boolean isRunning(AsyncTask task) {
        return task.getStatus() == DbReaderTask.Status.RUNNING;
    }

    /*
    This method reads all(Max number can be specified) the session names present in the database
    and returns an array.
     */
    public String[] getSessionNames(){
        try {
            /*
            TODO: Remove the params here and elsewhere.
             */
            String[] pair = {Integer.toString(GET_ALL_SESSION_NAMES), "dummy"};
            SessionNameDbReaderTask newSessionNameReader = new SessionNameDbReaderTask();
            newSessionNameReader.execute(pair).get();
            while (isRunning(newSessionNameReader)) ;

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }

        /*
        return the cursor variable if valid entries are found in the database, otherwise
        return null.
         */
        if(!errorFlag){
            String[] sessionNameArray = allSessionNames.toArray(new String[allSessionNames.size()]);
            return sessionNameArray;
        }
        else{
            return null;
        }
    }

    public boolean isWordInDb(){
        return wordFoundInDb;
    }

}
