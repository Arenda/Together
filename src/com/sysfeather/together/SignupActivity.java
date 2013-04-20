package com.sysfeather.together;

import com.sysfeather.together.js.JavaScriptInterface;
import com.sysfeather.together.js.SignupJs;

import android.os.Bundle;


public class SignupActivity extends TActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.addJavascriptInterface(new SignupJs(this), 
                JavaScriptInterface.INTERFACE_NAME);
        loadUrl(WWW_PATH + "signup.html");
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of the WebView
        mWebView.restoreState(savedInstanceState);
    }
}
