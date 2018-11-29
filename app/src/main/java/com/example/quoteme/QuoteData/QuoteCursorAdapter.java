package com.example.quoteme.QuoteData;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.quoteme.R;

import org.w3c.dom.Text;

public class QuoteCursorAdapter extends CursorAdapter {

    //Constructor
    public QuoteCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    //Creates a empty view using list_item.xml ready to hold data from a cursor
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Binds data from the current cursor position to list_item.xml in newView() above^
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Find views
        TextView titleTextView = view.findViewById(R.id.name);
        TextView vendorTextView = view.findViewById(R.id.summary);
        //TextView statusTextView = view.findViewById(R.id.status);

        //Define column headings to receive data from
        int titleColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_TITLE);
        int vendorColumnIndex = cursor.getColumnIndex(QuoteContract.QuoteEntry.COLUMN_QUOTE_VENDOR);

        //Read values from cursor to current variables
        String quoteTitle = cursor.getString(titleColumnIndex);
        String quoteVendor = cursor.getString(vendorColumnIndex);

        //Update TextViews in list_item.xml with values from associated columns
        titleTextView.setText(quoteTitle);
        vendorTextView.setText(quoteVendor);
    }
}
