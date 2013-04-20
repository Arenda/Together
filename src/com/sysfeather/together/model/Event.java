package com.sysfeather.together.model;

import org.json.JSONArray;


import com.sysfeather.together.Config;
import com.sysfeather.together.util.HttpUtil;

public class Event {
    private static final String URL = "together/Event/";
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
    public JSONArray list(String memberId, String token) {
        // Building Parameters
        JSONArray json = new JSONArray();
        StringBuilder sb = new StringBuilder();
        sb.append(Config.HOST).append(URL).append("?memberId=").append(memberId).append("&token=").append(token);
        json = HttpUtil.getJSONArrayFromUrl(sb.toString(), null);
        return json;
    }
}
