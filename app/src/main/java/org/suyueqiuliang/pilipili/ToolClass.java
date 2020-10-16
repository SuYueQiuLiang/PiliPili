package org.suyueqiuliang.pilipili;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

class UserData{
    String DedeUserID,DedeUserID__ckMd5,Expires,SESSDATA,bili_jct;
}

class LevelInfo{
    //当前等级，当前等级最低经验，当前经验，当前等级最高经验
    int current_level,current_min,current_exp,next_exp;
}

class UserInformation{
    LevelInfo levelInfo;
    //头像url，用户uid，用户昵称
    String face,mid,uname;
    //硬币数量，节操值（诚信值，70封顶），是否大会员，b币数量
    int money,moral,vipStatus,wallet;
}

class LoginKey{
    String hash,RSAPublicKey;
    public LoginKey(String hash,String RSAPublicKey){
        this.hash = hash;
        this.RSAPublicKey = RSAPublicKey;
    }
}

public class ToolClass {
    private String localFilePath;
    private Context context;
    private final String appkey = "1d8b6e7d45233436";
    private final String appSecKey = "560c52ccd288fed045859ed18bffd973";
    private String app_head;
    private String api_head;
    private final String getKey = "https://passport.bilibili.com/api/oauth2/getKey";
    public ToolClass(Context context){
        this.context = context;
        this.localFilePath = context.getExternalFilesDir("res")+"/";
    }
    public LoginKey getLoginKey(){
        try {
            JSONObject jsonObject = new JSONObject(urlPostRequest( getKey , getSign("appkey=" + appkey)));
            jsonObject = jsonObject.getJSONObject("data");
            LoginKey loginKey = new LoginKey(jsonObject.getString("hash"),jsonObject.getString("key"));
            return loginKey;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveUserInfo(String url) {
        try {
            UserData userData = new UserData();
            String params = url.substring(url.indexOf("?") + 1);
            userData.DedeUserID = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1);
            userData.DedeUserID__ckMd5 = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1);
            userData.Expires = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1);
            userData.SESSDATA = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            params = params.substring(params.indexOf("&") + 1);
            userData.bili_jct = params.substring(params.indexOf("=") + 1,params.indexOf("&"));
            byte[] saveData = (userData.DedeUserID + "\n" + userData.DedeUserID__ckMd5 + "\n" + userData.Expires +"\n" + userData.SESSDATA + "\n" + userData.bili_jct).getBytes();
            OutputStream outputStream = new FileOutputStream(localFilePath + "userInfo.inf");
            outputStream.write(saveData);
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public UserData readUserInfo(){
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
    public Bitmap makeQrCore(String string){
        Bitmap bitmap = null;
        try {
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix;
            bitMatrix = new MultiFormatWriter().encode(string, BarcodeFormat.QR_CODE, 300, 300, hints);
            final int width=300,height=300;
            final int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
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
    public boolean testLogin(String SESSDATA) {
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL("https://api.bilibili.com/x/web-interface/nav");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("Cookie", "SESSDATA=" + SESSDATA);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
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
    public boolean logout(){
        File file = new File(localFilePath + "userInfo.inf");
        final boolean delete = file.delete();
        return true;
    }
    public UserInformation getUserSelfInformation(String SESSDATA){
        UserInformation userInformation = new UserInformation();
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL("https://api.bilibili.com/x/web-interface/nav");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("Cookie", "SESSDATA=" + SESSDATA);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            System.out.println(document);
            JSONObject jsonObject = new JSONObject(document.toString());
            JSONObject data = jsonObject.getJSONObject("data");
            userInformation.face = data.getString("face");
            userInformation.mid = data.getString("mid");
            userInformation.money = data.getInt("money");
            userInformation.moral = data.getInt("moral");
            userInformation.uname = data.getString("uname");
            userInformation.vipStatus = data.getInt("vipStatus");
            JSONObject wallet = data.getJSONObject("wallet");
            userInformation.wallet = wallet.getInt("bcoin_balance");
            JSONObject level_info = data.getJSONObject("level_info");
            LevelInfo levelInfo = new LevelInfo();
            levelInfo.current_level = level_info.getInt("current_level");
            levelInfo.current_min = level_info.getInt("current_min");
            levelInfo.current_exp = level_info.getInt("current_level");
            levelInfo.next_exp = level_info.getInt("next_exp");
            userInformation.levelInfo = levelInfo;
            return userInformation;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Bitmap getUserFaceBitmap(String faceUrl){
        try {
            URL url = new URL(faceUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);//不缓存
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();//获得图片的数据流
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return  bmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String urlPostRequest(String urlStr,String postData) {
        StringBuilder document = new StringBuilder();
        try {
            //链接URL
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false); //不使用缓存
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            out.write(postData);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            out.close();
            in.close();
            return document.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String urlPostRequestWithCookie(String urlStr,String Cookie,String postData) {
        StringBuilder document = new StringBuilder();
        try {
            //链接URL
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie",Cookie);
            connection.setUseCaches(false); //不使用缓存
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            out.write(postData);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            out.close();
            in.close();
            return document.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String urlGetRequestWithCookie(String urlStr,String Cookie) {
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("Cookie",Cookie);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            return document.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String urlGetRequest(String urlStr){
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            return document.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String encrypt( String str) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64("-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjb4V7EidX/ym28t2ybo0U6t0n\n" +
                "6p4ej8VjqKHg100va6jkNbNTrLQqMCQCAYtXMXXp2Fwkk6WR+12N9zknLjf+C9sx\n" +
                "/+l48mjUU8RqahiFD1XT/u2e0m2EN029OhCgkHx3Fc/KlFSIbak93EH/XlYis0w+\n" +
                "Xl69GV6klzgxW6d2xQIDAQAB\n" +
                "-----END PUBLIC KEY-----");
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }
    public String getSign(String parameter){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update((parameter + context.getString(R.string.appsec)).getBytes());
            byte[] byteArray = md5.digest();
            BigInteger bigInt = new BigInteger(1, byteArray);
            // 参数16表示16进制
            String result = bigInt.toString(16);
            // 不足32位高位补零
            while (result.length() < 32) {
                result = "0" + result;
            }
            return parameter + "&sign=" + result;
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean isNetworking() throws IOException {
        int timeOut = 3000;
        boolean status = InetAddress.getByName("www.bilibili.com").isReachable(timeOut);
        return status;
    }
}
