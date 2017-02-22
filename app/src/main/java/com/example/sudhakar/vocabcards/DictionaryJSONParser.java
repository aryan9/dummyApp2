package com.example.sudhakar.vocabcards;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sudhakar on 29/1/17.
 */

public class DictionaryJSONParser{

    /*
    Class Definitions.
     */
    public class SubsenseDefinitionExample{
        String definition;
        int numExamples;
        ArrayList<String> examples;

        public SubsenseDefinitionExample(){
            definition = null;
            numExamples = 0;
            examples = new ArrayList<>();
        }
    }

    public class DefinitionExample{
        String definition;
        int numExamples;
        ArrayList<String> examples;
        int numSubsenses;
        int numSubDefinitions;
        ArrayList<SubsenseDefinitionExample> subsenses;

        public DefinitionExample(){
            definition = null;
            numExamples = 0;
            examples = new ArrayList<>();
            numSubsenses = 0;
            numSubDefinitions = 0;
            subsenses = new ArrayList<>();
        }
    }

    /*
    Variables definitions.
     */
    String jsonStr;
    int numDefinitions;
    int numSenses;
    boolean errorFlag;
    String errorInfo;
    ArrayList<DefinitionExample> senses;


    /*
    Methods definitions.
     */
    public  DictionaryJSONParser(String str){
        jsonStr = str;
        senses = new ArrayList<>();
        numDefinitions = 0;
        numSenses = 0;
        errorFlag = false;
        errorInfo = null;
    }

    public void ParseJSON (){
        try {
                    /*
                    Read the string in to a JSON Object.
                     */
            if(jsonStr!= null) {
                JSONObject jsonObj = new JSONObject(jsonStr);

                //System.out.println(jsonObj);
                    /*
                     Get the value of "results" key.
                     */
                JSONObject results = jsonObj.getJSONArray("results").getJSONObject(0);

                    /*
                    Get the value of "lexicalEntries" key. A word might have multiple
                    lexicalEntries so read them into a JSON Array.
                     */
                JSONArray lexicalEntriesArr = results.getJSONArray("lexicalEntries");

                for (int i = 0; i < lexicalEntriesArr.length(); i++) {
                    //System.out.println("LexicalEntry : " + String.valueOf(i));
                        /*
                        Get each one of the lexicalEntries to process for "entries" > "senses" >
                        "definitions" && "examples"
                         */
                    JSONObject lexicalEntry = lexicalEntriesArr.getJSONObject(i);
                    JSONArray entriesArr = lexicalEntry.getJSONArray("entries");

                    for (int num_entry = 0; num_entry < entriesArr.length(); num_entry++) {
                        JSONObject entry = entriesArr.getJSONObject(num_entry);
                        JSONArray sensesArr = entry.getJSONArray("senses");

                        numSenses = sensesArr.length();
                        for (int num_senses = 0; num_senses < sensesArr.length(); num_senses++) {

                            //System.out.println("Sense : " + String.valueOf(num_senses));
                            JSONObject sense = sensesArr.getJSONObject(num_senses);

                            DefinitionExample newDef = new DefinitionExample();
                                /*
                                Each "senses" object might not have deifinitions. Hence, proceed forward
                                only if the "definitions object is present in "senses".
                                TODO: Check what is the complete structure of "senses".
                                 */
                            if (!sense.isNull("definitions")) {
                                String definitions = sense.getJSONArray("definitions").getString(0);
                                //System.out.println("Definition : " + definitions);
                                newDef.definition = definitions;
                                numDefinitions += 1;
                            }
                                /*
                                Each single definition might have multiple examples hence read "examples"
                                into a JSON Array.
                                 */
                            if (!sense.isNull("examples")) {
                                JSONArray examplesArr = sense.getJSONArray("examples");
                                newDef.numExamples = examplesArr.length();
                                for (int num_Example = 0; num_Example < examplesArr.length(); num_Example++) {
                                    String example = examplesArr.getJSONObject(num_Example).getString("text");
                                    //System.out.println("Example " + String.valueOf(num_Example) + " of " + String.valueOf(examplesArr.length() - 1) + " : " + example);
                                    newDef.examples.add(example);
                                }
                            }
                                /*
                                Each "senses" object might have multiple "subsenses".
                                 */
                            if (!sense.isNull("subsenses")) {
                                //System.out.println("SUBSENSES");
                                JSONArray subsensesArr = sense.getJSONArray("subsenses");

                                newDef.numSubsenses = subsensesArr.length();
                                for (int num_subsenses = 0; num_subsenses < subsensesArr.length(); num_subsenses++) {
                                    JSONObject subsense = subsensesArr.getJSONObject(num_subsenses);

                                    SubsenseDefinitionExample newSubDef = new SubsenseDefinitionExample();
                                    if (!subsense.isNull("definitions")) {
                                        String subsenseDefinitions = subsense.getJSONArray("definitions").getString(0);
                                        //System.out.println("Subsense " + String.valueOf(num_subsenses) + " : " + subsenseDefinitions);
                                        newSubDef.definition = subsenseDefinitions;
                                        newDef.numSubDefinitions += 1;
                                    }

                                    if (!subsense.isNull("examples")) {
                                        JSONArray subsenseExamplesArr = subsense.getJSONArray("examples");
                                        newSubDef.numExamples = subsenseExamplesArr.length();
                                        for (int num_subsenseExample = 0; num_subsenseExample < subsenseExamplesArr.length(); num_subsenseExample++) {
                                            String subsenseExample = subsenseExamplesArr.getJSONObject(num_subsenseExample).getString("text");
                                            //System.out.println("Example " + String.valueOf(num_subsenseExample) + " of " + String.valueOf(subsenseExamplesArr.length() - 1) + " : " + subsenseExample);
                                            newSubDef.examples.add(subsenseExample);
                                        }
                                    }
                                    newDef.subsenses.add(newSubDef);
                                }
                            }

                            senses.add(newDef);
                        }
                    }

                }

                //System.out.println("Did something\n");
            }
        } catch (final JSONException e) {
            Log.e("JSON Exception", "Json parsing error: " + e.getMessage());
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),
//                            "Json parsing error: " + e.getMessage(),
//                            Toast.LENGTH_LONG)
//                            .show();
//                }
//
//            });
            errorFlag = true;
            errorInfo = "Json parsing error";
        }

    }


}