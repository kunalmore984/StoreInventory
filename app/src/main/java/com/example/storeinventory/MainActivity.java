package com.example.storeinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storeinventory.Data.StoreContract;
import com.example.storeinventory.Data.StoreDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.zip.DeflaterInputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private StoreCursorAdapter mStoreCursorAdapter;
    private StoreDbHelper mStoreDbHelper;
    private static final int URL_LOADER = 0;
    private Button mSalesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //calling detail activity on clicking floating button
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.floating_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send to new editor Activity no this one
                Intent add_new = new Intent(MainActivity.this,Detail.class);
                startActivity(add_new);
            }
        });
        //setting cursor adapter to listview
        ListView store_list = (ListView) findViewById(R.id.product_list);
        mStoreCursorAdapter = new StoreCursorAdapter(MainActivity.this,null);
        store_list.setAdapter(mStoreCursorAdapter);
        mStoreDbHelper = new StoreDbHelper(MainActivity.this);
        View empty_view = findViewById(R.id.empty_view);
        store_list.setEmptyView(empty_view);
        store_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,Detail.class);
                Uri currentItemUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI,id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });
        mSalesButton = (Button)findViewById(R.id.sales_btn);
        getLoaderManager().initLoader(URL_LOADER,null,MainActivity.this);
    }

    //creating menu items in home screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    //method to perform action on selected menu item

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Handling item selection
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                //TODO: insert a dummy data after selecting this option
                insetItem();
                return true;
            case R.id.action_delete_all_entries:
                //TODO: delete all entries from the database
                showDialogInterface();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY
        };
        return new CursorLoader(MainActivity.this,
                StoreContract.StoreEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //changing data in database for new list item
        mStoreCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStoreCursorAdapter.swapCursor(null);
    }
    //Method to insert dummy data
    private void insetItem(){
        ContentValues values = new ContentValues();
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,"Amazon Alexa");
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,100);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY,20);
        Uri link = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI,values);

    }

    private void showDialogInterface(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure to delete all items ?");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItems();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItems(){
        //delete all entries from main screen
        int rowsdeleted = getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI,null,null);
        if (rowsdeleted !=0){
            Toast.makeText(MainActivity.this,"Successfully deleted all items",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MainActivity.this,"Failed to deleted all items",Toast.LENGTH_LONG).show();
        }
    }

    public void Decrement(View view){
        /*LinearLayout parentRow = (LinearLayout) view.getParent();*/
        TextView quantityView = (TextView) findViewById(R.id.quantity);
        String quantity = quantityView.getText().toString();
        int q = Integer.parseInt(quantity);
        if(q>0){
            q -= 1;
            quantityView.setText(String.valueOf(q));
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Cannot have a negative value");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog!=null){
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}