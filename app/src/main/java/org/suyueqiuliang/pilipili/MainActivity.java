package org.suyueqiuliang.pilipili;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //控件变量

        NavigationView navView = findViewById(R.id.nav_view);
        View navHead = navView.getHeaderView(0);
        ImageView userHead = navHead.findViewById(R.id.user_head);

        //加密字符串

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

        //监听事件
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder customizeDialog =
                        new AlertDialog.Builder(MainActivity.this);
                final View dialogView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.loging_dialog,null);
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
                            JSONObject newob = jsonObject.getJSONObject("data");
                            String url = newob.getString("url");
                            String oauthKey = newob.getString("oauthKey");
                            final Bitmap bitmap = makeQrCore(url);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    qr_core.setImageBitmap(bitmap);
                                }
                            });
                        } catch (JSONException e) {
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
                customizeDialog.show();
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

    private String getWithContentAPIReturn(String urlStr,String content) {
// TODO Auto-generated method stub
        StringBuffer document = new StringBuffer();
        boolean status = false;
        try {
            //链接URL
            URL url = new URL(urlStr);
            //返回结果集
            //创建链接
            URLConnection conn = url.openConnection();
            //读取返回结果集
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setUseCaches(false);//设置不要缓存
            OutputStreamWriter out =out = new OutputStreamWriter(conn.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            out.write(content);
            out.flush();;
            while ((line = reader.readLine()) != null) {
                document.append(line);
            }
            toast(document.toString());
            reader.close();
            out.close();
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
                        pixels[y * width + x] =  Color.BLACK;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] =  Color.WHITE;// 白色色块像素设置
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