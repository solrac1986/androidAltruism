package com.fileWriters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.databases.DatabaseHelperTest;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Class to export database as XML 
 * @author troglodito22
 *
 */
public class XMLFileWriterTest {
	
	private static final String TAG = XMLFileWriterTest.class.getSimpleName();
	
	/**
     * XML header.
     */
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    
  	
    /**
     * Date format for a point timestamp.
     */
    private static final SimpleDateFormat fileNameFormatter = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
    
	
    /**
     * @function create a Xml file with all databases have been retrieved before
     * @param files
     * @param targetXml
     * @param pathDatabase
     * @throws IOException
     */
    public static void writeXmlFile(String[] files, File targetXml, String pathDatabase) throws IOException {
    	Log.v(TAG, "Write XML File");
    	FileWriter fw;
    	Cursor cursorData = null;
    	SQLiteDatabase db;
    	if (files == null){
    		Log.v(TAG, "Not files test to read");
    		return;
    	}
    	if(targetXml == null){
    		Log.v(TAG, "Target file null, create in file app directory");
    		fw = new FileWriter("XmlTest_"+fileNameFormatter.format(System.currentTimeMillis()));
    	}
    	else{
    		Log.v(TAG, "Target file not null: "+targetXml.getName());
    		fw = new FileWriter(targetXml);
    	}
		
    	fw.write(XML_HEADER + "\n");
    	
    	fw.write(Tag.FILE_TAG + "\n");
       
    	for(int i=0; i <files.length; i++){
    		Log.v(TAG, "Add to xml database: "+files[i]);
    		// Check if the file name database is not journal [security copy]
    		if(isJournalDatabase(files[i])){
    			Log.v(TAG, "Journal copy");
    			continue;
    		}
    		try{
    			db = SQLiteDatabase.openDatabase(pathDatabase + File.separator + files[i], 
    					null, 
    					SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    		}catch(Exception e){
    			Log.e(TAG, "Couldn't open database: " + files[i] + " " + e.getMessage());
    			continue;
    		}		
    		try{
    			cursorData = DatabaseHelperTest.getDataCursor(db);
    		}catch(Exception e){
    			Log.e(TAG, "Exception when getting cursor database: "+e.getMessage());
    		}
    		writeXmlItems(files[i], fw, cursorData);
    		try{
    			db.close();
    			cursorData.close();
    		}catch(Exception e){
    			Log.e(TAG, "Error when closing database");
    		}
    		
    	}
    	fw.write(Tag.FILE_TAG_END + "\n");
        fw.close();
       
    }
    
    
    /**
     * Writes the GPX file
     * @param trackName Name of the GPX track (metadata)
     * @param cTrackPoints Cursor to track points.
     * @param cWayPoints Cursor to way points.
     * @param target Target GPX file
     * @throws IOException 
     */
    public static void writeXmlFile(String databaseName, Cursor cursorData, File target) throws IOException {
    	Log.v(TAG, "Write XML File");
    	FileWriter fw;
    	if(target == null){
    		Log.v(TAG, "Target file null, create in file app directory");
    		fw = new FileWriter(databaseName);
    	}
    	else{
    		Log.v(TAG, "Target file not null: "+target.getName());
    		fw = new FileWriter(target);
    	}
		
    	fw.write(XML_HEADER + "\n");
       
        writeXmlItems(databaseName, fw, cursorData);
        
        fw.write("</xml>");
        
        fw.close();
        
    }
    
    
    /**
     * Iterates on track points and write them.
     * @param Name of the track (metadata).
     * @param fw Writer to the target file.
     * @param c Cursor to database packets collected.
     * @throws IOException
     */
    public static void writeXmlItems(String Name, FileWriter fw, Cursor c) throws IOException {
    	Log.v(TAG, "Writing items in xml");
    	
        fw.write("\t" + Tag.TEST_TAG + "\n");
        fw.write("\t\t" + "<name>" + Name + "</name>" + "\n");
        
        
        while (!c.isAfterLast() ) {
        	StringBuffer out = new StringBuffer();
        	
        	out.append("\t\t" + Tag.DATA_COLLECTED + "\n");
        	
            out.append("\t\t\t" + Tag.SEQ_NUMBER);
            out.append(c.getColumnIndex(DatabaseHelperTest.Schema.COL_ID));
            out.append(Tag.SEQ_NUMBER_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_TIME_STAMP_NOTIFICATION);
            out.append(fileNameFormatter.format(new Date(c.getLong(c.getColumnIndex(DatabaseHelperTest.Schema.COL_TIMESTAMP_NOTIFICATION)))));
            out.append(Tag.COL_TIME_STAMP_NOTIFICATION_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_TIME_STAMP_POPUP);
            out.append(fileNameFormatter.format(new Date(c.getLong(c.getColumnIndex(DatabaseHelperTest.Schema.COL_TIMESTAMP_POPUP)))));
            out.append(Tag.COL_TIME_STAMP_POPUP_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_TIME_STAMP_USER);
            out.append(fileNameFormatter.format(new Date(c.getLong(c.getColumnIndex(DatabaseHelperTest.Schema.COL_TIMESTAMP_USER)))));
            out.append(Tag.COL_TIME_STAMP_USER_END + "\n");
            
            out.append("\t\t\t" + Tag.CONTENT_TYPE);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperTest.Schema.CONTENT_TYPE)));
            out.append(Tag.CONTENT_TYPE_END + "\n");
            
            out.append("\t\t\t" + Tag.CONTENT_SIZE);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperTest.Schema.CONTENT_SIZE	)));
            out.append(Tag.CONTENT_SIZE_END + "\n");
            
            out.append("\t\t\t" + Tag.TYPE_SENDER);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperTest.Schema.TYPE_SENDER)));
            out.append(Tag.TYPE_SENDER_END + "\n");
            
            out.append("\t\t\t" + Tag.TYPE_RECEIVER);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperTest.Schema.TYPE_RECEIVER)));
            out.append(Tag.TYPE_RECEIVER_END + "\n");
            
            out.append("\t\t\t" + Tag.BATTERY_STATUS);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.BATTERY_STATUS)));
            out.append(Tag.BATTERY_STATUS_END + "\n");
            
            out.append("\t\t\t" + Tag.BATTERY_LEVEL);
            out.append(c.getString(c.getColumnIndex(DatabaseHelperTest.Schema.BATTERY_LEVEL)));
            out.append(Tag.BATTERY_LEVEL_END + "\n");
            
            out.append("\t\t\t" + Tag.USER_MOVING);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.USER_MOVING)));
            out.append(Tag.USER_MOVING_END + "\n");
            
            out.append("\t\t\t" + Tag.USER_LOCATION);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.USER_LOCATION)));
            out.append(Tag.USER_LOCATION_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_LONGITUDE);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.COL_LONGITUDE)));
            out.append(Tag.COL_LONGITUDE_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_LATITUDE);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.COL_LATITUDE)));
            out.append(Tag.COL_LATITUDE_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_CELLID);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.COL_CELLID)));
            out.append(Tag.COL_CELLID_END + "\n");
            
            out.append("\t\t\t" + Tag.COL_LAC);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.COL_LAC)));
            out.append(Tag.COL_LAC_END + "\n");

            out.append("\t\t\t" + Tag.COL_TIME_STAMP_GPS);
            out.append(fileNameFormatter.format(new Date(c.getLong(c.getColumnIndex(DatabaseHelperTest.Schema.COL_TIMESTAMP_GPS)))));
            out.append(Tag.COL_TIME_STAMP_GPS_END + "\n");
            
            out.append("\t\t\t" + Tag.OPTION_SELECTED);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.OPTION_SELECTED)));
            out.append(Tag.OPTION_SELECTED_END + "\n");

            out.append("\t\t\t" + Tag.OPTION_CAUSE);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.OPTION_CAUSE)));
            out.append(Tag.OPTION_CAUSE_END + "\n");
            
            out.append("\t\t\t" + Tag.MOOD);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperTest.Schema.MOOD)));
            out.append(Tag.MOOD_END + "\n");
            
   
            out.append("\t\t" + Tag.DATA_COLLECTED_END + "\n");
            
            fw.write(out.toString());
            c.moveToNext();
        }
        
        fw.write("\t" + Tag.TEST_TAG_END + "\n");
    }
    
     
    /**
     * Check if is a security copy of database [journal]
     * @param name
     * @return
     */
    public static boolean isJournalDatabase(String name){
    	String filter = "journal";
    	return 	(name.indexOf(filter)==-1)?false:true;
    }
    
    /**
     * Class with all tags xml file 
     * @author troglodito22
     *
     */
    public static class Tag{
    	
    	public static final String FILE_TAG = "<file_database>";
    	public static final String FILE_TAG_END = "</file_database>";
    	
    	public static final String TEST_TAG = "<test_altruism>";
    	public static final String TEST_TAG_END = "</test_altruism>";
    	
    	public static final String DATA_COLLECTED = "<data_collected>";
    	public static final String DATA_COLLECTED_END = "</data_collected>";
    	
    	public static final String SEQ_NUMBER="<seq_number>";
    	public static final String SEQ_NUMBER_END="</seq_number>";

    	public static final String COL_TIME_STAMP_NOTIFICATION="<time_stamp_notification>";
    	public static final String COL_TIME_STAMP_NOTIFICATION_END="</time_stamp_notification>";

    	public static final String COL_TIME_STAMP_POPUP="<time_stamp_popup>";
    	public static final String COL_TIME_STAMP_POPUP_END="</time_stamp_popup>";

    	public static final String COL_TIME_STAMP_USER="<time_stamp_user>";
    	public static final String COL_TIME_STAMP_USER_END="</time_stamp_user>";

    	public static final String CONTENT_TYPE="<content_type>";
    	public static final String CONTENT_TYPE_END="</content_type>";
    	
    	public static final String CONTENT_SIZE ="<content_size>";
    	public static final String CONTENT_SIZE_END="</content_size>";

    	public static final String TYPE_SENDER="<type_sender>";
    	public static final String TYPE_SENDER_END="</type_sender>";
    	
    	public static final String TYPE_RECEIVER="<type_receiver>";
    	public static final String TYPE_RECEIVER_END="</type_receiver>";
    	
    	public static final String BATTERY_STATUS="<battery_status>";
    	public static final String BATTERY_STATUS_END="</battery_status>";

    	public static final String BATTERY_LEVEL="<battery_level>";
    	public static final String BATTERY_LEVEL_END="</battery_level>";

    	public static final String USER_MOVING="<user_moving>";
    	public static final String USER_MOVING_END="</user_moving>";
    	
    	public static final String USER_LOCATION="<user_location>";
    	public static final String USER_LOCATION_END="</user_location>";
    	
    	public static final String COL_LONGITUDE = "<longitude>";
    	public static final String COL_LONGITUDE_END = "</longitude>";
    	
    	public static final String COL_LATITUDE = "<latitude>";
    	public static final String COL_LATITUDE_END = "</latitude>";
    	
    	public static final String COL_CELLID = "<cellID>";
    	public static final String COL_CELLID_END = "</cellID>";
    	
    	public static final String COL_LAC = "<lac>";
    	public static final String COL_LAC_END = "</lac>";
    	
    	public static final String COL_TIME_STAMP_GPS="<time_stamp_gps>";
    	public static final String COL_TIME_STAMP_GPS_END="</time_stamp_gps>";
    	
    	public static final String OPTION_SELECTED="<option_selected>";
    	public static final String OPTION_SELECTED_END="</option_selected>";
    	
    	public static final String OPTION_CAUSE = "<option_cause>";
    	public static final String OPTION_CAUSE_END = "</option_cause>";
    	
    	public static final String MOOD = "<mood>";
    	public static final String MOOD_END = "</mood>";
    }
    
    
}
