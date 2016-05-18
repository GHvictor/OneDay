package com.android.oneday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.oneday.vo.ScheduleDateTag;
import com.android.oneday.vo.ScheduleVO;

import java.util.ArrayList;

/**
 * Created by Feng on 3/1/2016.
 */
public class ScheduleModel {

	private DB dbOpenHelper = null;
	//private Context context = null;
	
	public ScheduleModel(Context context){

		//this.context = context;
		dbOpenHelper = new DB(context);
	}
	
	/**
	 * 保存日程信息
	 * @param scheduleVO
	 */
	public int save(ScheduleVO scheduleVO){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("scheduleTypeID", scheduleVO.getScheduleTypeID());
		values.put("remindID", scheduleVO.getRemindID());
		values.put("scheduleContent", scheduleVO.getScheduleContent());
		values.put("scheduleDate", scheduleVO.getScheduleDate());
		db.beginTransaction();
		int scheduleID = -1;
		try{
			db.insert("schedule", null, values);
		    Cursor cursor = db.rawQuery("select max(scheduleID) from schedule", null);
		    if(cursor.moveToFirst()){
		    	scheduleID = (int) cursor.getLong(0);
		    }
		    cursor.close();
		    db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	    return scheduleID;
	}
	
	/**
	 * 查询某一条日程信息
	 * @param scheduleID
	 * @return
	 */
	public ScheduleVO getScheduleByID(int scheduleID){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("schedule", new String[]{"scheduleID","scheduleTypeID","remindID","scheduleContent","scheduleDate"}, "scheduleID=?", new String[]{String.valueOf(scheduleID)}, null, null, null);
		if(cursor.moveToFirst()){
			int schID = cursor.getInt(cursor.getColumnIndex("scheduleID"));
			int scheduleTypeID = cursor.getInt(cursor.getColumnIndex("scheduleTypeID"));
			int remindID = cursor.getInt(cursor.getColumnIndex("remindID"));
			String scheduleContent = cursor.getString(cursor.getColumnIndex("scheduleContent"));
			String scheduleDate = cursor.getString(cursor.getColumnIndex("scheduleDate"));
			cursor.close();
			return new ScheduleVO(schID,scheduleTypeID,remindID,scheduleContent,scheduleDate);
		}
		cursor.close();
		return null;
		
	}

    /**
     * 按日期查询日程信息
     * @return
     */
    public ArrayList<ScheduleVO> getScheduleByDate(String scheduleDate){
        ArrayList<ScheduleVO> list = new ArrayList<ScheduleVO>();
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("schedule", new String[]{"scheduleID","scheduleTypeID","remindID","scheduleContent","scheduleDate"}, "scheduleDate"+" LIKE?", new String[]{"%"+scheduleDate+"%"}, null, null, null);
        while(cursor.moveToNext()){
            int scheduleID = cursor.getInt(cursor.getColumnIndex("scheduleID"));
            int scheduleTypeID = cursor.getInt(cursor.getColumnIndex("scheduleTypeID"));
            int remindID = cursor.getInt(cursor.getColumnIndex("remindID"));
            String scheduleContent = cursor.getString(cursor.getColumnIndex("scheduleContent"));
            String schDate = cursor.getString(cursor.getColumnIndex("scheduleDate"));
            ScheduleVO vo = new ScheduleVO(scheduleID, scheduleTypeID, remindID, scheduleContent, schDate);
            list.add(vo);
        }
        cursor.close();
        if(list != null && list.size() > 0){
            return list;
        }
        return null;
    }
	
	/**
	 * 查询全部日程信息
	 * @return
	 */
	public ArrayList<ScheduleVO> getAllSchedule(){
		ArrayList<ScheduleVO> list = new ArrayList<ScheduleVO>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("schedule", new String[]{"scheduleID","scheduleTypeID","remindID","scheduleContent","scheduleDate"}, null, null, null, null, "scheduleID desc");
		while(cursor.moveToNext()){
			int scheduleID = cursor.getInt(cursor.getColumnIndex("scheduleID")); 
			int scheduleTypeID = cursor.getInt(cursor.getColumnIndex("scheduleTypeID"));
			int remindID = cursor.getInt(cursor.getColumnIndex("remindID"));
			String scheduleContent = cursor.getString(cursor.getColumnIndex("scheduleContent"));
			String scheduleDate = cursor.getString(cursor.getColumnIndex("scheduleDate"));
			ScheduleVO vo = new ScheduleVO(scheduleID,scheduleTypeID,remindID,scheduleContent,scheduleDate);
			list.add(vo);
		}
		cursor.close();
		if(list != null && list.size() > 0){
			return list;
		}
		return null;
		
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
			db.delete("schedule", "scheduleID=?", new String[]{String.valueOf(scheduleID)});
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * 更新日程
	 * @param vo
	 */
	public void update(ScheduleVO vo){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("scheduleTypeID", vo.getScheduleTypeID());
		values.put("remindID", vo.getRemindID());
		values.put("scheduleContent", vo.getScheduleContent());
		values.put("scheduleDate", vo.getScheduleDate());
		db.update("schedule", values, "scheduleID=?", new String[]{String.valueOf(vo.getScheduleID())});
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
