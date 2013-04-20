package com.sysfeather.together.model;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TWebViewClient extends WebViewClient {
	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        // true makes the Webview have a normal viewport such as a normal desktop browser 
        // when false the webview will have a viewport constrained to it's own dimensions
        //settings.setUseWideViewPort(true);
        view.loadUrl(url);
        return true;
    }
}
