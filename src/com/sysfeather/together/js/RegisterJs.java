package com.sysfeather.together.js;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.android.gcm.GCMRegistrar;
import com.sysfeather.together.Config;
import com.sysfeather.together.TActivity;

public class RegisterJs extends JavaScriptInterface {
    private static final String TAG = "RegisterJs";
    public RegisterJs(TActivity activity) {
        super(activity);
    }
    
    @JavascriptInterface
    public void register() {
        GCMRegistrar.checkDevice(mActivity);
        GCMRegistrar.register(mActivity, Config.SENDER_ID);
        Log.i(TAG, GCMRegistrar.getRegistrationId(mActivity));
    }
    
    @JavascriptInterface
    public void unregister() {
        GCMRegistrar.unregister(mActivity);
    }

}
