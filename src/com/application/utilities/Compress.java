package com.application.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.databases.DatabaseUtils;

import android.content.Context;
import android.util.Log;

/**
 * Class to compress all data saved
 * @author troglodito22
 *
 */

public class Compress {

	private final static String TAG = Compress.class.getSimpleName();
	
	private final static int BUFFER = 2028;
	private final static String FILE_EXTENSION = ".zipp";
	
	private String[] mFiles;
	private String mZipFile;
	private Context mContext;
	
	public Compress (String[] files, String zipFile, Context context){
		mFiles = files;
		mZipFile = zipFile;
		mContext = context;
	}
	
	public boolean zip(){
		Log.v(TAG, "Zip method");
		try{
			BufferedInputStream origin =  null;
			String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?mContext.getFilesDir().getPath():
				DatabaseUtils.Database.DIRECTORY;
			File fileCompressed = new File(dir 
						+ File.separator
						+ DatabaseUtils.Database.XML_DIRECTORY
						+ File.separator
						+ mZipFile);
			FileOutputStream dest = new FileOutputStream(fileCompressed + FILE_EXTENSION);	
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			
			byte[] data = new byte [BUFFER];
			
			for(int i=0; i < mFiles.length; i++) {
				Log.v(TAG, "Adding: "+ mFiles[i]);
				try{
					FileInputStream fi = new FileInputStream(dir
							+ File.separator
							+ DatabaseUtils.Database.XML_DIRECTORY
							+ File.separator
							+ mFiles[i]);
					origin = new BufferedInputStream(fi, BUFFER);
					ZipEntry entry = new ZipEntry(mFiles[i].substring(mFiles[i].lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while((count = origin.read(data, 0 , BUFFER)) != -1){
							out.write(data, 0, count);
					}
					origin.close();
					fi.close();
					File file = new File(dir
							+ File.separator
							+ DatabaseUtils.Database.XML_DIRECTORY
							+ File.separator
							+ mFiles[i]);
					if(file.delete()){
						Log.v(TAG, "Delete file: "+mFiles[i]);
					}
				}catch(Exception e){
					Log.e(TAG, "Error compressing file: "+ mFiles[i] + " " + e.getMessage());
					return false;
				}
			}
			out.close();

		}catch(Exception e){
			Log.e(TAG, "Error zipping Files: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	public static class typeApp{
		public static final int ALTRUISM = 1;
		public static final int NETWORK = 0;
	}
	
	
	
}
