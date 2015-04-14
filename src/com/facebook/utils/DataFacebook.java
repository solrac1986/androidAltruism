package com.facebook.utils;

/**
 * Class to save all data with facebook relation
 * @author troglodito22
 *
 */

public class DataFacebook {

	
	public static class Feed{
		public final static String ACTION_FEED = "feed";
		public final static String WHO = "me";
		public final static String KEY_MESSAGE = "message";
		public final static String KEY_DESCRIPTION = "description";
		public final static String KEY_LINK = "link";
		public final static String MESSAGE = "Hey, I am using Shared Net emulator." + 
							"Do you want to help?. Search 'Shared Net' in Facebook.";
		public final static String DESCRIPTION = "Shared Net application";
		public final static String LINK = "https://www.facebook.com/pages/Shared-Net-Community/173542826087342";
		public final static String SEPARATOR = "/";
	} 
	
	public static class Action{
		public static final String POST_ON_WALL= "post_on_wall";
	}
}
