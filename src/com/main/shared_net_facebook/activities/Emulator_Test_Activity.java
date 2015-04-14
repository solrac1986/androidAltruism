package com.main.shared_net_facebook.activities;


import com.data.utils.DataCommons;
import com.gui.activities.Email_Activity;
import com.gui.main.CustomNotification;
import com.gui.main.ScreenMessage;
import com.gui.utils.InitializeInterface;
import com.main.shared_net_facebook.activities.R;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.main.CustomFeedFacebook;
import com.facebook.utils.DataFacebook;
import com.services.ServiceSensors;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
//import com.facebook.android.*;
//import com.facebook.android.Facebook.DialogListener;

/**
 * Main Activity class, start-stop application 
 * @author troglodito22
 *
 */

public class Emulator_Test_Activity extends Activity implements OnClickListener{
   
	private static final String TAG = Emulator_Test_Activity.class.getSimpleName();
	
	private ServiceSensors mServiceSensors;
	
	private ProgressBar mProgressBar;
	
	private ScreenMessage mScreenMessage;
	
	Facebook mFacebook;
	
	private SharedPreferences mPrefs;
	
	class IncomingHandler extends Handler  {

		@Override
		public void handleMessage (Message msg){
			Log.v(TAG, "Handle message");
			switch(msg.what){
			case DataCommons.MessageHandler.MESSAGE_CHANGE_STOP:
				Log.v(TAG, "Message stop service");
				onStopService();
		    	break;
			case DataCommons.MessageHandler.UPDATE_PROGRESSBAR:
				Log.v(TAG, "Update progressBar");
				updateProgressBar(msg.arg1);
				break;
			case DataCommons.MessageHandler.DELETED_OK:
				showScreenMessage(msg.what);
				break;
			case DataCommons.MessageHandler.DELETE_ERROR:
				showScreenMessage(msg.what);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	
	
	private ServiceConnection serviceSensorsConnector = new ServiceConnection() {
			
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "Handle to bind Service Sensor");
			mServiceSensors = ((ServiceSensors.ServiceSensorsBinder)service).getService();
			mServiceSensors.setMHandler(new IncomingHandler());
			Log.v(TAG, (mServiceSensors==null)?"mSensorService is null":"mService is not null");
		}

		
		public void onServiceDisconnected(ComponentName componentName ) {
			Log.v(TAG, "Disconnnect monitor Connection");
			mServiceSensors.setMHandler(null);
			mServiceSensors = null;
		}
		
	};
	
	
	/** Called when the activity is first created. */
	@SuppressWarnings("unused")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "On create method");
        setContentView(R.layout.main);
        mPrefs = getPreferences(MODE_PRIVATE);
        
        mScreenMessage = new ScreenMessage(this.getApplicationContext());
        
        ToggleButton toggle = (ToggleButton)findViewById(R.id.toggleButton);
        toggle.setOnClickListener(this);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBarRun);
		mProgressBar.setMax((int)(DataCommons.CountDown.TEST_DURATION));
		
		Intent intent = new Intent(this, ServiceSensors.class);
		intent.setAction(DataCommons.Filter.INIT_SERVICE_FILTER);
		setPreviousActivityStatus();
		
		if (mPrefs.getBoolean(DataCommons.Preferences.PAUSED_BEFORE, false)
				&& isServiceON()){
			intent.setAction(DataCommons.Filter.PAUSED_BEFORE_FILTER);
		}
		startService(intent);
		
        InitializeInterface init = new InitializeInterface();
        init.getParameters(this);   
        
         if(DataCommons.Settings.IS_FACEBOOK_ON == true && isServiceON() == false){
        	initFacebook();
        }
    }
 
    @Override
    public void onResume(){
    	Log.v(TAG, "On Resume");
    	super.onResume();
    	setPausedPreference(false);
    	doBindService();	
    }
    
    @Override
    public void onDestroy(){
    	Log.v(TAG, "On Destroy");
    	cancelActivity();
    	doUnbindService();
    	super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
	    moveTaskToBack(true);
	}
    
    @Override
    public void onPause(){
    	super.onPause();
    	Log.v(TAG, "On pause method");	
    	setPausedPreference(true);
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	Log.v(TAG, "On Start method, set ProgressBar value");
    	setPreviousActivityStatus();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	Log.v(TAG, "On stop method");
    	saveActivityStatus(mProgressBar.getProgress());
    }
    
    private void onStopService(){
    	Log.v(TAG, "On stop service, user pressed OFF");
    	cancelActivity();
    	doUnbindService();
    	stopService(new Intent(this, ServiceSensors.class));
		
    }
    
    /**
     * Initialized Facebook login
     */
    private void initFacebook(){
	      Log.v(TAG, "Init facebook");
	      mFacebook = new Facebook("169168529854940");
		  
	      String access_token = null;
		  long expires = 0;
		  try{
	    	  access_token = mPrefs.getString(DataCommons.Preferences.FACEBOOK_TOKEN, 
						null);
	    	  expires = mPrefs.getLong(DataCommons.Preferences.FACEBOOK_EXPIRES,
						0);  
	      }catch(Exception e){
	    	  Log.e(TAG, "Exception taking shared preferences: " + e.getMessage());
	      }
	      
	      if(access_token != null) {
	          mFacebook.setAccessToken(access_token);
	      }
	      if(expires != 0) {
	          mFacebook.setAccessExpires(expires);
	      }
	      
		  Log.v(TAG, "Create facebook login");
		  if(!mFacebook.isSessionValid()) {
	
	          mFacebook.authorize(this, new String[] {"publish_stream", 
	        		  "read_friendlists"},Facebook.FORCE_DIALOG_AUTH, 
	        		  new DialogListener() {
			              @Override
			              public void onComplete(Bundle values) {
			                  SharedPreferences.Editor editor = mPrefs.edit();
			                  editor.putString("access_token", mFacebook.getAccessToken());
			                  editor.putLong("access_expires", mFacebook.getAccessExpires());
			                  editor.commit();
			              }
			              @Override
			              public void onFacebookError(FacebookError error) {}
			              @Override
			              public void onError(DialogError e) {}     
			              @Override
			              public void onCancel() {}
			          });
		  }
		  
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult method");
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void doBindService(){
    	Log.v(TAG, "Do bind service");
    	boolean value = bindService(new Intent(this.getBaseContext(), ServiceSensors.class), 
    			serviceSensorsConnector, Context.BIND_AUTO_CREATE);
    	Log.v(TAG, "Bind service is "+(value?"create":"not created"));
    }	
    
    public void doUnbindService(){
		Log.v(TAG,"Do doUnBind Service");
		try{
			unbindService(serviceSensorsConnector);
		}catch(Exception e){
			Log.e(TAG, "Exception unbinding Service: " + e.getMessage());
		}
	}
	    
    /**
	 * Menu option
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		Log.v(TAG, "Create menu inflater"); 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_options, menu);
		
		return true;
	}
	
	private void resetTestSharedPreferences(){
		Log.v(TAG, "Reset count test shared preferences");
		ToggleButton toggle = (ToggleButton)(findViewById(R.id.toggleButton));
		toggle.setChecked(false);
		mProgressBar.setMax((int)(DataCommons.CountDown.TEST_DURATION));
		mProgressBar.setProgress(0);
		saveActivityStatus(0);
	}
	
	/**
	 * Update (increment) progressBar
	 */
	private void updateProgressBar(long increment){
		Log.v(TAG, "Updating progressBar: " + increment);
		long statusLevel = mPrefs.getLong(DataCommons.Preferences.PROGRESS_BAR_LEVEL,0);
		statusLevel += increment;
		mProgressBar.setProgress((int)(statusLevel));
		if (statusLevel == DataCommons.CountDown.TEST_DURATION){
//			Log.v(TAG, "Test finished");
//			sendBroadcast(new Intent(DataCommons.Filter.STOP_TRACKING_FILTER));
//			return;
			sendBroadcast(new Intent(DataCommons.Filter.SEND_DATA_FILTER));			
			Intent intent = new Intent(this, Email_Activity.class);
			try{
				startActivity(intent); 
			}catch(ActivityNotFoundException e){
				Log.e(TAG, "Error activity not found: "+e.getMessage());
			}
			return;
		}
		saveActivityStatus(statusLevel);
	}

	private void saveActivityStatus(long value){
		Log.v(TAG, "Save level progressbar: " + value);
		SharedPreferences.Editor editor = mPrefs.edit();
    	editor.putLong(DataCommons.Preferences.PROGRESS_BAR_LEVEL, value);
    	editor.putBoolean(DataCommons.Preferences.SERVICE_ON, isServiceON());
    	editor.commit();
	}
	
	private void setPreviousActivityStatus(){
		Log.v(TAG, "Set previous status");
		ToggleButton toggle = (ToggleButton)(findViewById(R.id.toggleButton));
		toggle.setChecked(mPrefs.getBoolean(DataCommons.Preferences.SERVICE_ON,
				false));
		mProgressBar.setProgress((int)mPrefs.getLong(DataCommons.Preferences.PROGRESS_BAR_LEVEL,
    			0));
		mProgressBar.setMax((int)(DataCommons.CountDown.TEST_DURATION));
	}
	
	private void setPausedPreference(boolean value){
		Log.v(TAG, "Set paused method in shared preferences");
		SharedPreferences.Editor editor = mPrefs.edit();
    	editor.putBoolean(DataCommons.Preferences.PAUSED_BEFORE, value);
    	editor.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Log.v(TAG, "Select option inflated menu");
		if(isServiceON()){
			mScreenMessage.showMessage("Info.", "Please, Turn OFF before", 
    				ScreenMessage.MessageType.TOAST_MESSAGE, 
    				0);
			return false;
		}
		switch(item.getItemId()){
		case R.id.menuDelete:
			Log.v(TAG, "Selected delete data");
			sendBroadcast(new Intent(DataCommons.Filter.DELETE_DATA_FILTER));
			break;
		case R.id.menuSend:
			Log.v(TAG, "Selected send data");
			sendBroadcast(new Intent(DataCommons.Filter.SEND_DATA_FILTER));
			Intent intent = new Intent(this, Email_Activity.class);
			try{
				startActivity(intent); 
			}catch(ActivityNotFoundException e){
				Log.e(TAG, "Error activity not found: "+e.getMessage());
			}
			break;
		}
		return true;
	}

	private boolean isServiceON(){
		boolean value = false;
		try{
			ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
			value = toggleButton.isChecked();
		}catch(Exception e){
			Log.e(TAG, "Exception toggle button not found: " + e.getMessage());
			return value;
		}
		Log.v(TAG, "Is service active: " + value);
		return value;
	}
	
	private void cancelActivity(){
		Log.v(TAG, "Change layout content");
		try{
			resetTestSharedPreferences();
		}catch(Exception e){
			Log.v(TAG, "Not found content in layout: "+e.getMessage());
		}
		try{
			CustomNotification.cancelNotifications();
		}catch(Exception e){
			Log.v(TAG, "Not notification bar deleted before: "+e.getMessage());
		}
	}
	
	private void showScreenMessage(int type){
		String message= " ";
		switch(type){
		case DataCommons.MessageHandler.DELETED_OK:
			message = "Delete was completed";
			break;
		case DataCommons.MessageHandler.DELETE_ERROR:
			message = "Error deleting data";
		}
		mScreenMessage.showMessage("Info.", message, 
				ScreenMessage.MessageType.TOAST_MESSAGE, 
				0);
	}
	
	@Override
	public void onClick(View v) {
		Log.v(TAG, "On click button listener");	
		String message=" ";
		switch(v.getId()){
		case R.id.toggleButton:
			if (isServiceON()){
				Log.v(TAG, "Onclick toggle ON");
				message = "Starting application";
				try{
					if (DataCommons.Settings.IS_FACEBOOK_ON){
						CustomFeedFacebook custom = new CustomFeedFacebook(mFacebook, 
								this.getApplicationContext());
						custom.execute(DataFacebook.Action.POST_ON_WALL);
					}
				}catch(Exception e){
					Log.e(TAG, "Exception when creating post wall message: " + e.getMessage());
				}
				Intent intent = new Intent(this, ServiceSensors.class);
				intent.setAction(DataCommons.Filter.START_TRACKING_FILTER);
				startService(intent);
				doBindService();
				if(mProgressBar.getProgress() == 0) {
					updateProgressBar(1);
				}
				sendBroadcast(new Intent(DataCommons.Filter.START_TRACKING_FILTER));	
			}
			else {
				Log.v(TAG, "Onclick toggle OFF");
				message = "Stopping application";	
				sendBroadcast(new Intent(DataCommons.Filter.PAUSE_TRACKING_FILTER));
			}
			mScreenMessage.showMessage("Info.", message, 
    				ScreenMessage.MessageType.TOAST_MESSAGE, 
    				0);
		}
	}
}

