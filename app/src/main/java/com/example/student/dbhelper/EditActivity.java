package com.example.student.dbhelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EditActivity extends Activity {

    ListView listView;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    EditText enterProduct;
    EditText enterPrice;
    EditText enterShop;
    DataBaseHelper sqlHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        listView = (ListView) findViewById(R.id.userList);
        enterPrice = (EditText) findViewById(R.id.editPrice);
        enterProduct = (EditText) findViewById(R.id.editTitle);
        enterShop = (EditText) findViewById(R.id.editShop);
        sqlHelper = new DataBaseHelper(getApplicationContext());
        //creating data base, opening existing
        sqlHelper.create_db();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sqlHelper.database.delete(DataBaseHelper.TABLE, DataBaseHelper.COLUMN_ID + " = ?" ,new String[]{String.valueOf(userCursor.getString(0))} );
                onResume();
            }
        });
    }

        @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        sqlHelper.open();
        userCursor = sqlHelper.database.rawQuery("select * from " + DataBaseHelper.TABLE + " order by   Price ASC", null);
        String[] headers = new String[]{DataBaseHelper.COLUMN_PRODUCT, DataBaseHelper.COLUMN_STORY, "Price"};
        userAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
                userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        listView.setAdapter(userAdapter);

    }

    public void onRetClick(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onAddClick(View view){
        String fshop = enterShop.getText().toString();
        String ftitle = enterProduct.getText().toString();
        String fprice = enterPrice.getText().toString();
        userCursor = sqlHelper.database.rawQuery("select * from " + DataBaseHelper.TABLE + " where " + DataBaseHelper.COLUMN_PRODUCT + "='" + ftitle +"' and " + DataBaseHelper.COLUMN_STORY + "='" + fshop+"'", null );
        String fid="";
        while (userCursor.moveToNext()) {
            fid += userCursor.getString(0);
        }
        if(!fid.equals("")){
            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.COLUMN_PRICE, fprice);
            cv.put(DataBaseHelper.COLUMN_PRODUCT, ftitle);
            cv.put(DataBaseHelper.COLUMN_STORY, fshop);
         long result = sqlHelper.database.update(DataBaseHelper.TABLE, cv, DataBaseHelper.COLUMN_ID +"="+fid, null );
         if(result == -1)
             Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
         else
             Toast.makeText(this, "Updated successfully", Toast.LENGTH_LONG).show();
        } else{
            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.COLUMN_PRICE, fprice);
            cv.put(DataBaseHelper.COLUMN_PRODUCT, ftitle);
            cv.put(DataBaseHelper.COLUMN_STORY, fshop);
            long result = sqlHelper.database.insert(DataBaseHelper.TABLE, null, cv);
            if(result == -1)
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Added successfully", Toast.LENGTH_LONG).show();
        }

        onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlHelper.database.close();

    }
}
