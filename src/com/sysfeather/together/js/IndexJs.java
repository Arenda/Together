package com.sysfeather.together.js;


import com.sysfeather.together.IndexActivity;
import com.sysfeather.together.R;
import com.sysfeather.together.ResponseActivity;
import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Event;
import com.sysfeather.together.model.Member;
import com.sysfeather.together.util.StringUtil;

import android.content.Intent;
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
            Toast.makeText(mActivity, mActivity.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    }
	
	@JavascriptInterface
    public void response(String eventId) {
	    Intent intent = new Intent((IndexActivity) mActivity, ResponseActivity.class);
	    intent.putExtra(IndexActivity.EVENT_ID, eventId);
	    mActivity.startActivity(intent);
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
            Event event = Event.getInstance();
            Log.d(TAG, ((IndexActivity) mActivity).mLocation.getLatitude() + "," + ((IndexActivity) mActivity).mLocation.getLongitude());
            String result = event.list(memberId, 
                    token, 
                    ((IndexActivity) mActivity).mLocation.getLatitude(), 
                    ((IndexActivity) mActivity).mLocation.getLongitude()).toString();
            return result;
        }  
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
            
        }
        
        @Override
        protected void onPostExecute(String result) {
            mActivity.mAsyncTask = null;
            result = StringUtil.replaceLineFeed(result);
            mActivity.mWebView.loadUrl("javascript:Together.android.callback('" + result + "')");
        }
    }
}
