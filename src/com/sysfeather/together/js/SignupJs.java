package com.sysfeather.together.js;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.sysfeather.together.IndexActivity;
import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Member;

public class SignupJs extends JavaScriptInterface {
    public SignupJs(TActivity activity) {
        super(activity);
    }
    
    @JavascriptInterface
    public void signup(String jsonString) {
        if(mActivity.isNetworkConnected()) {
            mActivity.spinnerStart("Together", "送出註冊資訊...");
            mActivity.mAsyncTask = new SignupAsyncTask();
            mActivity.mAsyncTask.execute(jsonString);
        } else {
            Toast.makeText(mActivity, "無法連線至網路", Toast.LENGTH_SHORT).show();
        }
    }
    
    private class SignupAsyncTask extends AsyncTask<String, Integer, String> {
        private static final String ERROR = "error";
        @Override
        protected String doInBackground(String... params) {
            JSONObject json = null;
            Member member = Member.getInstance();
            String result = "";
            try {
                json  = new JSONObject(params[0]);
                JSONObject jsonResult = member.signup(json);
                if(jsonResult.getInt("success") == 1) {
                    member.updateLoginInfo(jsonResult, mActivity.mPrefs); 
                }
                result = jsonResult.toString();
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                result = ERROR;
            }
            return result;
        }  
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
            
        }
        
        @Override
        protected void onPostExecute(String result) {
            mActivity.spinnerStop();
            if(ERROR.equals(result)) {
                Toast.makeText(mActivity, "很抱歉，發生程式錯誤", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.getInt("success") == 1) { // 註冊成功
                        Intent intent = new Intent(mActivity, IndexActivity.class);
                        mActivity.startActivity(intent);
                    } else {
                        Toast.makeText(mActivity, json.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                    Toast.makeText(mActivity, "資訊錯誤", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
