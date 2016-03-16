package com.android.oneday.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by Feng on 3/1/2016.
 */
public class DB extends SQLiteOpenHelper{

    private final static int VERSION = 1;

    public DB(Context context, String name, CursorFactory factory,
              int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public DB(Context context, String name){
        this(context, name, null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS schedule(scheduleID integer primary key autoincrement," +
                "scheduleTypeID integer,remindID integer,scheduleContent text,scheduleDate text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS scheduletagdate(tagID integer primary key autoincrement," +
                "year integer,month integer,day integer,scheduleID integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS schedule");
        db.execSQL("DROP TABLE IF EXISTS scheduletagdate");
        onCreate(db);
    }
}
