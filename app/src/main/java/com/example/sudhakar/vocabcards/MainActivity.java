package com.example.sudhakar.vocabcards;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button buttonSearchWord;
    private TextView tvWordMeaning;
    private EditText etWordToSearch;
    private String jsonResponse;
    private WordMeaningDbHelper WordMeaningDb;
    private SearchHistoryDbHelper SearchHistoryDb;
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

        WordMeaningDb = new WordMeaningDbHelper(getApplicationContext());
        SearchHistoryDb = new SearchHistoryDbHelper(getApplicationContext());

        tvWordMeaning = (TextView) findViewById(R.id.textViewWordMeaning);
        etWordToSearch = (EditText)findViewById(R.id.editTextWordToSearch);
        buttonSearchWord = (Button) findViewById(R.id.buttonSearchWord);

        alertDialogBuilder = new AlertDialog.Builder(this);

        showNotification();

        buttonSearchWord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        String word = etWordToSearch.getText().toString();
                        WordMeaningSearchHelper searchSpace = new WordMeaningSearchHelper(WordMeaningDb,SearchHistoryDb);
                        String meaning = searchSpace.getMeaning(word);
                        //System.out.print(meaning);

                        if(!searchSpace.errorFlag){
                            DictionaryJSONParser wordParser = new DictionaryJSONParser(meaning);
                            wordParser.ParseJSON();
                            String textOut = "";
                            textOut += "Definitions : " + wordParser.numDefinitions;
                            textOut += "\n";
                            //System.out.println("Definitions : " + wordParser.numDefinitions);
                            for (int i = 0 ; i < wordParser.numDefinitions ; i++){
                                if(wordParser.senses.get(i).definition != null) {
                                    //System.out.println(wordParser.senses.get(i).definition);
                                    textOut += wordParser.senses.get(i).definition;
                                    textOut += "\n";
                                }
                            }
                            UpdateUITask uiUpdater = new UpdateUITask();
                            uiUpdater.execute(textOut);
                            //System.out.print(textOut);
                        }
                        else{
                            //System.out.print(searchSpace.errorInfo);
                        }
                    }
                }).start();

//                showNotification();
//
//                FireMissilesDialogFragment dialog = new FireMissilesDialogFragment();
//                dialog.show(getSupportFragmentManager(),"missiles");



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

    public WordMeaningDbHelper getWordMeaningDb(){
        return WordMeaningDb;
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
                        .setContentTitle("Vocab Cards")
                        .setContentText("Standing By, touch to search words.");


        Intent resultIntent = new Intent(this, SearchDialogActivity.class);
//        resultIntent.setAction(Intent.ACTION_MAIN);
//        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        mBuilder.setContentIntent(pendingIntent);
        Notification n = mBuilder.build();
        n.flags =  Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, n);
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


        /*
    Creating a custom JSON string parser for the responses obtained from
    oxforddictionaries.com.
     */

    private String dictionaryEntries(String searchWord) {
        final String language = "en";
        //final String word = searchWord;
        final String word_id = searchWord.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }

    private class UpdateUITask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                return params[0];
            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvWordMeaning.setText(result);
        }
    }
}
