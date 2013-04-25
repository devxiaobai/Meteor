package com.xiaobai.meteor;


import android.util.Log;

public class LogUtils {
	public static final boolean DEBUG = true;
	private static final String TAG = "IsMan";
	
	public static void d(String message) {
		if(DEBUG) {
			Log.d(TAG, message);
		}
	}
	
	public static void LOG(Object tag, String message) {

		if (DEBUG) {
			if (tag instanceof String) {
				Log.v((String) tag, message);
				return;
			}
			String t = tag == null ? "onRoad" : tag.getClass()
					.getSimpleName();
			Log.v(t, message);
		}

	}
}
