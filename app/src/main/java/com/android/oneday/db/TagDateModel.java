package com.android.oneday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.oneday.vo.ScheduleDateTag;
import com.android.oneday.vo.ScheduleVO;

import java.util.ArrayList;

/**
 * Created by dell on 5/18/2016.
 */
public class TagDateModel {
    private DB dbOpenHelper = null;
    //private Context context = null;

    public TagDateModel(Context context){

        //this.context = context;
        dbOpenHelper = new DB(context);
    }

    /**
     * 将日程标志日期保存到数据库中
     * @param dateTagList
     */
    public void saveTagDate(ArrayList<ScheduleDateTag> dateTagList){
        //dbOpenHelper = new DBOpenHelper(context, "schedules.db");
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ScheduleDateTag dateTag = new ScheduleDateTag();
        for(int i = 0; i < dateTagList.size(); i++){
            dateTag = dateTagList.get(i);
            ContentValues values = new ContentValues();
            values.put("year", dateTag.getYear());
            values.put("month", dateTag.getMonth());
            values.put("day", dateTag.getDay());
            values.put("scheduleID", dateTag.getScheduleID());
            db.insert("scheduletagdate", null, values);
        }
    }

    /**
     * 删除日程
     * @param scheduleID
     */
    public void delete(int scheduleID){
        //dbOpenHelper = new DBOpenHelper(context, "schedules.db");
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            db.delete("scheduletagdate", "scheduleID=?", new String[]{String.valueOf(scheduleID)});
            db.setTransactionSuccessful();
        }finally{
            db.endTransaction();
        }
    }

    /**
     * 只查询出当前月的日程日期
     * @param currentYear
     * @param currentMonth
     * @return
     */
    public ArrayList<ScheduleDateTag> getTagDate(int currentYear, int currentMonth){
        ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("scheduletagdate", new String[]{"tagID", "year", "month", "day", "scheduleID"}, "year=? and month=?", new String[]{String.valueOf(currentYear), String.valueOf(currentMonth)}, null, null, null);
        while(cursor.moveToNext()){
            int tagID = cursor.getInt(cursor.getColumnIndex("tagID"));
            int year = cursor.getInt(cursor.getColumnIndex("year"));
            int month = cursor.getInt(cursor.getColumnIndex("month"));
            int day = cursor.getInt(cursor.getColumnIndex("day"));
            int scheduleID = cursor.getInt(cursor.getColumnIndex("scheduleID"));
            ScheduleDateTag dateTag = new ScheduleDateTag(tagID,year,month,day,scheduleID);
            dateTagList.add(dateTag);
        }
        cursor.close();
        if(dateTagList != null && dateTagList.size() > 0){
            return dateTagList;
        }
        return null;
    }

    /**
     * 当点击每个gridview中的item时，查询出此日期上所有的日程标记（ScheduleID）
     * @param year
     * @param month
     * @param day
     * @return
     */
    public String[] getScheduleByTagDate(int year, int month, int day){
        ArrayList<ScheduleVO> scheduleList = new ArrayList<ScheduleVO>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        Cursor cursor = db.query("scheduletagdate", new String[]{"scheduleID"}, "year=? and month=? and day=?", new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day)}, null, null, null);
        String scheduleIDs[] = null;
        scheduleIDs = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            String scheduleID = cursor.getString(cursor.getColumnIndex("scheduleID"));
            scheduleIDs[i] = scheduleID;
            i++;
        }
        cursor.close();

        return scheduleIDs;

    }

    /**
     * 关闭DB
     */
    public void destoryDB(){
        if(dbOpenHelper != null){
            dbOpenHelper.close();
        }
    }
}
