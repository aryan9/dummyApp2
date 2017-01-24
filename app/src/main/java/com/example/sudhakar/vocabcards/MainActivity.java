package com.example.sudhakar.vocabcards;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.tag;


public class MainActivity extends AppCompatActivity {

    private Button buttonSearchWord;
    private TextView tvWordMeaning;
    private EditText etWordToSearch;
    private String jsonResponse;
    private WordMeaningDbHelper activeDb;
    private AlertDialog.Builder alertDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //getApplicationContext().deleteDatabase("WordMeanings.db");

        activeDb = new WordMeaningDbHelper(getApplicationContext());

        tvWordMeaning = (TextView) findViewById(R.id.textViewWordMeaning);
        etWordToSearch = (EditText)findViewById(R.id.editTextWordToSearch);
        buttonSearchWord = (Button) findViewById(R.id.buttonSearchWord);

        alertDialogBuilder = new AlertDialog.Builder(this);

        showNotification();

        buttonSearchWord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    public void run() {
//                        String word = etWordToSearch.getText().toString();
//                        WordMeaningSearchHelper searchSpace = new WordMeaningSearchHelper(activeDb,tvWordMeaning);
//                        String meaning = searchSpace.getMeaning(word);
//                        System.out.println(meaning);
//                    }
//                }).start();

                showNotification();

                FireMissilesDialogFragment dialog = new FireMissilesDialogFragment();
                dialog.show(getSupportFragmentManager(),"missiles");



            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public WordMeaningDbHelper getActiveDb(){
        return activeDb;
    }

    private void showNotification() {
        // TODO Auto-generated method stub

//        NotificationManager nMN = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification n  = new Notification.Builder(this)
//                .setContentTitle("Whip And Weep")
//                .setContentText("Whip is On!")
//                .setSmallIcon(R.drawable.ic_stat_library_books)
//                .build();
//        n.flags = Notification.FLAG_ONGOING_EVENT;
//        nMN.notify(123, n);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_search)
                        .setContentTitle("CoderoMusicPlayer")
                        .setContentText("PLayer0!");

        Intent resultIntent = new Intent(this, searchDialogActivity.class);
//        resultIntent.setAction(Intent.ACTION_MAIN);
//        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void CreateDialogBox(){

        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void DictionaryJSONParser (String jsonStr){
        try {
                    /*
                    Read the string in to a JSON Object.
                     */
            JSONObject jsonObj = new JSONObject(jsonStr);

            System.out.println(jsonObj);
                    /*
                     Get the value of "results" key.
                     */
            JSONObject results = jsonObj.getJSONArray("results").getJSONObject(0);

                    /*
                    Get the value of "lexicalEntries" key. A word might have multiple
                    lexicalEntries so read them into a JSON Array.
                     */
            JSONArray lexicalEntriesArr = results.getJSONArray("lexicalEntries");

            for ( int i = 0 ; i < lexicalEntriesArr.length() ; i++){
                System.out.println("LexicalEntry : " + String.valueOf(i));
                        /*
                        Get each one of the lexicalEntries to process for "entries" > "senses" >
                        "definitions" && "examples"
                         */
                JSONObject lexicalEntry = lexicalEntriesArr.getJSONObject(i);
                JSONArray entriesArr = lexicalEntry.getJSONArray("entries");

                for (int numEntry = 0 ; numEntry < entriesArr.length() ; numEntry++ ) {
                    JSONObject entry = entriesArr.getJSONObject(numEntry);
                    JSONArray sensesArr = entry.getJSONArray("senses");
                    for (int numSenses = 0 ; numSenses < sensesArr.length() ; numSenses++){

                        System.out.println("Sense : " + String.valueOf(numSenses));
                        JSONObject sense = sensesArr.getJSONObject(numSenses);

                                /*
                                Each "senses" object might not have deifinitions. Hence, proceed forward
                                only if the "definitions object is present in "senses".
                                TODO: Check what is the complete structure of "senses".
                                 */
                        if (!sense.isNull("definitions")) {
                            String definitions = sense.getJSONArray("definitions").getString(0);
                            System.out.println("Definition : " + definitions);
                        }
                                /*
                                Each single definition might have multiple examples hence read "examples"
                                into a JSON Array.
                                 */
                        if (!sense.isNull("examples")) {
                            JSONArray examplesArr = sense.getJSONArray("examples");
                            for (int num_Example = 0; num_Example < examplesArr.length(); num_Example++) {
                                String example = examplesArr.getJSONObject(num_Example).getString("text");
                                System.out.println("Example " + String.valueOf(num_Example) + " of " + String.valueOf(examplesArr.length() - 1) + " : " + example);

                            }
                        }
                                /*
                                Each "senses" object might have multiple "subsenses".
                                 */
                        if (!sense.isNull("subsenses")) {
                            System.out.println("SUBSENSES");
                            JSONArray subsensesArr = sense.getJSONArray("subsenses");
                            for (int num_subsenses = 0; num_subsenses < subsensesArr.length(); num_subsenses++) {
                                JSONObject subsense = subsensesArr.getJSONObject(num_subsenses);
                                if (!subsense.isNull("definitions")) {
                                    String subsenseDefinitions = subsense.getJSONArray("definitions").getString(0);
                                    System.out.println("Subsense " + String.valueOf(num_subsenses) + " : " + subsenseDefinitions);
                                }

                                if (!subsense.isNull("examples")) {
                                    JSONArray subsenseExamplesArr = subsense.getJSONArray("examples");
                                    for (int num_subsenseExample = 0; num_subsenseExample < subsenseExamplesArr.length(); num_subsenseExample++) {
                                        String subsenseExample = subsenseExamplesArr.getJSONObject(num_subsenseExample).getString("text");
                                        System.out.println("Example " + String.valueOf(num_subsenseExample) + " of " + String.valueOf(subsenseExamplesArr.length() - 1) + " : " + subsenseExample);
                                    }
                                }
                            }
                        }

                    }
                }

            }

            System.out.println("Did something\n");

        } catch (final JSONException e) {
            Log.e("JSON Exception", "Json parsing error: " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        }

    }
    /*
    Creating a custom JSON string parser for the responses obtained from
    oxforddictionaries.com.
     */
//    public class DictionaryJSONParser{
//        String jsonStr;
//        JSONArray lexicalEntriesArr;
//
//        public void DictionaryJSONParser(String str){
//            jsonStr = str;
//        }
//
//    }

    private String dictionaryEntries(String searchWord) {
        final String language = "en";
        //final String word = searchWord;
        final String word_id = searchWord.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }
}
