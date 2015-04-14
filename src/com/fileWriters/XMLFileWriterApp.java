package com.fileWriters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.data.utils.DataCommons;
import com.databases.DatabaseHelperApp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Class to export database as XML 
 * @author troglodito22
 *
 */

public class XMLFileWriterApp {
	
	private static final String TAG = XMLFileWriterApp.class.getSimpleName();
	
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
    		Log.v(TAG, "Not files app to read");
    		return;
    	}
    	if(targetXml == null){
    		Log.v(TAG, "Target file null, create in file app directory");
    		fw = new FileWriter("XmlTestApp_"+fileNameFormatter.format(System.currentTimeMillis()));
    	}
    	else{
    		Log.v(TAG, "Target file not null: "+targetXml.getName());
    		fw = new FileWriter(targetXml);
    	}
		
    	fw.write(XML_HEADER + "\n");
    	fw.write(Tag.FILE_TAG + "\n");
    	
    	fw.write(Tag.VERSION_MODEL + DataCommons.UserData.versionModel + Tag.VERSION_MODEL_END);
        fw.write(Tag.VERSION_OS + DataCommons.UserData.versionOS + Tag.VERSION_OS_END + "\n");
        
       
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
    			cursorData = DatabaseHelperApp.getDataCursor(db);
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
        fw.write("\t\t" + "<name>");
        fw.write("\t\t" + Name  + "</name>" + "\n");
     
        
        while (!c.isAfterLast() ) {
        	StringBuffer out = new StringBuffer();
        	
        	fw.write("\t\t" + Tag.DATA_COLLECTED + "\n");
            
        	out.append("\t\t\t" + Tag.SEQ_NUMBER );
            out.append(c.getColumnIndex(DatabaseHelperApp.Schema.COL_ID));
            out.append(Tag.SEQ_NUMBER_END + "\n");
            
            out.append("\t\t\t" + Tag.TIME_STAMP);
            out.append(fileNameFormatter.format(new Date(c.getLong(c.getColumnIndex(DatabaseHelperApp.Schema.COL_TIMESTAMP)))));
            out.append(Tag.TIME_STAMP_END + "\n");
            
            out.append("\t\t\t" + Tag.APP_NAME);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperApp.Schema.COL_APP)));
            out.append(Tag.APP_NAME_END + "\n");
            
            out.append("\t\t\t" + Tag.NUM_PROC);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperApp.Schema.COL_NUM_PROC)));
            out.append(Tag.NUM_PROC_END + "\n");
            
            out.append("\t\t\t" + Tag.PACKET_COUNTER_TX);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperApp.Schema.COL_PACKET_COUNTER_TX)));
            out.append(Tag.PACKET_COUNTER_TX_END + "\n");
            
            out.append("\t\t\t" + Tag.PACKET_COUNTER_RX);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperApp.Schema.COL_PACKET_COUNTER_RX	)));
            out.append(Tag.PACKET_COUNTER_RX_END + "\n");
            
            out.append("\t\t\t" + Tag.PACKET_SIZE_TX);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperApp.Schema.COL_PACKET_SIZE_TX)));
            out.append(Tag.PACKET_SIZE_TX_END + "\n");
            
            out.append("\t\t\t" + Tag.PACKET_SIZE_RX);
            out.append(c.getDouble(c.getColumnIndex(DatabaseHelperApp.Schema.COL_PACKET_SIZE_RX)));
            out.append(Tag.PACKET_SIZE_RX_END + "\n");
            
            out.append("\t\t\t" + Tag.PACKET_TYPE_TX);
            out.append(c.getString(c.getColumnIndex(DatabaseHelperApp.Schema.COL_PACKET_TYPE_TX)));
            out.append(Tag.PACKET_TYPE_TX_END + "\n");
            
            out.append("\t\t\t" + Tag.PACKET_TYPE_RX);
            out.append(c.getString(c.getColumnIndex(DatabaseHelperApp.Schema.COL_PACKET_TYPE_RX)));
            out.append(Tag.PACKET_TYPE_RX_END + "\n");
            
            out.append("\t\t\t" + Tag.APP_FOREGROUND);
            out.append(c.getInt(c.getColumnIndex(DatabaseHelperApp.Schema.COL_APP_FOREGROUND)));
            out.append(Tag.APP_FOREGROUND_END + "\n");
            
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
    private static boolean isJournalDatabase(String name){
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
    	
    	public static final String TEST_TAG = "<test_app>";
    	public static final String TEST_TAG_END = "</test_app>";
    	
    	public static final String VERSION_MODEL = "<version_model>";
    	public static final String VERSION_MODEL_END = "</version_model>";

    	public static final String VERSION_OS = "<version_os>";
    	public static final String VERSION_OS_END = "</version_os>";
    	
    	public static final String DATA_COLLECTED = "<data_collected>";
    	public static final String DATA_COLLECTED_END = "</data_collected>";
    	
    	public static final String SEQ_NUMBER="<seq_number>";
    	public static final String SEQ_NUMBER_END="</seq_number>";

    	public static final String TIME_STAMP="<time_stamp>";
    	public static final String TIME_STAMP_END="</time_stamp>";

    	public static final String APP_NAME="<app_name>";
    	public static final String APP_NAME_END="</app_name>";
    	
    	public static final String NUM_PROC="<num_process>";
    	public static final String NUM_PROC_END="</num_process>";

    	public static final String PACKET_COUNTER_TX="<packet_counter_tx>";
    	public static final String PACKET_COUNTER_TX_END="</packet_counter_tx>";
    	
    	public static final String PACKET_COUNTER_RX="<packet_counter_rx>";
    	public static final String PACKET_COUNTER_RX_END="</packet_counter_rx>";

    	public static final String PACKET_SIZE_TX="<packet_size_tx>";
    	public static final String PACKET_SIZE_TX_END="</packet_size_tx>";
    	
    	public static final String PACKET_SIZE_RX="<packet_size_rx>";
    	public static final String PACKET_SIZE_RX_END="</packet_size_rx>";

    	public static final String PACKET_TYPE_TX="<packet_type_tx>";
    	public static final String PACKET_TYPE_TX_END="</packet_type_tx>";

    	public static final String PACKET_TYPE_RX="<packet_type_rx>";
    	public static final String PACKET_TYPE_RX_END="</packet_type_rx>";
    	
    	public static final String APP_FOREGROUND="<app_foreground>";
    	public static final String APP_FOREGROUND_END="</app_foreground>";
    	
    }
    
    
}
