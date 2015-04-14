package com.timers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Class Alarm to trigger notifications
 * @author troglodito22
 *
 */

public class CustomAlarm {
	
	
	private final String TAG = CustomAlarm.class.getSimpleName();
	
	private Context mContext;
	private AlarmManager mAlarmManager;
	
	private String mActionToPerform;
	
	public CustomAlarm(Context context, String action){
		Log.v(TAG, "Constructor");
		mContext = context;
		mActionToPerform = action;
		try{
			mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
		}catch(Exception e){
			Log.e(TAG, "Error when creating alarm manager: " + e.getMessage());
		}
		
	}
	
	public void setRepeatingAlarm(long interval) {
		Log.v(TAG, "Set interval Alarm");
		try{
			Intent intent = new Intent(mActionToPerform);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
			    intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +interval,
					System.currentTimeMillis() + interval, pendingIntent);
		}catch(Exception e){
			Log.e(TAG, "Error when setting alarm repeating: " + e.getMessage());
		}
	}
	
	public void cancel(){
		Log.v(TAG, "Cancel alarm");
		Intent intent = new Intent(mActionToPerform);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
		    intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mAlarmManager.cancel(pendingIntent);
	}
}
