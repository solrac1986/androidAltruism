package com.application.utilities;

import java.util.HashMap;
import java.util.Map;

import com.dataCollected.DataCollectedApp;

import android.util.Log;

/**
 * Class with Map<string,String[string,string]>, filter process application to monitor only determined by user
 * @author troglodito22
 *
 */

public class FilterName {

	private final String TAG = FilterName.class.getSimpleName();
	
	// "com." of process namen  
	public final int OFFSET = 4;
	
	private Map<String,DataHashMap> listFilterApp;
	
	
	public FilterName(){
		super();
		listFilterApp = new HashMap<String, DataHashMap>();
		//Create list filter <Key, Value>
		listFilterApp.put(DataCollectedApp.Application.applicationProcess[DataCollectedApp.Application.FACEBOOK],
							new DataHashMap(DataCollectedApp.Application.applicationName[DataCollectedApp.Application.FACEBOOK],
									DataCollectedApp.Application.FACEBOOK));
		listFilterApp.put(DataCollectedApp.Application.applicationProcess[DataCollectedApp.Application.WHATSAPP],
							new DataHashMap(DataCollectedApp.Application.applicationName[DataCollectedApp.Application.WHATSAPP], 
									DataCollectedApp.Application.WHATSAPP));
		listFilterApp.put(DataCollectedApp.Application.applicationProcess[DataCollectedApp.Application.SKYPE], 
							new DataHashMap(DataCollectedApp.Application.applicationName[DataCollectedApp.Application.SKYPE],
									DataCollectedApp.Application.SKYPE));
		listFilterApp.put(DataCollectedApp.Application.applicationProcess[DataCollectedApp.Application.GMAIL], 
							new DataHashMap(DataCollectedApp.Application.applicationName[DataCollectedApp.Application.GMAIL],
									DataCollectedApp.Application.GMAIL));
		listFilterApp.put(DataCollectedApp.Application.applicationProcess[DataCollectedApp.Application.TWITTER], 
							new DataHashMap(DataCollectedApp.Application.applicationName[DataCollectedApp.Application.TWITTER],
									DataCollectedApp.Application.TWITTER));
	}
	
	
	
	/**
	 * function returns name application,
	 * @param processname key for searching in hashtable
	 * @return
	 */
	public String getNameApp(String processname) {
		try {
			DataHashMap result = listFilterApp.get(getProcessString(processname));
			Log.v(TAG, "Data application: " + result +  " saved in HashTable");
			return result.getAppName();
		}catch(Exception e){
			Log.v(TAG, "Exception when searching for key in hashtable: "+e.getMessage());
			return processname;
		}
		
	}
	
	public int getIntApp(String processname) {
		try {
			DataHashMap result = listFilterApp.get(getProcessString(processname));
			Log.v(TAG, "Data application: " + result +  " has saved in HashTable");
			return result.getAppInt();
		}catch(Exception e){
			Log.v(TAG, "Exception when searching for key in hashtable: "+e.getMessage());
			return DataHashMap.NOT_FIND;
		}
		
	}
	
	
	public boolean setProcessID(String processname,int pid, int uid){
		Log.v(TAG, "Set procces ID");
		String mProcessName = getProcessString(processname);
		if (listFilterApp.containsKey(mProcessName)){
			try{
				DataHashMap result = listFilterApp.remove(mProcessName);
				result.setPID(pid);
				result.setUID(uid);
				listFilterApp.put(mProcessName,result);
				Log.v(TAG, "Key udpdate with UUID & PID");
				return true;
			}catch(Exception e){
				Log.v(TAG, "Not process name found: "+e.getMessage());
				return false;
			}
		}
		else {
			Log.v(TAG, "Key not found in list");
			return false;
		}
	}
	
	private String getProcessString(String process){
		Log.v(TAG,"Get proccess string name");
		try {
			return process.substring(0,process.indexOf(".",OFFSET));
		}catch(Exception e) {
			Log.v(TAG, "Not possible substring process name: "+e.getMessage());
			return process;
		}
	}
	
	public long getTxPackets(String name){
		Log.v(TAG, "Get Tx Packets");
		try{
			DataHashMap result = listFilterApp.get(getProcessString(name));
			return result.getTxPackets();
		}catch(Exception e){
			Log.v(TAG, "Not process found in hash table: "+e.getMessage());
			return 0;
		}
		
	}
	
	public long getRxPackets(String name){
		try{
			DataHashMap result = listFilterApp.get(getProcessString(name));
			return result.getRxPackets();
		}catch(Exception e){
			Log.v(TAG, "Not process found in hash table: "+e.getMessage());
			return 0;
		}
		
	}
	
	/**
	 * 
	 * @param name
	 * @param tx
	 * @param rx
	 * @return true if update table, new tx-rx has realized; false otherwise
	 */
	public boolean updateTrafficTableParams(String name, long tx, long rx){
		Log.v(TAG,"Updating Traffic Params");
		boolean valueReturn = false;
		String mProcessName = getProcessString(name);
		DataHashMap result = new DataHashMap();
		try{	
			result = listFilterApp.get(mProcessName);
		}catch(Exception e){
			Log.v(TAG, "Not process found in hash table: "+e.getMessage());
			return false;
		}
		if(result.getTxBytes() != tx && tx !=(-1)) {
			result.setTxBytes(tx);
			result.setTxPackets();
			listFilterApp.put(mProcessName, result);
			valueReturn = true;
		}
		else if (result.getRxBytes() != rx && rx !=(-1)){
			result.setRxBytes(rx);
			result.setRxPackets();
			listFilterApp.put(mProcessName, result);
			valueReturn = true;
		}
		return valueReturn;
	}
	
	protected  class DataHashMap {
		
		
		private  String appData = "nameApp";
		private int appInt = NOT_FIND;
		private  int pid = 0;
		private  int uid = 0;
		private  long TxPackets_last = 0;
		private  long  RxPackets_last = 0;
		private  long TxBytes_last = 0;
		private	 long RxBytes_last = 0;
		
		public static final int NOT_FIND = -1;
		
		public DataHashMap(){
			super();
		}
		
		public DataHashMap(String nameApp){
			appData = nameApp;
			pid=0;
			uid=0;
			TxPackets_last = 0;
			RxPackets_last = 0;
		}
		public DataHashMap(String nameApp, int pid,int uid){
			appData = nameApp;
			this.pid=pid;
			this.uid=uid;
			TxPackets_last = 0;
			RxPackets_last = 0;
			TxBytes_last = 0;
			RxBytes_last = 0;
		}
		
		public DataHashMap (String nameApp, int appNum){
			appData = nameApp;
			appInt = appNum;
			pid = 0;
			uid = 0;
			TxPackets_last = 0;
			RxPackets_last = 0;
			TxBytes_last = 0;
			RxBytes_last = 0;
			
		}
		
		//Methods for set/get values.
		public void setAppName(String nameProc){
			appData = nameProc;
		}
		public void setAppInt (int value){
			appInt = value;
		}
		public void setPID(int p){
			pid = p;
		}
		public void setUID(int u){
			uid = u;
		}
		public void setTxBytes(long tx){
			TxBytes_last = tx;
		}
		public void setRxBytes(long rx){
			RxBytes_last = rx;
		}
		public void setTxPackets(){
			TxPackets_last++;
		}
		public void setRxPackets(){
			RxPackets_last++;
		}
		public String getAppName(){
			 return appData;
		}
		public int getAppInt(){
			return appInt;
		}
		public int getPID(){
			return pid;
		}
		public int getUID(){
			return uid;
		}
		public long getTxBytes(){
			return TxBytes_last;
		}
		public long getRxBytes(){
			return RxBytes_last;
		}
		public long getTxPackets(){
			return TxPackets_last;
		}
		public long getRxPackets(){
			return RxPackets_last;
		}
	}
	
	
}
