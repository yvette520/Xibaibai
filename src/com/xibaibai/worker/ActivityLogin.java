package com.xibaibai.worker;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
 
 

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class ActivityLogin extends Activity  {
	 
	private String mBaiduID = "";
	private String mBaiduChannel = "";
	

	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
    
    
	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView; 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		 
		setContentView(R.layout.activity_login);
		
		Functions.cMainactivity=ActivityLogin.this;
		
		
	   /* mBaiduID = Pushutils.getConfig(getApplicationContext(),"baiduId");
		if(mBaiduID.length() > 0){  //之前绑定过
		   Pushutils.isBind  = true; 
		}*/
		
		if(isUserLogined()){ //检查绑定状态
		  loginDone();
		}
		 
		mEmailView = (EditText) findViewById(R.id.email);
		//mEmailView.setText("user");

		mPasswordView = (EditText) findViewById(R.id.password);
		 
		
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		  

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						loginBtnClick();//attemptLogin();
					}
				});
	}
	
	
	
	private void pageActive(boolean active){
		Button bt = (Button)findViewById(R.id.sign_in_button);
		if(active)
		{
			showProgress("");
		   bt.setEnabled(true);
		}else{			
			showProgress("连接服务器...");
		   bt.setEnabled(false);
		}
	}
	private void loginDone(){
		 showProgress("");
		 Intent i=new Intent(ActivityLogin.this,  ActivityMain.class);
			//Intent i=new Intent(LoginActivity.this,  PushActivity.class);
//			  Bundle bd = new Bundle();
//			  bd.putString("hello", message);
			  i.putExtra("hello", "");		  
           startActivity(i); 
             finish();
         
	}	
	/*********************************** baidu push ********************/
	@Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        
        
        if(action!=null  && action.equals("baiduBindEvent"))
        {   int errorCode = intent.getExtras().getInt("errorCode");
        	if(errorCode != 0 ){           	 
        		showProgress("");
        		reTry2server(errorCode);
            	return ;
            }
        	 
        	attemptLogin();
        }
	}
	
	private void reTry2server(int errorCode){
		DialogInterface.OnClickListener yescallback = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				isBaiduBind(); //重新开始检查连接				
			}
		};
		DialogInterface.OnClickListener nocallback = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.exit(0); //退出				
			}
		};
			
	   AlertDialog s = new AlertDialog.Builder(ActivityLogin.this)   
    	.setTitle("网络错误")  
    	.setMessage("连接服务器失败(错误码:"+errorCode+")，是否重新尝试?")  
    	.setPositiveButton("重新尝试", yescallback)  
    	.setNegativeButton("离开", nocallback)  
    	.setCancelable(false)
    	.show(); 
	}
	private boolean isBaiduBind(){ 
		String baiduid= Pushutils.getConfig( ActivityLogin.this, "baiduId");
		//if(Pushutils.isBind == false)
		if(baiduid.length() < 1)  // 没帮过
		{   
			//锁住屏幕
			pageActive(false); 
			
			//重新获baidu id
			PushManager.startWork(getApplicationContext(),
                 PushConstants.LOGIN_TYPE_API_KEY,
                 Pushutils.getMetaValue(getApplicationContext(), "api_key"));
			
			return false;
		}else{
			//loginDone();
			return true;
		}	 	 
	}
	
	private boolean isUserLogined(){ 
		String binduser= Pushutils.getConfig( ActivityLogin.this, "binduser");	
		String baiduId= Pushutils.getConfig( ActivityLogin.this, "baiduId");	
		if(binduser.length() < 1  || baiduId.length() < 1)    //未登陆
		{    
			return false;
		}else{			
			return true;
		}	 
		 
	}
	/*****************************************************baidu push**/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
 

    private void loginBtnClick(){

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
			
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (mEmail.length() < 3) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		
			


		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
			return;
		}
		
		
		boolean isb = isBaiduBind(); //去绑定baidu				
		if(isb){
			//do local login
			attemptLogin();
		}
    	
    }
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
	 
		 
		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		 
		showProgress("登录中....");
		mAuthTask = new UserLoginTask();
		mAuthTask.execute((Void) null);
		 
	}
	
	 
	ProgressDialog waitdialog = null;
	private void showProgress(String msg) {
		if(msg.length() > 1){
		 if(waitdialog ==null )  waitdialog = new ProgressDialog(this);
		    //waitdialog.setTitle("Indeterminate");
			waitdialog.setMessage(msg);
			waitdialog.setIndeterminate(true);
			waitdialog.setCancelable(false);
			Log.d("====", "waitdialog show");
			waitdialog.show();
		}else{
			if(waitdialog !=null ) waitdialog.dismiss();
			
		}
	}
	

	/**
	 * Shows the progress UI and hides the login form.
	 
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
*/
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private String mErrMsg="";
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String rltStr= "";
			 
		try {
				//耗时操作
			    String baiduId= Pushutils.getConfig( ActivityLogin.this, "baiduId");
				String url = Functions.REMOTE_HOST +"admin/app/auth.php?"
						   + "user=" + mEmail + "&pwd=" + mPassword 
						   + "&baiduid=" + baiduId 
						   + "&rnd=" + Math.random();

				rltStr =  new miniHTTP().httpGet(url);
				
				Log.e("Login:",url);
				Log.e("Login:",rltStr);
			 
			} catch ( Exception e) {
				new AlertDialog.Builder(ActivityLogin.this).setMessage("网络错误").show();
				return false;
			}
			
			if(rltStr.indexOf("LOGIN_OK") > 0){
				return true;
			}
			
			 mErrMsg = rltStr;
			 return false;
			 
			 
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			 super.onPostExecute(success);
			 
			mAuthTask = null;
			showProgress(""); 
			pageActive(true); 
			if (success) {
				//保存 远程用户id
				Pushutils.setConfig(getApplicationContext(),"binduser", mEmail);
								
	        	 
	        	 loginDone();
				
			} else {
				if(mErrMsg.indexOf("USER_NOT_EXISTS") > 0)
				{
					mEmailView.setError("用户不存在");
					mEmailView.requestFocus();
				}else if(mErrMsg.indexOf("PASSWORD_NOT_MATCH") > 0)
				{
				  mPasswordView.setError(getString(R.string.error_incorrect_password));
				  mPasswordView.requestFocus();
				}else if(mErrMsg.indexOf("HAVE_RETURN") < 1)
				{ //没有正常返回json
				  new AlertDialog.Builder(ActivityLogin.this).setMessage("服务器异常").show();
				}
			}	
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress("");
		}
	}
}
