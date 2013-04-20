package com.sysfeather.together.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;

import com.sysfeather.together.Config;
import com.sysfeather.together.util.HttpUtil;

public class Member {
    public static final String PREFS_ID = "member_id";
    public static final String PREFS_EMAIL = "member_email";
    public static final String PREFS_NAME = "member_name";
    public static final String PREFS_LOGIN_TIME = "member_login_time";
    public static final String PREFS_TOKEN = "token";
    private static final String TAG = "Member";
    private static final String LOGIN_URL = "together/Member/Login/";
    private static final String SIGNUP_URL = "together/Member/SignUp/";
    private static final String LOGIN_TAG = "login"; 
    private static final String SIGNUP_TAG = "signup";
    private static Member singleton;
    
    private Member() { }
    
    public static Member getInstance() {
        if(singleton == null) {
            synchronized(Member.class) {
                if(singleton == null) {
                    singleton = new Member();
                }
            }
        }
        return singleton;
    }
    
    /**
     * 會員登入
     * @param email
     * @param password
     * @return
     */
    public JSONObject login(String email, String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", LOGIN_TAG));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = HttpUtil.getJSONFromUrl(Config.HOST + LOGIN_URL, params);
        return json;
    }
    
    /**
     * 登入成功後更新會員登入資訊
     * @param jsonResult
     * @throws JSONException 
     * @throws NumberFormatException 
     */
    public void updateLoginInfo(JSONObject jsonResult, SharedPreferences prefs) throws NumberFormatException, JSONException {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_ID, jsonResult.getString("id"));
        editor.putString(PREFS_EMAIL, jsonResult.getString("email"));
        editor.putString(PREFS_NAME, jsonResult.getString("name"));
        editor.putString(PREFS_TOKEN, jsonResult.getString("token"));
        editor.putString(PREFS_LOGIN_TIME, jsonResult.getString("loginTime"));
        editor.commit();
    }
    
    /**
     * 會員註冊
     * @param email
     * @param password
     * @return
     */
    public JSONObject signup(JSONObject json) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", SIGNUP_TAG));
        try {
            params.add(new BasicNameValuePair("email", json.getString("email")));
            params.add(new BasicNameValuePair("name", json.getString("name")));
            params.add(new BasicNameValuePair("gender", json.getString("gender")));
            params.add(new BasicNameValuePair("birthYear", json.getString("birthYear")));
            params.add(new BasicNameValuePair("birthMonth", json.getString("birthMonth")));
            params.add(new BasicNameValuePair("birthDay", json.getString("birthDay")));
            params.add(new BasicNameValuePair("password", json.getString("password")));
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        JSONObject result = HttpUtil.getJSONFromUrl(Config.HOST + SIGNUP_URL, params);
        return result;
    }
}
