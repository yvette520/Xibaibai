package com.xibaibai.worker;

 
import com.baidu.android.pushservice.PushManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivitySetting extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		Button btnlogout = (Button) findViewById(R.id.btnlogout);
		btnlogout.setOnClickListener(btnclicks);
		
		Button btncleanDB = (Button) findViewById(R.id.btncleanDB);
		btncleanDB.setOnClickListener(btnclicks);
		Button btnnotify = (Button) findViewById(R.id.btnnotify);
		btnnotify.setOnClickListener(btnclicks);
		Button btnpost = (Button) findViewById(R.id.btnpost);
		btnpost.setOnClickListener(btnclicks);
		
	}
    private Button.OnClickListener btnclicks = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
		  int vid = v.getId();
		 if(vid == R.id.btnlogout)	{
			 Pushutils.setConfig(ActivitySetting.this, "binduser", "");
			 Pushutils.setConfig( ActivitySetting.this, "baiduId", "");
	         Pushutils.setConfig( ActivitySetting.this, "baiduChannel", ""); 
	         
			 Functions.Toast(ActivitySetting.this, "logout done. ");
			  
			  Pushutils.isBind = false;
			  PushManager.stopWork(getApplicationContext()) ;
			  System.exit(0);
			  
			  /*
			  Intent i=new Intent(SettingActivity.this, LoginActivity.class);
			  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  startActivity(i);
			  */
			 
		 }else if(vid==R.id.btncleanDB){
			 Functions.DBclean();
			 Functions.Toast(ActivitySetting.this, "Database is clean. "); 
			    Intent intent = new Intent();  //Itent就是我们要发送的内容
		        intent.putExtra("data", "");   
		        intent.setAction("update_MsgListData");    
		        sendBroadcast(intent);   //发送广播
		 }else if(vid==R.id.btnnotify){		 
		      Functions.notify(Functions.cMainactivity, "fromaddr000",  "subject");  
		      //Functions.notify(SettingActivity.this, "fromaddr",  "subject");  
		 }else if(vid==R.id.btnpost){
			// Functions.Post(Functions.REMOTE_HOST+"test.php");
		 }
			
	}};
    
		
		
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
