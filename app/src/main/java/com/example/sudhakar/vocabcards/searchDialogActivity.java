package com.example.sudhakar.vocabcards;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class searchDialogActivity extends AppCompatActivity {

    private TextView tvDialogWordMeaning;
    private WordMeaningDbHelper activeDb;
    private EditText etWordToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tvDialogWordMeaning = (TextView) findViewById(R.id.textViewDialogWordMeaning);
        etWordToSearch = (EditText) findViewById(R.id.editTextDialogWordToSearch) ;

        activeDb = new WordMeaningDbHelper(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                new Thread(new Runnable() {
                    public void run() {
                        String word = etWordToSearch.getText().toString();
                        WordMeaningSearchHelper searchSpace = new WordMeaningSearchHelper(activeDb,tvDialogWordMeaning);
                        String meaning = searchSpace.getMeaning(word);
                        System.out.println(meaning);
                    }
                }).start();
            }
        });
    }
}
