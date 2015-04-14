package com.application.utilities;

import java.util.Random;

import com.data.utils.DataCommons;

import android.util.Log;


/**
 * Class to randomize data, time, content size.
 * @author troglodito22
 *
 */

public class Randomize {
	
	private final static String TAG = Randomize.class.getSimpleName();
	
	private Random mRandom;
	
	
	public Randomize(){
		super();
		Log.v(TAG, "Constructor");
		mRandom = new Random();
	}
	
	public Randomize(long seed){
		super();
		Log.v(TAG, "Constructor");
		mRandom = new Random(seed);
	}
	
	public void setSeed(long seed){
		Log.v(TAG, "Set random seed");
		mRandom = new Random(seed);
	}
	
	public boolean getBoolean(){
		Log.v(TAG, "Get Boolean random");
		return mRandom.nextBoolean();
	}
	
	public double getDouble(int type){
		switch(type){
		case TypeRandomize.FILE_SIZE:
			return getDoubleFileSize();
		case TypeRandomize.NOTIFICATION_BAR:
			return getDoubleDialogBox();
			default:
				return 0;
		}
	}
	
	private double getDoubleFileSize(){
		/**
		 *  This way incremental value in progressBar [1000 ; 11000] 
		 *  [MAX_VALUE_PROGRESS_BAR = 100000]
		 */
		double number = mRandom.nextInt(20) * DataCommons.Content.SCALE_FILE_SIZE_STEP +
				DataCommons.Content.SCALE_FILE_SIZE_STEP * 2;
		Log.v(TAG, "File size[ms]: " + number);
		return number;
	}
	
	private double getDoubleDialogBox(){
		double value;
		if(DataCommons.Settings.IS_DEBUG){
			return (double)DataCommons.CountDown.TIMER_STEP_NOTIFICATION;
		}
		if(getBoolean()){
			Log.v(TAG, "Number positive");
			value = (mRandom.nextInt(100) / 10) * 
					DataCommons.CountDown.SCALE_NOTIFICATION_MINUTES_RANDOMIZE * 1000 * 6;
		}
		else{
			Log.v(TAG, "Number negative");
			value = (-1)*(mRandom.nextInt(100) / 10) * 
					DataCommons.CountDown.SCALE_NOTIFICATION_MINUTES_RANDOMIZE * 1000 * 6;
		}
		return (DataCommons.CountDown.TIMER_STEP_NOTIFICATION + value );
	}
	
	public int getIntRandom(int max){
		Log.v(TAG, "Get int random number");
		return mRandom.nextInt(max);
	}
	
	public static class TypeRandomize {
		public static final int FILE_SIZE = 1;
		public static final int NOTIFICATION_BAR = 2;
	}
	
	
}
