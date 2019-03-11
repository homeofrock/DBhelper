package com.example.student.dbhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
    ListView userList;
    EditText userFilter;
    DataBaseHelper sqlHelper;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userList = (ListView) findViewById(R.id.userList);
        userFilter = (EditText) findViewById(R.id.userFilter);
        sqlHelper = new DataBaseHelper(getApplicationContext());
        // создаем базу данных
        sqlHelper.create_db();
    }

    public void onEditClick(View v){
        Intent toEdit = new Intent(this, EditActivity.class);
        startActivity(toEdit);
        finish();
    }

    public void onClick(View view){
        sqlHelper.open();
        String filter_pr=userFilter.getText().toString();
        userCursor = sqlHelper.database.rawQuery("SELECT * FROM Products where Price=" +
                "(Select MIN(Price) FROM Products where Products='"+filter_pr+"')",null);
        String namestore="";
        while (userCursor.moveToNext()) {
            namestore += userCursor.getString(3);
        }
        if(!namestore.equals(""))
            Toast.makeText(this,"The cheapest " +filter_pr +" is in the "+namestore, 3).show();
        else
            Toast.makeText(this, "Not found", Toast.LENGTH_LONG).show();
        userCursor.close();
    }


    @Override
    public void onResume() {
        super.onResume();
        sqlHelper.open();
        userCursor = sqlHelper.database.rawQuery("select * from " + DataBaseHelper.TABLE + " order by   Price ASC", null);
        String[] headers = new String[]{DataBaseHelper.COLUMN_PRODUCT, DataBaseHelper.COLUMN_STORY, "Price"};
        userAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
                userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        // если в текстовом поле есть текст, выполняем фильтрацию
        if (!userFilter.getText().toString().isEmpty())
            userAdapter.getFilter().filter(userFilter.getText().toString());
        // установка слушателя изменения текста
        userFilter.addTextChangedListener(
                new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    // при изменении текста выполняем фильтрацию
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        userAdapter.getFilter().filter(s.toString());
                    }
                });

        // устанавливаем провайдер фильтрации
        userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0) {
                    return sqlHelper.database.rawQuery("select * from " + DataBaseHelper.TABLE + " order by Price ASC", null);
                } else {
                    return sqlHelper.database.rawQuery("select * from " +DataBaseHelper.TABLE + " where " +
                            DataBaseHelper.COLUMN_PRODUCT + " like ?" +" order by Price ASC", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

        userList.setAdapter(userAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключения
        sqlHelper.database.close();
        userCursor.close();
    }
}
