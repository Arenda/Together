package com.sysfeather.together.model;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


import com.sysfeather.together.Config;
import com.sysfeather.together.util.HttpUtil;

public class Event {
    private static final String URL = "together/Event/";
    private static final String PUBLISH_TAG = "publish";
    private static final String RESPONSE_TAG = "response";
    private static Event singleton;
    
    private Event() { }
    
    public static Event getInstance() {
        if(singleton == null) {
            synchronized(Event.class) {
                if(singleton == null) {
                    singleton = new Event();
                }
            }
        }
        return singleton;
    }
    
    /**
     * 事件列表
     * @param memberId
     * @param token
     * @return
     */
    public JSONArray list(String memberId, String token, double lat, double lng) {
        // Building Parameters
        JSONArray json = new JSONArray();
        StringBuilder sb = new StringBuilder();
        sb.append(Config.HOST).append(URL).append("?memberId=").append(memberId).append("&token=").append(token)
            .append("&lat=").append(lat).append("&lng=").append(lng);
        json = HttpUtil.getJSONArrayFromUrl(sb.toString(), null);
        return json;
    }
    
    /**
     * 發表活動
     * @param memberId
     * @param token
     * @param params
     * @return
     */
    public JSONObject publish(String memberId, String token, List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(Config.HOST).append(URL).append("?memberId=").append(memberId).append("&token=").append(token);
        // Building Parameters
        params.add(new BasicNameValuePair("tag", PUBLISH_TAG));
        JSONObject json = HttpUtil.getJSONFromUrl(sb.toString(), params);
        return json;
    }
    
    /**
     * 回應活動
     * @param memberId
     * @param token
     * @param params
     * @return
     */
    public JSONObject response(String memberId, String token, List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(Config.HOST).append(URL).append("?memberId=").append(memberId).append("&token=").append(token);
        // Building Parameters
        params.add(new BasicNameValuePair("tag", RESPONSE_TAG));
        JSONObject json = HttpUtil.getJSONFromUrl(sb.toString(), params);
        return json;
    }
}
