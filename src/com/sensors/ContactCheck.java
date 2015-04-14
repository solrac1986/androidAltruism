package com.sensors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;
import com.application.utilities.Randomize;
import com.data.utils.DataCommons;
import com.dataCollected.DataCollectedTest;
import com.interfaces.FeaturesInterface;


/**
 * Class Contact Check on addressbook.
 * @author troglodito22
 *
 */

public class ContactCheck implements FeaturesInterface{

	
	private final String TAG = ContactCheck.class.getSimpleName();
	
	private Intent mIntent;
	private Context mContext;


	private int[] arrayRecipient = {0 ,0};
	private final int KNOWN_NAME = 1;
	private final int UNKNOWN = 0;
	private final int KNOWN = 2;
	
	public ContactCheck(Context context){
		super();
		Log.v(TAG, "Constructor");
		mContext = context;
	}
	
	public ContactCheck(Context context, Intent intent){
		super();
		Log.v(TAG, "Constructor");
		mContext = context;
		mIntent = intent;
	}
	
	@Override
	public void setIntent(Intent intent) {
		mIntent = intent;
	}

	@Override
	public Intent getIntent() {
		return mIntent;
	}

	@Override
	public int isParameterTrue(Object object) {
		Log.v(TAG, "Paramater is true");
		String nameContact = " ";
		try{
			nameContact = (String)object;
		}catch(Exception e){
			Log.e(TAG, "Error parsing to String: "+ e.getMessage());
			return 0;
		}
		Log.v(TAG, "Name contact search: "+nameContact);
		Cursor cr = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
														null, 
														null, 
														null, 
														null);
		if(cr.getCount()>0){
			while(cr.moveToNext()){
				String name = cr.getString(cr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				Log.v(TAG, "Name list: "+name);
				if (name.equalsIgnoreCase(nameContact)){
					Log.v(TAG, "Contact, found: "+name+" is on List");
					return 1;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Update DataCollected sender-receiver
	 */
	public int[] updateDataContactCollected(){
		Log.v(TAG, "Update datacollected");
		return arrayRecipient;
	}
	
	@Override
	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public Context getContext() {
		return mContext;
	}

	public String getPersonType(int type, Bitmap bitmap){
		Log.v(TAG, "Get person form addressbook or not");
		Randomize randomize = new Randomize();
		if(randomize.getBoolean()){
			return getRandomContact(type, bitmap);
		}
		Log.v(TAG, "Person unknown");
		arrayRecipient[type]=UNKNOWN;
		return DataCollectedTest.PersonType.arrayType[DataCollectedTest.PersonType.UNKNOWN];
	}
	
	
	@SuppressWarnings("unused")
	private String getRandomContact( int type, Bitmap bitmap){
		Log.v(TAG, "Get random contact from addressbook");
		// Random contact or Known person
		Randomize random = new Randomize();
		if(random.getBoolean() && DataCommons.Settings.IS_CONTACT_KNOWN_ACTIVE){
			Log.v(TAG, "Person known");
			arrayRecipient[type] = KNOWN;
			return DataCollectedTest.PersonType.arrayType[DataCollectedTest.PersonType.KNOWN];
		}
		Cursor cr = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
				new String[]{ContactsContract.Contacts._ID,
		        ContactsContract.Contacts.DISPLAY_NAME,
		        ContactsContract.Contacts.PHOTO_ID}, 
				null, 
				null, 
				null);
		Randomize randomize = new Randomize();
		String name = DataCollectedTest.PersonType.arrayType[DataCollectedTest.PersonType.UNKNOWN];
		try{
			int sizeAddressbook = cr.getCount();
			int position = randomize.getIntRandom(sizeAddressbook);
			cr.moveToPosition(position);
			name = cr.getString(cr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			Log.v(TAG, "Random contact: "+name);
//			try{
//				byte[] buffer = cr.getBlob(cr.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
//				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
//				BitmapDrawable bitmapDrawable = new BitmapDrawable(in);
//				bitmap = bitmapDrawable.getBitmap();
//			}catch(Exception e){
//				Log.e(TAG, "Error when retrieving photo: " + name + " error: " + e.getMessage());
//				bitmap = null;
//			}
			
		}catch(Exception e){
			Log.e(TAG, "Error searching random contact: "+ e.getMessage());
			arrayRecipient[type]=UNKNOWN;
			return DataCollectedTest.PersonType.arrayType[DataCollectedTest.PersonType.UNKNOWN];
		}
		arrayRecipient[type]=KNOWN_NAME;
		return name;
	}
	
	public static class SenderReceiver{
		public static final int SENDER = 0;
		public static final int RECEIVER = 1;
	}
	
}
