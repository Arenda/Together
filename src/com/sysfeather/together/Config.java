package com.sysfeather.together;

import java.io.File;

import android.os.Environment;

public class Config {
    public static final String HOST = "http://220.133.188.197:81/";
    public static final String UPLOAD_PATH = "Together/File/";
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Together";
    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "796646124276";
    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.sysfeather.together.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "message";
}
