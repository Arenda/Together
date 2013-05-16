package com.sysfeather.together;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import com.sysfeather.together.js.PublishJs;
import com.sysfeather.together.js.JavaScriptInterface;
import com.sysfeather.together.model.TWebViewClient;
import com.sysfeather.together.util.FileUtil;
import com.sysfeather.together.util.StringUtil;


public class PublishActivity extends TActivity {
    private static final String TAG = "PublishActivity";
    public static final int TAKE_PICTURE = 1;
    public static final int SELECT_PICTURE = 2;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private static final int THUMBNAIL_WIDTH = 150;
    private static final int THUMBNAIL_HEIGHT = 150;
    private static int UPDATE_TIME = 5000; // 5秒
    private static int UPDATE_DISTANCE = 0; // 1公里
    public Uri mOutputFileUri; // 原始圖片
    public Uri mImageUri; // 壓縮後圖片
    public String mThumbnailPath; // 預覽圖
    public String mPostData; // 表單資訊
    public Location mLocation; // 地理資訊
    private String mProvider;
    private String mSelectedImagePath;
    private String mFilemanagerstring;
    private final Criteria mCriteria = new Criteria();
    private LocationManager mLocationManager;
    private LocationListener mBestProviderListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            reactToLocationChange(location);
        }
        
        public void onProviderDisabled(String provider) {
            
        }
        
        public void onProviderEnabled(String provider) {
            registerListener();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            
        }
    };
    
    private LocationListener mBestAvailableProviderListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            reactToLocationChange(location);
        }
        
        public void onProviderDisabled(String provider) {
            
        }
        
        public void onProviderEnabled(String provider) {
            registerListener();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 抓地理資訊            
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        mCriteria.setAltitudeRequired(false);
        mCriteria.setBearingRequired(false);
        mCriteria.setSpeedRequired(false);
        mCriteria.setCostAllowed(true);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mWebView.addJavascriptInterface(new PublishJs(this),
                JavaScriptInterface.INTERFACE_NAME);
        mWebView.loadUrl(WWW_PATH + "publish.html");
        mWebView.setWebViewClient(new TWebViewClient() {
            @Override  
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(mWebView, url);
                fireJs();
            }  
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.publish, menu);
        return true;
    }
    
    @Override
    protected void onPause() {
      unregisterAllListeners();
      super.onPause();
    }

    @Override
    protected void onResume() {
      super.onResume();
      registerListener();
      mLocation = mLocationManager.getLastKnownLocation(mProvider);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {  
                if(data != null) {
                    if(data.hasExtra("data")) { // default return a thumb nail
                        Log.d(TAG, "thumbnail");
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        thumbnail.compress(CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                        mThumbnailPath = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                        try {
                            bos.close();
                        } catch (IOException e) {
                            Log.d(TAG, e.toString());
                        }
                        thumbnail.recycle();
                    } else {
                        Log.d(TAG, "line camera...");
                    }
                } else { 
                    // 壓縮原圖，最後會上傳至server
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Bitmap bitmap = FileUtil.compressImage(mOutputFileUri, WIDTH, HEIGHT);
                    bitmap.compress(CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                    String fileName = FileUtil.genFileName("jpg");
                    File file = new File(Config.SDCARD_PATH, fileName);
                    if(file.exists()) {
                        file.delete();
                    }
                    try {
                        file.createNewFile();
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(file);
                        fo.write(bos.toByteArray());
                        // remember close FileOutput
                        fo.close();
                    } catch (IOException e1) {
                        Log.e(TAG, e1.toString());
                    }
                    mImageUri = Uri.fromFile(file);
                    bitmap.recycle();
                    // 預覽圖
                    bos.reset();
                    Bitmap thumbnail = FileUtil.compressImage(mOutputFileUri, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                    thumbnail.compress(CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                    mThumbnailPath = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                    try {
                        bos.close();
                    } catch (IOException e) {
                        Log.d(TAG, e.toString());
                    }
                    thumbnail.recycle();
                }
                fireJs();
            } else if(requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                // io FILE Manager
                mFilemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                mSelectedImagePath = StringUtil.getRealPathFromURI(this, selectedImageUri);

                // NOW WE HAVE OUR WANTED STRING
                if(mSelectedImagePath != null) {
                    Log.d(TAG, "selectedImagePath: " + mSelectedImagePath);
                } else {
                    Log.d(TAG, "filemanagerstring: " + mFilemanagerstring);
                }
            }
        } else {
            Log.d(TAG, "delete temp files...");
            FileUtil.deleteRecursive(new File(mOutputFileUri.getPath()));
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish_menu_publish:
                mWebView.loadUrl("javascript:Together.android.formSerialize();");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
    }
    
    private void reactToLocationChange(Location location) {
        Log.d(TAG, "reactToLocationChange: " + location.getLatitude() + "," + location.getLongitude());
        mLocation = location;
    } 
    
    private void registerListener() {
        unregisterAllListeners();
        String bestProvider = mLocationManager.getBestProvider(mCriteria, false);
        String bestAvailableProvider = mLocationManager.getBestProvider(mCriteria, true);
        if (bestProvider == null) {
            Log.d(TAG, "No Location Providers exist on device.");
        } else if (bestProvider.equals(bestAvailableProvider)) {
            mLocationManager.requestLocationUpdates(bestAvailableProvider, 
                    UPDATE_TIME, UPDATE_DISTANCE, mBestAvailableProviderListener);
            mProvider = bestAvailableProvider;
        } else {
            mLocationManager.requestLocationUpdates(bestProvider,
                    UPDATE_TIME, UPDATE_DISTANCE, mBestProviderListener);
            mProvider = bestProvider;
            if (bestAvailableProvider != null) {
                mLocationManager.requestLocationUpdates(bestAvailableProvider, 
                        UPDATE_TIME, UPDATE_DISTANCE, mBestAvailableProviderListener);
                mProvider = bestAvailableProvider;
            } else {
                List<String> allProviders = mLocationManager.getAllProviders();
                for (String provider : allProviders) {
                    mLocationManager.requestLocationUpdates(provider, 0, 0, mBestProviderListener);
                }
                mProvider = bestProvider;
                Log.d(TAG, "No Location Providers currently available.");
            }
        }
    }
    
    private void unregisterAllListeners() {
        mLocationManager.removeUpdates(mBestProviderListener);
        mLocationManager.removeUpdates(mBestAvailableProviderListener);
    }
    
    private void fireJs() {
        if(mThumbnailPath != null) {
            // check if the SD card exists
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // callback after the WebView loaded
                mWebView.loadUrl("javascript:setTimeout(function() { Together.android.takePhotoCallback() }, 500)");
            } else {
                Log.d(TAG, "No SDCARD");
            }
        }
    }
    
    private void destroy() {
        FileUtil.deleteRecursive(new File(Config.SDCARD_PATH));
    }
}
