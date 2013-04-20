package com.sysfeather.together;

import com.sysfeather.together.js.IndexJs;
import com.sysfeather.together.js.JavaScriptInterface;

import android.os.Bundle;

public class IndexActivity extends TActivity {	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.addJavascriptInterface(new IndexJs(this),
                JavaScriptInterface.INTERFACE_NAME);
        mWebView.loadUrl(WWW_PATH + "index.html");
    }
}
