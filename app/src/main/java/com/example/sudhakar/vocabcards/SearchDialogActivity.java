package com.example.sudhakar.vocabcards;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchDialogActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView tvDialogWordMeaning;
    private WordMeaningDbHelper WordMeaningDb;
    private SessionNameDbHelper SessionNameDb;
    private EditText etWordToSearch;
    private EditText etSessionName;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context appContext;
    private String sessionName;
    private Spinner spinner ;//= (Spinner) findViewById(R.id.spinner_session);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Custom Dictionary");

        /*
        Create and initialize databases.
         */
        WordMeaningDb = new WordMeaningDbHelper(getApplicationContext());
        SessionNameDb = new SessionNameDbHelper(getApplicationContext());


        /*
        Saving the Context in a variable so that it can be used elsewhere
        than this onCreate method.
         */
        appContext = this;


        /*
        Create recycler view to hold the returned search result.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewMeanings);
        mRecyclerView.getLayoutParams().height = 500;

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        /*
        Create a spinner to hold the session names as a drop-down list.
         */
        spinner = (Spinner) findViewById(R.id.spinner_session);
        spinner.setOnItemSelectedListener(this);

        updateSessionNames();

        /*
        Store the references to the GUI elements that may be needed outside
        this onCreate method.
         */
        tvDialogWordMeaning = (TextView) findViewById(R.id.textViewDialogWordMeaning);
        etWordToSearch = (EditText) findViewById(R.id.editTextDialogWordToSearch) ;
        etSessionName = (EditText) findViewById(R.id.editTextDialogRemarks);


        /*
        Create methods for all the buttons in the GUI.
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View finalView = view;
                new Thread(new Runnable() {
                    public void run() {

                        String word = etWordToSearch.getText().toString();
                        String session = etSessionName.getText().toString();
                        session = session.equals("")  ? sessionName : session;

                        if(!(session.equals("") || word.equals(""))){
                            WordMeaningSearchHelper searchSpace = new WordMeaningSearchHelper(WordMeaningDb, SessionNameDb);
                            String meaning = searchSpace.getMeaning(word, session);
                            String textOut = "";
                            textOut += searchSpace.isWordInDb() ? "Found in DB\n" : "Searched Online\n";
                            //Log.d("DB READ",meaning);

                            if (!searchSpace.errorFlag) {
                                final DictionaryJSONParser wordParser = new DictionaryJSONParser(meaning);
                                wordParser.ParseJSON();

                                //textOut += "Definitions : " + wordParser.numDefinitions;
                                //textOut += "\n";
                                if (wordParser.numDefinitions != 0) {
                                    for (int i = 0; i < wordParser.numDefinitions; i++) {
                                        if (wordParser.senses.get(i).definition != null) {
                                            //textOut += "\n";
                                            //textOut += Integer.toString(i + 1) + ". " + wordParser.senses.get(i).definition;

                                        /*
                                        If there are examples, add them to the rendering string.
                                         */
                                            if (wordParser.senses.get(i).numExamples > 0) {
                                                //textOut += "\n[";
                                                for (int j = 0; j < wordParser.senses.get(i).numExamples; j++) {
                                                    if (wordParser.senses.get(i).examples.get(j) != null) {
                                                        if (j > 0) {
                                                            //textOut += "\n";
                                                        }
                                                        //textOut += "\t" + wordParser.senses.get(i).examples.get(j) + ",";

                                                    }
                                                }
                                                //textOut += "]\n";
                                            }

                                        /*
                                        If there are subsenses, add them to the rendering string.
                                         */
                                            if (wordParser.senses.get(i).numSubsenses > 0) {
                                                //textOut += "{";
                                                for (int k = 0; k < wordParser.senses.get(i).numSubsenses; k++) {
                                                    if (k > 0) {
                                                        //textOut += "\n";
                                                    }
                                                    //textOut += "\t" + Integer.toString(k + 1) + ". " + wordParser.senses.get(i).subsenses.get(k).definition;

                                                }
                                                //textOut += "}\n";
                                            }
                                        }
                                    }

                            /*
                            Now display the meaning on screen.
                             */
                                    new UpdateUITask().execute(textOut);

                                /*
                                TODO: Is this safe to use?
                                Could not update the dynamic UI otherwise using normal threads or
                                Async Tasks. Hence using this method.
                                 */
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter = new MyAdapter(wordParser);
                                            mRecyclerView.setAdapter(mAdapter);

                                            /*
                                            TODO: Part of the cleaning up of Dialog GUI after successful search.
                                            TODO: Create an API which will do this job.
                                             */
                                            etSessionName.setText("");
//                                            tvDialogWordMeaning.setText();
                                        }
                                    });
                                    /*
                                    TODO: Part of the cleaning up of Dialog GUI after successful search.
                                    TODO: Create an API which will do this job.
                                     */
                                    updateSessionNames();

                                }
                            /*
                            TODO : Fix this bug ASAP!
                             */
                                else {
                                    new UpdateUITask().execute("Error in App. Fix this");
                                }
                            } else {
                                new UpdateUITask().execute(searchSpace.errorInfo);
                            }
                        }else{
                            Snackbar.make(finalView, "'Word' or 'Session' missing!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                        }

                    }
                }).start();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        //Log.d("SPINNER",parent.getItemAtPosition(pos).toString());
        sessionName = parent.getItemAtPosition(pos).toString();
    }

    /*
    TODO: No idea why this method needs to exist. Find out.
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        //Log.d("SPINNER2",parent.getItemAtPosition(parent.getFirstVisiblePosition()).toString());
    }

    public void updateSessionNames(){
        /*
        Populate the spinner by getting the session names from the database.
        Using new thread to avoid interfering with the main thread.
         */
        new Thread(new Runnable() {
            public void run() {

                WordMeaningSearchHelper justGetSessionNames = new WordMeaningSearchHelper(SessionNameDb);

                /*
                TODO: Error checking on the return value.
                 */
                final String[] sessionNamesList = justGetSessionNames.getSessionNames();
//                sessionNamesList.add("Random");

                /*
                TODO: Is using the following method safe enough?
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                appContext,
                                android.R.layout.simple_spinner_item,
                                sessionNamesList);
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);
                    }
                });
            }
        }).start();
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
