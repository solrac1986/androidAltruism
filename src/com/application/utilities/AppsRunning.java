package com.application.utilities;

import java.util.Iterator;
import java.util.List;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;

/**
 * Class to check all app runing in our Android OS
 * @author troglodito22
 *
 */


public class AppsRunning {

	private final String TAG = AppsRunning.class.getSimpleName();
	
	private List<RunningAppProcessInfo> listAppProcessInfo;
	
	private FilterName mFilterName;
	
	public AppsRunning(){
		super();
	}
	
	public AppsRunning(Context context){
		listAppProcessInfo = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
		//Create HashMap Table
		mFilterName = new FilterName();
		//Initialize Data
		
	}
	
	public String getNameApplication(String process){
		return mFilterName.getNameApp(process);
	}
	
	public int getIntApplication(String process){
		return mFilterName.getIntApp(process);
	}
	
	public Iterator<?> getIterator(){
		return listAppProcessInfo.iterator();
	}
	
	public int getNumRunningProcess(){
		return listAppProcessInfo.size();
	}
	
	public long getTxPackets(String name){
		return mFilterName.getTxPackets(name);
	}
	
	public long getRxPackets(String name){
		return mFilterName.getRxPackets(name);
	}
	
	public boolean checkPreviousData(String name,int pid, int uid,long tx, long rx){
		Log.v(TAG, "Check previous data,process: " + name);
		if(mFilterName.setProcessID(name, pid, uid)){
			return mFilterName.updateTrafficTableParams(name, tx, rx);
		}
		return false;
	}
	
	
	public void updateProcessRunning(){
		// Check If applications has been monitored has sent/received something.
		for(int i=0; i< listAppProcessInfo.size();i++){
			String nameApp = mFilterName.getNameApp(listAppProcessInfo.get(i).processName); 
			Log.v(TAG, "Process name: "+ nameApp+" uuid: "+ listAppProcessInfo.get(i).uid + " pid: "+listAppProcessInfo.get(i).pid);
			mFilterName.setProcessID(nameApp, listAppProcessInfo.get(i).uid, listAppProcessInfo.get(i).uid);

		}
	}
}
