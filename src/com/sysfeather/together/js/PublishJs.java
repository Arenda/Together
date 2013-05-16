package com.sysfeather.together.js;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.sysfeather.together.Config;
import com.sysfeather.together.PublishActivity;
import com.sysfeather.together.R;
import com.sysfeather.together.TActivity;
import com.sysfeather.together.model.Event;
import com.sysfeather.together.model.Member;
import com.sysfeather.together.util.CommonUtil;
import com.sysfeather.together.util.FileUtil;
import com.sysfeather.together.util.StringUtil;

public class PublishJs extends JavaScriptInterface {
    private static final String TAG = "PublishJs";
    private PublishActivity mPublishActivity; // 每次都要轉型很麻煩，先暫存起來用
    
    public PublishJs(TActivity activity) {
        super(activity);
        mPublishActivity = ((PublishActivity) mActivity);
    }
    
    @JavascriptInterface
    public void takePhoto() {   
        // create a File object for the parent directory
        File folder = new File(Config.SDCARD_PATH);
        // have the object build the directory structure, if needed.
        if(!folder.exists()) {
            folder.mkdirs();
        }
        // 產生圖檔名
        String fileName = FileUtil.genFileName("jpg");
        File file = new File(Config.SDCARD_PATH, fileName);
        mPublishActivity.mOutputFileUri = Uri.fromFile(file);
        Log.d(TAG, mPublishActivity.mOutputFileUri.getPath());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 取得原圖一定要設定Uri，不設定會回傳縮圖(thumbnail)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPublishActivity.mOutputFileUri);
        mActivity.startActivityForResult(intent, PublishActivity.TAKE_PICTURE);
    }
    
    @JavascriptInterface
    public void selectPhoto() {
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivity.startActivityForResult(Intent.createChooser(intent,
                mActivity.getString(R.string.publish_select_photo)), PublishActivity.SELECT_PICTURE);

        mActivity.startActivityForResult(intent, PublishActivity.SELECT_PICTURE);
    }
    
    @JavascriptInterface
    public String getPhoto() {
        String result = mPublishActivity.mThumbnailPath;
        mPublishActivity.mThumbnailPath = null;
        return result;
    }
    
    @JavascriptInterface
    public void publish(String json) {
        mPublishActivity.mPostData = json;
        mActivity.mAsyncTask = new PublishAsyncTask();
        mActivity.mAsyncTask.execute(json);
    }
    
    private class PublishAsyncTask extends AsyncTask<String, Integer, String> {
        private static final String ERROR = "error";
        
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivity.spinnerStart("Together", mActivity.getString(R.string.publish_publishing));
        }
        
        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            JSONObject json = null;
            String category = null;
            String event = null;
            String result = "1";
            String location = "";
            String token = mPublishActivity.mPrefs.getString(Member.PREFS_TOKEN, "");
            String memberId = mPublishActivity.mPrefs.getString(Member.PREFS_ID, "");
            StringBuilder sb = new StringBuilder();
            String url = sb.append(Config.HOST).append(Config.UPLOAD_PATH)
                    .append("?memberId=").append(memberId).append("&token=").append(token).toString();
            double lat = 0;
            double lng = 0;
            if(mPublishActivity.mLocation != null) {
                lat = mPublishActivity.mLocation.getLatitude();
                lng = mPublishActivity.mLocation.getLongitude();
                location = CommonUtil.getLocation(mActivity, lat, lng);
            }
            try {
                json  = new JSONObject(jsonData);
                category = json.getString("category");
                event = StringUtil.replaceLineFeed(json.getString("event"));
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                return ERROR;
            }
            // 要將 mOutputFileUri(圖片), 地理資訊, category, event, 帳號資訊 傳送給server          
            // 圖片上傳
            if(mPublishActivity.mImageUri != null) {
                Log.d(TAG, "uploading image...");
                result = FileUtil.uploadFile(mPublishActivity.mImageUri.getPath(), url);
            }
            Log.d(TAG, "result:" + result);
            // 如果圖片上傳成功就傳送活動資訊
            if(!"0".equals(result)) {
                Log.d(TAG, "upload success!");
                List<NameValuePair> postData = new ArrayList<NameValuePair>();
                postData.add(new BasicNameValuePair("category", category));
                postData.add(new BasicNameValuePair("content", event));
                postData.add(new BasicNameValuePair("lat", Double.toString(lat)));
                postData.add(new BasicNameValuePair("lng", Double.toString(lng)));
                postData.add(new BasicNameValuePair("location", location));
                if(mPublishActivity.mImageUri != null) {
                    postData.add(new BasicNameValuePair("imageUrl", mPublishActivity.mImageUri.getLastPathSegment()));
                }
                Event eventModel = Event.getInstance();
                result = eventModel.publish(memberId, token, postData).toString();
            } else {
                Log.d(TAG, "uploading failed");
                result = ERROR;
            }
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
            // 回活動列表
            if(result.equals(ERROR)) {
                Toast.makeText(mActivity, mActivity.getString(R.string.error_program), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.getInt("success") == 1) { // 回應成功
                        Toast.makeText(mActivity, mActivity.getString(R.string.publish_success), Toast.LENGTH_SHORT).show();
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
