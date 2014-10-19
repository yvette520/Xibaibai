package com.xibaibai.worker;

 

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class orderFunc {
	public static statestring STATE = null;
	public static int curRecordID;
	class statestring{
		 public static final String CREATE = "create";
		 public static final String COPY = "copy";
		 public static final String WASHING = "washing";
		 public static final String CLEAN = "clean"; //分拣完全待取回
		 public static final String BACK = "back";
		 public static final String DONE = "done";
		
	};
	orderFunc(){
		 STATE=new statestring();		 
	}
	 public static void setOrderBtnFace(Button bt, Button addbt, int  recordID  ){
		 ContentValues cv1= Functions.db_msg.readById("messages",recordID);
  	     String state = cv1.getAsString ("state");
  	   
		if(state.equals(STATE.CREATE)) 
		{ bt.setText("确定收单");
     	  //bt.setBackgroundColor(0xFFee7700);
     	  //bt.setBackgroundColor(0xFF009900);     	  
     	}else if(state.equals(STATE.COPY)) 
		{ bt.setText("请送往洗衣店");
		    bt.setBackgroundColor(0xFF009900);
		    bt.setEnabled(false);    	  
	   	}
     	/*else if(state.equals(STATE.WASHING) || state.equals(STATE.CLEAN) ){  //ver2 删掉此行
	   		bt.setText("确定取回");
		   // bt.setBackgroundColor(0xFFee7700);
	   	}*/
     	else if(state.equals(STATE.WASHING) ){
	   		bt.setText("清洗中");
		    bt.setBackgroundColor(0xFF009900);
		    bt.setEnabled(false);
	   	}
	   	else if(state.equals(STATE.CLEAN)) 
		{ 	   	bt.setText("清洗完成，请取回");
			    bt.setBackgroundColor(0xFF009900);
			    bt.setEnabled(false);    	  
	   	}else if(state.equals(STATE.BACK)) 
		{ bt.setText("确定送达");
	   	  //bt.setBackgroundColor(0xFFee7700);
	   	  //bt.setBackgroundColor(0xFF009900);     	  
	   	}else if(state.equals(STATE.DONE)) 
		{ bt.setText("已完成");
	   	  //btndone.setBackgroundColor(0xFFee7700);
	   	  bt.setBackgroundColor(0xFF666666);
	   	  bt.setEnabled(false);
	   	}
		
		if(!state.equals(STATE.CREATE) && !state.equals(STATE.COPY)){
		  addbt.setBackgroundColor(0x66666666);
	   	  addbt.setEnabled(false);
	   	  addbt.setVisibility(View.GONE);
		}
		
		 
	 }
	
	 
	 public static void orderBtnClick(final Context c, final int recordID){
		 DialogInterface.OnClickListener create2copy_callback = new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if(recordID == 0){return;}
					
					curRecordID = recordID;
					  
					  ContentValues cv1= Functions.db_msg.readById("messages",curRecordID);
					  String curState = cv1.getAsString ("state");	 
			    	   String newState ="";
		    		if(curState.equals(STATE.CREATE))
		    		{  
		    		   copyDone(c, recordID);
		    		}else if(curState.equals(STATE.COPY))
		    		{  //change state by server;
		    			newState =  STATE.WASHING;		    		   
		    		}/*else if(curState.equals(STATE.WASHING ) || curState.equals(STATE.CLEAN)) //ver2 删掉此行
		    		{  //change state by server;
		    			newState= STATE.BACK;
		    		}*/else if(curState.equals(STATE.WASHING ))
		    		{  //change state by server;
		    			newState= STATE.CLEAN;
		    		}else if(curState.equals(STATE.CLEAN) )
		    		{  //change state by server;
		    			newState= STATE.BACK;
		    		}else if(curState.equals(STATE.BACK))
		    		{   orderDone(c, recordID);
		    		   newState =STATE.DONE;
		    		}else{
		    			newState = STATE.CREATE;
		    		}
		    		
		    		  cv1.put("state", newState);		    		
		    		 // Functions.db_msg.write("messages", cv1);
		    		  
		    		  Functions.Toast(c,  curState + "->new state: " + newState); 
					
		    		 ((Activity) c).finish();
		    		  
				}
			};				
		 		
	   DialogInterface.OnClickListener	yescallback  =	create2copy_callback;
	   
	   
 	   AlertDialog s = new AlertDialog.Builder(c)   
     	.setTitle("确认")  
     	.setMessage("您确定要 修改订单状态 吗？")  
     	.setPositiveButton("是", yescallback)  
     	.setNegativeButton("否", null)  
     	.setCancelable(false)
     	.show(); 
	 }
	
	 private static void copyDone(Context c, int recordID){
		 //TODO @yipei 收件结果  发送给服务器
		 //从数据库读取
		 //goods, paid, package id.
		 //用URL post方法
		 String baiduId = Pushutils.getConfig( c, "baiduId");;
		 ContentValues cv1= Functions.db_msg.readById("messages",recordID);
  	     String curDetails = cv1.getAsString ("details");
  	      
  	      try {
  	    	  JSONObject post=new JSONObject(curDetails);
			  post.put("workerbaiduid", baiduId);
			  
			  curDetails = post.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
  	      
		 orderHttp("copy", curDetails, copyDone_complete);
		    

   	        
	 }
	 
	  static Httpcallback copyDone_complete= new Httpcallback(){										 
			public void complete(String data) {
				// TODO Auto-generated method stub
				changeState	(STATE.COPY);
				
				
	 }};
			
	 private static void changeState(String state){
		  ContentValues cv1= Functions.db_msg.readById("messages",curRecordID);
		  String curState = cv1.getAsString ("state");	 
		  cv1.put("state", state);	
		  
		   String details = cv1.getAsString("details");		     
			 try {JSONObject jsonObject=new JSONObject(details);
				jsonObject.put("state", state);
				details = jsonObject.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      Functions.db_msg.write("messages", cv1);
	      
		       Intent intent = new Intent();  
	   	        //intent.putExtra("data", message);   
	   	        intent.setAction("update_MsgListData");    	   	         
	   	       Functions.cMainactivity.sendBroadcast(intent); 
	   	        
	 }
	 
	 
	 //shop rec & washing
	 private static void beWashing(int recordID){
		 //TODO @yipei  should be call by server push
		 //DONE
	 }
	 
	 //clean and waiting back
	 private static void CleanDone(int recordID){
		 //TODO @yipei  should be call by server push
		  //DONE
	 }	
	 //is back, and waiting to done
	 private static void BackDone(int recordID){
		 //TODO @yipei  should be call by server push
		 //DONE
	 }
	 //finish the order
	 private static void orderDone(Context c, int recordID){
		 //TODO @yipei  交付完毕
		 //发生消息给服务器 
		 String postStr="";
		 String baiduId = Pushutils.getConfig( c, "baiduId");;
		 
		 ContentValues cv1= Functions.db_msg.readById("messages",recordID);
  	     String orderid = cv1.getAsString ("orderid");
  	     
		 try {
 	    	  JSONObject post=new JSONObject();
			  post.put("workerbaiduid", baiduId);
			  post.put("orderid", orderid);
			  postStr = post.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 orderHttp("done", postStr, orderDone_complete);
	 }
	 
	  static Httpcallback orderDone_complete= new Httpcallback(){										 
			public void complete(String data) {
				// TODO Auto-generated method stub
				changeState	(STATE.DONE);	
				Functions.Toast(Functions.cMainactivity, "订单:" +",状态改为:完成");
	 }};
	  
	 
	 
	 private static void orderHttp( final String action, final String Postdata,  Httpcallback callback) {		  
				  
			Log.e("post", Postdata);
			HashMap<String, String> hm=new HashMap<String, String>();
			hm.put("POST", Postdata);
			
			String url = Functions.REMOTE_HOST +"act.php?target=orders&from=app&func="+action;
			miniHTTP http = new miniHTTP(url);					 
			http.postdata=hm;
			http.callback=callback;
			http.Http();
					 
	   }
		

}


