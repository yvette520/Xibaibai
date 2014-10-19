package com.xibaibai.worker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xibaibai.worker.ActivityDetail.GoodsListAdapter;
import com.xibaibai.worker.ActivityDetail.GoodsListAdapter.ViewHolder;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
 
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityChoose extends     ListActivity {
	private GoodsListAdapter mAdapter; 
    private ListView mListView;
    private String clothes_price;
    private  int recordID = 0;
    
    private final String[] numEnum={"x0件","x1件","x2件","x3件","x4件","x5件","x6件","x7件","x8件","x9件","x10件","x11件"};
    public JSONArray jsPriceArr = new JSONArray();
    private  JSONArray jsGoodsArr = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		 
		Bundle bundle=getIntent().getExtras();
	    recordID =  bundle.getInt("id");       
	        
	        
		clothes_price ="[{'key':'CHENYI','title':'\u886c\u8863','price':1},{'key':'TXU','title':'T\u6064','price':0.5},{'key':'WAITAO','title':'\u5916\u5957','price':1.8},{'key':'YURONG','title':'\u7fbd\u7ed2\u670d','price':3.5},{'key':'MAOYI','title':'\u6bdb\u8863','price':2},{'key':'JIAKE','title':'\u5939\u514b','price':2.8},{'key':'XIAKU','title':'\u590f\u88e4','price':1},{'key':'QUNZI','title':'\u88d9\u5b50','price':1},{'key':'CHUNQIUKU','title':'\u6625\u79cb\u88e4','price':1.2},{'key':'QIUYI','title':'\u79cb\u8863\u88e4','price':1.2},{'key':'MAOYI','title':'\u6bdb\u79cb\u88e4','price':2},{'key':'YUNDONGXIE','title':'\u8fd0\u52a8\u978b','price':2.8},{'key':'PIXIE','title':'\u76ae\u978b','price':2.8}]";;
        // //TODO @yipei  VER2从服务器获得此值，本地无期限缓存
		//根据服务器push命令更新
		
		
		updateData(clothes_price);
		
        mAdapter = new GoodsListAdapter(this);
        setListAdapter(mAdapter);
        
        mListView = getListView();
        getListView().setEmptyView(findViewById(R.id.empty));
        
        Button  evalbtn= (Button) findViewById(R.id.evalcount);
        evalbtn.setOnClickListener(  btn_listener );
         
       
       
        
        
	}
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {         
       /*
	//按下键盘上返回按钮

     if(keyCode == KeyEvent.KEYCODE_BACK){
         return true; 
      }else{
       return super.onKeyDown(keyCode, event); 
      } */
		return super.onKeyDown(keyCode, event); 
	}
	
	private void updateData(String json){
		
        try { 
        	jsPriceArr = new JSONArray(json);
        	
        	for(int i=0; i < jsPriceArr.length(); i++){
        		 JSONObject jg = jsPriceArr.getJSONObject(i);
        		 int num=getDetailsOneNumByKey(jg.getString("key"));
        		 
        		 jsPriceArr.getJSONObject(i).put("num", num);
        	}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        
 	   
	
	}
	private int getDetailsOneNumByKey(String key){
		Log.e("", "getDetailsOneNumBy  Key: "+ key);
	 try {	
		if(jsGoodsArr==null){
			 
				jsGoodsArr = new JSONArray();
				ContentValues curRecord = Functions.db_msg.readById("messages",recordID);
	 	        String details = curRecord.getAsString("details");
	 	        JSONObject jsonObject=new JSONObject(details);
	 	        jsGoodsArr = jsonObject.getJSONArray("goods");
			
 	    }
		for(int i=0; i < jsGoodsArr.length(); i++){
   		    JSONObject jg = jsGoodsArr.getJSONObject(i);
   		    if(jg.get("key").equals(key)){
   		    	return jg.getInt("num");
   		    }
   	    }
		
		
 	  } catch (JSONException e) {
		 e.printStackTrace();
	  }
	 
	 return 0;
	}
	private OnClickListener btn_listener = new OnClickListener() 
    {   @Override 
       public void onClick(View v) {
       if(v.getId()==R.id.evalcount)
       {   
    	   
    	   
    	   JSONArray jsgoodsArr=new JSONArray();
    	   float paid=0;
    	   for(int i=0; i < jsPriceArr.length(); i++)
    	   {//简化内容  jsgoods    		   
    		   try {
    			     JSONObject jg = jsPriceArr.getJSONObject(i);
    			     int num= (Integer) jg.get("num");
				     if( num > 0){
				    	 jsgoodsArr.put(jg);
				    	  paid += num *  Float.parseFloat(jg.get("price").toString());
				      }
				} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
    		   
    	   }
    	   Log.e("",  jsgoodsArr.toString() );
    	   //this shoud be send back to server
    	   
    	   ContentValues curRecord = Functions.db_msg.readById("messages",recordID);
    	   String details = curRecord.getAsString("details");
    	   JSONObject jsonDetail=null;
    	   try {
           	    jsonDetail=new JSONObject(details); 
           	    jsonDetail.put("goods", jsgoodsArr);
           	    jsonDetail.put("paid", ""+paid);
           	    jsonDetail.put("total", ""+paid);
           	    Log.e("","save2detail:  total=" + paid);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	   
    	    
    	   
    	    curRecord.put("details", jsonDetail.toString());
    	    //curRecord.put("paid", paid);
    	    Functions.db_msg.write("messages", curRecord);
    	    
    	    Intent data=new Intent();  
            data.putExtra("paid", paid);  
    	    setResult(700, data); 
    	    finish(); //close
       }
      
    } } ;

	
	


    /**
     * A simple adapter which maintains an ArrayList of photo resource Ids. 
     * Each photo is displayed as an image. This adapter supports clearing the
     * list of photos and adding a new photo.
     *
     */
    public class GoodsListAdapter extends BaseAdapter {
    	private Context mContext;
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
            return  jsPriceArr.length();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	 //TODO @yipei  为不同的衣服，设置不同的图片，参考ActiveMsg
        	
        	
        	 ViewHolder holder;
        	 String goodsimg = "", texttitle = null, txtprice = null;
        	 int txtnum = 0;
        	 try {
				JSONObject jsonGoods = jsPriceArr.getJSONObject(position);
				//goodsimg = jsonGoods.getString("img");
				texttitle = jsonGoods.getString("title");
				txtprice = jsonGoods.getString("price");
				txtnum = jsonGoods.getInt("num");
				 
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	 
	            // When convertView is not null, we can reuse it directly, there is no need
	            // to reinflate it. We only inflate a new View when the convertView supplied
	            // by ListView is null.
	            if (convertView == null) 
	            {
	            	//Log.e("","old view:" + position);
	             
	                
	                convertView = mInflater.inflate(R.layout.listitem_chose, null);

	                // Creates a ViewHolder and store references to the two children views
	                // we want to bind data to.
	                holder = new ViewHolder();
	                if(goodsimg.length() > 0){
		                holder.img = (ImageView) convertView.findViewById(R.id.detailpic);
		                holder.img.setImageResource(R.drawable.ic_launcher);
		               	/*RemoteImg rImg = new RemoteImg(mContext, holder.img , 
		                		Functions.REMOTE_HOST+  "imgzoom.php?img=" +  goodsimg);			                
		                rImg.execute();
		                */
	                }
	                
	                holder.title = (TextView) convertView.findViewById(R.id.texttitle);
	                holder.price = (TextView) convertView.findViewById(R.id.txtprice);             
	                holder.num = (TextView) convertView.findViewById(R.id.txtnum);  
	                holder.spinner = (Spinner) convertView.findViewById(R.id.spinner1); 
	                
	                convertView.setTag(holder);
	            
                } else {
	                // Get the ViewHolder back to get fast access to the TextView
	                // and the ImageView.
	                holder = (ViewHolder) convertView.getTag();
	            }		           
	             
	                
	      
                holder.title.setText( texttitle);
                holder.price.setText("￥"+txtprice );
                holder.num.setVisibility(View.GONE);
               // holder.num.setText( "x "+txtnum);
               // holder.num.setOnClickListener(btn_listener);
                
                
                if( adapter==null)
                { adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,numEnum);                
                  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //设置下拉列表的风格
                }
                
                //int oldTag = (Integer) holder.spinner.getTag();
                if(holder.spinner.getTag() ==null){
                  holder.spinner.setAdapter(adapter); 
                  holder.spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
                }
                holder.spinner.setTag(position);
                holder.spinner.setSelection(txtnum); 
                
                int Txtcolor = 0xff333333;
               if(txtnum > 0) 
            	{
            	   //convertView.setBackgroundResource(R.drawable.btnbg2);
            	   convertView.setBackgroundColor(0x3088bb00);
            	   Txtcolor = 0xff333333;
            	}
               else { 
            	   convertView.setBackgroundColor(Color.WHITE);
            	   Txtcolor = 0xff333333;
               }
               holder.title.setTextColor(Txtcolor);
               
	            return convertView;
        }
        class ViewHolder {
        	ImageView img;	TextView title;TextView price;TextView num;
            Spinner spinner;
             
        }
        private ArrayAdapter<String> adapter =null;
        private OnClickListener btn_listener = new OnClickListener() 
	    {   @Override 
	       public void onClick(View v) {
           if(v.getId()!=R.id.addclothes)
           {           
        	   new AlertDialog.Builder(mContext)  
               .setTitle("请点击选择")  
               .setItems(numEnum, new DialogInterface.OnClickListener() {  

                   public void onClick(DialogInterface dialog,  
                           int which) {  
                       
                   }  
			 }).show();  
        	   
           }
	      
	    } } ;

        public void clearPhotos() {
            //mPhotos.clear();
            notifyDataSetChanged();
        }



    }

	 class SpinnerSelectedListener implements OnItemSelectedListener{
		 
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
        	
        	int i = (Integer) arg0.getTag();
        	try {				 
				 JSONObject jsonGoods = jsPriceArr.getJSONObject(i);
        	     jsonGoods.put("num", arg2);
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
        	mAdapter.notifyDataSetChanged();
        	
             
        }
 
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
