package com.sensors;

import java.util.Random;

import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedTest;
import com.interfaces.FeaturesInterface;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;


/**
 * Class Battery sensor
 * @author troglodito22
 *
 */

public class BatteryCheck implements FeaturesInterface{

	
	private static final String TAG = BatteryCheck.class.getSimpleName();
	
	private static Intent mIntent;
	private static Context mContext;
	
	private static final int MAX_VALUE_REDUCE_FILE = 5;
	private static final int MAX_VALUE_REDUCE_MESSAGE= 1;

	
	public BatteryCheck (Context context, Intent intent){
		super();
		mContext = context;
		mIntent = intent;
	}
	
	public BatteryCheck(Context context){
		super();
		mContext = context;
	}
	
	
	public int isParameterTrue() {
		Log.v(TAG, "Return Parameter value");
		int batteryStatus = 1;
		try{
			mIntent = mContext.getApplicationContext().registerReceiver(null, 
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			batteryStatus = mIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		}catch(Exception e){
			Log.e(TAG, "Error getting battery status: " + e.getMessage());
			return 1;
		}
		switch(batteryStatus){
		case BatteryManager.BATTERY_STATUS_CHARGING:
			Log.v(TAG, "Battery is charging[false]");
			return 0;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
			Log.v(TAG, "Battery is not charging[true]");
			return 1;
		}
		Log.v(TAG, "Battery not get status");
		return 0;
	}


	@Override
	public void setIntent(Intent intent) {
		mIntent = intent;
	}

	@Override
	public Intent getIntent() {
		return mIntent;
	}
	
	public int[] getParameters(){
		Log.v(TAG, "Get parameters");
		int[] result = new int[2];
		result[TypeParameter.BATTERY_LEVEL] = getBatteryLevel();
		result[TypeParameter.BATTERY_STATUS] = isParameterTrue();
		return result;
	}
	
	public int getBatteryLevel(){
		Log.v(TAG, "Get battery level");
		try{
			mIntent = mContext.getApplicationContext().registerReceiver(null, 
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));	
		}catch(Exception e){
			Log.e(TAG, "Error when creating intent from context");
			return 0;
		}
		int rawlevel = mIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = mIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int level = -1;
		if(rawlevel >= 0 && scale > 0){
			level = (rawlevel * 100) / scale;
			Log.v(TAG, "Battery level: "+level);
		}
		if (level <= DataCommons.BatteryParamaters.MIN_VALUE_BATTERY){
			Log.v(TAG, "Battery very low, saving data");
		}
		return level;
	}

	@Override
	public void setContext(Context context) {
		mContext = context;
	}


	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 
	 * @param actual
	 * @param typeContent
	 * @return
	 */
	public int reduceBatteryLevel(int actual, int type){
		Log.v(TAG, "Reduce, estimate new battery level");
		try{
			mIntent = mContext.getApplicationContext().registerReceiver(null, 
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));	
		}catch(Exception e){
			Log.e(TAG, "Error when creating intent from context");
			return 0;
		}
		Random random = new Random();
		int value;
		if (type ==DataCollectedTest.ContentType.MESSAGE){
			Log.v(TAG, "Content type: Message");
			value = (actual - MAX_VALUE_REDUCE_MESSAGE);	
		}
		else {
			Log.v(TAG, "Content type: File");
			value = (actual - random.nextInt(MAX_VALUE_REDUCE_FILE));
		}
		Log.v(TAG, "Reduce battery level in: " + value);
		return value >=0? value: 1;
	}
	
	public static class TypeParameter{
		public static final int BATTERY_LEVEL = 0;
		public static final int BATTERY_STATUS = 1;
	}

	@Override
	public int isParameterTrue(Object objec) {
		// TODO Auto-generated method stub
		return 0;
	}
}
