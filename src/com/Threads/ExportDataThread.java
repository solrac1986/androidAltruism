package com.Threads;

import com.application.utilities.AppTools;
import com.data.utils.DataCommons;
import com.databases.DatabaseHelperTest;
import com.databases.DatabaseHelperApp;
//import com.fileWriters.XMLFileWriterContacts;
import com.gui.activities.Email_Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Class to Export data, creates a thread to work on background mode.
 * @author troglodito22
 *
 */

public class ExportDataThread extends AsyncTask<String, Void, String>{
	
	private static final String TAG = ExportDataThread.class.getSimpleName();

	private Context mContext;
	
	
	public ExportDataThread(Context context){
		super();
		Log.v(TAG, "Constructor");
		mContext = context;
	}

	@Override
	protected String doInBackground(String... params) {
		Log.v(TAG, "Do in background");
		String message;
		try{
			Email_Activity.IS_EXPORTING = true;
			DatabaseHelperTest.exportDataAsFile(mContext);
			// PRIVACY 
//			XMLFileWriterContacts.writeXmlFile(mContext);
			DatabaseHelperApp.exportDataAsFile();
		}catch(Exception e){
			Log.e(TAG, "Exception when exporting data: " + e.getMessage());
			message = DataCommons.ThreadParameters.MESSAGE_ERROR;
			Email_Activity.IS_EXPORTING = false;
		}
		try{
			Log.v(TAG, "Zipping files");
			AppTools.zipFiles(mContext.getApplicationContext());
			message = DataCommons.ThreadParameters.MESSAGE_OK;
			Email_Activity.IS_EXPORTING = false;
		}catch(Exception e){
			Log.e(TAG, "Error when zipping files: "+e.getMessage());
			message = DataCommons.ThreadParameters.MESSAGE_ERROR;
			Email_Activity.IS_EXPORTING = false;
		}
		return message;
	}
	
	@Override
	protected void onPostExecute(String result){
		Log.v(TAG, "On post executed, msg: " + result);
		Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	}
}
