/**
 * ARViewActivitySimple.java
 * Example SDK Internal
 *
 * Created by Arsalan Malik on 08.03.2011
 * Copyright 2011 metaio GmbH. All rights reserved.
 *
 */

package com.android.augmentedManual.simple;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.augmentedManual.R;
import com.android.augmentedManual.utility.ManualXMLParser;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.ndk.IUnifeyeMobileAndroid;
import com.metaio.unifeye.ndk.IUnifeyeMobileGeometry;
import com.metaio.unifeye.ndk.PoseVector;
import com.metaio.unifeye.ndk.Vector3d;


//------------------------------------------------------------------------
public class ARManualViewActivity extends ARViewActivity  {
	
	// ------------------------------------------------------------------------
	static {
		IUnifeyeMobileAndroid.loadNativeLibs();
	}
	
	private IUnifeyeMobileGeometry mGeometryMetaioMan;
	private IUnifeyeMobileGeometry mGeometryTruck;

	private IUnifeyeMobileGeometry mCurrentGeometry;
	
	private int CounterGeometry = 0;
	
	private ManualXMLParser XmlParser;

	private final String mTrackingDataML3D = "TrackingData_ML3D.xml";

	public final static float PI_2 = (float) (Math.PI / 2.0);
	

	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Init variables
		Bundle b = getIntent().getExtras();
		String manualName = b.getString("manualName");
		
		try {
			this.XmlParser = new ManualXMLParser();
			
			String path = manualName.replaceAll("_", "") + "/" + manualName + ".xml";

//			this.XmlParser.setXMLDescription(getAssets().open("XML/ManualXMLDescription.xsd"));
			this.XmlParser.setXMLManual(getAssets().open(path));
		
			if (this.XmlParser.getCurrentFile() == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(true);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("Alert");
				builder.setMessage("The manual haven't been loaded.\n" +
						"It might that the XML is not valid.\n" +
						"Please try to update the manual and then retry");
				builder.setInverseBackgroundForced(true);
				builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				  }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			else {
				Log.v("DEBUG", this.XmlParser.getManualInfo().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ------------------------------------------------------------------------
	protected int getGUILayout() {
		Log.v("DEBUG", "getGUILayout");
		return R.layout.manualarlayout;
	};
	
	
	/**
	 * Gets called by the super-class after the GLSurface has been created. 
	 * It runs on the OpenGL-thread.
	 */
	@Override
	// ------------------------------------------------------------------------
	protected void loadUnifeyeContents() {
		try {

			// Load Tracking data
			UnifeyeDebug.log("Hello.loadTrackingData()");
			boolean success = loadTrackingData( this.mTrackingDataML3D );
			if ( !success )
			{
				UnifeyeDebug.log("Loading of the tracking data failed.");
			}
			
			// Load all geometry
			// MetaioMan
			this.mGeometryMetaioMan = this.loadGeometry("metaioman.md2");
			this.mGeometryMetaioMan.setMoveScale( new Vector3d(10,10,10) );
			this.mGeometryMetaioMan.setMoveRotation(new Vector3d(0, PI_2, 0 ));
			this.mGeometryMetaioMan.setMoveTranslation(new Vector3d(50, 75 ,2));
			this.mGeometryMetaioMan.setVisible(true);
			this.mGeometryMetaioMan.setCos(2);
		
			// Truck
			this.mGeometryTruck = this.loadGeometry("truck/truck.obj");
			this.mGeometryTruck.setMoveRotation(
					new Vector3d( (float) Math.PI / 2.0f, 0 , 0 ));
			this.mGeometryTruck.setMoveRotation(
					new Vector3d( 0, 0 , (float) Math.PI  ), true);
			this.mGeometryTruck.setVisible(true);
			this.mGeometryTruck.setCos(1);

			
		} catch (Exception e) {
			UnifeyeDebug.printStackTrace(Log.ERROR, e);
		}
	}
	
	// ------------------------------------------------------------------------
	protected String getNextTrackingDataName() {
		String newName = null; 
		return newName;
	}
	
	// ------------------------------------------------------------------------
	protected IUnifeyeMobileGeometry getNextGeometry() {
		this.CounterGeometry ++;
		if (this.CounterGeometry%2 == 0) {
			this.mCurrentGeometry = this.mGeometryMetaioMan;
		}
		else {
			this.mCurrentGeometry = this.mGeometryTruck;
		}
		Log.v("CURRENT GEO", this.mCurrentGeometry.toString());
//		mCurrentGeometry = getNextGeometryFromXML(); return a String
		return mCurrentGeometry;
	}
	
	// ------------------------------------------------------------------------
	private void showGeometry(IUnifeyeMobileGeometry newGeometry) {
		this.mGeometryMetaioMan.setVisible(newGeometry == this.mGeometryMetaioMan);
		this.mGeometryTruck.setVisible(newGeometry == this.mGeometryTruck);
	}

	// ------------------------------------------------------------------------
	public void onNextButtonClick(final View view) {
		this.XmlParser.nextStep();
//		this.showGeometry(this.XmlParser.getCurrentGeometry());
//		this.setCurrentTrackedData(this.XmlParser.getCurrentCosName());
	}
	
	// ------------------------------------------------------------------------
	public void onInfoButtonClick(final View view) {
		// TODO Give more information about what to do.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("Info");
		builder.setMessage(this.XmlParser.getCurrentStepInfo());
		builder.setInverseBackgroundForced(true);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		  }
		});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	// ------------------------------------------------------------------------
	public void onPreviousButtonClick(final View view) {
		// TODO ask the user if he really want to skip this step,
		// then do as the nextButton.
		this.XmlParser.previousStep();
//		this.showGeometry(this.XmlParser.getCurrentGeometry());
//		this.setCurrentTrackedData(this.XmlParser.getCurrentCosName());
	}
	
	@Override
	// ------------------------------------------------------------------------
	public void onDrawFrame() {
		super.onDrawFrame();
		
		PoseVector poses = mMobileSDK.getValidTrackingValues();
		if( poses.size() > 0)
		{
//			Log.v("DEBUG", "*************************************" + poses.size());
//			Log.v("DEBUG", "Sensor informatio " + this.mMobileSDK.getSensorInformation(mTrackingDataML3D));
//			Log.v("DEBUG", "Sensor Type " + this.mMobileSDK.getSensorType());
//			Log.v("DEBUG", "CosName " + poses.get(0).getCosName());
//			Log.v("DEBUG", "Cos ID " + poses.get(0).getCosID());
//			Log.v("DEBUG", "Rotation " +  poses.get(0).getRotation().toString() );
//			Log.v("DEBUG", "Translation " +  poses.get(0).getTranslation().toString() );
//			Log.v("DEBUG", "Quality " +  poses.get(0).getQuality() );
//			Log.v("DEBUG", "Additional Value " +  poses.get(0).getAdditionalValues() );
//			Log.v("DEBUG", "CPtr " +  Pose.getCPtr(poses.get(0)) );
		}
	}
}
