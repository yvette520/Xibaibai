package com.xibaibai.worker;

 

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

////////////////////////////////////////////////////////////////////////////////////////
class MiniTask extends Thread {
	OnTaskDoneListener mListener;
	Task mTask = null;
	Handler mRaiseEvent; 
	
	final int INTERFACE_DONE = 0x1;
	 
	
    public MiniTask() {        
        initHanlder();
         

    }
     
	public void run() {
		
		String CallbackRlt = "";
		if(mTask != null)
		{CallbackRlt = mTask.DoTask();  //目前只能返回字符串型
		}
		 
	    Message msg = new Message();
        msg.what = INTERFACE_DONE;
	 	Bundle data = new Bundle();	 	
	    data.putString("CallbackRlt", CallbackRlt);
	 	msg.setData(data);
	    mRaiseEvent.sendMessage(msg); 
	}
	
	public void setTask(Task Task) {
		this.mTask = Task;
	}
	public interface Task { 
		public String DoTask();// 这个是想调用的函数  不是回调函数
	}
	
	public void setOnTaskDoneListener(OnTaskDoneListener mListener) {
		this.mListener = mListener;
	}
	public interface OnTaskDoneListener {// 这个就是回调接口
		public void OnTaskDone(String  data);// 这个是回调函数
	}	 
	private void initHanlder() {
        mRaiseEvent = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                case INTERFACE_DONE: 
                	
                	mListener.OnTaskDone(msg.getData().getString("CallbackRlt").toString());
                    break;
                case 2:// 接收myhttp2的消息
                    break;
                default:
                    break;
                }
            };
        };
    }
	 
}

