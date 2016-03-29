package com.example.jdarcy.ordermydrinks;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Adapter to bind a Drinkstem List to a view
 */
public class DrinksListAdapter extends ArrayAdapter<Drinks> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public DrinksListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Drinks currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final TextView textViewName = (TextView) row.findViewById(R.id.checkDrinksItem);
        textViewName.setText((currentItem.getText()));

        final TextView textViewNum = (TextView) row.findViewById(R.id.numDrinks);
        textViewNum.setText("0");


//        final Spinner spinner = (Spinner) row.findViewById(R.id.spinnerDrinksItem);

        return row;
    }

}