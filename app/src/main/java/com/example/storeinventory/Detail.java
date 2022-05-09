package com.example.storeinventory;

import static java.net.Proxy.Type.HTTP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.storeinventory.Data.StoreContract;

public class Detail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = Detail.class.getName();
    private Uri mCurrentUri;
    private final int URI_ID = 1;
    //edit texts declarations.
    private EditText mItemName;
    private EditText mPriceText;
    private EditText mQuantityText;
    //Buttons in the layout
    private Button mAdd;
    private Button mMinus;
    private Button mOrderNow;
    private int quantity =0;

    private Boolean mItemHasChanged = false;
    private View.OnTouchListener mOnTouchListener =new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged=true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        //Checking the whether add button or item list is clicked

        mAdd =(Button) findViewById(R.id.add);
        mMinus =(Button) findViewById(R.id.minus);
        mItemName = (EditText) findViewById(R.id.name_edit);
        mPriceText = (EditText) findViewById(R.id.price_edit);
        mQuantityText = (EditText) findViewById(R.id.quantity_edit);
        mOrderNow = (Button) findViewById(R.id.order_btn);
        if (mCurrentUri !=null){
            setTitle("Details");
            getLoaderManager().initLoader(URI_ID,null,this);
        }else {
            setTitle("Add a item");
            mOrderNow.setVisibility(View.GONE);
            invalidateOptionsMenu();
        }
        mItemName.setOnTouchListener(mOnTouchListener);
        mQuantityText.setOnTouchListener(mOnTouchListener);
        mPriceText.setOnTouchListener(mOnTouchListener);

        mOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email_intent = new Intent(Intent.ACTION_SENDTO);
                email_intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                email_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example.yahoo.com"});
                email_intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback");
                try {
                    startActivity(email_intent);
                }catch (ActivityNotFoundException a){

                    Log.e(TAG,"Cannot open email app : "+a);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                //TODO: save the inserted data in the database
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                //TODO: delete the selected data
                showDialogInterface();
                return true;
            case android.R.id.home:
                //TODO: Do nothing for now
                if (!mItemHasChanged){
                    NavUtils.navigateUpFromSameTask(Detail.this);
                    return true;
                }
                DialogInterface.OnClickListener dialoginterface = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(Detail.this);
                    }
                };
                //showUnsavedChangesDialog(dialoginterface);
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
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
        return new CursorLoader(this,
                mCurrentUri,projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            int n = data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
            int p = data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
            int q = data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
            //binding values to the text views
            String name = data.getString(n);
            int price = data.getInt(p);
            quantity = data.getInt(q);
            mPriceText.setText(String.valueOf(price));
            mQuantityText.setText(String.valueOf(quantity));
            mItemName.setText(name);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPriceText.setText("");
        mQuantityText.setText("");
        mItemName.setText("");
    }

    private void showUnSavedChanges(DialogInterface.OnClickListener discardButton){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard the changes");
        builder.setNegativeButton("Discard", discardButton);
        builder.setPositiveButton(" Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //deleteItems();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnSavedChanges(dialog);
    }
    //Disable deleted option when adding new Item

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
         if (mCurrentUri == null){
             MenuItem menuItem = menu.findItem(R.id.action_delete);
             menuItem.setVisible(false);
         }
         return true;
    }
    //Delete dialogue interface
    private void showDialogInterface(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete the item ?");
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
        if (mCurrentUri !=null){
            //delete the current item
            int rowsdeleted = getContentResolver().delete(mCurrentUri,null,null);
            Log.e(TAG,"rows deleted are : "+rowsdeleted);
            if (rowsdeleted !=0){
                Toast.makeText(this,"Successfully deleted the items",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,"Failed to delete the items",Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }
    //Method to save a item
    private void saveItem(){
        //getting values from edit texts
        String nameString = mItemName.getText().toString().trim();
        String priceString = mPriceText.getText().toString().trim();
        String quantityString = mQuantityText.getText().toString().trim();
        if (mCurrentUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString)){
            Log.e(TAG,"exiting the activity as no data is inserted ");
            Toast.makeText(this,"Fields cannot be empty",Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,nameString);
        contentValues.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,Integer.parseInt(priceString));
        contentValues.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY,Integer.parseInt(quantityString));
        //checking whether activity is in edit mode or not
        if (mCurrentUri ==null){
            //Activity is add mode

            Uri newUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI,contentValues);
            //Checking whether changes are made
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Insert Item Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Item Insert successfully",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsUpdated = 0;
            rowsUpdated = getContentResolver().update(mCurrentUri,contentValues,null,null);
            if (rowsUpdated == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "update Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Item updated successfully",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Add and minus methods
    public void increase(View view){
       String quan = mQuantityText.getText().toString();
       int q = Integer.parseInt(quan);
        Log.e(TAG,"Outside if");
       if (q>0){
           Log.e(TAG,"Not Working ");
           q= q+1;
           mQuantityText.setText(String.valueOf(q));
       }
    }

    public void Minus(View view){
        String quan = mQuantityText.getText().toString();
        int q = Integer.parseInt(quan);
        if (q>0){
            q= q-1;
            mQuantityText.setText(String.valueOf(q));
        }
    }
}