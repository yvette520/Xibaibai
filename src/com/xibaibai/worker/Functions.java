package com.xibaibai.worker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class Functions {
	
    public static String REMOTE_HOST ="http://ding.scicompound.com/xibaibai/";
	public static DBclass db_msg = null ;
	
	
	 Functions (){
		
	}
	
	public static Activity cMainactivity=null;
	
	
    public static void DBtableInit(Context c, String tablename){    	
    	  if (db_msg!=null)  {    		  
    		return;
    	}
		  db_msg = new DBclass(c, tablename , DATABASE_CREATE);
		  //db_msg.execSQL("DROP TABLE IF EXISTS messages");
		  db_msg.execSQL(DATABASE_CREATE);
    }
    public static void DBclean(){
    	db_msg.execSQL("DROP TABLE IF EXISTS messages");
		db_msg.execSQL(DATABASE_CREATE);    	
    }
    
    
    
	public static String MD5(String val) {    
	        MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        md5.update(val.getBytes());    
	        byte[] m = md5.digest();//加密     
	        return byte2str(m);    
	}    
	private static String byte2str(byte[] b){    
	        StringBuffer sb = new StringBuffer();    
	         for(int i = 0; i < b.length; i ++){    
	          sb.append(b[i]);    
	         }    
	         return sb.toString();    
	}    
 
	
public static void Toast(Context c,  String Msg){	
	Toast.makeText(c, Msg, Toast.LENGTH_LONG).show();
}


static ProgressDialog waitdialog;
public static void showWait(Context c, String msg) {
	waitdialog = new ProgressDialog(c);
	// waitdialog.setTitle("Indeterminate");
	waitdialog.setMessage(msg);
	waitdialog.setIndeterminate(true);
	waitdialog.setCancelable(true);
	Log.d("====", "waitdialog show");
	waitdialog.show();
}
 
@SuppressWarnings("deprecation")
public static void notify(Context c, String title, String Message){
	   NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);               
	   Notification n = new Notification(R.drawable.ic_launcher,  Message, System.currentTimeMillis());  
	   n.defaults = Notification.DEFAULT_ALL;  
	   
	   n.flags |= Notification.FLAG_AUTO_CANCEL; 
	  // n.flags |= Notification.FLAG_NO_CLEAR; 
	   
	   n.defaults |= Notification.DEFAULT_LIGHTS;  
	   n.ledARGB = 0xff00ff00; 
	   n.ledOnMS = 1000; 
	   n.ledOffMS = 1000; 	   
	   n.flags |= Notification.FLAG_SHOW_LIGHTS;  
	   
	   //n.sound=Uri.parse("android.resource://" + c.getPackageName() + "/" +R.raw.mm); 
	   
	   Intent i = new Intent(c, ActivityLogin.class);
	   //i.setAction(Intent.ACTION_MAIN);
	   i.addCategory(Intent.CATEGORY_LAUNCHER);
	   i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
	   //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	   //PendingIntent
	  // int notifyid = R.string.app_name;
	   int notifyid = (int) (new Date()).getTime();
	   PendingIntent contentIntent = PendingIntent.getActivity(
	           c,  
	           notifyid, 
	           i, 
	           PendingIntent.FLAG_UPDATE_CURRENT);
	                    
	   n.setLatestEventInfo(
	           c,
	           title, 
	           Message, 
	           contentIntent);
	   nm.notify(notifyid, n); 
	  
   }

   
   
	 private static String DATABASE_CREATE =
				"CREATE TABLE IF NOT EXISTS messages (_id integer primary key autoincrement, "
				+ "orderid text not null, msgfrom text not null, content text not null, "
				+ "state text not null, details text not null,"
				+ "msgtime text null);";


	 
	
}
