/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sysfeather.together.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.sysfeather.together.Config;
import com.sysfeather.together.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtil {
    /**
     * Tag used on log messages.
     */
    static final String TAG = "CommonUtil";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Log.d(TAG, "message:" + message);
        Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Config.EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    /**
     * Get the address by lat lng
     * @param activity
     * @param lat
     * @param lng
     * @return
     */
    public static String getLocation(Activity activity, double lat, double lng) {
        Geocoder gc = new Geocoder(activity, Locale.getDefault());
        String location = activity.getString(R.string.unknown_location);
        if(Geocoder.isPresent()) {
           try {
               List<Address> addresses = gc.getFromLocation(lat, lng, 1);
               StringBuilder sb = new StringBuilder();
               if(addresses.size() > 0) {
                   Address address = addresses.get(0);
                   for(int i = 0; i < address.getMaxAddressLineIndex(); ++i) {
                       Log.d(TAG, address.getAddressLine(i));
                       // delete zip code
                       sb.append(address.getAddressLine(i).replace(address.getPostalCode(), ""));
                   }
                   Log.d(TAG, address.getLocality());
                   Log.d(TAG, address.getPostalCode());
                   Log.d(TAG, address.getCountryName());
                   location = sb.toString();
               }
           } catch(IOException e) {
               Log.d(TAG, e.toString());
           }
        }
        return location;
    }
}
