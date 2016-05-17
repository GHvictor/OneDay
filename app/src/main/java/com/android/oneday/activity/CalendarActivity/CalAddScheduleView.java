package com.android.oneday.activity.CalendarActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.ScheduleActivity.ScheduleInfoView;
import com.android.oneday.activity.ScheduleActivity.ScheduleTypeView;
import com.android.oneday.adapter.DateAdapter;
import com.android.oneday.constant.ScheduleConstant;
import com.android.oneday.db.ScheduleModel;
import com.android.oneday.util.LunarCalendar;
import com.android.oneday.util.SpecialCalendar;
import com.android.oneday.util.SysApp;
import com.android.oneday.vo.ScheduleDateTag;
import com.android.oneday.vo.ScheduleVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalAddScheduleView extends BaseActivity implements OnGestureListener {

    private ViewFlipper topFlipper = null;
    private GridView gridView = null;
    private TextView calDate;
    private ImageView returnCalendar = null;
    private GestureDetector gestureDetector = null;
    private TextView scheduleType = null;
    private TextView dateText = null;
    private EditText scheduleText = null;
    private TextView scheduleSave = null;  //保存按钮图片

    private SpecialCalendar sc = null;
    private DateAdapter dateAdapter;
    private LunarCalendar lc = null;
    private ScheduleModel model = null;

    private static ArrayList<String> scheduleDate = null;
    private ArrayList<ScheduleDateTag> dateTagList = new ArrayList<ScheduleDateTag>();
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0;   // 具体某一天是星期几
    private int weeksOfMonth = 0;
    private boolean isLeapyear = false; // 是否为闰年
    private int selectPostion = 0;
    private String dayNumbers[] = new String[7];
    private int currentYear = 0;
    private int currentMonth = 0;
    private int currentWeek = 0;
    private int currentDay = 0;
    private static int hour = -1;
    private static int minute = -1;
    private String scheduleYear = "";
    private String scheduleMonth = "";
    private String scheduleDay = "";
    private String week = "";

    private static String[] sch_type = ScheduleConstant.sch_type;
    private static String[] remind = ScheduleConstant.remind;
    private int sch_typeID = 0;   //日程类型
    private int remindID = 0;     //提醒类型
    private static String schText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal_add_schedule_view);
        SysApp.getInstance().addActivity(this);

        initView();
        Date date = new Date();
        model = new ScheduleModel(this);
        lc = new LunarCalendar();
        sc = new SpecialCalendar();
        getScheduleDate();

        currentYear = Integer.parseInt(scheduleYear);
        currentMonth = Integer.parseInt(scheduleMonth);
        currentDay = Integer.parseInt(scheduleDay);

        getCalendar(currentYear, currentMonth);
        getCurrent();

        calDate.setText(currentYear + "年" + currentMonth + "月" + currentDay + "日");

        //Toast.makeText(this, currentYear + "-" + currentMonth + "-" + currentDay + "/" + currentWeek + "-" + weeksOfMonth, Toast.LENGTH_LONG).show();
        gestureDetector = new GestureDetector(this);

        dateAdapter = new DateAdapter(this, getResources(), currentYear,
                currentMonth, currentWeek, weeksOfMonth, selectPostion,
                currentWeek == 1 ? true : false);
        addGridView();
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        selectPostion = dateAdapter.getChoosePosition(currentYear, currentMonth, currentDay);
        gridView.setSelection(selectPostion);
        topFlipper.addView(gridView, 0);

        scheduleType.setText(sch_type[0] + "\t\t\t\t" + remind[remindID]);

        if (schText != null) {
            //在选择日程类型之前已经输入了日程的信息，则在跳转到选择日程类型之前应当将日程信息保存到schText中，当返回时再次可以取得。
            scheduleText.setText(schText);
            //一旦设置完成之后就应该将此静态变量设置为空，
            schText = "";
        }

        if (hour == -1 && minute == -1) {
            hour = date.getHours();
            minute = date.getMinutes();
        }

        dateText.setText(getScheduleDate());

        returnCalendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获得日程类型
        scheduleType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                schText = scheduleText.getText().toString();
                Intent intent = new Intent();
                intent.setClass(CalAddScheduleView.this, ScheduleTypeView.class);
                intent.putExtra("sch_remind", new int[]{sch_typeID, remindID});
                startActivity(intent);
                finish();
            }
        });

        //获得时间
        dateText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new TimePickerDialog(CalAddScheduleView.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {

                        hour = hourOfDay;
                        minute = min;
                        dateText.setText(getScheduleDate());
                    }
                }, hour, minute, true).show();
            }
        });

        //保存日程信息
        scheduleSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(scheduleText.getText().toString())) {
                    //判断输入框是否为空
                    new AlertDialog.Builder(CalAddScheduleView.this).setTitle("输入日程").setMessage("日程信息不能为空").setPositiveButton("确认", null).show();
                } else {
                    //将日程信息保存
                    String showDate = handleInfo(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay), hour, minute, week, remindID);
                    ScheduleVO schedulevo = new ScheduleVO();
                    schedulevo.setScheduleTypeID(sch_typeID);
                    schedulevo.setRemindID(remindID);
                    schedulevo.setScheduleDate(showDate);
                    schedulevo.setScheduleContent(scheduleText.getText().toString());
                    //model.save(schedulevo);
                    int scheduleID = model.save(schedulevo);
                    //将scheduleID保存到数据中(因为在CalendarActivity中点击gridView中的一个Item可能会对应多个标记日程(scheduleID))
                    String[] scheduleIDs = new String[]{String.valueOf(scheduleID)};
                    Intent intent = new Intent();
                    intent.setClass(CalAddScheduleView.this, ScheduleInfoView.class);
                    intent.putExtra("scheduleID", String.valueOf(scheduleID));
                    intent.putExtra("scheduleID", scheduleIDs);
                    startActivity(intent);

                    //设置日程标记日期(将所有日程标记日期封装到list中)
                    setScheduleDateTag(remindID, scheduleYear, scheduleMonth, scheduleDay, scheduleID);
                    finish();
                }
            }
        });

    }

    public void initView(){
        calDate = (TextView) findViewById(R.id.calSchdate);
        topFlipper = (ViewFlipper) findViewById(R.id.calSchFlipper);
        returnCalendar = (ImageView) findViewById(R.id.calSchReturnButton);
        scheduleType = (TextView) findViewById(R.id.calScheduleType);
        scheduleSave = (TextView) findViewById(R.id.save);
        dateText = (TextView) findViewById(R.id.calScheduleDate);
        scheduleText = (EditText) findViewById(R.id.calScheduleText);
    }

    /**
     * 设置日程标记日期
     * using
     * @param remindID
     * @param year
     * @param month
     * @param day
     */
    public void setScheduleDateTag(int remindID, String year, String month, String day, int scheduleID) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        String d = year + "-" + month + "-" + day;
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(d));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //封装要标记的日期
        if (remindID >= 1 && remindID <= 4) {
            //"提醒一次","隔10分钟","隔30分钟","隔一小时"（只需标记当前这一天）
            ScheduleDateTag dateTag = new ScheduleDateTag();
            dateTag.setYear(Integer.parseInt(year));
            dateTag.setMonth(Integer.parseInt(month));
            dateTag.setDay(Integer.parseInt(day));
            dateTag.setScheduleID(scheduleID);
            dateTagList.add(dateTag);
        } else if (remindID == 5) {
            //每天重复(从设置的日程的开始的之后每一天多要标记)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4 * 7; i++) {
                if (i == 0) {
                    cal.add(Calendar.DATE, 0);
                } else {
                    cal.add(Calendar.DATE, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 6) {
            //每周重复(从设置日程的这天(星期几)，接下来的每周的这一天多要标记)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12 * 4; i++) {
                if (i == 0) {
                    cal.add(Calendar.WEEK_OF_MONTH, 0);
                } else {
                    cal.add(Calendar.WEEK_OF_MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 7) {
            //每月重复(从设置日程的这天(几月几号)，接下来的每月的这一天多要标记)
            for (int i = 0; i <= (2049 - Integer.parseInt(year)) * 12; i++) {
                if (i == 0) {
                    cal.add(Calendar.MONTH, 0);
                } else {
                    cal.add(Calendar.MONTH, 1);
                }
                handleDate(cal, scheduleID);
            }
        } else if (remindID == 8) {
            //每年重复(从设置日程的这天(哪一年几月几号)，接下来的每年的这一天多要标记)
            for (int i = 0; i <= 2049 - Integer.parseInt(year); i++) {
                if (i == 0) {
                    cal.add(Calendar.YEAR, 0);
                } else {
                    cal.add(Calendar.YEAR, 1);
                }
                handleDate(cal, scheduleID);
            }
        }
        //将标记日期存入数据库中
        model.saveTagDate(dateTagList);
    }

    /**
     * 日程标记日期的处理
     *
     * @param cal
     */
    public void handleDate(Calendar cal, int scheduleID) {
        ScheduleDateTag dateTag = new ScheduleDateTag();
        dateTag.setYear(cal.get(Calendar.YEAR));
        dateTag.setMonth(cal.get(Calendar.MONTH) + 1);
        dateTag.setDay(cal.get(Calendar.DATE));
        dateTag.setScheduleID(scheduleID);
        dateTagList.add(dateTag);
    }

    /**
     * 通过选择提醒次数来处理最后的显示结果
     * no using change & del
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param week
     * @param remindID
     */
    public String handleInfo(int year, int month, int day, int hour, int minute, String week, int remindID) {
        String remindType = remind[remindID];     //提醒类型
        String show = "";
        if (0 <= remindID && remindID <= 4) {
            //提醒一次,隔10分钟,隔30分钟,隔一小时
            show = year + "-" + month + "-" + day + "\t" + hour + ":" + minute + "\t" + week + "\t\t" + remindType;
        } else if (remindID == 5) {
            //每周
            show = "每周" + week + "\t" + hour + ":" + minute;
        } else if (remindID == 6) {
            //每月
            show = "每月" + day + "号" + "\t" + hour + ":" + minute;
        } else if (remindID == 7) {
            //每年
            show = "每年" + month + "-" + day + "\t" + hour + ":" + minute;
        }
        return show;
    }

    /**
     * 点击item之后，显示的日期信息
     * using
     * @return
     */
    public String getScheduleDate() {

        Intent intent = getIntent();

        if (intent.getStringArrayListExtra("scheduleDate") != null) {
            //从CalendarActivity中传来的值（包含年与日信息）
            scheduleDate = intent.getStringArrayListExtra("scheduleDate");
        }
        int[] schType_remind = intent.getIntArrayExtra("schType_remind");  //从ScheduleTypeView中传来的值(包含日程类型和提醒次数信息)

        if (schType_remind != null) {
            sch_typeID = schType_remind[0];
            remindID = schType_remind[1];
            scheduleType.setText(sch_type[sch_typeID] + "\t\t\t\t" + remind[remindID]);
        }

        // 得到年月日和星期
        scheduleYear = scheduleDate.get(0);
        scheduleMonth = scheduleDate.get(1);
        scheduleDay = scheduleDate.get(2);
        week = scheduleDate.get(3);

        if (Integer.parseInt(scheduleMonth) < 10) {
            scheduleMonth = "0" + scheduleMonth;
        }

        if (Integer.parseInt(scheduleDay) < 10) {
            scheduleDay = "0" + scheduleDay;
        }

        String hour_c = String.valueOf(hour);
        String minute_c = String.valueOf(minute);
        if (hour < 10) {
            hour_c = "0" + hour_c;
        }
        if (minute < 10) {
            minute_c = "0" + minute_c;
        }
        // 得到对应的阴历日期
        String scheduleLunarDay = getLunarDay(Integer.parseInt(scheduleYear),
                Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
        String scheduleLunarMonth = lc.getLunarMonth(); // 得到阴历的月份
        StringBuffer scheduleDateStr = new StringBuffer();
        scheduleDateStr.append(scheduleYear).append("-").append(scheduleMonth)
                .append("-").append(scheduleDay).append(" ").append(hour_c).append(":").append(minute_c).append("\n").append(
                scheduleLunarMonth).append(scheduleLunarDay)
                .append(" ").append(week);
        return scheduleDateStr.toString();
    }

    /**
     * 根据日期的年月日返回阴历日期
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public String getLunarDay(int year, int month, int day) {
        String lunarDay = lc.getLunarDate(year, month, day, true);
        // {由于在取得阳历对应的阴历日期时，如果阳历日期对应的阴历日期为"初一"，就被设置成了月份(如:四月，五月。。。等)},所以在此就要判断得到的阴历日期是否为月份，如果是月份就设置为"初一"
        if (lunarDay.substring(1, 2).equals("月")) {
            lunarDay = "初一";
        }
        return lunarDay;
    }

    /**
     * @param year
     * @param month
     */
    public int getLastDayOfWeek(int year, int month) {
        return sc.getWeekDayOfLastMonth(year, month,
                sc.getDaysOfMonth(isLeapyear, month));
    }

    public void getCalendar(int year, int month) {
        isLeapyear = sc.isLeapYear(year);                    // 是否为闰年
        daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);  // 某月的总天数
        dayOfWeek = sc.getWeekdayOfMonth(year, month);       // 某月第一天为星期几
        weeksOfMonth = sc.getWeeksOfMonth(year, month);      //该月共有多少周
        currentWeek = sc.getWeekOfMonth(currentDay, dayOfWeek);
    }

    /**
     * 重新计算当前的年月
     */
    public void getCurrent() {
        if (currentWeek > weeksOfMonth) {
            if (currentMonth + 1 <= 12) {
                currentMonth++;
            } else {
                currentMonth = 1;
                currentYear++;
            }
            currentWeek = 1;
            weeksOfMonth = sc.getWeeksOfMonth(currentYear, currentMonth);
        } else if (currentWeek == weeksOfMonth) {
            if (getLastDayOfWeek(currentYear, currentMonth) == 6) {
            } else {
                if (currentMonth + 1 <= 12) {
                    currentMonth++;
                } else {
                    currentMonth = 1;
                    currentYear++;
                }
                currentWeek = 1;
                weeksOfMonth = sc.getWeeksOfMonth(currentYear, currentMonth);
            }

        } else if (currentWeek < 1) {
            if (currentMonth - 1 >= 1) {
                currentMonth--;
            } else {
                currentMonth = 12;
                currentYear--;
            }
            weeksOfMonth = sc.getWeeksOfMonth(currentYear, currentMonth);
            currentWeek = weeksOfMonth - 1;
        }
    }

    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        gridView = new GridView(this);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CalAddScheduleView.this.gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectPostion = position;
                dateAdapter.setSeclection(position);
                dateAdapter.notifyDataSetChanged();
                calDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                        + dateAdapter.getCurrentMonth(selectPostion) + "月"
                        + dayNumbers[position] + "日");
            }
        });
        gridView.setLayoutParams(params);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int gvFlag = 0;
        if (e1.getX() - e2.getX() > 80) {
            // 向左滑
            addGridView();
            currentWeek++;
            getCurrent();
            dateAdapter = new DateAdapter(this, getResources(), currentYear,
                    currentMonth, currentWeek, weeksOfMonth, selectPostion,
                    currentWeek == 1 ? true : false);
            dayNumbers = dateAdapter.getDayNumbers();
            gridView.setAdapter(dateAdapter);
            calDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                    + dateAdapter.getCurrentMonth(selectPostion) + "月"
                    + dayNumbers[selectPostion] + "日");
            gvFlag++;
            topFlipper.addView(gridView, gvFlag);
            dateAdapter.setSeclection(selectPostion);
            this.topFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_left_in));
            this.topFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_left_out));
            this.topFlipper.showNext();
            topFlipper.removeViewAt(0);
            return true;

        } else if (e1.getX() - e2.getX() < -80) {
            addGridView();
            currentWeek--;
            getCurrent();
            dateAdapter = new DateAdapter(this, getResources(), currentYear,
                    currentMonth, currentWeek, weeksOfMonth , selectPostion,
                    currentWeek == 1 ? true : false);
            dayNumbers = dateAdapter.getDayNumbers();
            gridView.setAdapter(dateAdapter);
            calDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                    + dateAdapter.getCurrentMonth(selectPostion) + "月"
                    + dayNumbers[selectPostion] + "日");
            gvFlag++;
            topFlipper.addView(gridView, gvFlag);
            dateAdapter.setSeclection(selectPostion);
            this.topFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_right_in));
            this.topFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.push_right_out));
            this.topFlipper.showPrevious();
            topFlipper.removeViewAt(0);
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //jumpWeek = 0;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

}
