package com.android.oneday.activity.PasswordActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.oneday.R;
import com.android.oneday.activity.Base.MyApplication;
import com.android.oneday.activity.MainPageActivity.MainPageActivity;
import com.android.oneday.db.PwdModel;

import static com.android.oneday.util.StringUtils.MD5;

public class EasyPwdActivity extends Activity {

    private MyApplication application = null;
    private PwdModel pwdModel = null;

    private TextView pwdTextTopTab = null;
    private EditText easyPwd = null;
    private EditText reEasyPwd = null;
    private Button submitButton = null;
    private Drawable OkIcon = null;
    private Drawable ErrorIcon = null;

    private boolean passwordBool = false, rePawBool = false;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_easy_pwd);
        application = (MyApplication) getApplication();
        pwdModel = new PwdModel(this);
        initView();
        clickSubmit();
    }

    private void initView(){
        this.OkIcon = getResources().getDrawable(R.drawable.ok_icon);
        this.ErrorIcon = getResources().getDrawable(R.drawable.error_icon);
        OkIcon.setBounds(0, 0, OkIcon.getMinimumWidth(), OkIcon.getMinimumHeight()); //设置边界
        ErrorIcon.setBounds(0, 0, ErrorIcon.getMinimumWidth(), ErrorIcon.getMinimumHeight()); //设置边界
        this.pwdTextTopTab = (TextView) findViewById(R.id.pwdTextTopTab);
        this.easyPwd = (EditText) findViewById(R.id.easyPwd);
        this.reEasyPwd = (EditText) findViewById(R.id.reEasyPwd);
        this.submitButton = (Button) findViewById(R.id.submitButton);
        this.easyPwd.setOnFocusChangeListener(new passwordOnFocusChangeListenerImpl());
        this.reEasyPwd.setOnFocusChangeListener(new rePawOnFocusChangeListenerImpl());

        if (application.isLocked) {
            pwdTextTopTab.setText("输入密码");
            reEasyPwd.setVisibility(View.GONE);
        }else{
            pwdTextTopTab.setText("设置密码");
            reEasyPwd.setVisibility(View.VISIBLE);
        }
    }

    private void clickSubmit(){
        if (application.isLocked) {
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    password = easyPwd.getText().toString();
                    password = MD5(password);
                    boolean isPwd = pwdModel.checkPwd(0, password);
                    if (password != null && isPwd) {
                        Toast.makeText(EasyPwdActivity.this, "密码正确！", Toast.LENGTH_SHORT).show();
                        application.isLocked = false;
                        EasyPwdActivity.this.finish();
                    } else {
                        Toast.makeText(EasyPwdActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                        easyPwd.setText("");
                    }
                }
            });
        }else{
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (passwordBool && rePawBool){
                        password = MD5(password);
                        pwdModel.savePwd(0, password);
                        pwdModel.destoryDB();
                        Intent intent = new Intent();
                        intent.setClass(EasyPwdActivity.this, MainPageActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(EasyPwdActivity.this, "请确认信息是否填写完整", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    public void EasyPwdOnClick(View view) {
        switch (view.getId()) {
            case R.id.easyPwd:
                easyPwd.setText("");
                easyPwd.setTextColor(Color.parseColor("#ffa89d87"));
                easyPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                easyPwd.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.reEasyPwd:
                reEasyPwd.setText("");
                reEasyPwd.setTextColor(Color.parseColor("#ffa89d87"));
                reEasyPwd.setCompoundDrawables(null, null, null, null);
                reEasyPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
    }

    private class passwordOnFocusChangeListenerImpl implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view,boolean focus){
            if (view.getId() ==  easyPwd.getId()){
                if (!focus){
                    passwordBool = false;
                    easyPwd.setHintTextColor(Color.parseColor("#FF0000"));
                    if ( easyPwd.getText().length() == 0){
                        easyPwd.setCompoundDrawables(null, null, ErrorIcon, null);//画在右边
                        easyPwd.setText("");
                        easyPwd.setHint("密码不能为空");
                    }else if ( easyPwd.getText().length() < 6){
                        easyPwd.setCompoundDrawables(null, null, ErrorIcon, null);//画在右边
                        easyPwd.setText("");
                        easyPwd.setHint("长度不能小于6个字符");
                    }else if (!checkPassword(easyPwd.getText().toString())) {
                        easyPwd.setCompoundDrawables(null, null, ErrorIcon, null);//画在右边
                        easyPwd.setText("");
                        easyPwd.setHint("只能由数字和字母组成");
                    }else{
                        passwordBool = true;
                        easyPwd.setCompoundDrawables(null, null, OkIcon, null);//画在右边
                        easyPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        easyPwd.setTextColor(Color.parseColor("#ffa89d87"));
                    }
                }
            }
        }
    }

    private class rePawOnFocusChangeListenerImpl implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view,boolean focus){
            if (view.getId() ==  reEasyPwd.getId()){
                if (!focus){
                    rePawBool = false;
                    String rePawC =  reEasyPwd.getText().toString();
                    String passwordC =  easyPwd.getText().toString();
                    if (!rePawC.equals(passwordC)) {
                        reEasyPwd.setCompoundDrawables(null, null, ErrorIcon, null);//画在右边
                        reEasyPwd.setHintTextColor(Color.parseColor("#FF0000"));
                        reEasyPwd.setText("");
                        reEasyPwd.setHint("两次密码不一致");
                    }else{
                        rePawBool = true;
                        password = reEasyPwd.getText().toString();
                        reEasyPwd.setCompoundDrawables(null, null, OkIcon, null);//画在右边
                    }
                }
            }
        }
    }

    private boolean checkPassword(String s){
        return s.matches("^[a-zA-Z0-9]{6,20}");       //数字和字母
    }

}
