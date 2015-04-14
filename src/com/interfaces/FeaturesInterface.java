package com.interfaces;

import android.content.Context;
import android.content.Intent;

/**
 * Interface for Sensors classes
 * @author troglodito22
 *
 */

public interface FeaturesInterface {
	
	public void setIntent(Intent intent);
	public Intent getIntent();
	
	public void setContext (Context context);
	public Context getContext();
	
	public int isParameterTrue(Object objec);
	
}
