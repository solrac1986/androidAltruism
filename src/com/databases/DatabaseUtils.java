package com.databases;

import java.io.File;

import android.os.Environment;


/**
 * Class helper for SQLite databases.
 * @author troglodito22
 *
 */

public class DatabaseUtils {
	

	public static class Database {
		public static final String DATABASE_DIRECTORY = "dataSharedNet" + File.separator + "database" 
														+ File.separator + "test";
		public static final String DATABASE_DIRECTORY_APP = "dataSharedNet" + File.separator +
														"database" + File.separator + "app";
		public static final String XML_DIRECTORY = "dataSharedNet" + File.separator +"xml";
		public static final String EXTENSION_FILE = ".xml";
		public static final String DIRECTORY = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)?
				   (Environment.getExternalStorageDirectory().getPath()):"";
	    public static final String RESET_DATABASE = "_reset";

	}
	
	public static class databaseType {
		public static final int TEST = 1;
		public static final int APP = 2;
	}
	
	
}
