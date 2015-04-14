package com.data.utils;

import com.main.shared_net_facebook.activities.Emulator_Test_Activity;

import android.content.Context;
import android.content.res.Resources;

/**
 * Data class for data uses from different classes
 * @author troglodito22
 *
 */

public class DataCommons {

	
	
	public static String[] getArrayFromResources(Context context, int name){
		Resources res = context.getResources();
		return res.getStringArray(name);
	}
	
	public static class Settings{
		public static final boolean IS_DEBUG = false;
		public static final boolean IS_CONNECTED = false;
		public static final boolean IS_3G_LOCATION_ON = false;
		public static final boolean IS_FACEBOOK_ON = false;
		public static final boolean IS_CONTACT_KNOWN_ACTIVE = false;
		public static final boolean IS_AMAZON_TURK = false;
	}
	
	
	public static class UserData {
		public static String userIMEI = "0";
		public static String userAgeSex="5";
		public static String userId="22";
		public static int versionOS = 0;
		public static String versionModel = "_";
		
		public static final String DATA_SEPARATOR = ";";
	}
	
	public static class CompressData {
		public final static String ZIP_NAME = DataCommons.UserData.userIMEI
				+ "_" 
				+ DataCommons.UserData.userId
				+ "_"
				+ "test";
	}
	
	public static class BatteryParamaters{		
		public static final int MIN_VALUE_BATTERY = 2;
	}
	
	public static class Content{
		// File progress Bar
		public static final long FILE_SIZE_MAX = 100000;	//[]	
		public static final int MESSAGE_SIZE = 140;
		public static final int SCALE_FILE_SIZE_STEP = 500; // [KB]
		
	}
	
	public static class CountDown{
		public static final int COUNT_DOWN_DIALOG= 1;
		public static final int COUNT_DOWN_TEST = 2;
		
		// Timer Global Frequency Notifications & Test Duration
		private final static long FREQUENCY_NOTIFICATION = 1000 * 60 * 8;
		// Debug parameters
		private static long DEBUG_MAX_TEST = 2;
		// TEST
		private static long REAL_TEST_MAX = 20 + 1; // because first is to show progress to user
		public static final long TEST_DURATION = (Settings.IS_DEBUG)? DEBUG_MAX_TEST:
			REAL_TEST_MAX;		// [minutes] : [hours] 
		// NOTIFICATIONs
		public final static long TIMER_STEP_NOTIFICATION = (Settings.IS_DEBUG)? 100 * 60 * 1 : 
			FREQUENCY_NOTIFICATION;
		public static final int SCALE_NOTIFICATION_MINUTES_RANDOMIZE = 2; // [minutes]
	}
	
	public static class Preferences{
		public static final int ACTIVITY_MAIN = 1;	
		public static String NOTIFICATION_EXTRA="";
		//TAG shared preferences
		public static final String PREFERENCES_NAME = Emulator_Test_Activity.class.getName();
		public static final String USER_NAME = "userName";
		public static final String USER_ID ="userId";
		public static final String FACEBOOK_TOKEN = "access_token";
		public static final String FACEBOOK_EXPIRES = "access_expires";
		public static final String PROGRESS_BAR_LEVEL = "progressbar_level";
		public static final String SERVICE_ON = "service_on";
		public static final String PAUSED_BEFORE = "is_paused_before";
		public static final String DB_TEST_NAME_PREVIOUS = "db_test_name_previous";
		public static final String DB_APP_NAME_PREVIOUS = "db_app_name_previous";
	}
	
	public static class DialogBoxKeys{
		public static final String DATA_COLLECTED_KEY = "data_collected";
		public static final String NAME_SENDER_KEY = "name_sender";
		public static final String NAME_RECEIVER_KEY = "name_receiver";
		public static final String TIME_POPUP_KEY = "time_popup_key";
		public static final String TIME_USER_KEY = "time_user";
		public static final String OPTION_SELECTED_KEY = "option_selected";
		public static final String CONTENT_TYPE_KEY ="content_type";
		public static final String CONTENT_SIZE_KEY ="content_size";
	}
	
	public static class ProgressBarParameters{
		public final static int PROGRESS_BAR_STEP = 1000;
	}
	
	public static class Filter{
		public static final String INIT_SERVICE_FILTER = "init_service_filter";
		public static final String TRACK_DATA_POPUP_FILTER = "track_data_popup";
		public static final String TRACK_DATA_USER_FILTER = "track_data_user";
		public static final String START_TRACKING_FILTER = "start_tracking";
		public static final String STOP_TRACKING_FILTER = "stop_tracking";
		public static final String PAUSE_TRACKING_FILTER = "pause_tracking";
		public static final String NOTIFICATION_BAR ="notification_bar";
		public static final String DIALOG_BOX_FILTER = "dialog_box_filter";
		public static final String SEND_DATA_FILTER = "send_data_filter";
		public static final String DELETE_DATA_FILTER = "delete_data_filter";
		public static final String SEND_DATA_DIALOG_BOX = "send_data_dialog_box";
		public static final String UPDATING_PROGRESSBAR_FILTER = "updating_progressbar_filter";
		public static final String PAUSED_BEFORE_FILTER = "paused_before_filter";
	}
	
	public static class StopPause{
		public static final int STOP = 1;
		public static final int PAUSE = 2;
	}
	
	public static class ThreadParameters{
		public static final String NAME_THREAD = "export_data";
		public static final String MESSAGE_ERROR = "Error when exporting";
		public static final String MESSAGE_OK = "Exporting succesfull";
		public static final int MESSAGE_INT_ERROR = 1;
		public static final int MESSAGE_INT_OK = 2;
	}
	
	public static class MessageHandler {
		public static final int MESSAGE_CHANGE_STOP = 1;
		public static final int UPDATE_PROGRESSBAR = 2;
		public static final int DELETED_OK = 3;
		public static final int DELETE_ERROR = 4; 
	}
}
