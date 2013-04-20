package com.sysfeather.together.js;


import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Event;
import com.sysfeather.together.model.Member;
import com.sysfeather.together.util.StringUtil;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class IndexJs extends JavaScriptInterface {
    private static final String TAG = "IndexJs";
	public IndexJs(TActivity activity) {
	    super(activity);
    }
	
	@JavascriptInterface
    public void init() {
        if(mActivity.isNetworkConnected()) {
            String token = this.mActivity.mPrefs.getString(Member.PREFS_TOKEN, "");
            String memberId = this.mActivity.mPrefs.getString(Member.PREFS_ID, "");
            if(!"".equals(token) && !"".equals(memberId)) {
                mActivity.mAsyncTask = new IndexInitAsyncTask();
                mActivity.mAsyncTask.execute(token, memberId);
            }
        } else {
            Toast.makeText(mActivity, "無法連線至網路", Toast.LENGTH_SHORT).show();
        }
    }
    
	/**
	 * 一開始進入頁面的列表載入功能
	 * @author Arenda
	 *
	 */
    private class IndexInitAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            String memberId = params[1];
            Log.d(TAG, "token: " + token);
            Log.d(TAG, "memberId: " + memberId);
            Event event = Event.getInstance();
            String result = event.list(memberId, token).toString();
            return result;
        }  
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
            
        }
        
        @Override
        protected void onPostExecute(String result) {
            result = StringUtil.replaceLineFeed(result);
            Log.d(TAG, result);
            mActivity.mWebView.loadUrl("javascript:jsBridge.initCallback('" + result + "')");
        }
    }
}
