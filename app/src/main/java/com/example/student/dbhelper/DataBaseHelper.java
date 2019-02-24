package com.example.student.dbhelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper  extends SQLiteOpenHelper {


    private static String DB_PATH;
    private static String DB_NAME = "mydb.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "Products";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT = "Products";
    public static final Double COLUMN_PRICE = 0.0;
    public static final String COLUMN_STORY = "Stores";
    public SQLiteDatabase database;
    private Context myContext;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;

        this.myContext = context;    }

    @Override
    public void onCreate(SQLiteDatabase db) {   }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }

    public void create_db() {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                myInput = myContext.getAssets().open(DB_NAME);    //получаем локальную бд как поток
                myOutput = new FileOutputStream(DB_PATH);  // открываем пустую бд
                byte[] buffer = new byte[1024]; // побайтово копируем данные
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException ex) {

        }
    }

    public void open() throws SQLException {
        database = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }
}
