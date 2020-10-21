package org.suyueqiuliang.pilipili;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    EditText searchBar;
    boolean wasLogin = false;
    UserData userData = null;
    UserInformation userInformation = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        requestMyPermissions();

        //创建一个工具类对象
        final ToolClass toolClass = new ToolClass(MainActivity.this);

        //控件变量
        final NavigationView navView = findViewById(R.id.nav_view);
        final View navHead = navView.getHeaderView(0);
        final ImageView userHead = navHead.findViewById(R.id.user_head);
        final TextView userName = navHead.findViewById(R.id.user_name);
        searchBar = findViewById(R.id.search_bar);

        //侧边栏和状态栏初始化

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        @SuppressLint("ResourceType") ColorStateList csl= getColorStateList(R.animator.navigation_menu_item_color);
        navView.setItemTextColor(csl);
        navView.setItemIconTintList(csl);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getColor(R.color.colorAccent));



        //尝试从本地读取数据登陆
        userData = toolClass.readUserInfo();

        if(userData!=null){
            login(toolClass,userHead,userName);
        }

        //监听事件
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.clearFocus();
                if (!wasLogin) {
                    AlertDialog.Builder customizeDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    final View dialogView = LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.loging_dialog, null);
                    customizeDialog.setView(dialogView);
                    AlertDialog alertDialog = customizeDialog.show();
                    final ProgressBar loggingProgressBar = alertDialog.findViewById(R.id.logging_progressBar);
                    Button loggingButton = alertDialog.findViewById(R.id.logging_button);
                    final EditText inputUserName = alertDialog.findViewById(R.id.input_user_name);
                    final EditText inputUserPassword = alertDialog.findViewById(R.id.input_user_password);
                    final TextView loginMessage = alertDialog.findViewById(R.id.login_message);
                    loggingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        String user_name = inputUserName.getText().toString();
                                        String user_password = inputUserPassword.getText().toString();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loggingProgressBar.setVisibility(View.VISIBLE);
                                                inputUserName.setFocusable(false);
                                                inputUserPassword.setFocusable(false);
                                            }
                                        });
                                        String loginInfo = toolClass.login(user_name,user_password);
                                        final JSONObject jsonObject = new JSONObject(loginInfo);
                                        if(jsonObject.has("message")){
                                        final String message = jsonObject.getString("message");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginMessage.setText(message);
                                                loggingProgressBar.setVisibility(View.GONE);
                                                inputUserName.setFocusable(true);
                                                inputUserPassword.setFocusable(true);
                                            }
                                        });
                                        }else{
                                            JSONObject data = jsonObject.getJSONObject("data");
                                            toolClass.saveUserInfo(data);
                                            userData = toolClass.readUserInfo();
                                            login(toolClass,userHead,userName);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    });
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = toolClass.getUserFaceBitmap(userInformation.face);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Dialog mDialog = TransparentDialog.createLoadingDialog(MainActivity.this,bitmap,userInformation);
                                    mDialog.setCancelable(true);
                                    mDialog.show();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    private void requestMyPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }


















    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            searchBar.clearFocus();
        }
    }

    private void login (final ToolClass toolClass, final ImageView userHead, final TextView userName){
        new Thread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            public void run() {
                userInformation = toolClass.getUserInfo(userData);
                if(userInformation != null){
                    wasLogin = true;
                    final Bitmap bitmap = toolClass.getUserFaceBitmap(userInformation.face);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userHead.setImageBitmap(bitmap);
                            userName.setText(userInformation.name);
                            //userName.setTextColor(Color.parseColor(userInformation.nickname_color));
                        }
                    });
                }
                else {
                    wasLogin = false;
                    userData = null;
                    userInformation = null;
                    userHead.setImageDrawable(getDrawable(R.drawable.test_head));
                    userName.setText(getResources().getText(R.string.login_message));
                    toolClass.logout();
                }
            }
        }).start();
    }










    private void toast(String s){
        System.out.println(s);
    }
}