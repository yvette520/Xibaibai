package com.xibaibai.worker;  
import android.app.Activity;
import android.app.ActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle; 
 
import android.util.Log;
import android.widget.TabHost; 

 
@SuppressWarnings("deprecation")
public class ActivityMain extends TabActivity {
	Intent newPage = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_main);
		  
		Functions.cMainactivity=ActivityMain.this;
		
		Log.e("MainActivity", " mainactivity onCreate");
		
		
		final TabHost tabHost = getTabHost();

		//LayoutInflater.from(this).iate(R.layout.test, tabHost.getTabContentView(), true);
		
		newPage=new Intent(this,  ActivityMsg.class); 
		newPage.putExtra("state", "copying");
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("新件")
                .setContent(newPage));

        
        Intent goingPage=new Intent(this,  ActivityMsg.class);
//		  Bundle bd = new Bundle();
//		  bd.putString("hello", message);
        
        goingPage.putExtra("state", "ongoing");        
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("取回",  getResources().getDrawable(R.drawable.btnbg))
                .setContent(goingPage));
      
        Intent donePage=new Intent(this,  ActivityMsg.class);
        donePage.putExtra("state", "done");        
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("已完成",  getResources().getDrawable(R.drawable.btnbg))
                .setContent(donePage));
        
        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator("其他")
                .setContent(new Intent(this, ActivitySetting.class)
                 ));
        
       // startForegound();
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        am.moveTaskToFront(getTaskId(), BIND_IMPORTANT);
 		   
	} 
	 
 
 
}
