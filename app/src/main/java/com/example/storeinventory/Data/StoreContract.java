package com.example.storeinventory.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class StoreContract {

    private StoreContract(){
        //Keep it null so no other class can access this initializer
    }

    //Constant for authority of database
    public static final String CONTENT_AUTHORITY = "com.example.storeinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "store";
    //class for database items..
    public static final class StoreEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_ITEMS;
        public static final String TABLE_NAME = "store";
        public static final String _ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        //public static final String COLUMN_PET_WEIGHT = "pet_weight";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_ITEMS);
    }
}
