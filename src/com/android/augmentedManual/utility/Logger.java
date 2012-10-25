package com.android.augmentedManual.utility;

import android.util.Log;


public  abstract  class Logger  extends java.util.logging.Logger
{	
	
	public static boolean isDebuggable;
	
	public static String logTag = "EXAMPLE";
	
	protected Logger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	
	}
	
	/**
	 * Display log messages with the given priority
	 * 
	 * @param priority Priority, e.g. Log.INFO, Log.ERROR
	 * @param msg Message to display
	 * 
	 * @see Logger
	 */
	public static void log(int priority, String msg)
	{
		if (isDebuggable && msg != null)
		{
			android.util.Log.println(priority, logTag, msg);
		}
	}
	
	/**
	 * Display log messages with the given priority
	 * 
	 * @param priority Priority, e.g. Log.INFO, Log.ERROR
	 * @param msg Message to display
	 * 
	 * @see Logger
	 */
	public static void log(String msg)
	{
		if (isDebuggable && msg != null)
		{
			android.util.Log.println(Log.DEBUG, logTag, msg);
		}
	}
	
	/**
	 * Display log messages with the given priority
	 * 
	 * @param priority Priority, e.g. Log.INFO, Log.ERROR
	 * @param msg Message to display
	 * 
	 * @see Logger
	 */
	public static void logException(Exception e)
	{
		if (isDebuggable && e != null)
		{
			e.printStackTrace();
			android.util.Log.println(Log.DEBUG, logTag," "+ e.getMessage());
		}
	}
}
