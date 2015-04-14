 package com.databases;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.application.utilities.AppTools;
import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedApp;
import com.fileWriters.XMLFileWriterApp;

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

public class DatabaseHelperApp {

	private static String TAG = DatabaseHelperApp.class.getSimpleName();
	
	

	private static final SimpleDateFormat fileNameFormatter = new SimpleDateFormat ("yyyy_MM_dd_HH_mm_ss");
	
	private static final String DB_NAME = "application_table";
	
	
	// SQL for creating table data user applications have been monitored
	private static final String SQL_CREATE_TABLE_NET_MONITOR = ""
        + "create table " + DB_NAME + " ("
        + Schema.COL_ID + " integer primary key autoincrement," 
        + Schema.COL_TIMESTAMP + " double not null," 
        + Schema.COL_APP + " integer,"
        + Schema.COL_NUM_PROC + " integer,"
        + Schema.COL_PACKET_COUNTER_TX + " double not null," 
        + Schema.COL_PACKET_COUNTER_RX + " double not null," 
        + Schema.COL_PACKET_SIZE_TX + " double not null,"  
        + Schema.COL_PACKET_SIZE_RX + " double not null," 
        + Schema.COL_PACKET_TYPE_TX + " integer,"
        + Schema.COL_PACKET_TYPE_RX + " integer,"
        + Schema.COL_APP_FOREGROUND + " integer not null" 
        + ")";
	
	
	
	//Context to interact with Database
	private static Context mContext;
	
	//SQLite database
	private SQLiteDatabase database;
	
	public DatabaseHelperApp(){
		super();
	}
	
	/**
	 * @function create new Database, deletes previous.
	 * @return boolean, true SQLitedatabase has created correct, false otherwise
	 * @throws IOException
	 */
	public boolean createNewMonitorData (Context context) {
		Log.v(TAG, "Creating database apps");
		mContext = context;
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?mContext.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		File mDatabaseDir = new File(dir +File.separator + DatabaseUtils.Database.DATABASE_DIRECTORY_APP);
		try{
			if(mDatabaseDir.mkdirs()){
				Log.v(TAG, "Directory database files created: " + mDatabaseDir.getName());
			}
		}catch(Exception e){
			Log.e(TAG, "Exception creating directory: "+e.getMessage());
		}
		String dirDatabase= dir 
				+ File.separator 
				+ DatabaseUtils.Database.DATABASE_DIRECTORY_APP
				+ File.separator
				+ DataCommons.UserData.userIMEI
				+ "_"
				+ DB_NAME
				+ "_"
				+ fileNameFormatter.format(System.currentTimeMillis());
		try{
			String aux = AppTools.getDatabaseName(mContext, 
					DataCommons.Preferences.DB_APP_NAME_PREVIOUS);
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
			AppTools.setDatabaseName(mContext, DataCommons.Preferences.DB_APP_NAME_PREVIOUS,
					dirDatabase); 
		}catch(Exception e){
			Log.e(TAG, "Error when opening database: " + e.getMessage());
		}
		try{
			database.execSQL("drop table if exists "+ DB_NAME);
			database.execSQL(SQL_CREATE_TABLE_NET_MONITOR);
			database.close();
		}catch(Exception e){
			Log.e(TAG, "Exception execSQL: "+e.getMessage());
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
	
	private int getBoolean(int bool){
		return (bool==DatabaseHelperApp.BooleanSQLite.isTrue)?BooleanSQLite.isTrue:BooleanSQLite.isFalse;
	}
	
	
	// Return a 'Cursor' to the collected data in db
	public Cursor getDataCursor() {
		if(database != null && database.isOpen()) {
			//Query for collected data
			Cursor cursorDatabase = database.query(DB_NAME, new String[] {
								Schema.COL_ID, Schema.COL_TIMESTAMP, Schema.COL_APP,
								Schema.COL_NUM_PROC,
								Schema.COL_PACKET_COUNTER_TX, Schema.COL_PACKET_COUNTER_RX,
								Schema.COL_PACKET_SIZE_TX, Schema.COL_PACKET_SIZE_RX,
								Schema.COL_PACKET_TYPE_TX, Schema.COL_PACKET_TYPE_RX,
								Schema.COL_APP_FOREGROUND }, 
								null, null, null, null,
								Schema.COL_TIMESTAMP + " asc");
			
			cursorDatabase.moveToFirst();
			return cursorDatabase;
		}
		
		else {
			return null;  
		}
		
	}
	
	public static Cursor getDataCursor(SQLiteDatabase db){
		Log.v(TAG, "Cursor getCursor database: " + db.getPath());
		if(db !=null && db.isOpen()){
			// Query for collected data
			Cursor cursorDatabase = db.query(DB_NAME, new String[] {
					Schema.COL_ID, Schema.COL_TIMESTAMP, Schema.COL_APP,
					Schema.COL_NUM_PROC,
					Schema.COL_PACKET_COUNTER_TX, Schema.COL_PACKET_COUNTER_RX,
					Schema.COL_PACKET_SIZE_TX, Schema.COL_PACKET_SIZE_RX,
					Schema.COL_PACKET_TYPE_TX, Schema.COL_PACKET_TYPE_RX,
					Schema.COL_APP_FOREGROUND },
					null, null, null, null,Schema.COL_TIMESTAMP + " asc");
			cursorDatabase.moveToFirst();
			return cursorDatabase;
		}
		else {
			Log.v(TAG, "Couldn't open database app: "+ db.getPath());
			return null;
		}
	}
	
	// Save application_data into DB
	public void track(DataCollectedApp collectedData){
		Log.v(TAG, "Saving application data: "+ collectedData.getClass().getSimpleName());
		String dirDatabase = AppTools.getDatabaseName(mContext, 
				DataCommons.Preferences.DB_APP_NAME_PREVIOUS);
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
		values.put(Schema.COL_TIMESTAMP, collectedData.getTime());
		values.put(Schema.COL_APP, collectedData.getAppName());
		values.put(Schema.COL_NUM_PROC, collectedData.getNumProcess());
		values.put(Schema.COL_PACKET_COUNTER_TX, collectedData.getPacketCounterTx());
		values.put(Schema.COL_PACKET_COUNTER_RX, collectedData.getPacketCounterRx());
		values.put(Schema.COL_PACKET_SIZE_TX, collectedData.getPacketSizeTx());
		values.put(Schema.COL_PACKET_SIZE_RX, collectedData.getPacketSizeRx());
		values.put(Schema.COL_PACKET_TYPE_TX, collectedData.getPacketTypeTx());
		values.put(Schema.COL_PACKET_TYPE_RX, collectedData.getPacketTypeRx());
		values.put(Schema.COL_APP_FOREGROUND, getBoolean(collectedData.getIsForeground()));
		
		database.insert(DB_NAME, null, values);
		database.close();
		
	}
	
	
	
	/**
	 *  Exports current database to a File.
	 */
	public static void exportDataAsFile() {
		Log.v(TAG, "Export database as XML file, method init");
		
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?mContext.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;		
		// FileDir where databases have been saved
		File databaseDir = new File(dir +File.separator + DatabaseUtils.Database.DATABASE_DIRECTORY_APP);
		// FileDir where we export databases as Xml
		File targetXml = new File(dir +File.separator + DatabaseUtils.Database.XML_DIRECTORY,
						DB_NAME
						+"_"
						+ fileNameFormatter.format(new Date()) 
						+ DatabaseUtils.Database.EXTENSION_FILE);

		try {
//			XMLFileWriterTest.writeXmlFile(DB_NAME, cursorData, testDataFile);
			XMLFileWriterApp.writeXmlFile(databaseDir.list(), targetXml, databaseDir.getPath());
			} catch(IOException ioe){
			System.out.println("Error in exportDataAsFile: "+ioe.getMessage());
			Log.e(TAG, "Error when exporting sqldatabase as file: "+ioe.getMessage());
		}
		Log.v(TAG, "Exporting correct");
	}
	
	public void closeDatabase(){
		Log.v(TAG, "Closing datatabse");
		try{
			AppTools.setDatabaseName(mContext, DataCommons.Preferences.DB_APP_NAME_PREVIOUS,
					DatabaseUtils.Database.RESET_DATABASE);
			database.close();
		}catch(Exception e){
			Log.e(TAG, "Error when closing database: "+e.getMessage());
		}
	}
	
	public void deleteDatabase(){
		Log.v(TAG, "Deleting Database");
		mContext.deleteDatabase(DB_NAME);
	}
	
	
	public static final class Schema {
		public static final String TBL_DATA = "network_data";
		public static final String COL_ID = "_id";
		public static final String COL_TIMESTAMP = "timestamp";
		public static final String COL_APP = "application";
		public static final String COL_NUM_PROC = "num_process";
		public static final String COL_PACKET_SIZE_TX = "packet_size_tx";
		public static final String COL_PACKET_SIZE_RX = "packet_size_rx";
		public static final String COL_PACKET_COUNTER_TX = "packet_counter_tx";
		public static final String COL_PACKET_COUNTER_RX = "packet_counter_rx";
		public static final String COL_PACKET_TYPE_TX = "packet_type_tx";
		public static final String COL_PACKET_TYPE_RX = "packet_type_rx";
		public static final String COL_APP_FOREGROUND = "application_foreground";
	}
	
	public static class BooleanSQLite {
		public static final int isTrue = 1;
		public static final int isFalse = 0;
	}
	
	
}
