package com.services;

import com.application.main.RunningAppCollecter;
import com.application.utilities.AppTools;
import com.application.utilities.Randomize;
import com.application.utilities.Randomize.TypeRandomize;
import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedTest;
import com.databases.DatabaseHelperTest;
import com.databases.DatabaseUtils;
import com.gui.activities.DialogBoxActivity;
import com.gui.main.CustomNotification;
import com.sensors.BatteryCheck;
import com.sensors.ConnectivityInternet;
import com.sensors.ContactCheck;
import com.sensors.UserPosition;
import com.timers.CustomAlarm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Class Service creates all classes to check status cell-phone.
 * @author troglodito22
 *
 */

public class ServiceSensors extends Service{

	private final String TAG = ServiceSensors.class.getSimpleName();
	
	// Data collected class during the testing
	private DataCollectedTest mDataCollectedAltruism;

	private CustomAlarm mCustomAlarm;
	
	private Randomize mRandomize;
	private UserPosition mUserPosition;
	
	private DatabaseHelperTest db;
	private RunningAppCollecter mRunningAppCollecter;
	
	private Handler mHandler;
	private final IBinder mBinder = new ServiceSensorsBinder();
	
	public class ServiceSensorsBinder extends Binder{
		public ServiceSensors getService(){
			Log.v(TAG, "Service sensor binder class, getService method");
			return ServiceSensors.this;
		}
	}
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context,Intent intent){
			if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.START_TRACKING_FILTER)){
				startTrackService();
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.STOP_TRACKING_FILTER)){
				stopPauseTracking(DataCommons.StopPause.STOP);
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.PAUSE_TRACKING_FILTER)){
				stopPauseTracking(DataCommons.StopPause.PAUSE);
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.TRACK_DATA_POPUP_FILTER)){
				sendNotificationBar();
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.TRACK_DATA_USER_FILTER)){
				trackDataUser(intent.getExtras());
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.SEND_DATA_FILTER)){
				sendDataEmail();
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.DELETE_DATA_FILTER)){
				deleteData();
			}
			else if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.UPDATING_PROGRESSBAR_FILTER)){
				updateProgressBar();
			}
		}
	};
	
	@Override
	public void onCreate(){
		Log.v(TAG, "On create method");
		super.onCreate();
		
		mDataCollectedAltruism = new DataCollectedTest();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(DataCommons.Filter.START_TRACKING_FILTER);
		filter.addAction(DataCommons.Filter.STOP_TRACKING_FILTER);
		filter.addAction(DataCommons.Filter.PAUSE_TRACKING_FILTER);
		filter.addAction(DataCommons.Filter.TRACK_DATA_POPUP_FILTER);
		filter.addAction(DataCommons.Filter.TRACK_DATA_USER_FILTER);
		filter.addAction(DataCommons.Filter.DIALOG_BOX_FILTER);
		filter.addAction(DataCommons.Filter.SEND_DATA_FILTER);
		filter.addAction(DataCommons.Filter.DELETE_DATA_FILTER);
		filter.addAction(DataCommons.Filter.UPDATING_PROGRESSBAR_FILTER);
		filter.addAction(DataCommons.Filter.SEND_DATA_DIALOG_BOX);
		registerReceiver(receiver, filter);
		 // Set seed for Random numbers
		mRandomize = new Randomize();
		mRandomize.setSeed(Long.parseLong(DataCommons.UserData.userIMEI) + 
				System.currentTimeMillis());
		 //	Create Database
		db = new DatabaseHelperTest();
		db.createNewMonitorData(this.getApplicationContext());
		Log.v(TAG,DataCommons.Settings.IS_DEBUG?"Debug mode": "Normal mode");
		mUserPosition = new UserPosition(this.getApplicationContext());
		mRunningAppCollecter = new RunningAppCollecter(this.getApplicationContext());
		// Count down timer, custom alarm
		Log.v(TAG, "Creating Alarm Manager notification");
		mCustomAlarm = new CustomAlarm(this.getApplicationContext(),
				DataCommons.Filter.TRACK_DATA_POPUP_FILTER);
		
		// Start location 3G position
		mUserPosition.startLocationListener();
		
	}
	
	 @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        try{
        	if(intent.getAction().equalsIgnoreCase(DataCommons.Filter.START_TRACKING_FILTER)
        			|| intent.getAction().equalsIgnoreCase(DataCommons.Filter.PAUSED_BEFORE_FILTER)){
    			startTrackService();
    		}
        }catch(Exception e){
        	Log.e(TAG, "Error getting intent action: " + e.getMessage());
        }
        return START_STICKY;
    }
	
	 
	@Override
	public void onDestroy(){
		Log.v(TAG, "On Destroy method");
		try{
			unregisterReceiver(receiver);
		}catch(Exception e){
			Log.e(TAG, "Error Unregistering receiver: "+e.getMessage());
		}
		Log.v(TAG, "Receiver unregistered");
		super.onDestroy();
	}
	
	private void startTrackService(){
		Log.v(TAG,"Start monitoring, creating database");
		mDataCollectedAltruism = new DataCollectedTest();
		mCustomAlarm.setRepeatingAlarm(DataCommons.CountDown.TIMER_STEP_NOTIFICATION);
	}
	
	
	private void sendNotificationBar(){
		if(DialogBoxActivity.IS_ACTIVE){
			Log.v(TAG, "Dialog box popup, not change content");
			return;
		}
		Log.v(TAG, "Checking if send notification");
		if(DataCommons.Settings.IS_CONNECTED){
			Log.v(TAG, "Checking network connection");
			ConnectivityInternet ci = new ConnectivityInternet(this.getApplicationContext());
			if(ci.isParameterTrue("") == 0){
				Log.v(TAG, "Network connection is not active, not send notification");
				return;
			}
		}
		Log.v(TAG, "Track data, notification popup");
		mDataCollectedAltruism.setTimeNotification(System.currentTimeMillis());
		Bundle bundle = new Bundle();
		String contentType = " ";
		try{
			ContactCheck contact = new ContactCheck(this.getApplicationContext());
			
			contentType = setContentType();
			bundle.putString(DataCommons.DialogBoxKeys.NAME_SENDER_KEY,
					(contact.getPersonType(ContactCheck.SenderReceiver.SENDER, null)));
			bundle.putString(DataCommons.DialogBoxKeys.NAME_RECEIVER_KEY,
					contact.getPersonType(ContactCheck.SenderReceiver.RECEIVER, null));
			bundle.putInt(DataCommons.DialogBoxKeys.CONTENT_TYPE_KEY, 
					mDataCollectedAltruism.getContentType());
			bundle.putLong(DataCommons.DialogBoxKeys.CONTENT_SIZE_KEY, 
					mDataCollectedAltruism.getContentSize());
			int[] result = contact.updateDataContactCollected();
			mDataCollectedAltruism.setTypeSender(result[ContactCheck.SenderReceiver.SENDER]);
			mDataCollectedAltruism.setTypeReceiver(result[ContactCheck.SenderReceiver.RECEIVER]);
		}catch(Exception e){
			Log.e(TAG, "Exception when creating bundle: " + e.getMessage());
			contentType = " ";
			return;
		}
		CustomNotification customNotification = new CustomNotification(getApplicationContext());
		customNotification.setTitleTextNotification(contentType);
		Log.v(TAG, customNotification.showNotification(bundle) ? 
				"NotificationBar created":
				"NotificationBar has created before");
	}
	
	
	private void trackDataUser(Bundle bundle){
		Log.v(TAG, "Track user option selected and data phone");
		try{
			mDataCollectedAltruism.setTimePopup(bundle.getLong(
					DataCommons.DialogBoxKeys.TIME_POPUP_KEY));
			mDataCollectedAltruism.setTimeUser(bundle.getLong(
					DataCommons.DialogBoxKeys.TIME_USER_KEY));
			mDataCollectedAltruism.setOptionSelected(bundle.getInt(
					DataCommons.DialogBoxKeys.OPTION_SELECTED_KEY));
		}catch(Exception e){
			Log.e(TAG, "Exception when retrieving data dialog: " + e.getMessage());
		}
		try{
			Log.v(TAG, "Get current battery position values");
			BatteryCheck battery = new BatteryCheck(this.getApplicationContext());
			int[] result = battery.getParameters();
			mDataCollectedAltruism.setBatteryLevel(result[BatteryCheck.TypeParameter.BATTERY_LEVEL]);
			mDataCollectedAltruism.setBatteryStatus(result[BatteryCheck.TypeParameter.BATTERY_STATUS]);
			result = mUserPosition.getLocationUserGSM();
			mDataCollectedAltruism.setCellID(result[UserPosition.TypeReference.CELL_ID]);
			mDataCollectedAltruism.setLac(result[UserPosition.TypeReference.LAC]);
		}catch(Exception e){
			Log.e(TAG, "Error in current battery,position values: " + e.getMessage());
		}
		try{	
			db.track(mDataCollectedAltruism);
			mRunningAppCollecter.saveAppsRunning();
		}catch(Exception e){
			Log.e(TAG, "Exception when saving values in database: " + e.getMessage());
		}
		// Update with random value interval ticker count down
		long valueInterval = DataCommons.CountDown.TIMER_STEP_NOTIFICATION;
		try{
			valueInterval = (long)(mRandomize.getDouble(Randomize.TypeRandomize.NOTIFICATION_BAR));
		}catch(Exception e){
			Log.e(TAG, "Exception gettin random value: " + e.getMessage());
			valueInterval = DataCommons.CountDown.TIMER_STEP_NOTIFICATION;
		}
		Log.v(TAG, "Restart count down timer, with random value: "+ valueInterval);
		try{
			mCustomAlarm.setRepeatingAlarm(valueInterval);
		}catch(Exception e){
			Log.e(TAG, "Error when setting custom Alarm: " + e.getMessage());
		}
		transmitContent(bundle.getInt(DataCommons.DialogBoxKeys.CONTENT_TYPE_KEY), 
				bundle.getLong(DataCommons.DialogBoxKeys.CONTENT_SIZE_KEY));
	}
	
	private void stopPauseTracking(int type){
 		Log.v(TAG, "StopPause Service,type : " + (type==DataCommons.StopPause.STOP?
 				"STOP" : "PAUSE"));
		try{
			if(type == DataCommons.StopPause.STOP){
				db.closeDatabase();	
				mRunningAppCollecter.closeDatabase();
			}
		}catch(Exception e){
			Log.v(TAG, "Database hadn't been created before: "+e.getMessage());
		}
		try{
			mCustomAlarm.cancel();
			mUserPosition.stopLocationListener();
			CustomNotification.cancelNotifications();
		}catch(Exception e){
			Log.v(TAG, "Countdown has been closed before: " + e.getMessage());
		}
		try{
			if (type == DataCommons.StopPause.STOP){
				Message changeContent =  new Message();
				changeContent.what = DataCommons.MessageHandler.MESSAGE_CHANGE_STOP;
				mHandler.sendMessage(changeContent);
			}
		}catch(Exception e){
			Log.e(TAG, "Error when sending message to handler: "+e.getMessage());
		}
	}
	
	private void deleteData(){
		Log.v(TAG, "Delete data; databases and Xmls");
		stopPauseTracking(DataCommons.StopPause.STOP);
		int typeMessage = DataCommons.MessageHandler.DELETED_OK;  
		try{
			AppTools.deleteFiles(this, DatabaseUtils.Database.XML_DIRECTORY);
			AppTools.deleteFiles(this, DatabaseUtils.Database.DATABASE_DIRECTORY_APP);
			AppTools.deleteFiles(this, DatabaseUtils.Database.DATABASE_DIRECTORY);
		}catch(Exception e){
			Log.e(TAG, "Error delting data: "+ e.getMessage());
			typeMessage = DataCommons.MessageHandler.DELETE_ERROR;
		}	
		Message changeContent =  new Message(); 
		changeContent.what = typeMessage;
		mHandler.sendMessage(changeContent);
		
	}
	
	private void sendDataEmail(){
		Log.v(TAG, "Send Data email, stop and close databases");
		stopPauseTracking(DataCommons.StopPause.STOP);
	}
	
	private void updateProgressBar(){
		Log.v(TAG, "Update progress bar");
		Message changeContent =  new Message();
		changeContent.what = DataCommons.MessageHandler.UPDATE_PROGRESSBAR;
		changeContent.arg1 = 1;
		try{
			Log.v(TAG, mHandler.sendMessage(changeContent)?"Message sent" : "Can't send msg"); 
		}catch(Exception e){
			Log.e(TAG, "Exception sending message to activity: " + e.getMessage());
		}
	}
	/**
	 * set content type of data
	 * @return String type content
	 */
	private String setContentType(){
		Log.v(TAG, "get random type content and update Data collected");
		if(mRandomize.getBoolean()){
			mDataCollectedAltruism.setContentType(DataCollectedTest.ContentType.MESSAGE);
			mDataCollectedAltruism.setContentSize(DataCommons.Content.MESSAGE_SIZE);
			return DataCollectedTest.ContentType.arrayType[DataCollectedTest.ContentType.MESSAGE];
		}
		else{
			mDataCollectedAltruism.setContentType(DataCollectedTest.ContentType.FILE);
			setContentFileSize();
			return (DataCollectedTest.ContentType.arrayType[DataCollectedTest.ContentType.FILE]);
		}
		
	}
	/**
	 * set content size of data
	 */
	private void setContentFileSize(){
		Log.v(TAG, "Get file size");
		try{
			mDataCollectedAltruism.setContentSize ((long) mRandomize.getDouble(TypeRandomize.FILE_SIZE));
		}catch(Exception e){
			Log.e(TAG, "Exception when get random size file");
			mDataCollectedAltruism.setContentSize (DataCommons.Content.SCALE_FILE_SIZE_STEP);		
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent){
		Log.v(TAG, "On unBind Service");
		return super.onUnbind(intent);
	}
   
	/**
	 * function set Handler; messages to Activity
	 * @param handler
	 */
	public void setMHandler(Handler handler){
		Log.v(TAG, "Set message handler");
		mHandler = handler;
	}
	/**
	 * Transmit content, case File
	 * @param size
	 * @param type
	 */
	public void transmitContent(int type, long size){
		CustomNotification.IS_ACTIVE = false;
		switch(mDataCollectedAltruism.getOptionSelected()){
		case DataCollectedTest.Option.YES:
			if (type == DataCollectedTest.ContentType.FILE){
				Log.v(TAG, "Transmitting content, type file");
				CustomNotification notification = new CustomNotification(this.getApplicationContext());
				boolean result = notification.showNotificationProgressBar(size);
				Log.v(TAG, result?"Notification file created":"Notification file not created");
			}
			break;
		case DataCollectedTest.Option.NO:
			break;
		}
		// New DataCollected()
		mDataCollectedAltruism = new DataCollectedTest();
		updateProgressBar();
	}
	
}
