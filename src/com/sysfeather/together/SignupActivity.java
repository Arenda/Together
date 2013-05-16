package com.sysfeather.together;

import com.sysfeather.together.js.JavaScriptInterface;
import com.sysfeather.together.js.SignupJs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


public class SignupActivity extends TActivity {
    private static final String TAG = "SignupActivity";
    public AsyncTask<Void, Void, Void> mRegisterTask;
    
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
    
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        super.onDestroy();
    }
    
    public final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
            Log.d(TAG, "receiver:" + newMessage);
        }
    };
}
