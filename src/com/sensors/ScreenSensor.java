package com.sensors;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.interfaces.FeaturesInterface;

/**
 * Class Screen ON checks. Checks if application is using screen (activity on foreground)
 * @author troglodito22
 *
 */

public class ScreenSensor implements FeaturesInterface{

	private final String TAG = ScreenSensor.class.getSimpleName();
	
	private Intent mIntent;
	private int isScreenON = 0;

	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)){
				isScreenON  = 1;
			}
			else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)){
				isScreenON = 0;
			}
			
		}
		
	};

	@Override
	public int isParameterTrue(Object object) {
		Log.v(TAG, "Parameter boolean function, ScreenON: "+isScreenON);
		return isScreenON;
	}



	@Override
	public void setIntent(Intent intent) {
		mIntent = intent;
	}
	
	@Override
	public Intent getIntent() {
		return mIntent;
	}



	@Override
	public void setContext(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		context.registerReceiver(receiver, intentFilter);
	}



	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
