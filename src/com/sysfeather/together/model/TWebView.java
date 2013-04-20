package com.sysfeather.together.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TWebView extends WebView { 
    /**
     * Constructor.
     *
     * @param context
     */
    public TWebView(Context context) {
        super(context);
        setup();
    }

    /**
     * Constructor.
     *
     * @param context
     * @param attrs
     */
    public TWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup();
    }

    /**
     * Constructor.
     *
     * @param context
     * @param attrs
     * @param defStyle
     *
     */
    public TWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setup();
    }

    /**
     * Constructor.
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @param privateBrowsing
     */
    @SuppressLint("NewApi")
	public TWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
        this.setup();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
	private void setup() {
        WebSettings settings = this.getSettings();
        // enable JavaScript
        settings.setJavaScriptEnabled(true); 
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // loads the WebView completely zoomed out
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(false);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(0);
    }
}
