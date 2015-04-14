package com.gui.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.Threads.ExportDataThread;
import com.application.utilities.AppTools;
import com.data.utils.DataCommons;
import com.databases.DatabaseUtils;

import com.main.shared_net_facebook.activities.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Class Activity to send email
 * @author troglodito22
 *
 */

public class Email_Activity extends Activity implements OnClickListener{

	private final String TAG = Email_Activity.class.getSimpleName();
	
	/**
	 * Communication with Thread
	 */
	public static boolean IS_EXPORTING = false;
	
	private final String EMAIL_TO = "sharednetapplication@googlemail.com";
	private final String EMAIL_CC = "";
	private final String EMAIL_SUBJECT = "Application Shared Network, retrieving info";
	
	private boolean AFTER_MAIL;
	
	private String mEmailText;
	private List<String> mFilePaths;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_email);
		Log.v(TAG, "On create method");
		try{
			AFTER_MAIL = false;
			Log.v(TAG, "Exporting database,contacts in XML file");
			ExportDataThread exportDataThread = new ExportDataThread(this.getApplicationContext());
			exportDataThread.execute("Activity");
		}catch(Exception e){
			Log.e(TAG, "Error when exporting asynctaskt: " + e.getMessage());
		}
		Spinner spinnner = (Spinner)findViewById(R.id.spinnerMaleFemale);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this, 
        		R.array.sex_array, R.layout.simple_spinner_item);
        spinnner.setAdapter(spinner_adapter);
        
		Button button = (Button)findViewById(R.id.buttonSend);
		if (DataCommons.Settings.IS_AMAZON_TURK){
			Log.v(TAG,"onCreate is Amazon Turk application");
			button.setText("Zip");
		}
		button.setOnClickListener(this);
		button = (Button)findViewById(R.id.buttonCancel);
		button.setOnClickListener(this);
	}

	@Override
	public void onResume(){
		Log.v(TAG, "On Resume");
		super.onResume();
		if(AFTER_MAIL){
			Log.v(TAG, "Finishing application");	
			finish();
		}
		getSharedPreferencesSaved();
	}
	
	@Override
	public void onDestroy(){
		Log.v(TAG, "On Destroy");
		super.onDestroy();
	}
	
	private void setUserData(){
		Log.v(TAG, "Setting saved Preferences");
		Spinner spinner = (Spinner)findViewById(R.id.spinnerMaleFemale);
		DataCommons.UserData.userAgeSex = spinner.getSelectedItem().toString();
		EditText editText = (EditText)findViewById(R.id.editTextAge);
		DataCommons.UserData.userAgeSex += DataCommons.UserData.DATA_SEPARATOR + editText.getText().toString();
		SharedPreferences settings = getSharedPreferences(DataCommons.Preferences.PREFERENCES_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DataCommons.Preferences.USER_NAME, DataCommons.UserData.userAgeSex);
		editor.putString(DataCommons.Preferences.USER_ID, DataCommons.UserData.userId);
		editor.commit();
	}
	
	
	private void getSharedPreferencesSaved(){
		SharedPreferences settings = getSharedPreferences(DataCommons.Preferences.PREFERENCES_NAME, MODE_PRIVATE);
		Spinner spinner = (Spinner)findViewById(R.id.spinnerMaleFemale);
		EditText editText = (EditText)findViewById(R.id.editTextAge);
		String[] userData= AppTools.splitString(settings.getString(DataCommons.Preferences.USER_NAME, " "), 
											DataCommons.UserData.DATA_SEPARATOR);
		DataCommons.UserData.userAgeSex = userData[0] + DataCommons.UserData.DATA_SEPARATOR + userData[1];
		//We have tow fields in the interface Name-Surname; 
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> arrayAdapter =(ArrayAdapter<String>)spinner.getAdapter();
		try{
			spinner.setSelection(arrayAdapter.getPosition(userData[0]));
		}catch(Exception e){
			Log.e(TAG,"Exception setting up spinnner: " + e.getMessage());
		}
		editText.setText(userData[1]);
		editText.setSelection(editText.getText().length());
	}
	
	@Override
	public void onClick(View view) {
		Log.v(TAG, "On click button");
		retrieveData();
		
		switch(view.getId()){
		case R.id.buttonCancel:
			break;
		case R.id.buttonSend:
			setUserData();
			if(IS_EXPORTING){
				Log.v(TAG, "Exporting data on progress");
				Toast.makeText(this, "Wait until exporting has been completed", 
						Toast.LENGTH_SHORT).show();
				return;
			}
			if(checkDataInputCorrect(R.id.editTextAge)){
				if (!getFilesAttached()){
					Toast.makeText(this, "No file to attach. Please restart testing", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(DataCommons.Settings.IS_AMAZON_TURK){
					String message;
					message = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
							"sdcard/dataSharedNet/xml":"data/dataSharedNet/xml";
					Toast.makeText(this, "You can find the file: " + message, 
							Toast.LENGTH_LONG).show();
					break;
				}
				else{
					boolean result = sendEmail();
					String message;
					message = result?"Email has been created": "Not email created";
					AFTER_MAIL = result? true : false;
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
					Log.v(TAG, message);
					return;
				}
			}
			Toast.makeText(this, "Please fill 'Age' field ", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.v(TAG, "Finishing application");	
		finish();
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean sendEmail(){
		Log.v(TAG, "Send email method");
		try{
			boolean result = AppTools.sendDataEmail(this, 
					EMAIL_TO,
					EMAIL_CC, 
					EMAIL_SUBJECT,
					mEmailText,
					mFilePaths);
			Log.v(TAG, result?"Email created correctly": "Error when creating email");
			return result;
		}catch(Exception e){
			Log.e(TAG, "Error when getting files to attach: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * We have supposed only Zip files are in the folder
	 */
	public boolean getFilesAttached(){
		Log.v(TAG, "Get files to attach");
		String dir = DatabaseUtils.Database.DIRECTORY.equalsIgnoreCase("")?getFilesDir().getPath():
			DatabaseUtils.Database.DIRECTORY;
		File fileDir = new File(dir + File.separator + DatabaseUtils.Database.XML_DIRECTORY);
		try{
			String[] arrayFiles = fileDir.list();
			mFilePaths = new ArrayList<String>();
			for(int i=0; i<arrayFiles.length; i++){
				mFilePaths.add(fileDir + File.separator + arrayFiles[i]);
			}
		}catch(Exception e){
			Log.e(TAG, "Error when reading Xml dir: "+e.getMessage());
			return false;
		}
		if(mFilePaths.size() < 1){
			Log.v(TAG, "Files to attached no test xml");
			return false;
		}
		return true;
	}
	
	private void retrieveData(){
		Log.v(TAG,"Retrieving data from screen");
		Spinner spinner = (Spinner)findViewById(R.id.spinnerMaleFemale);
		DataCommons.UserData.userAgeSex = spinner.getSelectedItem().toString();
		EditText editText = (EditText)findViewById(R.id.editTextAge);
		DataCommons.UserData.userAgeSex += DataCommons.UserData.DATA_SEPARATOR + editText.getText().toString(); 
		mEmailText = DataCommons.UserData.userAgeSex;
		Log.v(TAG, "Sex and Age: " + mEmailText);
	}

	private boolean checkDataInputCorrect(int interfaceId){
		try{
			return ((((EditText)findViewById(interfaceId)).getText().toString()).equals(""))?false:true;
		}catch(Exception e){
			Log.e(TAG, "Error checking data input: "+e.getMessage());
			return false;
		}
	}
	
}
