package org.suyueqiuliang.pilipili;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class UserData{
    //expires_in到期时间
    String access_token,refresh_token,expires_in,bili_jct,Expires,DedeUserID,DedeUserID__ckMd5,sid,SESSDATA;
}

class LevelInfo{
    public LevelInfo(int current_level, int current_min, int current_exp, int next_exp) {
        this.current_level = current_level;
        this.current_min = current_min;
        this.current_exp = current_exp;
        this.next_exp = next_exp;
    }
    //当前等级，当前等级最低经验，当前经验，当前等级最高经验
    int current_level,current_min,current_exp,next_exp;
}

class UserInformation{
    public UserInformation(String mid, String name, String sign, int coins, String face, int sex, int level, boolean vip,String nickname_color) {
        this.mid = mid;
        this.name = name;
        this.sign = sign;
        this.coins = coins;
        this.face = face;
        this.sex = sex;
        this.level = level;
        this.vip = vip;
        this.nickname_color = nickname_color;
    }
    //mid，用户名，签名，头像url，性别
    String mid,name,sign,face,nickname_color;
    //硬币，性别（0保密，1男，2女），等级
    int coins,sex,level;
    boolean vip;
}

class LoginKey{
    String hash,RSAPublicKey;
    public LoginKey(String hash,String RSAPublicKey){
        this.hash = hash;
        this.RSAPublicKey = RSAPublicKey;
    }
}
class UrlReply{
    public UrlReply(String json, String cookie) {
        this.json = json;
        this.cookie = cookie;
    }
    String json,cookie;
}
public class ToolClass {
    private final String localFilePath;
    private final String appKey = "4409e2ce8ffd12b8";
    private final String appSecKey = "59b43e04ad6965f34319062b478f83dd";

    private final String app_head = "http://app.bilibili.com";
    private final String api_head = "http://api.bilibili.com";
    private final String passportHead = "http://passport.bilibili.com";


    private final String getKeyUrl = passportHead + "/api/oauth2/getKey";
    private final String loginUrl = passportHead + "/api/v3/oauth2/login";
    private final String userInfo = app_head + "/x/v2/account/myinfo";
    public ToolClass(Context context){
        this.localFilePath = context.getExternalFilesDir("res")+"/";
    }
    public String login(String username, String password){
        try {
            UrlReply urlReply = urlPostRequest(getKeyUrl , getSign("appkey=" + appKey));
            saveSid(getCookie(urlReply.cookie,"sid"));
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            LoginKey loginKey = new LoginKey(jsonObject.getString("hash"),subStringRSAPublicKey(jsonObject.getString("key")));
            Log.d("RSAPublicKey",loginKey.RSAPublicKey);
            UrlReply urlReply1 = urlPostRequestWithCookie(loginUrl,"sid=" + readSid(),getSign("appkey=" + URLEncoder.encode(appKey,"UTF-8") + "&mobi_app=android&password=" + URLEncoder.encode(encrypt(password, loginKey),"UTF-8")  + "&platform=android&ts=" + URLEncoder.encode(String.valueOf(System.currentTimeMillis()/1000),"UTF-8") + "&username="+URLEncoder.encode(username,"UTF-8")));
            Log.d("loginReturn",urlReply1.json);
            return urlReply1.json;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String loginWithIdentifyingCode(String username,String password){
        try {
            UrlReply urlReply = urlPostRequest(getKeyUrl , getSign("appkey=" + appKey));
            saveSid(getCookie(urlReply.cookie,"sid"));
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            LoginKey loginKey = new LoginKey(jsonObject.getString("hash"),subStringRSAPublicKey(jsonObject.getString("key")));
            Log.d("RSAPublicKey",loginKey.RSAPublicKey);
            UrlReply urlReply1 = urlPostRequestWithCookie(loginUrl,"sid=" + readSid(),getSign("appkey=" + URLEncoder.encode(appKey,"UTF-8") + "&mobi_app=android&password=" + URLEncoder.encode(encrypt(password, loginKey),"UTF-8")  + "&platform=android&ts=" + URLEncoder.encode(String.valueOf(getCurrentTimeMillis()),"UTF-8") + "&username="+URLEncoder.encode(username,"UTF-8")));
            Log.d("loginReturn",urlReply1.json);
            return urlReply1.json;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean saveUserInfo(JSONObject jsonObject) {
        try {
            UserData userData = new UserData();
            userData.access_token = jsonObject.getJSONObject("token_info").getString("access_token");
            userData.refresh_token = jsonObject.getJSONObject("token_info").getString("refresh_token");
            userData.expires_in = jsonObject.getJSONObject("token_info").getString("expires_in");
            userData.bili_jct = jsonObject.getJSONObject("cookie_info").getJSONArray("cookies").getJSONObject(0).getString("value");
            userData.Expires = jsonObject.getJSONObject("cookie_info").getJSONArray("cookies").getJSONObject(0).getString("expires");
            userData.DedeUserID = jsonObject.getJSONObject("cookie_info").getJSONArray("cookies").getJSONObject(1).getString("value");
            userData.DedeUserID__ckMd5 = jsonObject.getJSONObject("cookie_info").getJSONArray("cookies").getJSONObject(2).getString("value");
            userData.sid = jsonObject.getJSONObject("cookie_info").getJSONArray("cookies").getJSONObject(3).getString("value");
            userData.SESSDATA = jsonObject.getJSONObject("cookie_info").getJSONArray("cookies").getJSONObject(4).getString("value");
            byte[] saveData = (userData.access_token + "\n" + userData.refresh_token + "\n" + userData.expires_in +"\n"
                    + userData.bili_jct + "\n" + userData.Expires + "\n" + userData.DedeUserID + "\n" + userData.DedeUserID__ckMd5 + "\n"
                    + userData.sid + "\n" + userData.SESSDATA).getBytes();
            OutputStream outputStream = new FileOutputStream(localFilePath + "userInfo.inf");
            outputStream.write(saveData);
            outputStream.close();
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    public UserData readUserInfo(){
        UserData userData = new UserData();
        try {
            InputStream inputStream = new FileInputStream(localFilePath + "userInfo.inf");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            userData.access_token = bufferedReader.readLine();
            userData.refresh_token = bufferedReader.readLine();
            userData.expires_in = bufferedReader.readLine();
            userData.bili_jct = bufferedReader.readLine();
            userData.Expires = bufferedReader.readLine();
            userData.DedeUserID = bufferedReader.readLine();
            userData.DedeUserID__ckMd5 = bufferedReader.readLine();
            userData.sid = bufferedReader.readLine();
            userData.SESSDATA = bufferedReader.readLine();
        } catch (IOException e) {
            return null;
        }
        return userData;
    }
    private boolean saveSid(String sid){
        try {
            byte[] saveData = (sid).getBytes();
            OutputStream outputStream = new FileOutputStream(localFilePath + "sid.inf");
            outputStream.write(saveData);
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private String readSid(){
        try {
            InputStream inputStream = new FileInputStream(localFilePath + "sid.inf");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return bufferedReader.readLine();
        } catch (IOException e) {
            return null;
        }
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
    public UserInformation getUserInfo(UserData userData){
        try{
            UrlReply urlReply = urlGetRequest(userInfo + "?" + getSign("access_key=" + userData.access_token + "&appkey=" + appKey + "&ts=" + getCurrentTimeMillis()));
            Log.d("getUserInfo", urlReply.json);
            JSONObject data = (new JSONObject(urlReply.json)).getJSONObject("data");
            boolean vip = false;
            if(data.getJSONObject("vip").getInt("status")==1)
                vip = true;
            return new UserInformation(data.getString("mid"),data.getString("name"),data.getString("sign"),data.getInt("coins"),data.getString("face"),data.getInt("sex"),data.getInt("level"),vip,data.getJSONObject("vip").getString("nickname_color"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean logout(){
        final boolean delete1 = new File(localFilePath + "sid.inf").delete();
        final boolean delete2 = new File(localFilePath + "userInfo.inf").delete();
        return delete1&&delete2;
    }
    /*
    public UserInformation getUserSelfInformation(String SESSDATA){
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
            UserInformation userInformation = new UserInformation();
            userInformation.face = data.getString("face");
            userInformation.mid = data.getString("mid");
            userInformation.money = data.getInt("money");
            userInformation.moral = data.getInt("moral");
            userInformation.uname = data.getString("uname");
            userInformation.vipStatus = data.getInt("vipStatus");
            JSONObject wallet = data.getJSONObject("wallet");
            userInformation.wallet = wallet.getInt("bcoin_balance");
            JSONObject level_info = data.getJSONObject("level_info");
            LevelInfo levelInfo = new LevelInfo(level_info.getInt("current_level"),level_info.getInt("current_min"),level_info.getInt("current_exp"),level_info.getInt("next_exp"));
            userInformation.levelInfo = levelInfo;
            return userInformation;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    */
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
    public UrlReply urlPostRequest(String urlStr,String postData) {
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
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),StandardCharsets.UTF_8));
            out.write(postData);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            out.close();
            in.close();
            return new UrlReply(document.toString(),connection.getHeaderField("Set-Cookie"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public UrlReply urlPostRequestWithCookie(String urlStr,String Cookie,String postData) {
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
            return new UrlReply(document.toString(),connection.getHeaderField("Set-Cookie"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private UrlReply urlGetRequestWithCookie(String urlStr,String Cookie) {
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
            return new UrlReply(document.toString(),connection.getHeaderField("Set-Cookie"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public UrlReply urlGetRequest(String urlStr){
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
            return new UrlReply(document.toString(),connection.getHeaderField("Set-Cookie"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String subStringRSAPublicKey(String RSAPublicKey){
        return RSAPublicKey.substring(RSAPublicKey.indexOf("-----BEGIN PUBLIC KEY-----")+"-----BEGIN PUBLIC KEY-----".length(),RSAPublicKey.indexOf("-----END PUBLIC KEY-----"));
    }
    private String encrypt(String str, LoginKey loginKey){
        try {
            //base64编码的公钥
            byte[] decoded = java.util.Base64.getMimeDecoder().decode(loginKey.RSAPublicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String outStr = Base64.getEncoder().encodeToString(cipher.doFinal((loginKey.hash+str).getBytes(StandardCharsets.UTF_8)));
            return outStr;
        }catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getSign(String parameter){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update((parameter + appSecKey).getBytes());
            byte[] byteArray = md5.digest();
            BigInteger bigInt = new BigInteger(1, byteArray);
            // 参数16表示16进制
            StringBuilder result = new StringBuilder(bigInt.toString(16));
            // 不足32位高位补零
            while (result.length() < 32) {
                result.insert(0, "0");
            }
            return parameter + "&sign=" + result;
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean isNetworking() throws IOException {
        int timeOut = 3000;
        return InetAddress.getByName("www.bilibili.com").isReachable(timeOut);
    }
    private String getCookie(String cookie,String data){
        int n1 = cookie.indexOf("=",cookie.indexOf(data));
        int n2 = cookie.indexOf(";",n1);
        return cookie.substring(n1+1,n2);
    }
    private long getCurrentTimeMillis(){
        return System.currentTimeMillis()/1000;
    }
}
