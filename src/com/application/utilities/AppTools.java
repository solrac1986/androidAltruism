package com.application.utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.data.utils.DataCommons;
import com.databases.DatabaseUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Class which offers differents tools for example: send email intent, split one value in two words
 * @author troglodito22
 *
 */

public class AppTools {

	private final static String TAG = AppTools.class.getSimpleName();

	private final static int NUMBER_SPLITS = 2;
	
	private final static int FIRST_ELEMENT = 0;
	private final static int SECOND_ELEMENT = 1;
	
	public static String[] splitString(String userData, String separator){
		String[] result = new String[2];
		try{
			result  = (userData).split(DataCommons.UserData.DATA_SEPARATOR, NUMBER_SPLITS);
		}catch(Exception npe){
			Log.e(TAG,"Exception, split: "+npe.getMessage());
			result[FIRST_ELEMENT]="";
			result[SECOND_ELEMENT]="";
		}
		if(result.length != NUMBER_SPLITS){
			Log.v(TAG,"Error index bound");
			result = new String[NUMBER_SPLITS];
			result[FIRST_ELEMENT]= "";
			result[SECOND_ELEMENT] = "";
		}
		return result;
	}

	public static String getDatabaseName(Context context, String key){
		Log.v(TAG, "Get database name");
		SharedPreferences sp = context.getSharedPreferences(DataCommons.Preferences.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, DatabaseUtils.Database.RESET_DATABASE);
	}
	
	/**
	 * 
	 * @param context
	 * @param key
	 * @param name
	 */
	public static void setDatabaseName(Context context, String key, String name){
		Log.v(TAG, "Set database name: " + name);
		SharedPreferences sp = context.getSharedPreferences(DataCommons.Preferences.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, name);
		editor.commit();
	}
	
	
	public static boolean sendDataEmail(Context context, String emailTo, String emailCC, String subject, 
					String emailText, List<String> filePaths){
		Log.v(TAG, "Sending email");
		final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
		emailIntent.putExtra(Intent.EXTRA_CC, new String[]{emailCC});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		ArrayList<Uri> uris = new ArrayList<Uri>();
		try{
			for(String file :  filePaths){
				File fileIn = new File(file);
				Uri u = Uri.fromFile(fileIn);
				uris.add(u);
			}
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		}catch(Exception e){
			Log.e(TAG, "Exception when attaching files to email: "+e.getMessage());
		}
		try{
			context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}catch(Exception e){
			Log.e(TAG, "NO email client installed: "+e.getMessage());
			Toast.makeText(context, "No email clients installed", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	/**
	 * Method top create zip file
	 * @param context
	 */
	public static boolean zipFiles(Context context){
		Log.v(TAG, "Compressing files");
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?context.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		
		File fileDir = new File(dir +File.separator + DatabaseUtils.Database.XML_DIRECTORY);
		SimpleDateFormat fileNameFormatter = new SimpleDateFormat ("yyyy_MM_d_HH-mm-ss");
//		String [] resultString = splitString(Data.Interface.userAgeSex, Data.Interface.DATA_SEPARATOR);
		
		Compress compress = new Compress(fileDir.list(), DataCommons.CompressData.ZIP_NAME
				+ fileNameFormatter.format(new Date()),
				context.getApplicationContext());
		boolean zipCorrect = compress.zip();
		Log.v(TAG, zipCorrect?"Zip correct":"Zip error");
		return zipCorrect;
	}
	
	public static boolean deleteFiles(Context context, String directory){
		Log.v(TAG, "Deleting files");
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?context.getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		File fileDir = new File(dir + File.separator + directory);
		File[] arrayFiles = null;
		try{
			arrayFiles = fileDir.listFiles();
		}catch(Exception e){
			Log.e(TAG, "Error getting files: "+e.getMessage());
			return false;
		}
		if ((arrayFiles == null)){
			Log.v(TAG,"Directory empty");
			return false;
		}
		Log.v(TAG,"Num files found: "+ arrayFiles.length);
		for (int i = 0; i < arrayFiles.length; i++){
			if(arrayFiles[i].delete()){
				Log.v(TAG, "Deleting file: "+arrayFiles[i].getName());
			}
			else{
				Log.v(TAG, "Not file found");
			}
		}
		return true;
	}
	
}
