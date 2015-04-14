package com.gui.utils;



import com.data.utils.DataCommons;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Class set all interface parameters at the beginning, check Model Version, OS version and IMEI; 
 * visualizes those parameters on the screen.
 * @author troglodito22
 *
 */

public class InitializeInterface{
	
	private static String TAG= InitializeInterface.class.getSimpleName();
	
	private String versionModel;
	private int versionOS;
	
	
	/**
	 * Constructor
	 */
	public InitializeInterface(){
		super();
		Log.v(TAG, "Constructor");
	}
	
	public void getParameters(Context context){
		Log.v(TAG, "Get phone parameters");		
		versionModel = android.os.Build.MODEL;
		DataCommons.UserData.versionModel = versionModel;
		versionOS = android.os.Build.VERSION.SDK_INT;	
		DataCommons.UserData.versionOS = (versionOS);
		
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    	DataCommons.UserData.userIMEI = tm.getDeviceId()==null?tm.getLine1Number():tm.getDeviceId();
    	Log.v(TAG, "IMEI: " + DataCommons.UserData.userIMEI);
	}

}
