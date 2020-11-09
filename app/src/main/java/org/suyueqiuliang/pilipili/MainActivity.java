package org.suyueqiuliang.pilipili;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.suyueqiuliang.pilipili.tool.LevelWalletInfo;
import org.suyueqiuliang.pilipili.tool.LoginAccess;
import org.suyueqiuliang.pilipili.tool.ToolClass;
import org.suyueqiuliang.pilipili.tool.UserData;
import org.suyueqiuliang.pilipili.tool.UserInformation;
import org.suyueqiuliang.pilipili.tool.loginWithStorageDataReturnInfo;
import org.suyueqiuliang.pilipili.tool.video;
import org.suyueqiuliang.pilipili.ui.home.HomeFragment;
import org.suyueqiuliang.pilipili.ui.home.HomeVideoCardRecyclerViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText searchBar;
    static ArrayList<video> videos;
    ToolClass toolClass;
    HomeFragment homeFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        ArrayList<video> videos = new ArrayList<>();
        requestMyPermissions();

        //创建一个工具类对象
        toolClass = new ToolClass(MainActivity.this);
        homeFragment = new HomeFragment();
        //控件变量
        final NavigationView navView = findViewById(R.id.nav_view);
        final View navHead = navView.getHeaderView(0);
        final ImageView userHead = navHead.findViewById(R.id.user_head);
        final ImageView refreshImage = findViewById(R.id.refresh_button);
        final TextView userName = navHead.findViewById(R.id.user_name);
        final androidx.constraintlayout.widget.ConstraintLayout constraintLayout = findViewById(R.id.container);
        searchBar = findViewById(R.id.search_bar);
        //侧边栏和状态栏初始化
        //hideSystemNavigationBar();
        ViewGroup.LayoutParams params = navView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 5; //屏幕的三分之一
        navView.setLayoutParams(params);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginWithStorageDataReturnInfo loginWithStorageDataReturnInfo = toolClass.loginWithStorageData();
                if(loginWithStorageDataReturnInfo.equals(org.suyueqiuliang.pilipili.tool.loginWithStorageDataReturnInfo.ok)){
                    showUserInformation(toolClass,userHead,userName);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();


        //监听事件
        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.flushRecycler();
            }
        });
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.clearFocus();
                if (!toolClass.wasLogin()) {
                    AlertDialog.Builder customizeDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    final View dialogView = LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.loging_dialog, null);
                    customizeDialog.setView(dialogView);
                    final AlertDialog alertDialog = customizeDialog.show();
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
                                    final String loginReturn = toolClass.login(user_name,user_password);
                                    if(loginReturn == null)
                                        alertDialog.cancel();
                                    else if(loginReturn.equals("true")){

                                    }
                                    else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginMessage.setText(loginReturn);
                                                loggingProgressBar.setVisibility(View.GONE);
                                                inputUserName.setFocusable(true);
                                                inputUserPassword.setFocusable(true);
                                            }
                                        });
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
                            final LoginAccess loginAccess = toolClass.getUserInformation();
                            if(loginAccess.access){
                                final Bitmap bitmap = toolClass.getUrlImageBitmap(loginAccess.userInformation.face);
                                final LevelWalletInfo levelWalletInfo = toolClass.getUserLevelWalletInfo();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Dialog mDialog = TransparentDialog.createLoadingDialog(MainActivity.this,bitmap,loginAccess.userInformation,levelWalletInfo);
                                        mDialog.setCancelable(true);
                                        TextView textView = mDialog.findViewById(R.id.user_information_dialog_exit);
                                        textView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                logout(toolClass,userHead,userName);
                                                mDialog.cancel();
                                            }
                                        });
                                        mDialog.show();
                                    }
                                });
                            }
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

    private void showUserInformation (final ToolClass toolClass, final ImageView userHead, final TextView userName){
        LoginAccess loginAccess = toolClass.getUserInformation();
        if(loginAccess.access){
            final UserInformation userInformation = loginAccess.userInformation;
            final Bitmap faceBitmap = toolClass.getUrlImageBitmap(userInformation.face);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userName.setText(userInformation.name);
                    userHead.setImageBitmap(faceBitmap);
                }
            });
        }
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,getString(R.string.login_success_but_get_information_failed),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean logout(final ToolClass toolClass, final ImageView userHead, final TextView userName){
        if(toolClass.wasLogin()){
            toolClass.logout();
            userHead.setImageResource(R.drawable.avatar_square_grey);
            userName.setText(getString(R.string.not_login_message));
            return true;
        }
        else return false;
    }
    private void hideSystemNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    private void toast(String s){
        System.out.println(s);
    }
}
