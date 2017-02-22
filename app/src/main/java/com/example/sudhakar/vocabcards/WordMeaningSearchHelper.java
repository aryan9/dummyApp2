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
    private SearchHistoryDbHelper SearchHistoryDb;
    private String jsonResponse;
    private String wordToSearch;
    public boolean wordFoundInDb;

    public boolean errorFlag;
    public String errorInfo;

    private class SearchHistoryDbWriterTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                //System.out.print(params[1]);
                // Gets the data repository in write mode
                SQLiteDatabase db = SearchHistoryDb.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                values.put(SearchHistoryContract.FeedEntry.COLUMN_NAME_WORD, params[0]);
                values.put(SearchHistoryContract.FeedEntry.COLUMN_NAME_TIMESTAMP, params[1]);
                values.put(SearchHistoryContract.FeedEntry.COLUMN_NAME_REMARKS, params[2]);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(SearchHistoryContract.FeedEntry.TABLE_NAME, null, values);
                return "done";
            }
            catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error while logging in Search History DB.";
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

                values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_WORD, params[0]);
                values.put(WordMeaningContract.FeedEntry.COLUMN_NAME_JSON, params[1]);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(WordMeaningContract.FeedEntry.TABLE_NAME, null, values);
                return "done";
            }
            catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error while storing word in DB.";
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
                        WordMeaningContract.FeedEntry.COLUMN_NAME_JSON
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

                List itemIds = new ArrayList<>();
                List items = new ArrayList<>();
                while (cursor.moveToNext()) {
                    long itemId = cursor.getLong(
                            cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry._ID));
                    itemIds.add(itemId);
                    String item = cursor.getString(cursor.getColumnIndexOrThrow(WordMeaningContract.FeedEntry.COLUMN_NAME_JSON));
                    items.add(item);
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
                        errorInfo = "Multiple entries found in DB";

                }
                return retVal;

            } catch (Exception e) {
                e.printStackTrace();
                errorFlag = true;
                errorInfo = "Error in reading DB";
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

    public WordMeaningSearchHelper(WordMeaningDbHelper db1, SearchHistoryDbHelper db2){
        WordMeaningDb = db1;
        SearchHistoryDb = db2;
        jsonResponse = null;
        wordFoundInDb = false;
        errorFlag = false;
        errorInfo = null;
    }

    public String getMeaning(String word){
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
                //System.out.println("Calling DB Reader");
                DbReaderTask newReader =  new DbReaderTask();
                newReader.execute(word).get();
                while(isRunning(newReader));

                //Log.d("DB READ",newReader.getStatus().toString());
                //System.out.println("Back From DB Reader");
                //System.out.println(jsonResponse);
                /*
                If word not found in the local database, search it on the internet
                and store the meaning for future references.
                 */
                if (!(errorFlag || wordFoundInDb)) {
                //System.out.println("Calling Dict API");
                    CallbackTask newSearch = new CallbackTask();
                    newSearch.execute(dictionaryEntries(word)).get();
                    while(isRunning(newSearch));
                //System.out.println("Back From Dict API");

                if(!errorFlag) {
                    //System.out.println("Saving the Word");
                    String[] pair = {word, jsonResponse};
                    DbWriterTask newWriter = new DbWriterTask();
                    newWriter.execute(pair).get();
                    while(isRunning(newWriter));
                    //System.out.println("Done Saving");
                }
            }

             /*
              Word search was successful, now log it.
              */
            if(!errorFlag && jsonResponse != null){
                String[] save = {word, searchTimestamp.toString(), "default"};
                SearchHistoryDbWriterTask newLogger = new SearchHistoryDbWriterTask();
                newLogger.execute(save).get();
                while(isRunning(newLogger));
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
}
