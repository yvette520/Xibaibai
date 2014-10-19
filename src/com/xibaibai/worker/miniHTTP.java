package com.xibaibai.worker;
/*
 * 
 * dingchengliang 2014.9.9
 * 
 **/
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

public class miniHTTP {
	enum METHOD
	{
		 GET,POST;
	}
	public METHOD method;
	public HashMap<String, String> postdata ;
	public String url;
	public Httpcallback callback;
	
	private Context mC;
	
	miniHTTP (){		
	}
	 miniHTTP (String murl){
		url= murl;
	}
	
	    
   public   String httpGet(String url) {
		String strResult = "";
		HttpGet httpRequest = new HttpGet(url);
		try {
			// HttpClient对象
			HttpClient httpClient = new DefaultHttpClient();
			// 获得HttpResponse对象
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			Log.d("xxx", "httpResponse.getStatusLine().getStatusCode():"
					+ httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的数据
				strResult = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				//Log.e("xxx", strResult);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			;
		}

		Log.d("strResult", strResult);
		return strResult;

	}
    
   public   String httpPost(String uriAPI, HashMap<String, String> postdata){
	 //Post运作传送变数必须用NameValuePair[]阵列储存
	   //传参数 服务端获取的方法为request.getParameter("name")
	   List <NameValuePair> params=new ArrayList<NameValuePair>();
	  // params.add(new BasicNameValuePair("name","this is post"));
	   // params.add(new BasicNameValuePair("address","beijing this is post"));
	   
	   
       Iterator iter = postdata.entrySet().iterator();  
       while (iter.hasNext()) {  
           Map.Entry entry = (Map.Entry) iter.next();  
           String key = entry.getKey().toString();  
           String val = entry.getValue().toString();  
           params.add(new BasicNameValuePair(key, val));
       }  
	   
	   Log.e("","post call:"+uriAPI);
	   HttpPost httpRequest =new HttpPost(uriAPI);
	   	   
	   
	   try{	    
	        //发出HTTP request
		    httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
		    //取得HTTP response
		    HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
	    
	        //若状态码为200 ok 
	    if(httpResponse.getStatusLine().getStatusCode()==200){
	       //取出回应字串
	        String strResult=EntityUtils.toString(httpResponse.getEntity());
	        Log.e("", strResult);
	        return strResult;
	    }else{
	       Log.e("","Error Response"+httpResponse.getStatusLine().toString());
	    }
	   }catch(ClientProtocolException e){
		   Log.e("", e.getMessage().toString());
	    e.printStackTrace();
	   } catch (UnsupportedEncodingException e) {
		   Log.e("",e.getMessage().toString());
	       e.printStackTrace();
	   } catch (IOException e) {
		   Log.e("",e.getMessage().toString());
	       e.printStackTrace();
	   }
	   return null;
  }

   public   void Http(String murl, Httpcallback mcallback, METHOD msd) {	
	   method = msd;
	   callback = mcallback;
	   url = murl;
	   Http();
   }
   
   public   void Http(String murl, Httpcallback mcallback) {	   
	   callback = mcallback;
	   url = murl;
	   Http();
   }
   public   void Http(Httpcallback mcallback) {	   
	   callback = mcallback;
	   Http();
   }
   
    public   void Http() {
		//Functions.showWait(c,"服务器处理中....");
		MiniTask httptask = new MiniTask();
		httptask.setTask(new MiniTask.Task(){
			@Override
			public String DoTask() {			 
				 
				 if(method==METHOD.POST || postdata !=null){
					 return httpPost(url, postdata); 
				 }else{
				     return	httpGet(url);  
				 }
				 
			}

							
		});
		httptask.setOnTaskDoneListener( new MiniTask.OnTaskDoneListener() {			
			@Override
			public void OnTaskDone(String  data){
				//Functions.waitdialog.dismiss();
				//获得返回的结果 并处理
				callback.complete(data);
			  return;
			}
		});		
	httptask.start();// 启动线程了;
  }
	
	 
	
}


interface Httpcallback {   
	 public void   complete(String data);    
} 
