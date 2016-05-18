package com.android.oneday.activity.SettingActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.PasswordActivity.EasyPwdActivity;
import com.android.oneday.activity.PasswordActivity.SudokuActivity;
import com.android.oneday.constant.SettingConstant;

import com.android.oneday.R;
import com.android.oneday.util.SysApp;

import java.util.LinkedList;
import java.util.List;

public class SettingPageActivity extends BaseActivity {

    private ImageView returnButton = null;
    private ListView setListView = null;

    private List<BaseActivity> mList = new LinkedList<BaseActivity>();
    private static String[] setList = SettingConstant.set_list;
    private Adapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        initView();
        initData();
        chooseReturn();
        chooseItem();
    }

    private void initView(){
        this.returnButton = (ImageView) findViewById(R.id.setReturnButton);
        this.setListView = (ListView) findViewById(R.id.setListView);
    }

    private void initData(){
        this.setListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, setList));
    }

    private void chooseReturn(){
        this.returnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void chooseItem(){
        this.setListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingPageActivity.this);
                        builder.setTitle("选择密码方式");
                        //    指定下拉列表的显示数据
                        final String[] pwdList = SettingConstant.pwd_list;

                        //    设置一个下拉的列表选择项
                        builder.setItems(pwdList, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent();
                                switch (which){
                                    case 0:
                                        intent.setClass(SettingPageActivity.this, EasyPwdActivity.class);
                                        break;
                                    case 1:
                                        intent.setClass(SettingPageActivity.this, SudokuActivity.class);
                                        break;
                                }
                                startActivity(intent);
                                finish();
                                Toast.makeText(SettingPageActivity.this, "选择密码方式：" , Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                        break;
                    case 1:
                        SysApp.getInstance().exit();
                        break;
                }
            }
        });
    }
}
