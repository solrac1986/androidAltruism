package com.application.main;

import java.util.Iterator;

import com.application.utilities.AppsRunning;
import com.dataCollected.DataCollectedApp;
import com.databases.DatabaseHelperApp;


import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

/**
 * Class to retrieve all running applications when user selects option and app track data into databases
 * @author troglodito22
 *
 */

public class RunningAppCollecter {

	private static final String TAG = RunningAppCollecter.class.getSimpleName();
	
	private AppsRunning mAppsRunning;
	private DataCollectedApp mCollectedData;
	private Context mContext;
	
	private DatabaseHelperApp mDatabaseHelperApp;  
	
	public RunningAppCollecter(Context context){
		Log.v(TAG, "Constructor class");
		mContext = context;
		mDatabaseHelperApp = new DatabaseHelperApp();
		mDatabaseHelperApp.createNewMonitorData(context.getApplicationContext());
	}
	
	public void saveAppsRunning(){
		Log.v(TAG, "Start Apps running method");
		mAppsRunning = new AppsRunning(mContext.getApplicationContext());
		boolean appUpdated = false;
		Iterator<?> iterat = mAppsRunning.getIterator();
		RunningAppProcessInfo mProcess;
		while(iterat.hasNext()){
			boolean appIsUpdate=false;
			mProcess = (RunningAppProcessInfo)iterat.next();
			appIsUpdate = mAppsRunning.checkPreviousData(mProcess.processName, mProcess.pid, mProcess.uid,
					TrafficStats.getUidTxBytes(mProcess.uid), TrafficStats.getUidRxBytes(mProcess.uid));
			if(appIsUpdate){
				Log.v(TAG, "Data app: "+mProcess.processName + " has been changed");
				appUpdated = true;
				mCollectedData = new DataCollectedApp(System.currentTimeMillis(),mAppsRunning.getIntApplication(mProcess.processName),
								mAppsRunning.getNumRunningProcess(),
								TrafficStats.getUidTxBytes(mProcess.uid), TrafficStats.getUidRxBytes(mProcess.uid),
								mAppsRunning.getTxPackets(mProcess.processName), mAppsRunning.getRxPackets(mProcess.processName),
								DataCollectedApp.PacketType.NORMAL, DataCollectedApp.PacketType.NORMAL,
								isForeground(mProcess.importance));
				pushToDatabase();
			}
		}
		if(appUpdated == false){
			Log.v(TAG, "Updating only running process");
			mCollectedData = new DataCollectedApp();
			mCollectedData.setTime(System.currentTimeMillis());
			mCollectedData.setNumProcess(mAppsRunning.getNumRunningProcess());
			pushToDatabase();
		}
		
	}
	
	private void pushToDatabase(){
		Log.v(TAG, "Push data to database");
		mDatabaseHelperApp.track(mCollectedData);
	}
	
	public DataCollectedApp getCollectedDataApp(){
		return mCollectedData;
	}
	
	public void closeDatabase(){
		Log.v(TAG, "Closing database");
		mDatabaseHelperApp.closeDatabase();
	}
	
	private int isForeground(int importance){
		if(importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
			return DatabaseHelperApp.BooleanSQLite.isTrue;
		}
		else{
			return DatabaseHelperApp.BooleanSQLite.isFalse;
		}
	}
	
}
