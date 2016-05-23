package com.android.oneday.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 5/19/2016.
 */
public class CityModel {
    private DB dbOpenHelper = null;

    public CityModel(Context context) {
        dbOpenHelper = new DB(context);
    }

    /**
     * 得到所有支持的省份或直辖市名称的String类型数组
     *
     * @return 支持的省份或直辖市数组
     */
    public String[] getAllProvinces() {
        String[] columns = {"name"};

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        //查询获得游标
        Cursor cursor = db.query("provinces", columns, null, null, null, null, null);
        columns = null;
        int count = cursor.getCount();
        String[] provinces = new String[count];
        count = 0;
        while (!cursor.isLast()) {
            cursor.moveToNext();
            provinces[count] = cursor.getString(0);
            count = count + 1;
        }
        cursor.close();
        db.close();
        return provinces;
    }

    /**
     * 根据省份数组来得到对应装有对应的城市名和城市编码的列表对象
     *
     * @param provinces 省份数组
     * @return 索引0为对应的城市名的二维数组和索引1为对应城市名的二维数组
     */
    public List<String[][]> getAllCityAndCode(String[] provinces) {
        int length = provinces.length;
        String[][] city = new String[length][];
        String[][] code = new String[length][];
        int count = 0;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        for (int i = 0; i < length; i++) {
            Cursor cursor = db.query("citys", new String[]{"name", "city_num"},
                    "province_id = ? ", new String[]{String.valueOf(i)}, null, null, null);
            count = cursor.getCount();
            city[i] = new String[count];
            code[i] = new String[count];
            count = 0;
            while (!cursor.isLast()) {
                cursor.moveToNext();
                city[i][count] = cursor.getString(0);
                code[i][count] = cursor.getString(1);
                count = count + 1;
            }
            cursor.close();
        }
        db.close();
        List<String[][]> result = new ArrayList<String[][]>();
        result.add(city);
        result.add(code);
        return result;
    }

    /**
     * 由城市名查询数据库来得到城市码
     *
     * @param cityName 城市名
     * @return 城市码
     */
    public String getCityCodeByName(String cityName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("citys", new String[]{"city_num"},
                "name = ? ", new String[]{cityName}, null, null, null);
        String cityCode = null;
        if (!cursor.isLast()) {
            cursor.moveToNext();
            cityCode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return cityCode;
    }

}
