package com.sysfeather.together;

import android.os.Bundle;

import com.sysfeather.together.js.JavaScriptInterface;
import com.sysfeather.together.js.ResponseJs;

public class ResponseActivity extends TActivity {
    public String mEventId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        mEventId = extras.getString(IndexActivity.EVENT_ID);
        mWebView.addJavascriptInterface(new ResponseJs(this),
                JavaScriptInterface.INTERFACE_NAME);
        mWebView.loadUrl(WWW_PATH + "response.html");
    }
}
