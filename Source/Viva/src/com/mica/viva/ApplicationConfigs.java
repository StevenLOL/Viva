/**
 * @author Binhpro
 */
package com.mica.viva;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationConfigs {
	public static final String Client_address = "Client_address";
	public static final String Read_Message = "Read_Message";
	public static final String Read_Receiver_Message = "Read_Receiver_Message";
	

	private SharedPreferences prefs_;
	private static final String prerferenceName_ = "preference";
		
	private static ApplicationConfigs instance_ = new ApplicationConfigs();

	/**
	 * get SharedPreferences of application
	 * 
	 * @return
	 */
	public SharedPreferences getSharedPreferences() {
		return prefs_;
	}

	/**
	 * get instance of ApplicationConfigs class
	 * 
	 * @return
	 */
	public static ApplicationConfigs getInstance() {
		return instance_;
	}

	public ApplicationConfigs() {
		prefs_ = PreferenceManager
				.getDefaultSharedPreferences(ApplicationContext.getApplicationContext());
	}

	public String getConnectionMode(){
		return prefs_.getString(Client_address, "1");
	}
	
	public  boolean enableReadReceiverMessage(){
		return prefs_.getString(ApplicationConfigs.Read_Receiver_Message, "0").equals("0");
	}
}
