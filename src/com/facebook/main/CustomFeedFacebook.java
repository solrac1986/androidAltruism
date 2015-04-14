package com.facebook.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.data.utils.DataCommons;
import com.facebook.android.Facebook;
import com.facebook.utils.DataFacebook;

/**
 * Class to create and manage Facebook feeds; 
 * @author troglodito22
 *
 */

public class CustomFeedFacebook extends AsyncTask<String, Void, String>{

	private final String TAG = CustomFeedFacebook.class.getSimpleName();
	
	private Facebook mFacebook;
	private Context mContext;
	
	public CustomFeedFacebook(Facebook facebook, Context context){
		super(); 
		Log.v(TAG, "Constructor");
		mFacebook = facebook;
		mContext = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String result =" ";
		if(params[0].equalsIgnoreCase(DataFacebook.Action.POST_ON_WALL)){
			result = postOnWall(params[0]) ? "Post on facebook wall" :
				"Couldn't post on facebook wall";
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String result){
		Log.v(TAG, "On post executed, msg: " + result);
		Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	}
	
	@SuppressWarnings("unused")
	private boolean postOnWall(String message){
		Log.v(TAG, "Post on wall method init");
		if (! DataCommons.Settings.IS_FACEBOOK_ON){
			Log.v(TAG, "Facebook paramater is OFF");
			return false;
		}
		try{
			String response = mFacebook.request(DataFacebook.Feed.WHO);
			Bundle parameters = new Bundle();
			parameters.putString(DataFacebook.Feed.KEY_MESSAGE, (DataFacebook.Feed.MESSAGE + 
					 " " + System.currentTimeMillis()));
			parameters.putString(DataFacebook.Feed.KEY_DESCRIPTION, 
					(DataFacebook.Feed.DESCRIPTION));
			parameters.putString(DataFacebook.Feed.KEY_LINK, 
					(DataFacebook.Feed.LINK));
			response = mFacebook.request(DataFacebook.Feed.WHO +
					DataFacebook.Feed.SEPARATOR +
					DataFacebook.Feed.ACTION_FEED,
					parameters, "POST"); 
			Log.v(TAG, "Got response: " + response);
			if(response == null || response.equals("") || 
					(response.indexOf("error") != -1) ){
				Log.w(TAG, "Error, blank response");
				return false;
			}
		}catch(Exception e){
			Log.e(TAG, "Exception posting message on wall: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param context
	 * @param facebook
	 */
	public void getFacebookFriends(Context context, Facebook facebook){
		Log.v(TAG, "Get facebook friends list");
	
	}

}
