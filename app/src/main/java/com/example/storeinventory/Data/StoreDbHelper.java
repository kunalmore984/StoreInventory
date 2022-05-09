package com.example.storeinventory.Data;

import static com.example.storeinventory.Data.StoreContract.StoreEntry.COLUMN_PRODUCT_NAME;
import static com.example.storeinventory.Data.StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE;
import static com.example.storeinventory.Data.StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.storeinventory.Data.StoreContract.StoreEntry.TABLE_NAME;
import static com.example.storeinventory.Data.StoreContract.StoreEntry._ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create a database.....
        String SQL_CREATE_STORE_TABLE = "CREATE TABLE " + TABLE_NAME
                +"("
                + _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME +" TEXT NOT NULL, "
                + COLUMN_PRODUCT_PRICE+" INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_PRODUCT_QUANTITY +" INTEGER NOT NULL DEFAULT 0)";
        ;
        //execute sql statement....
        db.execSQL(SQL_CREATE_STORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: Do nothing for now
    }
}
