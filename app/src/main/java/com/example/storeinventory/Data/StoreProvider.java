package com.example.storeinventory.Data;

import static com.example.storeinventory.Data.StoreContract.StoreEntry.COLUMN_PRODUCT_NAME;
import static com.example.storeinventory.Data.StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE;
import static com.example.storeinventory.Data.StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.storeinventory.Data.StoreContract.StoreEntry.TABLE_NAME;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StoreProvider extends ContentProvider {

    private static final String TAG = StoreProvider.class.getName();
    private StoreDbHelper mStoreDbHelper;
    private static final int ITEM = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_ITEMS,ITEM);
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY,StoreContract.PATH_ITEMS+"/#",ITEM_ID);
    }
    @Override
    public boolean onCreate() {
        mStoreDbHelper =new StoreDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mStoreDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                cursor = sqLiteDatabase.query(StoreContract.StoreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ITEM_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(StoreContract.StoreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Notify any change in the database to main UI
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                return insertitem(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsdeleted = 0;
        SQLiteDatabase sqLiteDatabase = mStoreDbHelper.getWritableDatabase();
         int match = sUriMatcher.match(uri);
         switch (match){
             case ITEM:
                 rowsdeleted = sqLiteDatabase.delete(StoreContract.StoreEntry.TABLE_NAME,selection,selectionArgs);
                 break;
             case ITEM_ID:
                 selection = StoreContract.StoreEntry._ID + "=?";
                 selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                 rowsdeleted = sqLiteDatabase.delete(StoreContract.StoreEntry.TABLE_NAME,selection,selectionArgs);
                 break;
             default:
                 throw new IllegalArgumentException("Insertion is not supported for " + uri);
         }
         if (rowsdeleted !=0){
             getContext().getContentResolver().notifyChange(uri,null);
         }
        return rowsdeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEM:
                return updateItem(uri,values,selection,selectionArgs);
            case ITEM_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    //method to insert item
    private Uri insertitem(Uri uri, ContentValues contentValues){
        SQLiteDatabase sqLiteDatabase = mStoreDbHelper.getWritableDatabase();
        //checking values before adding
        String name = contentValues.getAsString(COLUMN_PRODUCT_NAME);
        if (name == null){
            throw new IllegalArgumentException("Item requires a name");
        }
        Integer price = contentValues.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price<0){
            throw new IllegalArgumentException("Item requires a valid amount");
        }
        Integer quantity = contentValues.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0){
            throw new IllegalArgumentException("Item requires a valid quantity");
        }
        //inserting items in the database
        long rowsAdded = sqLiteDatabase.insert(StoreContract.StoreEntry.TABLE_NAME,null,contentValues);
        if (rowsAdded == -1){
            Log.e(TAG,"Failed to insert item : "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,rowsAdded);
    }

    //method to update the item in the store
    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        SQLiteDatabase sqLiteDatabase = mStoreDbHelper.getWritableDatabase();
        //Checking whether none of the values in edit texts are empty
        if (contentValues.containsKey(COLUMN_PRODUCT_NAME)){
            String name = contentValues.getAsString(COLUMN_PRODUCT_NAME);
            if (name == null){
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (contentValues.containsKey(COLUMN_PRODUCT_PRICE)){
            Integer price = contentValues.getAsInteger(COLUMN_PRODUCT_PRICE);
            if (price == null || price <0){
                throw new IllegalArgumentException("Item requires a valid price");
            }
        }
        if (contentValues.containsKey(COLUMN_PRODUCT_QUANTITY)){
            Integer quantity = contentValues.getAsInteger(COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0){
                throw new IllegalArgumentException("Item requires a valid quantity");
            }
        }

        //if no updates are made return to home screen
        if (contentValues.size() == 0){
            return 0;
        }
         int rowsupdated = sqLiteDatabase.update(TABLE_NAME,contentValues,selection,selectionArgs);

        if (rowsupdated !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsupdated;
    }
}
