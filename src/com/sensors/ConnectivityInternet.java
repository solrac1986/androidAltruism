package com.sensors;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.interfaces.FeaturesInterface;


/**
 * Class Internet Connectivity, status.
 * @author troglodito22
 *
 */

public class ConnectivityInternet implements FeaturesInterface{

	private final String TAG = ConnectivityInternet.class.getSimpleName();
	
	private ConnectivityManager mConnectivityManager;
	private Context mContext;
	
	
	public ConnectivityInternet(Context context){
		super();
		mContext = context;
	}
	
	@Override
	public void setIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(Context context) {
		Log.v(TAG, "Set context");
		mContext = context;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return mContext;
	}

	@Override
	public int isParameterTrue(Object objec) {
		Log.v(TAG, "Is parameter true");
		try{
			mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo =  mConnectivityManager.getActiveNetworkInfo();
			if (mConnectivityManager.getActiveNetworkInfo() != null && netInfo.isConnected()){
				Log.v(TAG, "Internet connection active");
				return 1;
			}
			Log.v(TAG, "Not internet connection active");
			return 0;
		}catch(Exception e){
			Log.v(TAG, "Exception taking net info");
			return 0;
		}
	}

}
