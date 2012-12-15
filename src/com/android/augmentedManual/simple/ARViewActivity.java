/**
 * ARViewActivity.java
 * Example SDK Internal
 * 
 * Created by Arsalan Malik on 03.11.2011
 * Copyright 2011 metaio GmbH. All rights reserved.

 */

package com.android.augmentedManual.simple;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.android.augmentedManual.Configuration;
import com.android.augmentedManual.R;
//import com.android.augmentedManual.MobileSDKExampleApplication;
import com.metaio.tools.io.AssetsManager;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.MetaioSurfaceView;
import com.metaio.sdk.SensorsComponentAndroid;
//import com.metaio.sdk.jni.AS_IMeatioSDKMobile;
import com.metaio.sdk.jni.ERENDER_SYSTEM;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.MetaioSDK;
import com.metaio.sdk.jni.PoseVector;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector2di;

/**
 * This is base activity to use Unifeye SDK Mobile. It creates UnifeyeGLSurface
 * view and handle all its callbacks and lifecycle.
 * 
 * 
 * @author arsalan.malik
 * 
 */
//------------------------------------------------------------------------
public abstract class ARViewActivity extends Activity implements
		MetaioSurfaceView.Callback, OnTouchListener {
	
	// ------------------------------------------------------------------------
	static {
		IMetaioSDKAndroid.loadNativeLibs();
	}

	/**
	 * Sensor manager
	 */
	protected SensorsComponentAndroid mSensorsManager;

	/**
	 * Unifeye OpenGL View
	 */
	protected MetaioSurfaceView mUnifeyeSurfaceView;

	/**
	 * The detected coordinate system id of the previous frame. This is used in onDrawFrame
	 * to check if a target has been just detected.
	 */
	protected int mDetectedCosID = -1;

	/**
	 * GUI overlay, only valid in onStart and if a resource is provided in
	 * getGUILayout.
	 */
	protected View mGUIView;

	/**
	 * UnifeyeSDK object
	 */
	protected IMetaioSDKAndroid mMobileSDK;

	/**
	 * flag for the renderer
	 */
	private boolean rendererInitialized = false;

	/**
	 * Wake lock to avoid screen time outs.
	 * <p>
	 * The application must request WAKE_LOCK permission.
	 */
	protected PowerManager.WakeLock mWakeLock;

	/**
	 * UnifeyeMobile callback handler
	 */
	private IMetaioSDKCallback mHandler;

	/**
	 * Provide resource for GUI overlay if required.
	 * <p>
	 * The resource is inflated into mGUIView which is added in onStart
	 * 
	 * @return Resource ID of the GUI view
	 */
	// ------------------------------------------------------------------------
	protected int getGUILayout() {
		Log.v("DEBUG", "getGUILayout");
		return 0;
	};

	/**
	 * Provide Unifeye callback handler if desired.
	 * 
	 * @see IMetaioSDKCallback
	 * 
	 * @return Return unifeye callback handler
	 */
	// ------------------------------------------------------------------------
	protected IMetaioSDKCallback getMobileSDKCallbackHandler() {
		return null;
	};

	/**
	 * Load contents to unifeye in this method, e.g. tracking data, geometries
	 * etc.
	 */
	// ------------------------------------------------------------------------
	protected abstract void loadUnifeyeContents();

	/**
	 * Called when a geometry is touched.
	 * 
	 * @param geometry
	 *            Geometry that is touched
	 */
	// ------------------------------------------------------------------------
	protected void onGeometryTouched(IGeometry geometry) {
	};

	@Override
	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
		Log.v("DEBUG", "onCreate !");
		super.onCreate(savedInstanceState);
		
		MetaioDebug.log("ARViewActivity.onCreate()");
		mMobileSDK = null;
		mUnifeyeSurfaceView = null;

		mHandler = null;
		
		try {
			// create the sensor manager
			if (mSensorsManager == null) {
				mSensorsManager = new SensorsComponentAndroid(getApplicationContext());
			}

			// create the MobileSDK
			Log.v("NOS", "creating UnifeyeMobile in onCreate()");
			createMobileSDK();
			
			// Inflate GUI view if provided
			mGUIView = View.inflate(this, getGUILayout(), null);
			Log.v("DEBUG", "ARVIewActivity created");
		} catch (Exception e) {

		}

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				getPackageName());

	}

	@SuppressWarnings("deprecation")
	@Override
	// ------------------------------------------------------------------------
	protected void onStart() {
		Log.v("DEBUG", "ARViewActivity::onStart");
		Log.v("DEBUG", "ARViewActivity.onStart(): "
				+ Thread.currentThread().getId());
		super.onStart();
		MetaioDebug.log("ARViewActivity.onStart(): "
				+ Thread.currentThread().getId());

		try {
			mUnifeyeSurfaceView = null;

			// start/resume the camera
			if (mMobileSDK != null) {

				// create a empty layout, required for the camera on some devices
				setContentView(new FrameLayout(this));
//				setContentView(R.layout.activity_manual);
				
				
				Vector2di cameraResolution = mMobileSDK.startCamera(
						Configuration.Camera.deviceId,
						Configuration.Camera.resolutionX,
						Configuration.Camera.resolutionY);
				Log.v("DEBUG", "ARViewActivity, startCamera called");
				
				// Create views (UnifeyeGL and GUI)

				// Add Unifeye GL Surface view
				mUnifeyeSurfaceView = new MetaioSurfaceView(this);
				mUnifeyeSurfaceView.registerCallback(this);
				mUnifeyeSurfaceView.setKeepScreenOn(true);
				mUnifeyeSurfaceView.setOnTouchListener(this);

				// Get layout params that stretches surface view to entire
				// screen while keeping aspect ratio.
				// Pass false, to fit entire surface view in the center of the
				// parent
				FrameLayout.LayoutParams params = mUnifeyeSurfaceView
						.getLayoutParams(cameraResolution, true);

				MetaioDebug.log("UnifeyeSurfaceView layout: " + params.width + ", "
						+ params.height);

				addContentView(mUnifeyeSurfaceView, params);

				mUnifeyeSurfaceView.setZOrderMediaOverlay(true);

			}

			// If GUI view is inflated, add it
			if (mGUIView != null) {
				addContentView(mGUIView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mGUIView.bringToFront();
			}

		} catch (Exception e) {
			MetaioDebug.log(e.getMessage());
		}

	}

	@Override
	// ------------------------------------------------------------------------
	protected void onPause() {
		Log.v("DEBUG", "onPause !");
		super.onPause();
		MetaioDebug.log("ARViewActivity.onPause()");

		if (mWakeLock != null)
			mWakeLock.release();

		// pause the Unifeye surface
		if (mUnifeyeSurfaceView != null)
			mUnifeyeSurfaceView.onPause();

		if (mSensorsManager != null) {
			mSensorsManager.registerCallback(null);
			mSensorsManager = null;
		}
	}

	@Override
	// ------------------------------------------------------------------------
	protected void onResume() {
		Log.v("DEBUG", "onPause !");
		super.onResume();
		MetaioDebug.log("ARViewActivity.onResume()");

		if (mWakeLock != null)
			mWakeLock.acquire();

		// make sure to resume the Unifeye surface
		if (mUnifeyeSurfaceView != null)
			mUnifeyeSurfaceView.onResume();

		// Open all sensors
		if (mSensorsManager == null) {
			mSensorsManager = new SensorsComponentAndroid(getApplicationContext());
		}
	}

	@Override
	// ------------------------------------------------------------------------
	protected void onStop() {
		
		Log.v("DEBUG", "onStop !");
		super.onStop();

		MetaioDebug.log("ARViewActivity.onStop()");

		if (mMobileSDK != null) {
			// Disable camera
			mMobileSDK.stopCamera();
		}

		System.runFinalization();
		System.gc();

	}

	@Override
	// ------------------------------------------------------------------------
	protected void onDestroy() {
		super.onDestroy();

		MetaioDebug.log("ARViewActivity.onDestroy()");

		if (mMobileSDK != null) {
			mMobileSDK.delete();
			mMobileSDK = null;
		}

		if (mSensorsManager != null) {
			mSensorsManager.registerCallback(null);
			mSensorsManager = null;
		}

//		MobileSDKExampleApplication
//				.unbindDrawables(findViewById(android.R.id.content));

		System.runFinalization();
		System.gc();

	}

	
	// ------------------------------------------------------------------------
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			MetaioDebug.log("ARViewActivity touched at: " + event.toString());

			try {
				final int x = (int) event.getX();
				final int y = (int) event.getY();

				// ask the SDK if a geometry has been hit
				IGeometry geometry = mMobileSDK
						.getGeometryFromScreenCoordinates(x, y, true);
				if (geometry != null) {
					onGeometryTouched(geometry);
				}
			} catch (Exception e) {

			}

		}

		// don't ask why we always need to return true contrary to what
		// documentation says
		return true;
	}

	/**
	 * This function will be called, right after the OpenGL context was created.
	 * All calls to UnifeyeMobile must be done after this callback has been
	 * triggered.
	 */
	// ------------------------------------------------------------------------
	public void onSurfaceCreated() {
		MetaioDebug.log("onSurfaceCreated() TRIGGERED");
		try {
			MetaioDebug.log("onSurfaceCreated: " + Thread.currentThread().getId());

			// initialize the renderer
			if (!rendererInitialized) {

				mMobileSDK.initializeRenderer(mUnifeyeSurfaceView.getWidth(),
						mUnifeyeSurfaceView.getHeight(),
						ERENDER_SYSTEM.ERENDER_SYSTEM_OPENGL_ES_2_0);
				loadUnifeyeContents();
				rendererInitialized = true;
			} else
//				mMobileSDK.reloadTextures();
			
			// connect the audio callbacks
			mMobileSDK.registerAudioCallback(mUnifeyeSurfaceView
					.getAudioRenderer());
			mHandler = getMobileSDKCallbackHandler();
			if (mHandler != null)
				mMobileSDK.registerCallback(mHandler);
			
			this.runOnUiThread(new Runnable() {
				public void run() {
					if (mGUIView != null) 
						mGUIView.findViewById(R.id.manualActivityLoadingProgressBar).setVisibility(View.GONE);
						mGUIView.findViewById(R.id.manualActivityLoadingTextView).setVisibility(View.GONE);
						mGUIView.findViewById(R.id.manualActivityButtonBar).setVisibility(View.VISIBLE);
						mGUIView.findViewById(R.id.manualActivityPanelRelativeLayout).setVisibility(View.VISIBLE);
					}
			});

			
		} catch (Exception e) {
			Log.v("DEBUG", "onSurfaceNOTCreated: " + e);
			MetaioDebug.log("onSurfaceNOTCreated: " + e);
		}
	}

	/**
	 * Create  MobileSDK instance
	 */
	// ------------------------------------------------------------------------
	private void createMobileSDK() {
		Log.v("DEBUG", "createMobileSDK !");
		try {
			MetaioDebug.log("Creating the metaio mobile SDK");

			// Make sure to provide a valid application signature
			mMobileSDK = MetaioSDK.CreateMetaioSDKAndroid(this, Configuration.signature);
			mMobileSDK.registerSensorsComponent(mSensorsManager);
			
		} catch (Exception e) {
			MetaioDebug.log(Log.ERROR,
					"Error creating unifeye mobile: " + e.getMessage());
		}

	}

	// ------------------------------------------------------------------------
	protected boolean loadTrackingData(String trackingDataFileName) {
		
		Log.v("DEBUG", "loadingTrackingData !");
		
		MetaioDebug.log("ARViewActivity.loadTrackingData()");
		String filepathTracking = AssetsManager.getAssetPath(trackingDataFileName);
		boolean result = mMobileSDK.setTrackingConfiguration(filepathTracking);
		MetaioDebug.log(Log.ASSERT, "Tracking data loaded: " + result + filepathTracking);
		Log.v("DEBUG", "Tracking data loaded: " + result + filepathTracking);
		return result;
	}

	/**
	 * Loads a geometry from the assets. Requires a running MobileSDK.
	 * 
	 * @param modelFileName The name of the model as in the assets folder
	 * @return A geometry object on success, null on failure.
	 * @throws FileNotFoundException
	 */
	// ------------------------------------------------------------------------
	protected IGeometry loadGeometry(String modelFileName)
			throws FileNotFoundException {
		Log.v("DEBUG", "loadGeometry !");
		IGeometry loadedGeometry = null;
		String filepath = AssetsManager.getAssetPath(modelFileName);
		if (filepath != null) {
			loadedGeometry = mMobileSDK.createGeometry(filepath);
			if (loadedGeometry == null) {
				throw new RuntimeException(
						"Could not load the model file named " + modelFileName);
			}
		} else {
			throw new FileNotFoundException("Could not find the model named '"
					+ modelFileName + "' at: " + filepath);
		}
		return loadedGeometry;
	}

	
	// ------------------------------------------------------------------------
	public void onDrawFrame() {
		// To implement in the child class
	}

	
	// ------------------------------------------------------------------------
	public void onSurfaceDestroyed() {
		mUnifeyeSurfaceView = null;
		MetaioDebug.log("onSurfaceDestroyed");
	}

	
	// ------------------------------------------------------------------------
	public void onSurfaceChanged() {
		MetaioDebug.log("onSurfaceChanged");
	}

	
	// ------------------------------------------------------------------------
	public void onScreenshot(Bitmap bitmap) {
	}

}
