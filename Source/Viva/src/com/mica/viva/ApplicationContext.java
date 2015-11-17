package com.mica.viva;

import android.content.Context;

public class ApplicationContext {
	private static Context _applicationContext;
	
	
	public static void setApplicationContext(Context applicationContext)
	{
		_applicationContext = applicationContext;
	}
	
	public static Context getApplicationContext (){
		return _applicationContext;
	}
}
