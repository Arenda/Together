package com.sysfeather.together.js;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.sysfeather.together.IndexActivity;
import com.sysfeather.together.R;
import com.sysfeather.together.ResponseActivity;
import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Event;
import com.sysfeather.together.model.Member;
import com.sysfeather.together.util.StringUtil;

public class ResponseJs extends JavaScriptInterface {    
    private static final String TAG = "ResponseJs";
    
    public ResponseJs(TActivity activity) {
        super(activity);
    }
    
    @JavascriptInterface
    public void response(String json) {
        mActivity.mAsyncTask = new ResponseAsyncTask();
        mActivity.mAsyncTask.execute(json);
    }
    
    private class ResponseAsyncTask extends AsyncTask<String, Integer, String> {
        private static final String ERROR = "error";
          
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivity.spinnerStart("Together", mActivity.getString(R.string.response_send));
        }
        
        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            JSONObject json = null;
            String result = "";
            String token = mActivity.mPrefs.getString(Member.PREFS_TOKEN, "");
            String memberId = mActivity.mPrefs.getString(Member.PREFS_ID, "");
            String response = "";
            try {
                json  = new JSONObject(jsonData);
                response = StringUtil.replaceLineFeed(json.getString("message"));
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                return ERROR;
            }
            List<NameValuePair> postData = new ArrayList<NameValuePair>();
            postData.add(new BasicNameValuePair(IndexActivity.EVENT_ID, ((ResponseActivity) mActivity).mEventId));
            postData.add(new BasicNameValuePair("message", response));
            Event eventModel = Event.getInstance();
            result = eventModel.response(memberId, token, postData).toString();
            return result;
        }
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
            
        }
        
        @Override
        protected void onPostExecute(String result) {
            mActivity.mAsyncTask = null;
            mActivity.spinnerStop();
            Log.d(TAG, result);
            if(result.equals(ERROR)) {
                Toast.makeText(mActivity, mActivity.getString(R.string.error_program), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.getInt("success") == 1) { // response successfully
                        Toast.makeText(mActivity, mActivity.getString(R.string.response_success), Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    } else {
                        Toast.makeText(mActivity, json.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                    Toast.makeText(mActivity, mActivity.getString(R.string.error_program), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
