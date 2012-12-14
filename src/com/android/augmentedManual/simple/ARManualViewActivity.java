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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.augmentedManual.R;
import com.android.augmentedManual.utility.ManualXMLParser;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;


//------------------------------------------------------------------------
public class ARManualViewActivity extends ARViewActivity  {
	
	// ------------------------------------------------------------------------
	static {
		IMetaioSDKAndroid.loadNativeLibs();
	}
	
	private String					mManualName;
	private List<IGeometry> 		mGeometryList;
	private List<IGeometry> 		mCurrentGeometries = null;
	
	private List<String> 			mCurrentCosIDs;
//	private String					mCurrentCosName = "";
	
	private ManualXMLParser 		XmlParser;
	private String 					mTrackingDataML3D = "";
	private TextView				mInfoView;

	public final static float PI_2 = (float) (Math.PI / 2.0);
	

	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
		Log.v("DEBUG", "ARManualViewActivity::onCreate");
		super.onCreate(savedInstanceState);
		
		// Init variables
		Intent launchingIntent = getIntent();
        this.mManualName = launchingIntent.getData().toString();
		Log.v("DEBUG", "ARManualViewActivity::onCreate manual Name : " + this.mManualName);
		
		try {
			this.XmlParser = new ManualXMLParser();
			
			String path = this.mManualName + "/" + this.mManualName + ".xml";
//			this.XmlParser.setXMLDescription(getAssets().open("XML/ManualXMLDescription.xsd"));
			this.XmlParser.setXMLManual(getAssets().open(path));
			
			String trackingData = this.XmlParser.getManualInfo().get("trackingdata");
			this.mTrackingDataML3D = this.mManualName + "/" + trackingData;
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
			
			// Init variables
			this.mCurrentCosIDs = new ArrayList<String>();
			this.mCurrentGeometries = new ArrayList<IGeometry>();
			
			Log.v("DEBUG", "INFO : " + this.XmlParser.getManualInfo().toString());
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
	}
	
	@Override
	// ------------------------------------------------------------------------
	public void onDrawFrame() {
		super.onDrawFrame();
		
		try {
			// render the the results
			mMobileSDK.render();

			// Recover all the poses tracked
			TrackingValuesVector poses = mMobileSDK.getTrackingValues();
			int size = (int) poses.size();
			if (size > 0 ) {
//				Log.v("DEBUG", "Number of poses detected : " + size);
				int cosID = poses.get(0).getCoordinateSystemID();
				
				// Check if a target has been just detected
				if( cosID!= mDetectedCosID ) {
					MetaioDebug.log( "DETECTED " +  cosID + " " + poses + " " + size);
					mDetectedCosID = cosID;
					
					if(this.mCurrentCosIDs.contains(String.valueOf(cosID))) {
//						Log.v("DEBUG", "DrawFrame : CurrentGeos name" + this.mCurrentGeometries.toString());
						// TODO Maybe something better ?!
						List<String> geometriesName = 
								this.XmlParser.getCurrentGeometries(String.valueOf(cosID));
						// Set the geometry that we have to link to this specific cosID
						this.setCoordinatesSystemIdToGeometries(
								this.getGeometriesFromName(geometriesName), cosID);
						// Set the position
						this.setGeometriesPosition(cosID);
					}
				}
			}
			else{
				// reset the detected COS if nothing has been detected 
				mDetectedCosID = -1;
			}	
		} catch (Exception e) {
		}

	}
	
	// ------------------------------------------------------------------------
	protected void onStart() {
		super.onStart();
		
		if (mGUIView != null) {
			this.mInfoView = (TextView)mGUIView.findViewById(R.id.manualActivityTopText);
			this.mInfoView.setText("Please click the next button to start the manual");
			mGUIView.findViewById(R.id.manualActivityButtonBar).setVisibility(View.GONE);
			mGUIView.findViewById(R.id.manualActivityTopText).setVisibility(View.GONE);
			mGUIView.findViewById(R.id.manualActivityLoadingProgressBar).setVisibility(View.VISIBLE);
			mGUIView.findViewById(R.id.manualActivityLoadingTextView).setVisibility(View.VISIBLE);
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
			MetaioDebug.log("Hello.loadTrackingData()");
			Log.v("DEBUG", "tracking data name : " + this.mTrackingDataML3D); 
			
			boolean success = loadTrackingData( this.mTrackingDataML3D );
			if ( !success )
			{
				MetaioDebug.log("Loading of the tracking data failed.");
			}
			
			// Load all geometry
			this.mGeometryList = new ArrayList<IGeometry>();
			this.loadGeometries(this.XmlParser.getGeometryList());
			
		} catch (Exception e) {
			MetaioDebug.printStackTrace(Log.ERROR, e);
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
	protected List<IGeometry> getCurrentGeometries() {
		return this.mCurrentGeometries;
	}
	
	// ------------------------------------------------------------------------
	protected void setCurrentGeometries(List<IGeometry> newGeometries) {
		this.mCurrentGeometries = newGeometries;
	}
	
	// ------------------------------------------------------------------------
	private void showGeometries(List<IGeometry> geometries) {
		for(int i = 0; i < geometries.size(); i++ ) {
			this.showGeometry(geometries.get(i));
		}
	}
	
	// ------------------------------------------------------------------------
	private void showGeometry(IGeometry geometry) {
		geometry.setVisible(true);
	}
	
	// ------------------------------------------------------------------------
	private void hideGeometries(List<IGeometry> geometries) {
		for(int i = 0; i < geometries.size(); i++ ) {
			this.hideGeometry(geometries.get(i));
		}
	}
	
	// ------------------------------------------------------------------------
	private void hideGeometry(IGeometry geometry) {
		geometry.setVisible(false);
	}
	
	// ------------------------------------------------------------------------
	private void setCoordinatesSystemIdToGeometries(List<IGeometry> geometries,
													int id) {
		for (int i = 0; i < geometries.size(); i++) {
			geometries.get(i).setCoordinateSystemID(id);
		}
	}
	
	// ------------------------------------------------------------------------
	private void setGeometriesPosition(int cosID) {
		Log.v("DEBUG", "SetPosition : ");
		for (int i = 0; i < this.mCurrentGeometries.size(); i++) {
			Float[] temp = new Float[3];
			temp = this.XmlParser.getRotation(this.mCurrentGeometries.get(i).getName(),
											  String.valueOf(cosID));
			this.mCurrentGeometries.get(i).setRotation(
					new Rotation(temp[0], temp[1], temp[2]));
			temp = this.XmlParser.getScale(this.mCurrentGeometries.get(i).getName(),
					  					   String.valueOf(cosID));
			this.mCurrentGeometries.get(i).setScale(
					new Vector3d(temp[0], temp[1], temp[2]));
			temp = this.XmlParser.getTranslation(this.mCurrentGeometries.get(i).getName(),
					  							 String.valueOf(cosID));
			this.mCurrentGeometries.get(i).setTranslation(
					new Vector3d(temp[0], temp[1], temp[2]));
		}
	}
	
	// ------------------------------------------------------------------------
	private List<IGeometry> getGeometriesFromName(List<String> names) {
		List<IGeometry> temp = new ArrayList<IGeometry>();
		for (int i = 0; i < names.size(); i++) {
			temp.add(this.getGeometryFromName(names.get(i)));
		}
		return temp;
	}
	
	// ------------------------------------------------------------------------
	private IGeometry getGeometryFromName(String name) {
//		Log.v("DEBUG", "::getGeometryFromName START with name = " + name);
		for (int i = 0; i < this.mGeometryList.size(); i++ ) {
			Log.v("DEBUG", "i = " + i + ", geo name = " + this.mGeometryList.get(i).getName() +", " +name );
			if (name.equalsIgnoreCase(this.mGeometryList.get(i).getName())) {
				Log.v("DEBUG", "name found, geo is : " + this.mGeometryList.get(i).getName());
				return this.mGeometryList.get(i);
			}
		}
		
		Log.v("DEBUG", "::getGeometryFromName is null");
		return null;
	}
	
	// ------------------------------------------------------------------------
	private void setCurrentContent() {
		Log.v("DEBUG", "SetCurrentContent::START");
		
		this.mDetectedCosID = -1;
		
		// Remove the previous geometry to the renderer, hide and id = 0.
		Log.v("DEBUG", "Action on previous geometries");
		if (!this.mCurrentGeometries.isEmpty()) {
			this.hideGeometries(this.mCurrentGeometries);
			this.setCoordinatesSystemIdToGeometries(this.mCurrentGeometries, 0);
			this.mCurrentGeometries.clear();
		}
		Log.v("DEBUG", "Action on previous geometries DONE");
		
		// If the manual is over
		Log.v("DEBUG", "Manual Over ?");
		if (this.XmlParser.getStepCount() == -1){
//			this.mCurrentCosID = 0;
			return;
		}
		Log.v("DEBUG", "Manual Over DONE");
		
		// We recover the current geometry name, and set it to currentGeometry
		Log.v("DEBUG", "Recover Geometries and set them");
		List<String> geometriesName = this.XmlParser.getCurrentGeometries();
		this.mCurrentGeometries.addAll(this.getGeometriesFromName(geometriesName));
		this.showGeometries(this.mCurrentGeometries);
		Log.v("DEBUG", "Geometries name : " + geometriesName.toString());
		Log.v("DEBUG", "Current Geometries : " + this.mCurrentGeometries.toString());
		
		// Current Cos ids to track
		this.mCurrentCosIDs = this.XmlParser.getCurrentCosIDs();
		Log.v("DEBUG", "Current Ids : " + this.mCurrentCosIDs.toString());
		
		// We update the top view to give information
		this.mInfoView.setText(this.XmlParser.getCurrentStepInfo());
	}
	
	// ------------------------------------------------------------------------
	private void loadGeometries(List<String> geometries) {
		
		if (geometries.isEmpty()) {
			return;
		}
		
		try {
			for (int i = 0; i < geometries.size(); i++) {
				String geometryName = geometries.get(i);
				String geometryPath =
						this.mManualName + "/Geometries/" + geometryName;
				IGeometry geometry = this.loadGeometry(geometryPath);
				geometry.setVisible(false);
				geometry.setName(geometryName);
//				geometry.(i + 1);
				this.mGeometryList.add(geometry);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
