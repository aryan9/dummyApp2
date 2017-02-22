package com.example.sudhakar.vocabcards;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SearchDialogActivity extends AppCompatActivity {

    private TextView tvDialogWordMeaning;
    private WordMeaningDbHelper WordMeaningDb;
    private SearchHistoryDbHelper SearchHistoryDb;
    private EditText etWordToSearch;
    private EditText etRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tvDialogWordMeaning = (TextView) findViewById(R.id.textViewDialogWordMeaning);
        etWordToSearch = (EditText) findViewById(R.id.editTextDialogWordToSearch) ;
        etRemarks = (EditText) findViewById(R.id.editTextDialogRemarks);

        WordMeaningDb = new WordMeaningDbHelper(getApplicationContext());
        SearchHistoryDb = new SearchHistoryDbHelper(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                new Thread(new Runnable() {
                    public void run() {
                        String word = etWordToSearch.getText().toString();
                        WordMeaningSearchHelper searchSpace = new WordMeaningSearchHelper(WordMeaningDb,SearchHistoryDb);
                        String meaning = searchSpace.getMeaning(word);
                        String textOut = "";
                        textOut += searchSpace.wordFoundInDb ? "Found in DB\n" : "Searched Online\n";
                        //Log.d("DB READ",meaning);

                        if(!searchSpace.errorFlag){
                            DictionaryJSONParser wordParser = new DictionaryJSONParser(meaning);
                            wordParser.ParseJSON();

                            textOut += "Definitions : " + wordParser.numDefinitions;
                            textOut += "\n";
                            if (wordParser.numDefinitions != 0) {
                                for (int i = 0; i < wordParser.numDefinitions; i++) {
                                    if (wordParser.senses.get(i).definition != null) {
                                        textOut += wordParser.senses.get(i).definition;
                                        textOut += "\n";
                                    }
                                }

                            /*
                            Now display the meaning on screen.
                             */
                                new UpdateUITask().execute(textOut);
                            }
                            /*
                            TODO : Fix this bug ASAP!
                             */
                            else{
                                new UpdateUITask().execute("Error in App. Fix this");
                            }
                    }
                    else{
                            new UpdateUITask().execute(searchSpace.errorInfo);
                    }


                    }
                }).start();
            }
        });
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
        tvDialogWordMeaning.setText(result);
    }
}
}
