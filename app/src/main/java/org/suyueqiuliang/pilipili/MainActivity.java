package org.suyueqiuliang.pilipili;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

class UserData{
    String DedeUserID,DedeUserID__ckMd5,Expires,SESSDATA,bili_jct;
}

public class MainActivity extends AppCompatActivity {

    boolean wasLogin=false,keepLoginListen=false;
    String localFilePath;
    UserData userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //申请权限

        requestMyPermissions();

        //控件变量

        localFilePath = MainActivity.this.getExternalFilesDir("res")+"/";
        toast(localFilePath);
        NavigationView navView = findViewById(R.id.nav_view);
        View navHead = navView.getHeaderView(0);
        ImageView userHead = navHead.findViewById(R.id.user_head);

        //侧边栏和状态栏初始化

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        Resources resource= getBaseContext().getResources();
        @SuppressLint("ResourceType") ColorStateList csl= getColorStateList(R.animator.navigation_menu_item_color);
        navView.setItemTextColor(csl);
        navView.setItemIconTintList(csl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));

        //尝试从本地读取数据登陆
        userData = readUserInfo();
        if(userData!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    wasLogin = testLogin(userData.SESSDATA);
                    if(wasLogin);
                }
            }).start();
        }

        //监听事件
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wasLogin) {
                    AlertDialog.Builder customizeDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    final View dialogView = LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.loging_dialog, null);
                    customizeDialog.setView(dialogView);

                    final ProgressBar loggingProgressBar = dialogView.findViewById(R.id.logging_progressBar);
                    Button loggingButton = dialogView.findViewById(R.id.logging_button);
                    final EditText inputUserName = dialogView.findViewById(R.id.input_user_name);
                    final EditText inputUserPassword = dialogView.findViewById(R.id.input_user_password);
                    final ImageView qr_core = dialogView.findViewById(R.id.qr_code);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String apiReturn = getNoContentAPIReturn("https://passport.bilibili.com/qrcode/getLoginUrl");
                                JSONObject jsonObject = new JSONObject(apiReturn);
                                JSONObject newJsonObject = jsonObject.getJSONObject("data");
                                String url = newJsonObject.getString("url");
                                String oauthKey = newJsonObject.getString("oauthKey");
                                final Bitmap bitmap = makeQrCore(url);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        qr_core.setImageBitmap(bitmap);
                                    }
                                });
                                System.out.println(oauthKey);
                                keepLoginListen = true;
                                String LoginInfoReturn;
                                JSONObject LoginInfoJsonObject;
                                while (keepLoginListen && !wasLogin) {
                                    LoginInfoReturn = onLoginListener(oauthKey);
                                    LoginInfoJsonObject = new JSONObject(LoginInfoReturn);
                                    boolean status = LoginInfoJsonObject.getBoolean("status");
                                    wasLogin = status;
                                    if (wasLogin) {
                                        toast("已扫描确认!");
                                        JSONObject userInfoJsonObject = LoginInfoJsonObject.getJSONObject("data");
                                        String userInfoUrl = userInfoJsonObject.getString("url");
                                        saveUserInfo(userInfoUrl);
                                    }
                                    Thread.sleep(1000);
                                }

                            } catch (JSONException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    loggingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String userName = inputUserName.getText().toString();
                                    String userPassword = inputUserPassword.getText().toString();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loggingProgressBar.setVisibility(View.VISIBLE);
                                            inputUserName.setFocusable(false);
                                            inputUserPassword.setFocusable(false);
                                        }
                                    });
                                    System.out.println("password after encrypt:" + encryption(userPassword));
                                }
                            }).start();
                        }
                    });
                    customizeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            keepLoginListen = false;
                        }
                    });
                    customizeDialog.show();

                }
            }
        });
    }

    //一般无正文api获取返回值

    private String getNoContentAPIReturn(String urlStr){
        StringBuffer document = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                document.append(line);
            }
            reader.close();
            System.out.println(document);
        }catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document.toString();
    }

    //带正文api获取返回值

    private String onLoginListener(String oauthKey) {
        StringBuffer document = new StringBuffer();
        try {
            //链接URL
            URL url = new URL("https://passport.bilibili.com/qrcode/getLoginInfo");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST"); //发送请求的方式
            connection.setUseCaches(false); //不使用缓存
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded"); //设置服务器解析数据的方式
            connection.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            out.write("oauthKey=" + oauthKey);
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;

            while ((line = in.readLine()) != null) {
                    document.append(line);
            }
            toast(document.toString());

            out.close();
            in.close();
        }catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document.toString();
    }

    //使用String生成二维码

    private Bitmap makeQrCore(String string){
        Bitmap bitmap = null;
        try {
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = null;
            bitMatrix = new MultiFormatWriter().encode(string, BarcodeFormat.QR_CODE, 300, 300, hints);

            final int width=300,height=300;
            final int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] =  Color.BLACK;
                    } else {
                        pixels[y * width + x] =  Color.WHITE;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private boolean saveUserInfo(String url) {
        try {
            UserData userData = new UserData();
            String params = url.substring(url.indexOf("?") + 1, url.length());
            userData.DedeUserID = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1, params.length());
            userData.DedeUserID__ckMd5 = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1, params.length());
            userData.Expires = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1, params.length());
            userData.SESSDATA = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1, params.length());
            userData.bili_jct = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            byte[] saveData = (userData.DedeUserID + "\n" + userData.DedeUserID__ckMd5 + "\n" + userData.Expires +"\n" + userData.SESSDATA + "\n" + userData.bili_jct).getBytes();
            OutputStream outputStream = new FileOutputStream(localFilePath + "userInfo.inf");
            outputStream.write(saveData);
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private UserData readUserInfo(){
        UserData userData = new UserData();
        try {
            InputStream inputStream = new FileInputStream(localFilePath + "userInfo.inf");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            userData.DedeUserID = bufferedReader.readLine();
            userData.DedeUserID__ckMd5 = bufferedReader.readLine();
            userData.Expires = bufferedReader.readLine();
            userData.SESSDATA = bufferedReader.readLine();
            userData.bili_jct = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return userData;
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

    private boolean testLogin(String SESSDATA) {
        StringBuffer document = new StringBuffer();
        try {
            URL url = new URL("https://api.bilibili.com/x/web-interface/nav");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("Cookie", "SESSDATA=" + SESSDATA);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            JSONObject jsonObject = new JSONObject(document.toString());
            JSONObject newJsonObject = jsonObject.getJSONObject("data");
            return newJsonObject.getBoolean("isLogin");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showUserFace(){

    }




























    private void toast(String s){
        System.out.println(s);
    }

    private String encryption(String password){
        try {
            String returnString = getNoContentAPIReturn("https://passport.bilibili.com/login?act=getkey");
            int n1 = 0, n2, i = 3;
            String hash, publicKey;
            while (i-- != 0) {
                n1 = returnString.indexOf('\"', n1 + 1);
            }
            n2 = returnString.indexOf('\"', n1 + 1);
            hash = returnString.substring(n1 + 1, n2);
            n1 = returnString.indexOf("\":\"",n2);
            n2 = returnString.indexOf('"',n1+3);
            publicKey = returnString.substring(n1+31, n2-28);
            publicKey = publicKey.replace("\\n","");
            System.out.println("RAS publicKey:" + publicKey);
            hash += password;
            return encrypt(hash,publicKey);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }


}