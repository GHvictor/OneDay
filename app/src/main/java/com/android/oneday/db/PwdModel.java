package com.android.oneday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by dell on 5/18/2016.
 */
public class PwdModel {
    private DB dbOpenHelper = null;

    public PwdModel(Context context) {
        dbOpenHelper = new DB(context);
    }

    public void savePwd(int type, String pwd) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("passwordType", type);
        values.put("password", pwd);

        db.insert("pwdtable", null, values);

    }

    /**
     * 查询某一条日程信息
     * @param type & pwd
     * @return
     */
    public boolean checkPwd(int type, String pwd){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("pwdtable", new String[]{"password"}, "passwordType=?", new String[]{String.valueOf(type)}, null, null, null);
        if(cursor.moveToFirst()){
            String rightPwd = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();
            Log.i(pwd, rightPwd);
            if (pwd.equals(rightPwd)){
                return true;
            }else{
                return false;
            }
        }
        cursor.close();
        return false;

    }

    /**
     * 关闭DB
     */
    public void destoryDB() {
        if (dbOpenHelper != null) {
            dbOpenHelper.close();
        }
    }
}
