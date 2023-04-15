package com.example.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userstore.db";
    private static final int SHEMA = 1;
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_AGE + " TEXT);");
        db.execSQL("INSERT INTO "+ TABLE_USERS +" (" + COLUMN_NAME + ", " + COLUMN_AGE  + ") VALUES ('Том Смит', '1981');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addData(SQLiteDatabase db, String name, String age){
        db.execSQL("INSERT INTO "+ TABLE_USERS +" (" + COLUMN_NAME + ", " + COLUMN_AGE  + ") VALUES ('"+name+"', '"+age+"');");
    }
}
