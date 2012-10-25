package com.android.augmentedManual;

/**
 * Static constant values go here. 
 * 
 * @author tim.oppermann
 *
 */
//------------------------------------------------------------------------
public abstract class Configuration 
{
	public static final String signature = "wpq0KQEy5lf1fwuwhXpPfbQ9WjUo3jcIK0HZEUGOdF0=";

	// ------------------------------------------------------------------------
	public abstract class Camera
	{
		public static final long resolutionX = 640;  	
		public static final long resolutionY = 400;
		/*
		 * 0: normal camera
		 * 1: front facing camera
		 */
		public static final int deviceId = 0;
	}

}
