package com.databases;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.application.utilities.AppTools;
import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedTest;
import com.fileWriters.XMLFileWriterTest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Class to help SQLite Database
 * @author troglodito22
 *
 */

public class DatabaseHelperTest {

	private static String TAG = DatabaseHelperTest.class.getSimpleName();
	
	private static final SimpleDateFormat fileNameFormatter = new SimpleDateFormat ("yyyy_MM_dd_HH_mm_ss");
	
	//Database name, we have used Name + IMEI (IMEI if it is possible -legal staff- ).
	public static final String DB_NAME = "test_table";
					
	
	
	// SQL for creating table data user applications have been monitored
	private static final String SQL_CREATE_TABLE_NET_MONITOR = ""
        + "create table " + DB_NAME  + " ("
        + Schema.COL_ID + " integer primary key autoincrement,"
        + Schema.COL_TIMESTAMP_NOTIFICATION + " double not null,"
        + Schema.COL_TIMESTAMP_POPUP + " double not null," 
        + Schema.COL_TIMESTAMP_USER + " double not null," 
        + Schema.CONTENT_TYPE + " integer,"
        + Schema.CONTENT_SIZE + " double not null," 
        + Schema.TYPE_SENDER + " integer not null," 
        + Schema.TYPE_RECEIVER + " integer not null,"  
        + Schema.BATTERY_STATUS + " integer not null," 
        + Schema.BATTERY_LEVEL + " double not null,"
        + Schema.USER_MOVING + " integer not null,"
        + Schema.USER_LOCATION + " integer not null," 
        + Schema.COL_LONGITUDE + " double not null,"
        + Schema.COL_LATITUDE + " double not null,"
        + Schema.COL_CELLID + " integer,"
        + Schema.COL_LAC + " integer,"
        + Schema.COL_TIMESTAMP_GPS + " double not null,"
        + Schema.OPTION_SELECTED + " integer not null"
        + ")";
	
	
	//Context to interact with Database
	private static Context mContext;
	
	//SQLite database
	private SQLiteDatabase database;
	
	public DatabaseHelperTest(){
		super();
	}
	
	/**
	 * @function create new Database, deletes previous.
	 * @return boolean, true SQLitedatabase has created correct, false otherwise
	 * @throws IOException
	 */
	public boolean createNewMonitorData (Context context) {
		Log.v(TAG, "Creating database test");
		mContext = context;
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?mContext.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		File mDatabaseDir = new File(dir +File.separator + DatabaseUtils.Database.DATABASE_DIRECTORY);
		try{
			if(mDatabaseDir.mkdirs()){
				Log.v(TAG, "Directory database files created: " + mDatabaseDir.getName());
			}
		}catch(Exception e){
			Log.e(TAG, "Exception creating directory: "+e.getMessage());
		}
		String dirDatabase = dir 
				+ File.separator 
				+ DatabaseUtils.Database.DATABASE_DIRECTORY
				+ File.separator
				+ DataCommons.UserData.userIMEI
				+ "_"
				+ DB_NAME
				+ "_"
				+ fileNameFormatter.format(System.currentTimeMillis());
		try{
			String aux = AppTools.getDatabaseName(mContext, DataCommons.Preferences.DB_TEST_NAME_PREVIOUS);
			if(! aux.equalsIgnoreCase(DatabaseUtils.Database.RESET_DATABASE)){
				Log.v(TAG, "Reload database ");
				dirDatabase = aux;
			}
		}catch(Exception e){
			Log.e(TAG, "Error getting name database preferences: " + e.getMessage());
		}
		
		try{
			database = SQLiteDatabase.openDatabase(dirDatabase, 
					null,
					SQLiteDatabase.CREATE_IF_NECESSARY);
			AppTools.setDatabaseName(mContext, DataCommons.Preferences.DB_TEST_NAME_PREVIOUS,
					dirDatabase); 
		}catch(Exception e){
			Log.e(TAG, "Exception when opening database: " + e.getMessage());
		}
		
		try{
			database.execSQL("drop table if exists "+ DB_NAME);
			database.execSQL(SQL_CREATE_TABLE_NET_MONITOR);
			database.close();
		}catch(Exception e){
			Log.e(TAG, "Exception execSQL: " + e.getMessage());
		}
		File mXmlDir = new File(dir +File.separator + DatabaseUtils.Database.XML_DIRECTORY);
		try{
			if(mXmlDir.mkdirs()){
				Log.v(TAG, "Directory xml files created");
				return true;
			}
		}catch(Exception e){
			Log.e(TAG, "Exception creating directory: "+e.getMessage());
			return false;
		}
		Log.v(TAG, "Directory not created");
		return false;
	}
	
	// Return a 'Cursor' to the collected data in db
	public Cursor getDataCursor() {
		if(database != null && database.isOpen()) {
			//Query for collected data
			Cursor cursorDatabase = database.query(DB_NAME, new String[] {
								Schema.COL_ID, Schema.COL_TIMESTAMP_NOTIFICATION,
								Schema.COL_TIMESTAMP_POPUP, Schema.COL_TIMESTAMP_USER,
								Schema.CONTENT_TYPE, Schema.CONTENT_SIZE,
								Schema.TYPE_SENDER, Schema.TYPE_RECEIVER,
								Schema.BATTERY_STATUS, Schema.BATTERY_LEVEL,
								Schema.USER_MOVING, Schema.USER_LOCATION,
								Schema.COL_LONGITUDE, Schema.COL_LATITUDE,
								Schema.COL_CELLID, Schema.COL_LAC,
								Schema.COL_TIMESTAMP_GPS,Schema.OPTION_SELECTED}, 
								null, null, null, null,
								Schema.COL_TIMESTAMP_NOTIFICATION + " asc");
			
			cursorDatabase.moveToFirst();
			return cursorDatabase;
		}
		
		else {
			return null;  
		}
		
	}
	
	// Return a 'Cursor' to the collected data in db
		public static Cursor getDataCursor(SQLiteDatabase db) {
			Log.v(TAG, "Cursor getCursor database: "+ db.getPath());
			if(db != null && db.isOpen()) {
				//Query for collected data
				Cursor cursorDatabase = db.query(DB_NAME, new String[] {
									Schema.COL_ID, Schema.COL_TIMESTAMP_NOTIFICATION,
									Schema.COL_TIMESTAMP_POPUP, Schema.COL_TIMESTAMP_USER,
									Schema.CONTENT_TYPE, Schema.CONTENT_SIZE,
									Schema.TYPE_SENDER, Schema.TYPE_RECEIVER,
									Schema.BATTERY_STATUS, Schema.BATTERY_LEVEL,
									Schema.USER_MOVING, Schema.USER_LOCATION,
									Schema.COL_LONGITUDE, Schema.COL_LATITUDE,
									Schema.COL_CELLID, Schema.COL_LAC, 
									Schema.COL_TIMESTAMP_GPS,Schema.OPTION_SELECTED}, 
									null, null, null, null,
									Schema.COL_TIMESTAMP_NOTIFICATION + " asc");
				
				cursorDatabase.moveToFirst();
				return cursorDatabase;
			}
			
			else {
				Log.v(TAG, "Couldn't open database: "+ db.getPath());
				return null;  
			}
			
		}
	
	// Save application_data into DB
	public void track(DataCollectedTest collectedData){
		Log.v(TAG, "Saving application data: "+ collectedData.getClass().getSimpleName());
		String dirDatabase = AppTools.getDatabaseName(mContext, 
				DataCommons.Preferences.DB_TEST_NAME_PREVIOUS);
		try{
			Log.v(TAG, "Open database: " + dirDatabase);
			database = SQLiteDatabase.openDatabase(dirDatabase, 
					null,
					SQLiteDatabase.OPEN_READWRITE);
		}catch(Exception e){
			Log.e(TAG, "Exception when opening database: " + e.getMessage());
			return;
		}
		
		ContentValues values = new ContentValues();
		values.put(Schema.COL_TIMESTAMP_NOTIFICATION, collectedData.getTimeNotification());
		values.put(Schema.COL_TIMESTAMP_POPUP, collectedData.getTimePopup());
		values.put(Schema.COL_TIMESTAMP_USER, collectedData.getTimeUser());
		values.put(Schema.CONTENT_TYPE, collectedData.getContentType());
		values.put(Schema.CONTENT_SIZE, collectedData.getContentSize());
		values.put(Schema.TYPE_SENDER, collectedData.getContentSize());
		values.put(Schema.TYPE_SENDER, collectedData.getTypeSender());
		values.put(Schema.TYPE_RECEIVER, collectedData.getTypeReceiver());
		values.put(Schema.BATTERY_STATUS, collectedData.getBatteryStatus());
		values.put(Schema.BATTERY_LEVEL, collectedData.getBatteryLevel());
		values.put(Schema.USER_MOVING, collectedData.getUserLocation());
		values.put(Schema.USER_LOCATION, collectedData.getUserLocation());
		values.put(Schema.COL_LONGITUDE, collectedData.getLongitude());
		values.put(Schema.COL_LATITUDE, collectedData.getLatitude());
		values.put(Schema.COL_CELLID, collectedData.getCellID());
		values.put(Schema.COL_LAC, collectedData.getLac());
		values.put(Schema.COL_TIMESTAMP_GPS, collectedData.getTimeLocation());
		values.put(Schema.OPTION_SELECTED, collectedData.getOptionSelected());

		values.put(Schema.OPTION_CAUSE, collectedData.getOptionSelected());
		values.put(Schema.MOOD, collectedData.getOptionSelected());
		
		database.insert(DB_NAME, null, values);
		database.close();
	}
	
	
	
	/**
	 *  Exports current database to a File.
	 */
	public static void exportDataAsFile(Context context) {
		Log.v(TAG, "Export database as XML file, method init");
		mContext = context;
		
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?mContext.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		// FileDir where databases have been saved
		File databaseDir = new File(dir +File.separator + DatabaseUtils.Database.DATABASE_DIRECTORY);
		// FileDir where we export databases as Xml
		File targetXml = new File(dir +File.separator + DatabaseUtils.Database.XML_DIRECTORY,
				DB_NAME
				+ "_"
				+ fileNameFormatter.format(new Date()) 
				+ DatabaseUtils.Database.EXTENSION_FILE);

		try {
			XMLFileWriterTest.writeXmlFile(databaseDir.list(), targetXml, databaseDir.getPath());
			} catch(IOException ioe){
			System.out.println("Error in exportDataAsFile: "+ioe.getMessage());
			Log.e(TAG, "Error when exporting sqldatabase as file: "+ioe.getMessage());
		}	
		Log.v(TAG, "Export correct");
	}
	
	
	public  void closeDatabase(){
		Log.v(TAG, "Closing database");
		AppTools.setDatabaseName(mContext, DataCommons.Preferences.DB_TEST_NAME_PREVIOUS,
				DatabaseUtils.Database.RESET_DATABASE);
		database.close();
	}

	
	public void deleteDatabase(){
		Log.v(TAG, "Deleting Database");
		mContext.deleteDatabase(DB_NAME);
	}
	
	
	public static final class Schema {
		
		public static final String COL_ID = "_id";
		public static final String COL_TIMESTAMP_NOTIFICATION = "timestamp_notification";
		public static final String COL_TIMESTAMP_POPUP = "timestamp_popup";
		public static final String COL_TIMESTAMP_USER = "timestamp_user";
		public static final String CONTENT_TYPE = "content_type";
		public static final String CONTENT_SIZE = "content_size";
		public static final String TYPE_SENDER = "type_sender";
		public static final String TYPE_RECEIVER = "type_receiver";
		public static final String BATTERY_STATUS = "battery_status";
		public static final String BATTERY_LEVEL = "battery_level";
		public static final String USER_MOVING = "user_moving";
		public static final String USER_LOCATION = "user_location";
		public static final String COL_LONGITUDE = "longitude";
		public static final String COL_LATITUDE = "latitude";
		public static final String COL_CELLID = "cellid";
		public static final String COL_LAC = "lac";
		public static final String COL_TIMESTAMP_GPS = "timestamp_location";
		public static final String OPTION_SELECTED = "option_selected";
		
		public static final String OPTION_CAUSE = "option_cause";
		public static final String MOOD = "mood";
		
	}
	
	
}
