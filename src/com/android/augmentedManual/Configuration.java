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
	public static final String signature = "Xciq/dU9A6Znh2zP5aB3Xu8wHlHBDEx86xIVtIi6Tz8=";

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
