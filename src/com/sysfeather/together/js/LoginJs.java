package com.sysfeather.together.js;

import org.json.JSONException;
import org.json.JSONObject;

import com.sysfeather.together.IndexActivity;
import com.sysfeather.together.SignupActivity;
import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Member;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class LoginJs extends JavaScriptInterface {
    public LoginJs(TActivity activity) {
        super(activity);
    }
    
    @JavascriptInterface
    public void login(String jsonString) {
        if(mActivity.isNetworkConnected()) {
            mActivity.spinnerStart("Together", "登入中...");
            mActivity.mAsyncTask = new LoginAsyncTask();
            mActivity.mAsyncTask.execute(jsonString);
        } else {
            Toast.makeText(mActivity, "無法連線至網路", Toast.LENGTH_SHORT).show();
        }
    }
    
    @JavascriptInterface
    public void intent() {
        Intent intent = new Intent(mActivity, SignupActivity.class);
        mActivity.startActivity(intent);
    }
    
    private class LoginAsyncTask extends AsyncTask<String, Integer, String> {
        private static final String ERROR = "error";
        @Override
        protected String doInBackground(String... params) {
            JSONObject json = null;
            Member member = Member.getInstance();
            String result = "{}";
            try {
                json  = new JSONObject(params[0]);
                JSONObject jsonResult = member.login(json.getString("email"), json.getString("password"));
                result = jsonResult.toString();
                if(jsonResult.getInt("success") == 1) {
                    member.updateLoginInfo(jsonResult, mActivity.mPrefs); 
                }
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
                Toast.makeText(mActivity, "程式錯誤", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.getInt("success") == 1) { // 登入成功
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
