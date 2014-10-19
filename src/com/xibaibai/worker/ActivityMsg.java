package com.xibaibai.worker;

/* TODO:
 * 1. 收到后 返回通知  （服务器做对应标记）
 * 2. 相同orderid 不重复保存
 * 
 * 
 * 
 */
 
 
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


 

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

 
//TODO @yipei  VER2.0 添加搜索按钮的搜索功能 （ID,姓名，房间等匹配）


public class ActivityMsg extends ListActivity implements OnItemLongClickListener {
  
	private List<ContentValues>  msgData  = null;
	 
	String pageState = null;
	
	String filterString = "";
	private ReceiveBroadCast recBroadCast =null; 
	
	EfficientAdapter myEfficientAdapter;
	 
	


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        Bundle bundle=getIntent().getExtras();
        pageState = bundle.getString("state");
        
         
        setContentView(R.layout.activity_msg);	
        
       
        
        if(pageState.equals(orderFunc.STATE.CREATE)){
        	regBrodcastRec();
        }
        if(!pageState.equals(orderFunc.STATE.DONE)){
        	View   s = (View) findViewById(R.id.searchBox);
        	s.setVisibility(View.GONE);
        	
        }else{
        	Button  searchbtn = (Button ) findViewById(R.id.searchbtn);        	
        	searchbtn.setOnClickListener(btn_listener) ; 
        }
        
        
        msgData =  new ArrayList<ContentValues>();  
        myEfficientAdapter = new EfficientAdapter(this);
        setListAdapter(myEfficientAdapter); 
         
		TextView emptyView = new TextView(this);
        ((ViewGroup) getListView().getParent()).addView(emptyView);
        emptyView.setWidth(LayoutParams.FILL_PARENT);
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        emptyView.setText("列表为空");
        getListView().setEmptyView(emptyView);
        
        
        getMsgData(pageState);
        
        this.getListView().setDivider(null);
        //this.getListView().setBackgroundResource(R.drawable.bg2);
        getListView().setOnItemLongClickListener(this);
         
        
    }
	
	@Override
	public void onResume(){
		super.onResume();
		updateData();
	}
	@Override
	public void onDestroy() 
   {  super.onDestroy(); 
      //Functions.db_msg.close();
    // if(recBroadCast!=null) unregisterReceiver(recBroadCast); 
       
   }  
/******************************************/
    private void regBrodcastRec(){
    	// 注册广播接收
        recBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("update_MsgListData");   
        registerReceiver(recBroadCast, filter);
        Pushutils.is_dbreciverOn=true;
    }
	public class ReceiveBroadCast extends BroadcastReceiver
	{	 
	        @Override
	        public void onReceive(Context c, Intent intent)
	        {
	            // String message = intent.getStringExtra("data");
//	             Toast.makeText(context, "get data: " +message, Toast.LENGTH_SHORT)
//	                .show();
	            
	             updateData();
	            //txtShow.setText(message);
	        }
	 
	}
/*******************************************/
   

	
	private OnClickListener btn_listener = new OnClickListener() 
    {   @Override 
       public void onClick(View v) {
       if(v.getId()==R.id.searchbtn)
       {
    	   TextView  searchtxt = (TextView) findViewById(R.id.searchtxt); 
    	   String input = searchtxt.getText().toString();
    	   String filter = " 0 ";
    	   
    	   filter += " OR orderid ='"+input+"' ";
    	   if(input.length()>5){
    		   filter = " OR details LIKE '%"+input+"%' ";
    	   }
    	   filter += " OR msgfrom LIKE '%"+input+"%' ";
    	     
    	   msgData  = Functions.db_msg.readArray("messages",filter );
   		   //Functions.db_msg.mOrderby=null;
   		   myEfficientAdapter.notifyDataSetChanged();	
   		
       } 
      
    } } ;
    
    
	@Override
	public void onListItemClick(ListView parent, View v, int position, long arg3) {
		
		ContentValues cv;
		cv = msgData.get(position) ;
		int id = cv.getAsInteger("_id");
		String details = cv.getAsString("details").toString();
		
		
		details = msgData.get(position).getAsString("details");
		Intent it = new Intent(this, ActivityDetail.class);             
		it.putExtra("details", details);
		//it.putExtra("orderstate", pageState);
		it.putExtra("id", id); 
		startActivity(it);
		
		myEfficientAdapter.notifyDataSetChanged();
		 
		 
    }  	

	@Override
	 public boolean onItemLongClick(AdapterView<?> parent, View view,  final int position, long id) {
		ContentValues hm ; 
		hm = msgData.get(position); //.get(DBAdapter.MSG_ISNEW)="0";
		 final long recordid =  Integer.parseInt(hm.get("_id").toString());
	     final String msgbody = hm.get("content").toString();
	    /*
		 final MenuDialog md = new MenuDialog(ActivityMsg.this);
		 //md.menu_share.setVisibility(View.GONE);
		  md.menu_share.setText("转发");
		 
			md.menu_del.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					msgData.remove(position); 
					myEfficientAdapter.notifyDataSetChanged();
					//msg_db.deletecontent(recordid);
					Toast.makeText(ActivityMsg.this, "xxx已经删除",Toast.LENGTH_SHORT).show(); 
					 md.dismiss();
				}});
			md.menu_share.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					//SmsManager smsManager = SmsManager.getDefault();
					Uri smsToUri = Uri.parse("smsto:");           
					Intent it = new Intent(Intent.ACTION_SENDTO,smsToUri);             
					it.putExtra("sms_body", msgbody);             
					startActivity(it);
					 md.dismiss();
				}});			
			
			md.show();
			*/
       return false;
   }
 
 
	
	private void getMsgData(String state)
	{   	
		  String json = "{'total':207.5, 'address':'北京市朝阳区xxxx', 'tel':'13910136035', 'paytype':'paid'," +
    		" 'goods':[" +
    		"{'img':100,'title':'title1111','price':5.3, 'num':6}," +
    		"{'img':100,'title':'title22222','price':15.3, 'num':1}," +
    		"{'img':100,'title':'title333','price':10.3, 'num':2}," +
    		"{'img':100,'title':'title4444','price':5.3, 'num':1}," +
    		"{'img':100,'title':'title555','price':5.3, 'num':4}," +
    		"{'img':100,'title':'title666','price':5.3, 'num':7}" +
    		"]}";
		  
	  
		  Functions.DBtableInit(this, "mydb2");
	 
	/*	 
		initdata("sn1", "from1", "msgtime1111","11:32","new",json);
		initdata("sn22", "from2", "msgtime2222","11:32","done",json);
		initdata("sn33", "from3", "msgtime3333","11:32","new",json);
		initdata("sn44", "from4", "msgtime4444","11:32","new",json);
	 */
		updateData();
	}
	private void updateData(){
		//Functions.db_msg.mOrderby = "_id DESC";
        String filter="";
        if(pageState.equals("copying")){
        	filter = "state='create' OR state='copy'";
        }else if(pageState.equals("ongoing")){
        	filter = "state='washing' OR state='clean' OR state='back' ";
        }else if(pageState.equals("done")){
        	filter = "state='done'  ";
        }
        
        //TODO @yipei 需要根据状态 排序，未处理的排在上面 
        //可选操作
        
		msgData  = Functions.db_msg.readArray("messages",filter );
		//Functions.db_msg.mOrderby=null;
		myEfficientAdapter.notifyDataSetChanged();		


	}

	
	 private  class EfficientAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private Bitmap mIcon1;
	        private Bitmap mIcon2;

	        public EfficientAdapter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);

	            // Icons bound to the rows.
//	            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon48x48_1);
//	            mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon48x48_2);
	        }
	        
 
	        public int getCount() {
	            return msgData.size();
	        }
	        public Object getItem(int position) {
	            return position;
	        }
	        public long getItemId(int position) {
	            return position;
	        }
	        
	        @Override
	        public boolean isEnabled(int position) {
	        	String content = msgData.get(position).get("content").toString();
	            return !content.startsWith("-");
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	        	//TODO @yipei  按照日期划出分割线
	        	
	            // A ViewHolder keeps references to children views to avoid unneccessary calls
	            // to findViewById() on each row.
	            ViewHolder holder;

	            // When convertView is not null, we can reuse it directly, there is no need
	            // to reinflate it. We only inflate a new View when the convertView supplied
	            // by ListView is null.
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.listitem_msg, null);

	                // Creates a ViewHolder and store references to the two children views
	                // we want to bind data to.
	                holder = new ViewHolder();
	                holder.from = (TextView) convertView.findViewById(R.id.texttitle);
	                holder.time = (TextView) convertView.findViewById(R.id.textTime);
	                holder.orderid = (TextView) convertView.findViewById(R.id.orderid);             
                    ////TODO @yipei  为不同的状态设置不同的图标 给 imageView1
	                holder.icon = (ImageView) convertView.findViewById(R.id.imageView1);
	                
	                convertView.setTag(holder);
	            } else {
	                // Get the ViewHolder back to get fast access to the TextView
	                // and the ImageView.
	                holder = (ViewHolder) convertView.getTag();
	            }

	            // Bind the data efficiently with the holder.	            
	            String curState=  msgData.get(position).get("state").toString() ;
	            int bgrid = 0;
	            int col = 0;
	            
	            //TODO @yipei  为不同的状态设置不同的图标 给 icon
	            int Ricon=0;
	            if(curState.equals(orderFunc.STATE.CREATE)){
	            	bgrid = (R.drawable.create);
	            	col = Color.parseColor("#ffffff");
	            	Ricon = R.drawable.ic_create;
	            }
                else if(curState.equals(orderFunc.STATE.COPY)){
	            	bgrid = R.drawable.copy;
	            	col = Color.parseColor("#333333");
	            	Ricon = R.drawable.ic_copy;
	            } 
	            else if(curState.equals(orderFunc.STATE.WASHING)	){
	            	bgrid = R.drawable.washing;
	            	col = Color.parseColor("#333333");
	            	Ricon = R.drawable.ic_washing;
	            }
	           else if(	curState.equals(orderFunc.STATE.CLEAN)){
		            	bgrid = R.drawable.copy;
		            	col = Color.parseColor("#333333");
		            	Ricon = R.drawable.ic_clean;
		            } 
	            else if(curState.equals(orderFunc.STATE.BACK)){
	            	bgrid = R.drawable.back;
	            	col = Color.parseColor("#ffffff");
	            	Ricon = R.drawable.ic_back;
	            } 	            
	            else if(curState.equals(orderFunc.STATE.DONE)){
	            	bgrid = R.drawable.state;
	            	col = Color.parseColor("#333333");
	            	Ricon = R.drawable.ic_done;
	            } else{ 
	            	 bgrid= (R.drawable.state);
	            	 col = Color.parseColor("#333333");  
	            	//convertView.getBackground().setAlpha(200);
	            }
	            
	            
               //TODO @yipei  为不同的状态设置不同的图标 给 imageView1
               holder.icon.setImageResource(Ricon);
               holder.from.setTextColor(col);
               holder.time.setTextColor(col);
               holder.orderid.setTextColor(col);
	               
	            convertView.findViewById(R.id.msgtextbox).setBackgroundResource(bgrid);
                //if(convertView.getBackground() == null)
                {  
                	//convertView.setBackgroundResource(bgrid);                   
                }
                holder.from.setText( msgData.get(position).get("msgfrom").toString() );
                holder.orderid.setText( "id:"+msgData.get(position).get("orderid").toString() );
                holder.time.setText( ""+msgData.get(position).get("msgtime").toString() );
                
              
	            return convertView;
	        }

	         class ViewHolder {
	        	TextView from;TextView time;TextView orderid; ImageView icon ;
	             
	        }
	    }

 


	 
 
}
