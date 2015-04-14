package com.fileWriters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.databases.DatabaseUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Class to Retrieve contacts names. 
 * @return DEPRECIATED
 * @author troglodito22
 *
 */

public class XMLFileWriterContacts {

	
	private static final String TAG = XMLFileWriterContacts.class.getSimpleName();
	
	private static Context mContext;
	private static final String CONTACTS = "_contacts";
	
	/**
     * XML header.
     */
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    

    /**
     * Date format for a point timestamp.
     */
     private static final SimpleDateFormat fileNameFormatterFile = new SimpleDateFormat ("yyyy-MM-dd_HH-mm-ss");
    
	
    /**
     * Writes the GPX file
     * @param trackName Name of the GPX track (metadata)
     * @param cTrackPoints Cursor to track points.
     * @param cWayPoints Cursor to way points.
     * @param target Target GPX file
     * @throws IOException 
     */
    public static void writeXmlFile(Context context) throws IOException {
    	Log.v(TAG, "Write XML File");
    	mContext = context;
    	
    	String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?context.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		File testDataFile = new File(dir +File.separator + DatabaseUtils.Database.XML_DIRECTORY,
				fileNameFormatterFile.format(new Date()) + CONTACTS +DatabaseUtils.Database.EXTENSION_FILE);    	
    	FileWriter fw;
    	
    	Log.v(TAG, "Target file not null: "+testDataFile.getName());
		fw = new FileWriter(testDataFile);
		
    	fw.write(XML_HEADER + "\n");
		fw.write("\t" + Tag.Test_tag.INIT + "\n");
	     
        fw.write("\t\t" + Tag.Data_tag.INIT + "\n");
       
        writeContactInfo(fw);
        
        fw.write("\t\t" + Tag.Data_tag.END + "\n");
        fw.write("\t" + Tag.Test_tag.END + "\n"); 
        fw.close();
        Log.v(TAG, "File Xml has been closed");
    }
    
    
   /**
    * 
    * @param data
    * @param init
    * @param end
    * @param out
    * @throws IOException
    */
    public static void writeXmlItem(String data, String init, String end, StringBuffer out) throws IOException {
//    	Log.v(TAG, "Writing item in xml");
    	
    	out.append("\t\t\t\t" + init);
        out.append(data);
        out.append(end + "\n");    
    }
    
    /**
	 * Get contact info (Name, email)
	 * @param fw
	 */
    private static void writeContactInfo(FileWriter fw){
    	Log.v(TAG, "Get contacts information");
    	
    	StringBuffer out = new StringBuffer();
    	
    	ContentResolver cr = mContext.getContentResolver();
    	Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
    			null, null, null, null);
    	
    	if(cursor.getCount() > 0){
    		while(cursor.moveToNext()){
    	        out.append("\t\t\t" + Tag.Seq_tag.INIT + "\n");
    			String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
    			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//    			Log.v(TAG, "Get email address from contact: "+name);
    			try {
					writeXmlItem(name, Tag.Name_tag.INIT, Tag.Name_tag.END, out);
				} catch (IOException e) {
					Log.e(TAG, "Error writing name: "+e.getMessage());
				}
    			Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
    					null, 
    					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
    					new String[]{id}, 
    					null);
    			while(emailCursor.moveToNext()){
    				String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//    				String emailType = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
    				try {
						writeXmlItem(email, Tag.Email_item_tag.INIT, Tag.Email_item_tag.END, out);
					} catch (IOException e) {
						Log.e(TAG, "Error writing email: "+e.getMessage());
					}
    			}
    			emailCursor.close();
    			out.append("\t\t\t" + Tag.Seq_tag.END + "\n");
    		}
    		try {
				fw.write(out.toString());
			} catch (IOException e) {
				Log.e(TAG, "Error writing xml file: "+e.getMessage());
			}
    	}
    }
    
    
    
    /**
     * Class with all tags xml file 
     * @author troglodito22
     *
     */
    public static class Tag{
    	public static class Test_tag{
    		public static final String INIT = "<contacts_list>";
        	public static final String END = "</contacts_list>" + "\n";
    	}
    	public static class Data_tag{
    		public static final String INIT = "<data_collected>";
        	public static final String END = "</data_collected>" + "\n";
    	}
    	public static class Seq_tag{
    		public static final String INIT="<seq_number>";
        	public static final String END="</seq_number>" + "\n";
    	}
    	public static class Name_tag{
    		public static final String INIT = "<name>";
        	public static final String END = "</name>" + "\n";
    	}
    	public static class Email_item_tag{
    		public static final String INIT = "<email_item>";
        	public static final String END = "</email_item>" + "\n";
    	}
  
    }
    
}
