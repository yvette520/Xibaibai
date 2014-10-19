package com.xibaibai.worker;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


/*
 *download and set 
 * RemoteImg task = new RemoteImg(c,vi,url);
 *       task.execute();
 * 
 * download  only
 *  RemoteImg task = new RemoteImg(c，null,url);
 *       task.execute();   
 * 
 * */
 
public class RemoteImg extends AsyncTask<String, Integer, String>{
	
	static final String TAG = "HTTP";
    private static String tmpPicDir = "pic";
    Context mContext = null;
    String mImageUrl="";
    String newPath = "";
    ImageView mImgVi=null;
    
    RemoteImg (Context context, ImageView imgVi, String imageUrl ){
       mContext  = context;
       mImageUrl = imageUrl;
       mImgVi    = imgVi;
    }
	// 可变长的输入参数，与AsyncTask.exucute()对应  
    @Override  
    protected String doInBackground(String... params) {  
    	 File destDir = mContext.getDir(tmpPicDir, Context.MODE_PRIVATE);    	  
         if(destDir.exists()){  
        	 destDir.mkdir();
         }
         
    	 newPath = destDir +"/" + Functions.MD5(mImageUrl) +".png";
    	 File newfileFile = new File(newPath); 
    	  Date dt= new Date();
    	  Long time= dt.getTime();
         if(newfileFile.exists() &&  time - newfileFile.lastModified() < 30*3600*24){  
             ;//not update
    	 }else{
    		 saveRemoteUrl(newPath);
    	 } 
    	 return null;
    }
    
    private void saveRemoteUrl(String tofile){    	 
         Log.e("RemoteImg", "load Image: " + mImageUrl);
         
    	 HttpGet httpRequest = new HttpGet(mImageUrl);  
	        //取得HttpClient 对象  
	        HttpClient httpclient = new DefaultHttpClient();  
	        try {  
	            //请求httpClient ，取得HttpRestponse  
	            HttpResponse httpResponse = httpclient.execute(httpRequest);  
	            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
	                //取得相关信息 取得HttpEntiy  
	                HttpEntity httpEntity = httpResponse.getEntity();  
	                //获得一个输入流  
	                InputStream is = httpEntity.getContent();  
	                //System.out.println(is.available());  
	                //System.out.println("Get, Yes!");  
	                Bitmap bitmap = BitmapFactory.decodeStream(is);  
	                is.close();  
	                
	                saveFile(bitmap, tofile);
	            }  
	              
	        } catch (ClientProtocolException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        return ;  
	        
    }
    private void saveFile(Bitmap bm, String fileName) throws IOException {  
        if(bm==null) return;
         
        File myCaptureFile = new File(fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.PNG, 80, bos);  
        bos.flush();  
        bos.close();  
    }  
    
    @Override  
    protected void onCancelled() {  
        super.onCancelled();  
    }  
    @Override  
    protected void onPostExecute(String result) { // finished    	 
   	    File newfileFile = new File(newPath); 
    	if(newfileFile.exists() && mImgVi != null){ 
    	   mImgVi.setImageURI(Uri.parse(newPath));
    	}
    	/*URL picUrl = new URL(" http://www.souchiwang.com/images/user_3.jpg");
    	Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream()); 
    	imageView.setImageBitmap(pngBM);
    	既可实现andorid的imageView控件显示网络图片
    	*/
    }  
    @Override  
    protected void onPreExecute() {  
        // start 
    }  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        // progress  
        
    }  
	
}



     
