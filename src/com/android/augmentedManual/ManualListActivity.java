package com.android.augmentedManual;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.android.augmentedManual.simple.ARManualViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IMetaioSDKAndroid;

//------------------------------------------------------------------------
public class ManualListActivity extends Activity implements
		ManualListFragment.manualSelectedListener {

	// ------------------------------------------------------------------------
	static {     
		IMetaioSDKAndroid.loadNativeLibs();
	} 
	
	Boolean			 				beta = true;
	Button 							downloadButton = null;
	
	String							mManualName;
	
    @Override
 // ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MetaioDebug.enableLogging(true);
        
        setContentView(R.layout.manual_list_fragment);
    }

	@Override
	// ------------------------------------------------------------------------
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	// ------------------------------------------------------------------------
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	// ------------------------------------------------------------------------
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        getActionBar().setDisplayShowTitleEnabled(false);
        return true;
    }
	
	@Override
	// ------------------------------------------------------------------------
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String message = "Action on Menu not implemented yet";
		Toast msgT = Toast.makeText(this, message, Toast.LENGTH_SHORT);
    	msgT.show();
	    return false;
	}
    
	// ------------------------------------------------------------------------
	public void onButtonStartClick(View view) {
		Log.v("DEBUG", "ManualListActivity::onButtonStartClick start");
		if (view.getId() != R.id.manualViewerStartButton) {
			Log.v("DEBUG", "Start activity come from a wrong button");
			return;
		}
		
		// Pop up a message
		Toast msgT = Toast.makeText(this, 
									this.mManualName + " is starting ...",
									Toast.LENGTH_SHORT);
    	msgT.show();
    	
    	// Start the activity
    	final Intent newActivity;
    	newActivity = new Intent(this, ARManualViewActivity.class);
    	newActivity.setData(Uri.parse(this.mManualName));
    	Log.v("DEBUG", "ManualListActivity::onButtonStartClick start new Activity");
    	startActivity(newActivity);
    	Log.v("DEBUG", "ManualListActivity::onButtonStartClick end");
	}
	
	// ------------------------------------------------------------------------
	public void onManualSelected(String manualName) {
	    ManualViewerFragment viewer = (ManualViewerFragment) getFragmentManager()
	            .findFragmentById(R.id.manualView_fragment);
	    if (viewer == null || !viewer.isInLayout()) {
	        Intent showContent = new Intent(getApplicationContext(),
	                ManualViewerActivity.class);
	        showContent.setData(Uri.parse(manualName));
	        startActivity(showContent);
	    } else {
	        viewer.updateUi(manualName);
	    }
	    this.mManualName = manualName;
	}
}
