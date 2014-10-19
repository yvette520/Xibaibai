package com.xibaibai.worker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityDetail extends  ListActivity  {
	 GoodsListAdapter mAdapter;
	   JSONArray jsonarray = new JSONArray();
	   
	    private int recordID = 0;
	    private String mTel =null;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Use a custom layout file
	        setContentView(R.layout.activity_detail);	        
	        // Tell the list view which view to display when the list is empty
	        getListView().setEmptyView(findViewById(R.id.empty));
	        
	        //db_msg = new DBclass(DetailActivity.this,"mydb2", DATABASE_CREATE);
	        
	        Bundle bundle=getIntent().getExtras();
	        String details =  bundle.getString("details");
	        //Log.e("details==",  ""+ (details) );
	        recordID =  bundle.getInt("id"); 
	        
	        
	         restoreInfo(details);
	         updateData (details);
			
	        // Set up our adapter
	        mAdapter = new GoodsListAdapter(this);
	        setListAdapter(mAdapter);
	      
	        
	         
	        Button calluser = (Button) findViewById(R.id.calluser);
	        calluser.setOnClickListener(  btn_listener );
	        Button btndone = (Button) findViewById(R.id.btndone);
	        btndone.setOnClickListener(  btn_listener );
	        Button addcloth = (Button) findViewById(R.id.addclothes);
	        addcloth.setOnClickListener(  btn_listener );
	        
	        orderFunc.setOrderBtnFace(btndone, addcloth, recordID);
	        
	        

	        
	    }
	  
	    private void updateData(String json){
            
            try {
            	JSONObject jsonObject=new JSONObject(json);
				jsonarray = jsonObject.getJSONArray("goods");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	      
	    }
	    
	    
	    private OnClickListener btn_listener = new OnClickListener() 
	    {   @Override 
	       public void onClick(View v) {
           if(v.getId()==R.id.btndone)
           {
        	   orderFunc.orderBtnClick(ActivityDetail.this, recordID);
        	    
	    	     
           }
           else if(v.getId()==R.id.calluser)
           {
        	    Uri smsToUri = Uri.parse("tel:" + mTel );           
				Intent it = new Intent(Intent.ACTION_CALL, smsToUri);             
				//it.putExtra("sms_body", msgbody);             
				startActivity(it);
        	   
           }
           else if(v.getId()==R.id.addclothes)
           {           
				Intent it = new Intent(ActivityDetail.this, ActivityChoose.class);
				//Intent i=new Intent(LoginActivity.this,  PushActivity.cla
				//it.putExtra("sms_body", msgbody);  
				it.putExtra("id", recordID); 
				startActivityForResult(it, 100);
        	   
           }
	      
	    } } ;
	    
	    @Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	    {  
	         
	        if(700==resultCode)  
	        {  
	            float paid=  (data.getExtras().getFloat("paid")); 
	            
	            ((TextView) findViewById(R.id.total)).setText( ""+ paid);
	            ((TextView) findViewById(R.id.paid)).setText( ""+ paid);
	        	ContentValues curRecord = Functions.db_msg.readById("messages",recordID);
	     	    String details = curRecord.getAsString("details");
	     	   updateData (details);
	        	mAdapter.notifyDataSetChanged();
	            
	        }  
	        super.onActivityResult(requestCode, resultCode, data);  
	    }  
	    
	    private void restoreInfo(String details){
            String 	json = details;
           
	        JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(json);
				String total = jsonObject.getString("total");
				 
				Log.e("","getfrom detal:  total=" + total);
				
				String address =  jsonObject.getString("address");
				String tel =  jsonObject.getString("tel");
				mTel = tel;
				String paytype =  jsonObject.getString("paytype");
				String orderid =  jsonObject.getString("orderid");
				
				((TextView) findViewById(R.id.total)).setText( ""+total);
				((TextView) findViewById(R.id.paid)).setText( ""+total);
				((TextView) findViewById(R.id.address)).setText( ""+address);
				((TextView) findViewById(R.id.tel)).setText( ""+tel);
				((EditText) findViewById(R.id.packageid)).setText( ""+orderid);
				
				/*if(!paytype.equals("paid"))
				{  ((TextView) findViewById(R.id.paytype)).setText( "支付: 已支付");
				}else{
					((TextView) findViewById(R.id.paytype)).setText( "支付: 【货到付款】");	
					//((TextView) findViewById(R.id.paytype)).setBackgroundResource(android.R.drawable.btn_dialog);
				}*/
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    
	    
	    
	    

	    /**
	     * A simple adapter which maintains an ArrayList of photo resource Ids. 
	     * Each photo is displayed as an image. This adapter supports clearing the
	     * list of photos and adding a new photo.
	     *
	     */
	    public class GoodsListAdapter extends BaseAdapter {
	    	private LayoutInflater mInflater;
	        private Integer[] mPhotoPool = {
	        		 R.drawable.logo,  R.drawable.logo,  R.drawable.logo, 
	        		 R.drawable.logo,  R.drawable.logo,  R.drawable.logo, 
	        		 R.drawable.logo,  R.drawable.logo,  R.drawable.logo};
	     
	        
	        private ArrayList<Integer> mPhotos = new ArrayList<Integer>();
	        
	        public GoodsListAdapter(Context c) {
	            mContext = c;
	            mInflater = LayoutInflater.from(c);
	            
	        }

	        public int getCount() {
	            return  jsonarray.length();
	        }

	        public Object getItem(int position) {
	            return position;
	        }

	        public long getItemId(int position) {
	            return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            // Make an ImageView to show a photo
	        	 ViewHolder holder;
	        	 String goodsimg = "", texttitle = null, txtprice = null, txtnum = null;
	        	 try {
					JSONObject jsonGoods = jsonarray.getJSONObject(position);
					//goodsimg = jsonGoods.getString("img");
					texttitle = jsonGoods.getString("title");
					txtprice = jsonGoods.getString("price");
					txtnum = jsonGoods.getString("num");
					 
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	 
		            // When convertView is not null, we can reuse it directly, there is no need
		            // to reinflate it. We only inflate a new View when the convertView supplied
		            // by ListView is null.
		            if (convertView == null) 
		            {
		                convertView = mInflater.inflate(R.layout.listitem_detail, null);

		                // Creates a ViewHolder and store references to the two children views
		                // we want to bind data to.
		                holder = new ViewHolder();
		                if(goodsimg.length() > 0){
			                holder.img = (ImageView) convertView.findViewById(R.id.detailpic);
			               //本项目按衣物类型固定资源图片
			                /*RemoteImg rImg = new RemoteImg(mContext, holder.img , 
			                		Functions.REMOTE_HOST+  "imgzoom.php?img=" +  goodsimg);			                
			                rImg.execute();
			                */
		                }
		                
		                holder.title = (TextView) convertView.findViewById(R.id.texttitle);
		                holder.price = (TextView) convertView.findViewById(R.id.txtprice);             
		                holder.num = (TextView) convertView.findViewById(R.id.txtnum);  
		                
		                convertView.setTag(holder);
		            } else {
		                // Get the ViewHolder back to get fast access to the TextView
		                // and the ImageView.
		                holder = (ViewHolder) convertView.getTag();
		            }		           
		             
	               // if(convertView.getBackground() == null)
	              //  {convertView.setBackgroundResource(bgrid);
	               //  convertView.getBackground().setAlpha(200);
	             //   }
	                holder.title.setText( texttitle);
	                holder.price.setText("￥"+txtprice );
	                holder.num.setText( "x "+txtnum);
	                
	               // holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);
		            return convertView;
	        }
	        class ViewHolder {
	        	ImageView img;	TextView title;TextView price;TextView num;
	             
	        }

	        private Context mContext;

	        public void clearPhotos() {
	            //mPhotos.clear();
	            notifyDataSetChanged();
	        }
 


	    }
}
