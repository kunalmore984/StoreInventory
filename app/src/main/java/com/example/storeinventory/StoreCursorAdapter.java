package com.example.storeinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.storeinventory.Data.StoreContract;

public class StoreCursorAdapter extends CursorAdapter{

    public StoreCursorAdapter(Context context,Cursor c){
        super(context,c,0);
    }
    //inflate the layout for new data
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    //place the received values from database to edit texts.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //instances of the text views.
        TextView name_text = (TextView) view.findViewById(R.id.name);
        TextView price_text = (TextView) view.findViewById(R.id.price);
        TextView quantity_text = (TextView) view.findViewById(R.id.quantity);
        //column ids of the table columns
        int n = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        int p = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
        int q = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
        //binding values to the text views
        String name =cursor.getString(n);
        int price = cursor.getInt(p);
        int quantity = cursor.getInt(q);

        name_text.setText(name);
        price_text.setText(String.valueOf(price));
        quantity_text.setText(String.valueOf(quantity));
    }
}
