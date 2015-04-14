package com.gui.activities;

import com.main.shared_net_facebook.activities.R;
import com.sensors.BatteryCheck;
import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedTest;
import com.gui.main.CustomNotification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *  Class for dialogBox notification
 *	@author troglodito22
 *
 */

public class DialogBoxActivity extends Activity implements OnClickListener{
	
	private final String TAG = DialogBoxActivity.class.getSimpleName();
	
	
	public static boolean IS_ACTIVE = false;
	private Bundle mReturnBundle;
	
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.v(TAG, "On create method");
	        setContentView(R.layout.customdialogbox);
	        
	        Button button = (Button)findViewById(R.id.buttonYes);
			button.setOnClickListener(this);
			button = (Button)findViewById(R.id.buttonNo);
			button.setOnClickListener(this);
			
			startDialogBox();
	    }

	  
	  @Override
	  public void onResume(){
		  Log.v(TAG, "OnResume");
		  super.onResume();
		  
	  }
	  
	 

	  
	  private void showDialogBoxDefault(){
			Log.v(TAG, "Show dialog box, with standard parameters");
			IS_ACTIVE = true;
			Bundle bundle = getIntent().getExtras();
			int contentType = (bundle.getInt(DataCommons.DialogBoxKeys.CONTENT_TYPE_KEY));
			TextView textView = (TextView)findViewById(R.id.textDialogTitle);
			
			textView.setText(DataCollectedTest.ContentType.arrayType[contentType]);
			
			textView = (TextView)findViewById(R.id.textBatteryInit);
			BatteryCheck battery = new BatteryCheck(this.getApplicationContext());
			int batteryStatus = battery.getBatteryLevel(); 
			textView.setText(batteryStatus + " %");
			textView = (TextView)findViewById(R.id.textBatteryEnd);
			textView.setText(battery.reduceBatteryLevel(batteryStatus, contentType) 
					+ " %");

			String nameSender = bundle.getCharSequence(DataCommons.DialogBoxKeys.NAME_SENDER_KEY).toString();
			String nameReceiver = bundle.getCharSequence(DataCommons.DialogBoxKeys.NAME_RECEIVER_KEY).toString();

			
			textView = (TextView)findViewById(R.id.textSender);
			try{
				textView.setText(nameSender.substring(0, 30));
			}catch(IndexOutOfBoundsException e){
				textView.setText(nameSender);
			}
			textView = (TextView)findViewById(R.id.textReceiver);
			try{
				textView.setText(nameReceiver.substring(0,30));
			}catch(IndexOutOfBoundsException e){
				textView.setText(nameReceiver);
			}
			
	
		}


	    private void startDialogBox(){
	    	Log.v(TAG, "Create Dialog Box, after user press notification");
	    	if(IS_ACTIVE){
	    		Log.v(TAG, "Dialog box has been showed before");
	    		return;
	    	}
	    	try{
				try{
					NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.cancel(CustomNotification.NOTIFICATION_ALTRUISM_ID);
				}catch(Exception e){
					Log.v(TAG, "Exception when canceling notification: "+e.getMessage());
				}
				showDialogBoxDefault();
				mReturnBundle = new Bundle();
				// Save time-stamp when dialog box pop-up
				mReturnBundle.putLong(DataCommons.DialogBoxKeys.TIME_POPUP_KEY,
						System.currentTimeMillis());
			}catch(Exception e){
	    		Log.e(TAG, "Exception startDialog: "+e.getMessage());
	    		IS_ACTIVE = false;
	    		finish();
	    	}
	    }
	    
	    public void onBackPressed() {
		    moveTaskToBack(true);
		}
	    
	    @Override
		public void onClick(View v) {
			Log.v(TAG, "On click Listener");
			mReturnBundle.putLong(DataCommons.DialogBoxKeys.TIME_USER_KEY,
					System.currentTimeMillis());
			Bundle bundle = getIntent().getExtras();
			mReturnBundle.putInt(DataCommons.DialogBoxKeys.CONTENT_TYPE_KEY,
					(bundle.getInt(DataCommons.DialogBoxKeys.CONTENT_TYPE_KEY)));
			mReturnBundle.putLong(DataCommons.DialogBoxKeys.CONTENT_SIZE_KEY,
					bundle.getLong(DataCommons.DialogBoxKeys.CONTENT_SIZE_KEY));
			switch(v.getId()){
			case R.id.buttonYes:
				Log.v(TAG, "User pressed YES");
				mReturnBundle.putInt(DataCommons.DialogBoxKeys.OPTION_SELECTED_KEY,
						DataCollectedTest.Option.YES);
				break;
			case R.id.buttonNo:
				Log.v(TAG, "User pressed NO");
				mReturnBundle.putInt(DataCommons.DialogBoxKeys.OPTION_SELECTED_KEY,
						DataCollectedTest.Option.NO);
				break;
			}
			Intent intent = new Intent(DataCommons.Filter.TRACK_DATA_USER_FILTER);
			intent.putExtras(mReturnBundle);
			sendBroadcast(intent);
			IS_ACTIVE = false;
			finish();
		}
}
