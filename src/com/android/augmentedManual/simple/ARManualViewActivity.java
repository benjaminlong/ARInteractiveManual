/**
 * ARViewActivitySimple.java
 * Example SDK Internal
 *
 * Created by Arsalan Malik on 08.03.2011
 * Copyright 2011 metaio GmbH. All rights reserved.
 *
 */

package com.android.augmentedManual.simple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


//------------------------------------------------------------------------
public class ARManualViewActivity extends ARViewActivity  {
	
	// ------------------------------------------------------------------------
	static {
		IUnifeyeMobileAndroid.loadNativeLibs();
	}
	
	List<IUnifeyeMobileGeometry> 	mGeometryList;
	private IUnifeyeMobileGeometry 	mCurrentGeometry = null;
	private int 					mCurrentCosID = 0;
	private ManualXMLParser 		XmlParser;
	private String 					mTrackingDataML3D = "";

	public final static float PI_2 = (float) (Math.PI / 2.0);
	

	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Init variables
		Bundle b = getIntent().getExtras();
		String manualName = b.getString("manualName");
		
		try {
			this.XmlParser = new ManualXMLParser();
			
			String path = 
					manualName.replaceAll("_", "") + "/" + manualName + ".xml";
//			this.XmlParser.setXMLDescription(getAssets().open("XML/ManualXMLDescription.xsd"));
			this.XmlParser.setXMLManual(getAssets().open(path));
			
			String trackingData = this.XmlParser.getManualInfo().get("trackingdata");
			this.mTrackingDataML3D = 
					manualName.replaceAll("_", "") + "/" + trackingData;
			Log.v("DEBUG", "tracking data path : " + this.mTrackingDataML3D);
		
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
	public void onNextButtonClick(final View view) {
		this.XmlParser.nextStep();
		this.setCurrentContent();
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
		this.XmlParser.previousStep();
		this.setCurrentContent();
//		this.showGeometry(this.XmlParser.getCurrentGeometry());
//		this.setCurrentTrackedData(this.XmlParser.getCurrentCosName());
		// Remove the previous geometry to the renderer
	}
	
	@Override
	// ------------------------------------------------------------------------
	public void onDrawFrame() {
		super.onDrawFrame();
		
		PoseVector poses = mMobileSDK.getValidTrackingValues();
		if( poses.size() > 0)
		{
			// TODO
		}
	}
	
	// ------------------------------------------------------------------------
	protected void onStart() {
		super.onStart();
		
		if (mGUIView != null) {
			mGUIView.findViewById(R.id.buttonBar).setVisibility(View.GONE);
			mGUIView.findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
			mGUIView.findViewById(R.id.loadingTextView).setVisibility(View.VISIBLE);
		}
	}
	
//	// ------------------------------------------------------------------------
//	public void onSurfaceCreated() {
//		super.onSurfaceChanged();
//		// If GUI view is inflated, add it
//		this.runOnUiThread(new Runnable() {
//			public void run() {
//				if (mGUIView != null) 
//					mGUIView.findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
//					mGUIView.findViewById(R.id.loadingTextView	).setVisibility(View.GONE);
//					mGUIView.findViewById(R.id.buttonBar).setVisibility(View.VISIBLE);
//				}
//		});
//	}
	
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
			Log.v("DEBUG", "geometries :" + this.XmlParser.getGeometryList().toString());
			this.mGeometryList = new ArrayList<IUnifeyeMobileGeometry>();
			this.loadGeometries(this.XmlParser.getGeometryList());
			
		} catch (Exception e) {
			UnifeyeDebug.printStackTrace(Log.ERROR, e);
			Log.v("VISIBILITY", "tracking data fail exception");
		}
	}
	
	// ------------------------------------------------------------------------
	protected String getCurrentTrackingDataName() {
		String newName = null; 
		return newName;
	}
	
	// ------------------------------------------------------------------------
	protected void setCurrentTrackingDataName(String name) {
		
	}
	
	// ------------------------------------------------------------------------
	protected IUnifeyeMobileGeometry getCurrentGeometry() {
		return mCurrentGeometry;
	}
	
	// ------------------------------------------------------------------------
	protected void setCurrentGeometry(IUnifeyeMobileGeometry newGeometry) {
		Log.v("DEBUG", "::setCurrentGeometry START");
		this.mCurrentGeometry= newGeometry ;
	}
	
	// ------------------------------------------------------------------------
	private void showGeometry(IUnifeyeMobileGeometry newGeometry) {
		Log.v("DEBUG", "::showGeometry START");
		for (int i = 0; i < this.mGeometryList.size(); i++ ) {
			if (newGeometry == this.mGeometryList.get(i)) {
				this.mGeometryList.get(i).setVisible(true);
				continue;
			}
			this.mGeometryList.get(i).setVisible(false);
		}
	}
	
	// ------------------------------------------------------------------------
	private IUnifeyeMobileGeometry getGeometryFromName(String name) {
		Log.v("DEBUG", "::getGeometryFromName START with name = " + name);
		for (int i = 0; i < this.mGeometryList.size(); i++ ) {
			Log.v("DEBUG", "i = " + i + ", geo name = " + this.mGeometryList.get(i).getName() +", " +name );
			if (name.equalsIgnoreCase(this.mGeometryList.get(i).getName())) {
				Log.v("DEBUG", "name found, geo is : " + this.mGeometryList.get(i).toString());
				return this.mGeometryList.get(i);
			}
		}
		
		Log.v("DEBUG", "::getGeometryFromName is null");
		return null;
	}
	
	// ------------------------------------------------------------------------
	private void setCurrentContent() {
		// Remove the previous geometry to the renderer
		if (this.mCurrentGeometry != null) {
			this.mCurrentGeometry.setCos(0);
			this.mCurrentGeometry.setVisible(false);
		}
		
		if (this.XmlParser.getStepCount() == -1){
			this.mCurrentCosID = 0;
			this.setCurrentGeometry(null);
			return;
		}
		
		String geometryName = this.XmlParser.getCurrentGeometry();
		Log.v("DEBUG", "new geometry name :" + geometryName);
		this.setCurrentGeometry(this.getGeometryFromName(geometryName));
		
//				this.setCurrentTrackedData(this.XmlParser.getCurrentCosName());
		// If no current geo, we can't set the ID !
		if (this.mCurrentGeometry != null) {
			Log.v("DEBUG", "current geometry is : " + this.mCurrentGeometry.getName());
			int id = Integer.parseInt(this.XmlParser.getCurrentCosID());
			this.mCurrentGeometry.setCos(id);
		}
		
		this.showGeometry(this.mCurrentGeometry);
	}
	
	// ------------------------------------------------------------------------
	private void loadGeometries(List<String> geometries) {
		
		if (geometries.isEmpty()) {
			return;
		}
		
		try {
			for (int i = 0; i < geometries.size(); i++) {
				String geometryName = geometries.get(i);
				IUnifeyeMobileGeometry geometry = this.loadGeometry(geometryName);
				geometry.setVisible(false);
				geometry.setName(geometryName);
//				geometry.setCos(i + 1);
				this.mGeometryList.add(geometry);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
