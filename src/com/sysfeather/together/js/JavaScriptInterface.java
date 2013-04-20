package com.sysfeather.together.js;
import com.sysfeather.together.TActivity;
/**
 * 
 * Injects the supplied Java object into this WebView. The object is injected into the 
 * JavaScript context of the main frame, using the supplied name. This allows the 
 * Java object's public methods to be accessed from JavaScript.
 */
public class JavaScriptInterface {
    public static final String INTERFACE_NAME = "android";
    protected TActivity mActivity;

    // Instantiate the interface and set the context 
    public JavaScriptInterface(TActivity activity) {
        mActivity = activity;
    }     
}
