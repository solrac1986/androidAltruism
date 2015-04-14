package com.gui.main;


/** 
 * Class to show messages in user activity - Screen ON -
 * @author troglodito22
 * 
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.util.Log;
import android.widget.Toast;



public class ScreenMessage {

	private final String TAG = ScreenMessage.class.getSimpleName();
	
	private Context mContext;
	
	public ScreenMessage(Context context){
		super();
		Log.v(TAG, "Constructor");
		mContext = context;
	}
	
	/**
     * Screen message method
     * @param title
     * @param message
     * @param type
     * @param level
     */
	public void showMessage(String title,String message, int type, int level) {
    	switch(type){
    	case MessageType.ALERT_MESSAGE:
    		alertMessage(title, message, level);
    		break;
    	case MessageType.TOAST_MESSAGE:
    		toastMessage(title, message);
    		break;
    	}
    	
    	
    }
	/**
	 * 
	 * @param title
	 * @param message
	 * @param level
	 */
    private void alertMessage(String title,String message, int level) {
    	switch(level){
    		case MessageType.DIALOG_CLOSE_ID:
    			new AlertDialog.Builder(mContext).setTitle(title)
    			.setMessage(message).setNeutralButton("Accept",null)
    			.show();
    			break;
    		case MessageType.DIALOG_YES_NO_ID:
    			new AlertDialog.Builder(mContext)
    			.setTitle(title).setMessage(message)
    			.setNegativeButton("No", new DialogInterface.OnClickListener(){
    				public void onClick(DialogInterface dialog, int which){
    					dialog.cancel();    				}
    			})
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//TODO:Case some dialog box
					}
				})
    			.show();
    			break;
    	}
    }
    
    /**
     * 
     * @param title
     * @param message
     */
    private void toastMessage(String title, String message) {
    	Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
    
    public static class MessageType{
    	
    	public static final String MENU_SCREEN_MAIN = "main";
    	public static final String MENU_SCREEN_RUN = "run";
    	
    	public static final int ALERT_MESSAGE = 1;
    	public static final String ALERT_TEXT = "Attention";
    	
    	public static final int TOAST_MESSAGE = 2;
    	public static final String TOAST_TEXT = "";
    	
    	
    	public static final int DIALOG_CLOSE_ID = 2;
    	public static final int DIALOG_OK_CANCEL_ID = 3;
    	public static final int DIALOG_YES_NO_ID = 4;
    }
    
    
	
}
