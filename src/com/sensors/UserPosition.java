package com.sensors;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedTest;
import com.interfaces.FeaturesInterface;


/**
 * Class to get user position.
 * We have used NETWORK_LOCATION Provider, to save battery and not decrease phone performance.
 * @author troglodito22
 *
 */


public class UserPosition implements FeaturesInterface, LocationListener{

	private final String TAG = UserPosition.class.getSimpleName();
	
	private final long MINIMUM_DISTANCE_CHANGE = 50;		// [meters]
	private final long MINIMUM_TIME_INTERVAL = 1000 * 60 * 5;	//[minutes]
	
	// GSM Location provider
	private TelephonyManager mTM;
	
	@SuppressWarnings("unused")
	private Intent mIntent;
	private Context mContext;
	
	private Location lastLocation;
	
	public UserPosition(Context context){
		Log.v(TAG, "Constructor");
		mContext = context;
	}
	
	@Override
	public void setIntent(Intent intent) {
		mIntent = intent;
	}

	@Override
	public Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public Context getContext() {
		Log.v(TAG, "Get context");
		return mContext;
	}

	@Override
	public int isParameterTrue(Object objec) {
		Log.v(TAG, "Parameter is true");
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int[] getLocationUserGSM(){
		Log.v(TAG, "Get Location GSM");
		int[] result = new int[2];
		try{
			mTM = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
			GsmCellLocation location = (GsmCellLocation)mTM.getCellLocation();
			Log.v(TAG, "Get location CellID: " + location.getCid() +
					" lac: " + location.getLac());
			result[TypeReference.CELL_ID] = (location.getCid());
			result[TypeReference.LAC] = (location.getLac());
		}catch(Exception e){
			Log.e(TAG, "Error when saving CellID and lac: "+e.getMessage());
		}
		return result;
	}
	
	public void getLocationUser(DataCollectedTest dataCollected){
		Log.v(TAG,"Get location user");
		try{
			GsmCellLocation location = (GsmCellLocation)mTM.getCellLocation();
			Log.v(TAG, "Get location CellID: " + location.getCid() +
					" lac: " + location.getLac());
			dataCollected.setCellID(location.getCid());
			dataCollected.setLac(location.getLac());
		}catch(Exception e){
			Log.e(TAG, "Error when saving CellID and lac: "+e.getMessage());
		}
		if(DataCommons.Settings.IS_3G_LOCATION_ON){
			Log.v(TAG, " Get and save location user");
			try{
				dataCollected.setLatitude(lastLocation.getLatitude());
				dataCollected.setLongitude(lastLocation.getLongitude());
				dataCollected.setTimeLocation(lastLocation.getTime());
			}catch(Exception e){
				Log.e(TAG, "Error when saving lastLocation: "+e.getMessage());
			}	
		}
		else{
			Log.v(TAG, "Location provider OFF");
		}
	}
	
	public void startLocationListener(){
		Log.v(TAG, "Starting Location Listener, CellID");
		mTM = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if(DataCommons.Settings.IS_3G_LOCATION_ON){
			Log.v(TAG, "Creating location listener");
			try{
				LocationManager lmgr = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
				lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						MINIMUM_DISTANCE_CHANGE, 
						MINIMUM_TIME_INTERVAL,
						this);
			}catch(Exception e){
				Log.v(TAG, "Not location provider service: "+e.getMessage());
				return;
			}
			
		}
		else{
			Log.v(TAG, "Not location listener Activate");
		}
	}
	
	
	public void stopLocationListener(){
		Log.v(TAG, "Stopping Location listener");
		try{
			LocationManager lmgr = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
			lmgr.removeUpdates(this);
		}catch(Exception e){
			Log.e(TAG, "Error removing updates location listener: "+e.getMessage());
		}
	}
	
	/**
	 *  LocationListener methods.
	 */
	
	@Override
	public void onLocationChanged(Location location) {
		Log.v(TAG, "Location has changed");
		lastLocation = location;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public static class TypeReference{
		public static final int CELL_ID = 0;
		public static final int LAC = 1;
	}
}
