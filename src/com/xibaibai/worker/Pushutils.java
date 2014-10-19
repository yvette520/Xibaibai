package com.xibaibai.worker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class Pushutils {
	 public static final String TAG = "PushActivity";
	    public static final String RESPONSE_METHOD = "method";
	    public static final String RESPONSE_CONTENT = "content";
	    public static final String RESPONSE_ERRCODE = "errcode";
	    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
	    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
	    public static final String EXTRA_MESSAGE = "message";
 
	    public static boolean isBind = false;
        public static boolean is_dbreciverOn=false;
	    // 鑾峰彇ApiKey
	    public static String getMetaValue(Context context, String metaKey) {
	        Bundle metaData = null;
	        String apiKey = null;
	        if (context == null || metaKey == null) {
	            return null;
	        }
	        try {
	            ApplicationInfo ai = context.getPackageManager()
	                    .getApplicationInfo(context.getPackageName(),
	                            PackageManager.GET_META_DATA);
	            if (null != ai) {
	                metaData = ai.metaData;
	            }
	            if (null != metaData) {
	                apiKey = metaData.getString(metaKey);
	            }
	        } catch (NameNotFoundException e) {

	        }
	        return apiKey;
	    }

	 

	    public static void setConfig(Context context, String key, String value) {	         
	        SharedPreferences sp = PreferenceManager
	                .getDefaultSharedPreferences(context);
	        Editor editor = sp.edit();
	        editor.putString(key, value); 
	        editor.commit();
	    }
	    
	    public static String getConfig(Context context, String key){
	    	SharedPreferences sp = PreferenceManager
	                .getDefaultSharedPreferences(context);
	        return sp.getString(key, "");
	    }
	      

	 
 

}

