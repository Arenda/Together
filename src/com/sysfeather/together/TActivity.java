package com.sysfeather.together;

import com.sysfeather.together.model.TWebChromeClient;
import com.sysfeather.together.model.TWebView;
import com.sysfeather.together.model.TWebViewClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Toast;

public class TActivity extends Activity {
    protected final static String WWW_PATH = "file:///android_asset/www/";
    // declare public for performance, direct access
    public TWebView mWebView;
    public AsyncTask<String, Integer, String> mAsyncTask;
    public SharedPreferences mPrefs;
    protected ProgressDialog mSpinnerDialog;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStringProperty("loadingDialog", "Together,載入頁面中..."); // show loading dialog
        setContentView(R.layout.activity_main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mWebView = (TWebView) findViewById(R.id.twebview);
        mWebView.setWebChromeClient(new TWebChromeClient());
        mWebView.setWebViewClient(new TWebViewClient() {
            @Override  
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(mWebView, url);
                //spinnerStop();
            }  

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "糟糕...發生錯誤: " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Determine if the focus is on the current view or not
        if (mWebView.getHitTestResult() != null && 
            mWebView.getHitTestResult().getType() == WebView.HitTestResult.EDIT_TEXT_TYPE &&
            keyCode == KeyEvent.KEYCODE_BACK) {
            return mWebView.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    /**
     * Load the url into the webview.
     *
     * @param url
     */
    public void loadUrl(String url) {
        // Then load the spinner
        //this.loadSpinner();
        this.mWebView.loadUrl(url);
    }
    
    /*
     * Load the spinner
     */
    void loadSpinner() {

        // If loadingDialog property, then show the App loading dialog for first page of app
        String loading = null;
        if ((this.mWebView == null) || !this.mWebView.canGoBack()) {
            loading = this.getStringProperty("loadingDialog", null);
        }
        else {
            loading = this.getStringProperty("loadingPageDialog", null);
        }
        if (loading != null) {

            String title = "";
            String message = "載入中...";

            if (loading.length() > 0) {
                int comma = loading.indexOf(',');
                if (comma > 0) {
                    title = loading.substring(0, comma);
                    message = loading.substring(comma + 1);
                }
                else {
                    title = "";
                    message = loading;
                }
            }
            this.spinnerStart(title, message);
        }
    }
    
    /**
     * Show the spinner.  Must be called from the UI thread.
     *
     * @param title         Title of the dialog
     * @param message       The message of the dialog
     */
    public void spinnerStart(final String title, final String message) {
        if (this.mSpinnerDialog != null) {
            this.mSpinnerDialog.dismiss();
            this.mSpinnerDialog = null;
        }
        final TActivity me = this;
        
        this.mSpinnerDialog = ProgressDialog.show(TActivity.this, title, message, true, false,
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        me.mSpinnerDialog = null;
                        if(me.mAsyncTask != null) {
                            me.mAsyncTask.cancel(true);
                        }
                    }
                });
    }
    
    /**
     * Stop spinner - Must be called from UI thread
     */
    public void spinnerStop() {
        if (this.mSpinnerDialog != null && this.mSpinnerDialog.isShowing()) {
            this.mSpinnerDialog.dismiss();
            this.mSpinnerDialog = null;
        }
    }
    
    /**
     * Get boolean property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Boolean p;
        try {
            p = (Boolean) bundle.get(name);
        } catch (ClassCastException e) {
            String s = bundle.get(name).toString();
            if ("true".equals(s)) {
                p = true;
            }
            else {
                p = false;
            }
        }
        if (p == null) {
            return defaultValue;
        }
        return p.booleanValue();
    }

    /**
     * Get int property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public int getIntegerProperty(String name, int defaultValue) {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Integer p;
        try {
            p = (Integer) bundle.get(name);
        } catch (ClassCastException e) {
            p = Integer.parseInt(bundle.get(name).toString());
        }
        if (p == null) {
            return defaultValue;
        }
        return p.intValue();
    }

    /**
     * Get string property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String getStringProperty(String name, String defaultValue) {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        String p = bundle.getString(name);
        if (p == null) {
            return defaultValue;
        }
        return p;
    }

    /**
     * Get double property for activity.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public double getDoubleProperty(String name, double defaultValue) {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Double p;
        try {
            p = (Double) bundle.get(name);
        } catch (ClassCastException e) {
            p = Double.parseDouble(bundle.get(name).toString());
        }
        if (p == null) {
            return defaultValue;
        }
        return p.doubleValue();
    }

    /**
     * Set boolean property on activity.
     *
     * @param name
     * @param value
     */
    public void setBooleanProperty(String name, boolean value) {
        this.getIntent().putExtra(name, value);
    }

    /**
     * Set int property on activity.
     *
     * @param name
     * @param value
     */
    public void setIntegerProperty(String name, int value) {
        this.getIntent().putExtra(name, value);
    }

    /**
     * Set string property on activity.
     *
     * @param name
     * @param value
     */
    public void setStringProperty(String name, String value) {
        this.getIntent().putExtra(name, value);
    }

    /**
     * Set double property on activity.
     *
     * @param name
     * @param value
     */
    public void setDoubleProperty(String name, double value) {
        this.getIntent().putExtra(name, value);
    }
    
    @Override
    protected void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }
    
    /**
     * 檢查網路連線
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() == null ? false : true;
    }
}
