package com.sysfeather.together.js;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.sysfeather.together.Config;
import com.sysfeather.together.IndexActivity;
import com.sysfeather.together.R;
import com.sysfeather.together.SignupActivity;
import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Member;
import com.sysfeather.together.util.ServerUtil;

public class SignupJs extends JavaScriptInterface {
    private static final String TAG = "SignupJs";
    private SignupActivity mSignupActivity; // 每次都要轉型很麻煩，先暫存起來用
    
    public SignupJs(TActivity activity) {
        super(activity);
        mSignupActivity = (SignupActivity) mActivity;
    }
    
    @JavascriptInterface
    public void signup(String jsonString) {
        if(mActivity.isNetworkConnected()) {
            mActivity.spinnerStart("Together", mActivity.getString(R.string.signup_send));
            mActivity.mAsyncTask = new SignupAsyncTask();
            mActivity.mAsyncTask.execute(jsonString);
        } else {
            Toast.makeText(mActivity, mActivity.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
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
            if(ERROR.equals(result)) {
                mActivity.spinnerStop();
                Toast.makeText(mActivity, mActivity.getString(R.string.error_program), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.getInt("success") == 1) { // 註冊成功
                        // Register GCM while signup successfully
                        GCMRegistrar.checkDevice(mActivity);
                        // Make sure the manifest was properly set - comment out this line
                        // while developing the app, then uncomment it when it's ready.
                        GCMRegistrar.checkManifest(mActivity);
                        mActivity.registerReceiver(mSignupActivity.mHandleMessageReceiver,
                                new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
                        final String regId = GCMRegistrar.getRegistrationId(mActivity);
                        Log.d(TAG, "regId: " + regId);
                        if ("".equals(regId)) {
                            Log.d(TAG, "registering...");
                            // Automatically registers application on startup.
                            GCMRegistrar.register(mActivity, Config.SENDER_ID);
                        } else {
                            // Device is already registered on GCM, check server.
                            if (GCMRegistrar.isRegisteredOnServer(mActivity)) {
                                // Skips registration.
                                Log.d(TAG, "already registerd");
                            } else {
                                // Try to register again, but not in the UI thread.
                                // It's also necessary to cancel the thread onDestroy(),
                                // hence the use of AsyncTask instead of a raw thread.
                                final Context context = mActivity;
                                mSignupActivity.mRegisterTask = new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        boolean registered =
                                                ServerUtil.register(context, regId);
                                        // At this point all attempts to register with the app
                                        // server failed, so we need to unregister the device
                                        // from GCM - the app will try to register again when
                                        // it is restarted. Note that GCM will send an
                                        // unregistered callback upon completion, but
                                        // GCMIntentService.onUnregistered() will ignore it.
                                        if (!registered) {
                                            GCMRegistrar.unregister(context);
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        mSignupActivity.mRegisterTask = null;
                                    }

                                };
                                mSignupActivity.mRegisterTask.execute(null, null, null);
                            }
                        }
                        mActivity.spinnerStop();
                        mActivity.finish();
                        Intent intent = new Intent(mActivity, IndexActivity.class);
                        mActivity.startActivity(intent);
                    } else {
                        mActivity.spinnerStop();
                        Toast.makeText(mActivity, json.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    mActivity.spinnerStop();
                    Log.e("JSONException", e.toString());
                    Toast.makeText(mActivity, mActivity.getString(R.string.error_program), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
