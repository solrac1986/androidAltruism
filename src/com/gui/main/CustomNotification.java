package com.gui.main;

import com.data.utils.DataCommons;
import com.gui.activities.DialogBoxActivity;

import com.main.shared_net_facebook.activities.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Class Custom Notification on status bar
 * @author troglodito22
 *
 */

public class  CustomNotification {

	private static final String TAG = CustomNotification.class.getSimpleName();

	public static final int NOTIFICATION_ALTRUISM_ID = 1234;
	public static boolean IS_ACTIVE = false;
	public static int PROGRESS_BAR_NOTIFICATION_ID = 1;
	
	public static final String ACTION_NOTIFICATION = "notificationbar_pressed";
	
	private static Context mContext;
	private String mTitle;
	private String mText;
	private String mTickerText;
	
	private int mProgressStatus;
	private int mProgressStep = 0;
	private static Thread mThread;
	
	public CustomNotification(Context context){
		mContext = context;
	}
	
	public void setContext(Context context){
		mContext = context;
	}
	
	public void setTitle(String title){
		mTitle = title;
	}
	
	public void setText(String text){
		mText = text;
	}
	
	public void setTickerText(String tickerText){
		mTickerText = tickerText;
	}
	
	/**
	 * 
	 * @param tickertext
	 * @param title
	 * @param text
	 */
	public void setTitleText(String tickertext,String title, String text){
		mTickerText = tickertext;
		mTitle= title;
		mText = text;
	}
	
	public void setTitleTextNotification(String type){
		Log.v(TAG, "Set default title notification bar");
		Resources res = mContext.getResources();
		String[] arrayString = res.getStringArray(R.array.notificationbar);
		mTickerText = arrayString[0];
		mTitle = arrayString[1];
		mText = arrayString[2] + type;
	}
	
	public void cancelNotification(int uuid){
		Log.v(TAG, "Delete notificationbar");
		NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(uuid);
	}
	
	
	/**
	 * 
	 * @param typeContent
	 * @return
	 */
	public boolean showNotification(Bundle bundle){
		Log.v(TAG, "Show default notification");
		NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_share_statusbar;
		long when = System.currentTimeMillis();
		try{
			Notification notification = new Notification(icon, mTickerText, when);
			Intent notificationIntent = new Intent(mContext, DialogBoxActivity.class);		
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			notificationIntent.putExtras(new Bundle(bundle));
			PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, 
						notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(mContext.getApplicationContext(), mTitle, mText, contentIntent);
			
			notification.icon = R.drawable.ic_data_incoming; 
			notification.defaults |= Notification.DEFAULT_SOUND; 
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.flags |= Notification.FLAG_NO_CLEAR;
			
			notificationManager.notify(CustomNotification.NOTIFICATION_ALTRUISM_ID, notification);
			return true;
		}catch(Exception e){
			Log.e(TAG, "Error when creating notification bar: " + e.getMessage());
			return false;
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean showNotificationProgressBar(long size){
		Log.v(TAG, "Show notification progress Bar");
		mProgressStep = (int)size == 0 ? DataCommons.Content.SCALE_FILE_SIZE_STEP : (int)size;
		mProgressStatus = 0;
		
		final NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_file_tx;
		long when = System.currentTimeMillis();
	
		final Notification notification = new Notification(icon, "Sending", when);

		Intent notificationIntent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
		
		Log.v(TAG, "File content type");
		final RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
		try{
			contentView.setImageViewResource(R.id.image_notification, R.drawable.ic_file_tx);
			contentView.setTextViewText(R.id.title_notification, "Transmitting...");
			contentView.setTextViewText(R.id.text_notification, "Progress.");
			int maxProgress = (int)(DataCommons.Content.FILE_SIZE_MAX);
			contentView.setProgressBar(R.id.progressBarFile, 
										maxProgress, 
										mProgressStatus, 
										false);
			notification.contentView = contentView;
		}catch(Exception e){
			Log.e(TAG, "Error when creating notification progress bar: " + e.getMessage());
			return false;
		}
		// Create thread for update progress bar
		mThread = new Thread(new Runnable(){		
			public void run(){
				Log.v(TAG, "Thread update progressbar");
				try{
					while(mProgressStatus < DataCommons.Content.FILE_SIZE_MAX){
						mProgressStatus += 	mProgressStep;
						contentView.setProgressBar(R.id.progressBarFile, 
								(int) DataCommons.Content.FILE_SIZE_MAX, 
								mProgressStatus, 
								false);
						notification.contentView = contentView;
					
						notificationManager.notify(CustomNotification.PROGRESS_BAR_NOTIFICATION_ID, 
								notification);					
						try{
							Thread.sleep(500);	
						}catch(Exception e){
							Log.e(TAG, "Error when thread going to sleep: "+e.getMessage());
						}
					}
					notificationManager.cancel(PROGRESS_BAR_NOTIFICATION_ID);
					IS_ACTIVE = false;
					mProgressStatus = 0;
					mProgressStep = 0;
				}catch(Exception e){
					Log.e(TAG, "Exception in thread update progressBar");
				}
			}
		});
		notification.contentIntent = contentIntent;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notificationManager.notify(CustomNotification.PROGRESS_BAR_NOTIFICATION_ID, notification);
		try{
			mThread.start();
		}catch(Exception e){
			Log.e(TAG, "Thread has been started before: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Cancel all notifications
	 */
	public static void cancelNotifications(){
		Log.v(TAG, "Cancel notifications");
		NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(CustomNotification.NOTIFICATION_ALTRUISM_ID);
		notificationManager.cancel(CustomNotification.PROGRESS_BAR_NOTIFICATION_ID);
//		CustomNotification.IS_ACTIVE = false;
	}
	
	public void setProgressBarIncrement(long value){
		mProgressStep = (int)value;
	}
	
}
