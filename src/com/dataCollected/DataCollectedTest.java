package com.dataCollected;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to collect all data related with applications, bytes sent-received.
 * @author troglodito22
 * 
 */

public class DataCollectedTest implements Parcelable{

	//Variables collected
	private long time_notification;
	private long time_popup;
	private long time_user;
	private int content_type;
	private long content_size;
	private int type_sender;
	private int type_receiver;
	private int battery_status;
	private long battery_level;
	private int user_moving;
	private int user_location;
	private double longitude;
	private double latitude;
	private int cellID;
	private int lac;
	private long time_location;
	private int optionSelected;
	
	private int option_cause;
	private int mood;
	
	
	
	public DataCollectedTest(long time_n ,long time_p, long time_u, int content_t, long content_s, 
			int type_s, int type_r, int battery_s, long battery_l, int user_m, int user_l, 
			long longit, long lat,int cell, int lac_s, long time_l, int option, int option_c, int m){
		this.time_notification = time_n;
		this.time_popup = time_p;
		this.time_user = time_u;
		this.content_type = content_t;
		this.content_size = content_s;
		this.type_sender = type_s;
		this.type_receiver = type_r;
		this.battery_status = battery_s;
		this.battery_level = battery_l;
		this.user_moving = user_m;
		this.user_location = user_l;
		this.longitude = longit;
		this.latitude = lat;
		this.cellID = cell;
		this.lac = lac_s;
		this.time_location = time_l;
		this.optionSelected = option;		
		
		this.option_cause = option_c;
		this.mood = m;
	}
	
	public DataCollectedTest() { ; }
	
	public DataCollectedTest(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(time_notification);
		dest.writeLong(time_popup);
		dest.writeLong(time_user);
		dest.writeInt(content_type);
		dest.writeLong(content_size);
		dest.writeInt(type_sender);
		dest.writeInt(type_receiver);
		dest.writeInt(battery_status);
		dest.writeLong(battery_level);
		dest.writeInt(user_moving);
		dest.writeInt(user_location);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeInt(cellID);
		dest.writeInt(lac);
		dest.writeLong(time_location);
		dest.writeInt(optionSelected);
		
		dest.writeInt(option_cause);
		dest.writeInt(mood);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
		    	new Parcelable.Creator() {
		            public DataCollectedTest createFromParcel(Parcel in) {
		                return new DataCollectedTest(in);
		            }
		 
		            public DataCollectedTest[] newArray(int size) {
		                return new DataCollectedTest[size];
		            }
		        };
	
	private void readFromParcel(Parcel in){
		this.time_notification = in.readLong();
		this.time_popup = in.readLong();
		this.time_user = in.readLong();
		this.content_type = in.readInt();
		this.content_size = in.readLong();
		this.type_sender = in.readInt();
		this.type_receiver = in.readInt();
		this.battery_status = in.readInt();
		this.battery_level = in.readLong();
		this.user_moving = in.readInt();
		this.user_location = in.readInt();
		this.longitude = in.readDouble();
		this.latitude = in.readDouble();
		this.cellID = in.readInt();
		this.lac = in.readInt();
		this.time_location = in.readLong();
		this.optionSelected = in.readInt();

		this.option_cause = in.readInt();
		this.mood = in.readInt();
	}
	
	public void setTimeNotification(long time){
		this.time_notification = time;
	}
	
	public void setTimePopup(long time){
		this.time_popup = time;
	}
	
	public void setTimeUser(long time){
		this.time_user = time;
	}
	
	public void setContentType(int type){
		this.content_type = type;
	}
	
	public void setContentSize(long size){
		this.content_size = size;
	}
	
	public void setTypeSender(int type){
		this.type_sender = type;
	}
	
	public void setTypeReceiver(int type){
		this.type_receiver = type;
	}
	
	public void setBatteryStatus(int status){
		this.battery_status = status;
	}
	
	public void setBatteryLevel(long level){
		this.battery_level = level;
	}
	
	public void setUserMoving( int status){
		this.user_moving = status;
	}
	
	public void setUserLocation (int s){
		this.user_location = s;
	}
	
	public void setLongitude(double lng){
		this.longitude = lng;
	}
	
	public void setLatitude (double d){
		this.latitude = d;
	}
	
	public void setCellID(int cell){
		this.cellID = cell;
	}
	
	public void setLac (int l){
		this.lac = l;
	}
	
	public void setTimeLocation (long time){
		this.time_location = time;
	}
	
	public void setOptionSelected(int option){
		this.optionSelected = option;
	}
	
	public void setOptionCause(int cause){
		this.option_cause = cause;
	}
	
	public void setMood(int m){
		this.mood = m;
	}
	
	/**
	 ***********************************************************
	 * Get parameters methods
	 */
	
	public long getTimeNotification(){
		return this.time_notification;
	}
	
	public long getTimePopup(){
		return this.time_popup;
	}
	
	public long getTimeUser(){
		return this.time_user;
	}
	
	public int getContentType(){
		return this.content_type;
	}
	
	public long getContentSize(){
		return this.content_size;
	}
	
	public  int getTypeSender(){
		return this.type_sender;
	}
	
	public int getTypeReceiver(){
		return this.type_receiver;
	}
	
	public int getBatteryStatus(){
		return this.battery_status;
	}
	
	public long getBatteryLevel(){
		return this.battery_level;
	}
	
	public int getUserMoving(){
		return this.user_moving;
	}
	
	public int getUserLocation(){
		return this.user_location;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public double getLatitude (){
		return this.latitude;
	}
	
	public int getCellID(){
		return this.cellID;
	}
	
	public int getLac(){
		return this.lac;
	}
	
	public long getTimeLocation (){
		return this.time_location;
	}
	
	public int getOptionSelected(){
		return this.optionSelected;
	}
	
	public int getOptionCause(){
		return this.option_cause;
	}
	
	public int getMood(){
		return this.mood;
	
	}
	
	/**
	 * Constants.
	 * @author troglodito22
	 */
	
	public static class Option {
		public static final int YES = 1;
		public static final int NO = 0;
	}
	
	public static class ContentType{
		public static final int MESSAGE = 1;
		public static final int FILE = 0 ;
		public static final String[] arrayType = {"File", "Message"};
	}
	
	public static class PersonType{
		public static final int UNKNOWN = 0;
		public static final int KNOWN_NAME = 1;
		public static final int KNOWN = 2;
		public static final String[] arrayType = {"Unknown" , "Known_name", "Known"};
		
		public static final String SENDER="Sender";
		public static final String RECEVIER="Receiver";
	}
	
	public static class BatteryStatus{
		public static final int DISCHARGING = 1;
		public static final int CHARGING = 0;
	}
	
	public static class UserStatus{
		public static final int MOVING = 1;
		public static final int NOT_MOVING = 0;
	}
	
	public static class BooleanSQLite {
		public static final int isTrue = 1;
		public static final int isFalse = 0;
	}

	public static class MoodValues {
		public static final int REALLY_HAPPY = 0;  
		public static final int  HAPPY = 1;
		public static final int  OK = 2;
		public static final int  CONFUSED = 3;
		public static final int  SAD = 4;
		public static final int ANGRY = 5;
		
		public static final String[] arrayMood = {"Really_happy", "Happy", "Ok", "Confused", "Sad", "Angry" };
	}
	
}
