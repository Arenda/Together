package com.sysfeather.together;

import android.content.Intent;
import android.os.Bundle;

import com.sysfeather.together.js.JavaScriptInterface;
import com.sysfeather.together.js.LoginJs;
import com.sysfeather.together.model.Member;

public class LoginActivity extends TActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.addJavascriptInterface(new LoginJs(this), 
                JavaScriptInterface.INTERFACE_NAME);
        if("".equals(mPrefs.getString(Member.PREFS_TOKEN, ""))) { // 尚未登入
            loadUrl(WWW_PATH + "login.html");
        } else { // 已登入
            Intent intent = new Intent(this, IndexActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
