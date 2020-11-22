package org.suyueqiuliang.pilipili.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.suyueqiuliang.pilipili.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import java.net.MalformedURLException;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



public class ToolClass {
    static private String localFilePath = null;
    static private String localCachePath = null;
    @SuppressLint("StaticFieldLeak")
    static private Context context = null;
    static private UserData userData = null;
    static private boolean wasGetInfo = false;
    private final String appKey = "4409e2ce8ffd12b8";
    private final String appSecKey = "59b43e04ad6965f34319062b478f83dd";

    private final String app_head = "http://app.bilibili.com";
    private final String api_head = "http://api.bilibili.com";
    private final String passportHead = "http://passport.bilibili.com";


    private final String getKeyUrl = passportHead + "/api/oauth2/getKey";
    private final String loginUrl = passportHead + "/api/v3/oauth2/login";
    private final String userInfo = app_head + "/x/v2/account/myinfo";
    private final String userLevelWallet = api_head + "/x/web-interface/nav";
    private final String getAppRecommendVideo = app_head + "/x/v2/feed/index";
    private final String getVideoInformation = api_head + "/x/web-interface/view";
    private final String getVideoStream = api_head + "/x/player/playurl";

    public ToolClass(Context context){
        ToolClass.context = context;
        localFilePath = context.getExternalFilesDir("res")+"/";
        localCachePath = context.getCacheDir() + "/";
    }

    public ToolClass() {

    }
    public void playVideo(String urls){
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL(urls);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("Referer","https://www.bilibili.com");
            connection.setRequestProperty("Accept","*/*");
            connection.setRequestProperty("User-Agent","sdbyiuv");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String[] getVideoStream(int aid,int cid,int qn){
        try {
            if (userData == null || !wasGetInfo)
                return null;
            UrlReply urlReply;
            if(qn != 0)
                urlReply = urlGetRequestWithCookie(getVideoStream + "?avid=" + aid + "&cid=" + cid +"&qn=" +qn,"SESSDATA=" + ToolClass.userData.SESSDATA);
            else urlReply = urlGetRequestWithCookie(getVideoStream + "?avid=" + aid + "&cid=" + cid,"SESSDATA=" + ToolClass.userData.SESSDATA);
            if(urlReply == null)
                return null;
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("durl");
            String[] urls = new String[jsonArray.length()];
            for(int i=0;i<urls.length;i++)
                urls[i] = jsonArray.getJSONObject(i).getString("url");
            return urls;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public QualityList getVideoStreamQuality(int aid,int cid){
        try {
            if (userData == null || !wasGetInfo)
                return null;
            UrlReply urlReply = urlGetRequestWithCookie(getVideoStream + "?avid=" + aid + "&cid=" + cid + "&fnval=16","SESSDATA=" + ToolClass.userData.SESSDATA);
            if(urlReply == null)
                return null;
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            ArrayList<String> accept_description = new ArrayList<>();
            ArrayList<Integer> accept_quality = new ArrayList<>();
            for(int i =0;i<jsonObject.getJSONArray("accept_description").length();i++){
                accept_description.add(jsonObject.getJSONArray("accept_description").getString(i));
                accept_quality.add(jsonObject.getJSONArray("accept_quality").getInt(i));
            }
            return new QualityList(accept_description,accept_quality);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public VideoInformation getVideoInformation(int aid){
        try {
            if (userData != null && !wasGetInfo)
                return null;
            else if (userData != null) {
                UrlReply urlReply = urlGetRequest(getVideoInformation + "?aid=" + aid);
                if(urlReply == null)
                    return null;
                JSONObject jsonObject = new JSONObject(urlReply.json);
                if(jsonObject.getInt("code") == 0){
                    jsonObject = jsonObject.getJSONObject("data");
                    ArrayList<Page> pages = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("pages");
                    for(int i = 0;i <jsonObject.getInt("videos");i++){
                        Page page = new Page(jsonArray.getJSONObject(i).getInt("cid"),jsonArray.getJSONObject(i).getString("part"));
                        pages.add(page);
                    }
                    JSONObject ownerJsonObject = jsonObject.getJSONObject("owner");
                    Owner owner = new Owner(ownerJsonObject.getInt("mid"),ownerJsonObject.getString("name"),ownerJsonObject.getString("face"));
                    return new VideoInformation(jsonObject.getInt("aid"),jsonObject.getString("bvid"),jsonObject.getString("title"),jsonObject.getString("pic"),jsonObject.getString("desc"),owner,pages,jsonObject.getInt("videos"),jsonObject.getInt("duration"),jsonObject.getInt("pubdate"),jsonObject.getInt("ctime"),jsonObject.getInt("tid"),jsonObject.getString("tname"),jsonObject.getInt("copyright"),jsonObject.getInt("state"));
                }else
                    return null;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean wasLogin(){
        return userData!=null;
    }
    public ArrayList<video> getAppRecommendVideo(){
        try {
            if(userData != null && !wasGetInfo)
                return null;
            UrlReply urlReply;
            if(userData != null) urlReply = urlGetRequestWithCookie(getAppRecommendVideo + "?" + getSign("access_key=" + userData.access_token +"&appkey=1d8b6e7d45233436&mobi_app=android&network=wifi&platform=android&qn=32&ts=" + getCurrentTimeMillis()),"sid=" + readSid());
            else urlReply = urlGetRequestWithCookie(getAppRecommendVideo,"sid=" + readSid());
            if(urlReply == null)
                return null;
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            ArrayList<video> videos = new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                if(jsonArray.getJSONObject(i).getString("goto").equals("av")){
                    String duration = jsonArray.getJSONObject(i).getString("cover_right_text");
                    JSONObject args = jsonArray.getJSONObject(i).getJSONObject("args");
                    JSONObject data = jsonArray.getJSONObject(i);
                    videos.add(new video(data.getString("title"),data.getString("cover"),args.getInt("aid"),args.getInt("up_id"),args.getString("up_name"),data.getString("cover_left_text_1"),data.getString("cover_left_text_2"),duration));
                }
            }
            return videos;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public loginWithStorageDataReturnInfo loginWithStorageData(){
        try {
            UserData userData = readUserData();
            if(userData == null)
                return loginWithStorageDataReturnInfo.failed;
            UrlReply urlReply = urlGetRequest(userInfo + "?" + getSign("access_key=" + userData.access_token + "&appkey=" + appKey + "&ts=" + getCurrentTimeMillis()));
            if(urlReply ==null)
                return loginWithStorageDataReturnInfo.notOnline;
            if(new JSONObject(urlReply.json).getInt("code")!=0){
                logout();
                return loginWithStorageDataReturnInfo.failed;
            }else {
                ToolClass.userData = userData;
                return loginWithStorageDataReturnInfo.ok;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return loginWithStorageDataReturnInfo.failed;
        }
    }
    public String login(String username, String password){
        try {
            UrlReply urlReply = urlPostRequest(getKeyUrl , getSign("appkey=" + appKey));
            if(urlReply == null)
                return null;
            saveSid(getCookie(urlReply.cookie,"sid"));
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            LoginKey loginKey = new LoginKey(jsonObject.getString("hash"),subStringRSAPublicKey(jsonObject.getString("key")));
            urlReply = urlPostRequestWithCookie(loginUrl,"sid=" + readSid(),getSign("appkey=" + URLEncoder.encode(appKey,"UTF-8") + "&mobi_app=android&password=" + URLEncoder.encode(encrypt(password, loginKey),"UTF-8")  + "&platform=android&ts=" + URLEncoder.encode(String.valueOf(System.currentTimeMillis()/1000),"UTF-8") + "&username="+URLEncoder.encode(username,"UTF-8")));
            if(urlReply == null)
                return null;
            jsonObject = new JSONObject(urlReply.json);
            if(jsonObject.getInt("code") == 0){
                JSONObject data = jsonObject.getJSONObject("data");
                saveUserData(data);
                userData = readUserData();
                return "true";
            }
            else {
                if(jsonObject.has("message")){
                    return jsonObject.getString("message");
                }else return context.getString(R.string.unknown_failed_message);
            }
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
    private String loginWithIdentifyingCode(String username,String password){
        try {
            UrlReply urlReply = urlPostRequest(getKeyUrl , getSign("appkey=" + appKey));
            if(urlReply == null)
                return null;
            saveSid(getCookie(urlReply.cookie,"sid"));
            JSONObject jsonObject = new JSONObject(urlReply.json);
            jsonObject = jsonObject.getJSONObject("data");
            LoginKey loginKey = new LoginKey(jsonObject.getString("hash"),subStringRSAPublicKey(jsonObject.getString("key")));
            Log.d("RSAPublicKey",loginKey.RSAPublicKey);
            urlReply = urlPostRequestWithCookie(loginUrl,"sid=" + readSid(),getSign("appkey=" + URLEncoder.encode(appKey,"UTF-8") + "&mobi_app=android&password=" + URLEncoder.encode(encrypt(password, loginKey),"UTF-8")  + "&platform=android&ts=" + URLEncoder.encode(String.valueOf(getCurrentTimeMillis()),"UTF-8") + "&username="+URLEncoder.encode(username,"UTF-8")));
            if(urlReply == null)
                return null;
            return urlReply.json;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void saveUserData(JSONObject jsonObject) {
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    public UserData readUserData(){
        try {
            UserData userData = new UserData();
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
            return userData;
        } catch (IOException e) {
            return null;
        }
    }
    private void saveSid(String sid){
        try {
            byte[] saveData = (sid).getBytes();
            OutputStream outputStream = new FileOutputStream(localFilePath + "sid.inf");
            outputStream.write(saveData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
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
    public LoginAccess getUserInformation(){
        try{
            UrlReply urlReply = urlGetRequest(userInfo + "?" + getSign("access_key=" + userData.access_token + "&appkey=" + appKey + "&ts=" + getCurrentTimeMillis()));
            if(urlReply == null)
                return null;
            if(new JSONObject(urlReply.json).getInt("code")==0){
                JSONObject data = (new JSONObject(urlReply.json)).getJSONObject("data");
                boolean vip = false;
                if(data.getJSONObject("vip").getInt("status")==1)
                    vip = true;
                wasGetInfo = true;
                return new LoginAccess(true,new UserInformation(data.getString("mid"),data.getString("name"),data.getString("sign"),data.getInt("coins"),data.getString("face"),data.getInt("sex"),data.getInt("level"),vip,data.getJSONObject("vip").getString("nickname_color")));
            }else{
                logout();
                return new LoginAccess(false,null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public LevelWalletInfo getUserLevelWalletInfo(){
        try{
            if(!wasGetInfo)
                return null;
            UrlReply urlReply = urlGetRequestWithCookie(userLevelWallet,"SESSDATA=" + userData.SESSDATA);
            if(urlReply == null)
                return null;
            JSONObject jsonObject = new JSONObject(urlReply.json);
            if(jsonObject.getInt("code") == 0){
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject levelInfo = data.getJSONObject("level_info");
                JSONObject wallet = data.getJSONObject("wallet");
                return new LevelWalletInfo(levelInfo.getInt("current_level"),levelInfo.getInt("current_min"),levelInfo.getInt("current_exp"),levelInfo.getInt("next_exp"),wallet.getInt("bcoin_balance"));
            }else return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void logout(){
        new File(localFilePath + "sid.inf").delete();
        new File(localFilePath + "userInfo.inf").delete();
        userData = null;
        wasGetInfo = true;
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
    public Bitmap getUrlImageBitmap(String urlString){
        try {
            int position = 0,lastpositon = -1;
            boolean a =true;
            while (position != -1){
                lastpositon = position;
                position = urlString.indexOf("/",position+1);
            }
            File file = new File(localCachePath + urlString.substring(lastpositon+1));
            if(!file.exists()){
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setReadTimeout(3000);
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);
                is.close();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, bos);
                bos.flush();
                bos.close();
                return  bmp;
            }else {
                FileInputStream is = new FileInputStream(file);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                is.close();
                return bmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private UrlReply urlPostRequest(String urlStr,String postData) {
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
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
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
            connection.disconnect();
            return new UrlReply(document.toString(),connection.getHeaderField("Set-Cookie"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private UrlReply urlPostRequestWithCookie(String urlStr,String Cookie,String postData) {
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
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
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
            connection.disconnect();
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
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            connection.disconnect();
            return new UrlReply(document.toString(),connection.getHeaderField("Set-Cookie"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private UrlReply urlGetRequest(String urlStr){
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                document.append(line);
            }
            in.close();
            connection.disconnect();
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
            return Base64.getEncoder().encodeToString(cipher.doFinal((loginKey.hash+str).getBytes(StandardCharsets.UTF_8)));
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
    private String getCookie(String cookie,String data){
        int n1 = cookie.indexOf("=",cookie.indexOf(data));
        int n2 = cookie.indexOf(";",n1);
        return cookie.substring(n1+1,n2);
    }
    private long getCurrentTimeMillis(){
        return System.currentTimeMillis()/1000;
    }
}
