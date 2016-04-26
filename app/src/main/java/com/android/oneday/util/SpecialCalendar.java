package com.android.oneday.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Feng on 6/3/2016.
 * TODO FINISH 90%
 */
public class SpecialCalendar {
	/**
	 * @variable daysOfMonth
	 * @variable dayOfWeek
	 */
	private static int daysOfMonth = 0;
	private static int dayOfWeek = 0;
    private int eachDayOfWeek = 0;

	/**
	 * Determine it is or not a leap year
	 * @param year
	 * @return boolean.
	 */
	public static boolean isLeapYear(int year) {
		if (year % 100 == 0 && year % 400 == 0) {
			return true;
		} else if (year % 100 != 0 && year % 4 == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Get the num of days a month
	 * @param isLeapyear
	 * @param month
	 * @return daysOfMonth
	 */
	public static int getDaysOfMonth(boolean isLeapyear, int month) {
		switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				daysOfMonth = 31;
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				daysOfMonth = 30;
				break;
			case 2:
				if (isLeapyear) {
					daysOfMonth = 29;
				} else {
					daysOfMonth = 28;
				}

		}
		return daysOfMonth;
	}

	/**
	 * Specify a year in the first day of the month is the day of the week
	 * @param year
	 * @param month
	 * @return dayOfWeek
	 */
	public static int getWeekdayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek;
	}

    public int getWeekDayOfLastMonth(int year, int month, int day){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);
        eachDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        return eachDayOfWeek;
    }

	/**
	 * Get the current year and month
	 * @return 返回日期数组，整形array[0]，为年份，array[1]为月份, array[2]为日期
	 */
	public static int[] getCurrentYearAndMonth() {
		int[] result = new int[3];
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
		String dateStr = "";
		Date date = new Date();

		dateStr = dateFormat.format(date); // 当期日期
		result[0] = Integer.parseInt(dateStr.split("-")[0]);
		result[1] = Integer.parseInt(dateStr.split("-")[1]);
		result[2] = Integer.parseInt(dateStr.split("-")[2]);

		return result;
	}

    /**
     * 判断某年某月所有的星期数
     *
     * @param year
     * @param month
     */
    public int getWeeksOfMonth(int year, int month) {
        // 先判断某月的第一天为星期几
        int weeksOfMonth = 0;
        int preMonthRelax = 0;
        int dayFirst = getWeekdayOfMonth(year, month);
        int days = getDaysOfMonth(isLeapYear(year), month);

        if (dayFirst != 7) {
            preMonthRelax = dayFirst;
        }
        if ((days + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (days + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (days + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;
    }

    public int getWeekOfMonth(int day, int dayOfWeek){
        int currentWeek = 0;

        if (dayOfWeek == 7) {
            currentWeek = day / 7 + 1;
        } else {
            if (day <= (7 - dayOfWeek)) {
                currentWeek = 1;
            } else {
                if ((day - (7 - dayOfWeek)) % 7 == 0) {
                    currentWeek = (day - (7 - dayOfWeek)) / 7 + 1;
                } else {
                    currentWeek = (day - (7 - dayOfWeek)) / 7 + 2;
                }
            }
        }
        return currentWeek;
    }


}
