package com.timers;


import com.data.utils.DataCommons;
import com.gui.main.CustomNotification;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;


/**
 * Class Countdown timer to test monitor
 * @author troglodito22
 *
 */

public class CustomCountDownTimer extends CountDownTimer{

	private String TAG = CustomCountDownTimer.class.getSimpleName();
	private int typeCountDown = 0;
	
	private long mMillisUntilFinished;
	
	private Context mContext;
	
	private String nameCounter;
	private boolean isFirstTick= true;
	
	public CustomCountDownTimer(long millisInFuture, long countDownInterval, Context context, int type) {
		super(millisInFuture, countDownInterval);
		mContext = context;
		typeCountDown = type;
		mMillisUntilFinished = 0;
		switch(typeCountDown){
		case DataCommons.CountDown.COUNT_DOWN_DIALOG:
			nameCounter="Notification";
			break;
		case DataCommons.CountDown.COUNT_DOWN_TEST:
			nameCounter="Test";
			break;
		}
		Log.v(TAG, "Create CountDownTimer: "+ nameCounter + "timemax: " + millisInFuture + " " + " frec tick: " + countDownInterval);	
	}

	
	 
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	@Override
	public void onFinish() {
		Log.v(TAG, "CountDown finished "+ nameCounter);
		mContext.sendBroadcast(new Intent(DataCommons.Filter.STOP_TRACKING_FILTER));
	}

	@Override
	public void onTick(long millisUntilFinished) {
		Intent intent;
		switch(typeCountDown){
		case DataCommons.CountDown.COUNT_DOWN_DIALOG:
			Log.v(TAG, "CountDown tick notification");
			if(isFirstTick){
				isFirstTick = false;
				Log.v(TAG, "First tick triggered");
				return;
			}
			if(CustomNotification.IS_ACTIVE){
				Log.v(TAG, "Notificationbar is active, wait until user press");
				return;
			}
			intent = new Intent(DataCommons.Filter.TRACK_DATA_POPUP_FILTER);
			mContext.sendBroadcast(intent);
			break;
		case DataCommons.CountDown.COUNT_DOWN_TEST:
			Log.v(TAG, "CountDown tick test");
			mMillisUntilFinished = millisUntilFinished;
			if(isFirstTick){
				isFirstTick = false;
				Log.v(TAG, "First tick triggered");
//				mMillisUntilFinished = DataCommons.CountDown.TEST_STEP;
			}
			
			intent= new Intent(DataCommons.Filter.UPDATING_PROGRESSBAR_FILTER);
			mContext.sendBroadcast(intent);
			break;
		}
		
	}

	public void setCurrentTimeOnStop(long current){
		Log.v(TAG, "set previous time: " + current);
		mMillisUntilFinished = current;
	}
	
	public long getCurrentTestDown(){
		Log.v(TAG, "Restart timer, type: " + nameCounter);
		return (mMillisUntilFinished);
	}
}
