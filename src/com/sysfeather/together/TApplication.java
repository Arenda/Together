package com.sysfeather.together;

import android.app.Application;
import android.content.res.Configuration;

/**
 * 
 * @author Arenda
 *
 */
public class TApplication extends Application {
	private static TApplication singleton;
	
	public static TApplication getInstance() {
		return singleton;
	}
	
	@Override
	public final void onCreate() {
		super.onCreate();
		singleton = this;
	}
	
	@Override
	public final void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public final void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}
	
	@Override
	public final void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
