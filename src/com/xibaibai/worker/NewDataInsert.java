package com.xibaibai.worker;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
 

public class NewDataInsert {
	 
	public static void newdataRec(Context c,String json){
		 
	     if (!TextUtils.isEmpty(json))
	     {
	    	    String fromaddr = "";
	    	    String time = "";
	    	    String subject = "";
	    	    String orderid = "";
	    	    String state="";
	           JSONObject customJson = null;           
	           try {
	               customJson = new JSONObject(json);               
	               if (!customJson.isNull("address")) {
	            	   fromaddr = customJson.getString("address");
	            	   subject = customJson.getString("subject");
	            	   time = customJson.getString("time");
	            	   orderid = customJson.getString("orderid");
	            	   state = customJson.getString("state");
	            	   if(Functions.db_msg==null )  Functions.DBtableInit(c,"mydb2");
	            	    
	            	   initdata(orderid, fromaddr, subject, time ,state,json);
	            	   
	            	   Functions.Toast(c, fromaddr + "\r\n" + subject);  
			           Functions.notify(c, fromaddr,  subject);
			           //updateImages(c,customJson.getJSONArray("goods"));
			            
	               }else if (!customJson.isNull("command")){
	            	   //order change command
	            	  if(customJson.getString("command").equals("multi_changestate"))
	            	  {
	            		  JSONArray jsonarray = customJson.getJSONArray("commandjson");
	            		  for(int i=0; i<jsonarray.length(); i++){
	            			  JSONObject jsCmd = jsonarray.getJSONObject(i);
	            			  state = jsCmd.getString("state");
			            	  orderid = jsCmd.getString("orderid");
			            	  Log.e("", "new command: orderid="+orderid +"  state="+state);
			            	  changeState(orderid,state );
	            		  }
	            		  
	            	  }else{
		            	  state = customJson.getString("state");
		            	  orderid = customJson.getString("orderid");
		            	  Log.e("", "new command: orderid="+orderid +"  state="+state);
		            	  changeState(orderid,state );
	            	  }
	            	  
	               }
	           } catch (JSONException e) {	               
	               e.printStackTrace();
	           } 
	     }
	    
		   
	 }
	  private static void updateImages(Context c,JSONArray jsonarray){
		  
		  for(int i=0; i < jsonarray.length(); i++)
		  {    JSONObject jsonGoods;
				try {
					   jsonGoods = jsonarray.getJSONObject(i);
					   String goodsimg = jsonGoods.getString("img");
					    RemoteImg rImg = new RemoteImg(c, null , 
				      		Functions.REMOTE_HOST+ "imgzoom.php?img=" + goodsimg);
				        rImg.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
		  }
		  
	  }		
	  
	  public static void changeState(String orderid, String state){
		  String filter = " orderid='"+orderid+"' ";
		  ContentValues curRecord = Functions.db_msg.readOneByFilter("messages", filter);
          if(curRecord==null)
          {
        	  Log.e("","找不到orderid=" + orderid);
        	  return;
          }

		  String details = curRecord.getAsString("details");
		     
			 try {JSONObject jsonObject=new JSONObject(details);
				jsonObject.put("state", state);
				details = jsonObject.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			curRecord.put("details", details); 
			curRecord.put("state", state);
			Functions.db_msg.write("messages", curRecord);
			
			
			Intent intent = new Intent();     
   	        intent.setAction("update_MsgListData");    	   	         
   	        Functions.cMainactivity.sendBroadcast(intent); 
			
   	        String newstateTxt = "";
   	        if(state.equals(orderFunc.STATE.CLEAN)){
   	        	newstateTxt = "已洗干净，请取回";
   	        }else if(state.equals(orderFunc.STATE.WASHING)){
   	        	newstateTxt = "洗衣店已收";
   	        } else if(state.equals(orderFunc.STATE.BACK)){
	        	newstateTxt = "物流派件中";
	        }
   	        
   	       Functions.Toast(Functions.cMainactivity, "订单:"+orderid +",状态改为:"+ newstateTxt);
		 
	  }
		public static void initdata(String orderid, String from, String content,String time, String state, String details){
			ContentValues values = new ContentValues() ;
			values.put("orderid", orderid);
			values.put("msgfrom", from);
			values.put("content", content);
			values.put("state", state);
			values.put("msgtime",  time );
			values.put("details",  details);
			
			Functions.db_msg.write("messages", values);
			 
		}
}
