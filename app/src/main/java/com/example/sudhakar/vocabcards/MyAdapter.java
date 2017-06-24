package com.example.sudhakar.vocabcards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sudhakar on 2/4/17.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private DictionaryJSONParser mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
//        public TextView mTextView;
//        public EditText mEditText;
        public LinearLayout linearLayout;
        public ViewHolder(LinearLayout ll) {
            super(ll);
            linearLayout = ll;
//            mTextView = v;
//            mEditText = e;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(DictionaryJSONParser myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.my_text_view, parent, false);
//
//        // set the view's size, margins, paddings and layout parameters
////       // ...
//        v.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//v.layout(10,10,10,10);

        LinearLayout ll = (LinearLayout)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_custom_layout, parent, false);

        ll.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        ll.setPadding(0,20,0,0);

        ViewHolder vh = new ViewHolder(ll);

        //vh.mTextView.setHeight(50);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String textOut = "";
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        ((TextView) holder.linearLayout.getChildAt(0)).setText(mDataset.senses.get(position).definition);

         /*
        If there are examples, add them to the rendering string.
         */
        if(mDataset.senses.get(position).numExamples > 0) {
            for (int j = 0; j < mDataset.senses.get(position).numExamples; j++) {
                if (mDataset.senses.get(position).examples.get(j) != null) {
                    textOut +=  mDataset.senses.get(position).examples.get(j) + ";\n";
                }
            }
            textOut = textOut.substring(0,textOut.length()-1);
            ((TextView) holder.linearLayout.getChildAt(1)).setText(textOut);
        }
        /*
        Otherwise remove the view from the layout altogether.
         */
        else{
            holder.linearLayout.removeViewAt(1);
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.numDefinitions;
    }
}