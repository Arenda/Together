package com.sysfeather.together.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class StringUtil {
    public static String replaceLineFeed(String input) {
        if(input != null) {
            return input.replace("'", "\\'").replace("\\r\\n\\r\\n", "</p><p>").replace("\\n\\n", "</p><p>")
                .replace("\\r\\n", "<br />").replace("\\n", "<br />");
        } else {
            return null;
        }
    }
    
    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()) {
           int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
