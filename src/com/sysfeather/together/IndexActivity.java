package com.sysfeather.together;

import java.util.List;

import com.sysfeather.together.js.IndexJs;
import com.sysfeather.together.js.JavaScriptInterface;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class IndexActivity extends TActivity {	
    private static final String TAG = "IndexActivity";
    public static final String EVENT_ID = "eventId";
    public Location mLocation;
    private final Criteria mCriteria = new Criteria();
    private LocationManager mLocationManager;
    private String mProvider;
    private LocationListener mBestProviderListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            reactToLocationChange(location);
            unregisterAllListeners();
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
            unregisterAllListeners();
        }
        
        public void onProviderDisabled(String provider) {
            
        }
        
        public void onProviderEnabled(String provider) {
            registerListener();
            mLocation = mLocationManager.getLastKnownLocation(provider);
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
        mWebView.addJavascriptInterface(new IndexJs(this),
                JavaScriptInterface.INTERFACE_NAME);
        mWebView.loadUrl(WWW_PATH + "index.html");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index, menu);
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
      mWebView.loadUrl("javascript:Together.android.init()");
      registerListener();
      mLocation = mLocationManager.getLastKnownLocation(mProvider);
      unregisterAllListeners();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, IndexActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.index_menu_add:
                //Intent addIntent = new Intent(this, PublishActivity.class);
                Intent addIntent = new Intent(this, PublishActivity.class);
                startActivity(addIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void reactToLocationChange(Location location) {
        mLocation = location;
        Log.d(TAG, "reactToLocationChange:" + location.getLatitude() + "," + location.getLongitude());
    } 
    
    private void registerListener() {
        unregisterAllListeners();
        String bestProvider = mLocationManager.getBestProvider(mCriteria, false);
        String bestAvailableProvider = mLocationManager.getBestProvider(mCriteria, true);
        Log.d(TAG, bestProvider + " / " + bestAvailableProvider);
        Looper looper = null;
        if (bestProvider == null) {
            Log.d(TAG, "No Location Providers exist on device.");
        } else if (bestProvider.equals(bestAvailableProvider)) {
            mLocationManager.requestSingleUpdate(bestAvailableProvider, mBestAvailableProviderListener, looper);
            mProvider = bestAvailableProvider;
        } else {
            mLocationManager.requestSingleUpdate(bestProvider, mBestProviderListener, looper);
            mProvider = bestProvider;
            if (bestAvailableProvider != null) {
                mLocationManager.requestSingleUpdate(bestAvailableProvider, mBestAvailableProviderListener, looper);
                mProvider = bestAvailableProvider;
            } else {
                List<String> allProviders = mLocationManager.getAllProviders();
                for (String provider : allProviders) {
                    mLocationManager.requestSingleUpdate(provider, mBestProviderListener, looper);
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
}
