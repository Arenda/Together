package com.sysfeather.together.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class TWebChromeClient extends WebChromeClient {
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
       AlertDialog dialog = new AlertDialog.Builder(view.getContext()).
               setMessage(message).
               setPositiveButton("確定", new DialogInterface.OnClickListener() {                  
                  public void onClick(DialogInterface dialog, int which) {
                     //do nothing
                  }
               }).create();
       dialog.show();
       result.confirm();
       return true;
    }
}
