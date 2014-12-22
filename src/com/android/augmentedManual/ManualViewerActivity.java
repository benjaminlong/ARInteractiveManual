package com.android.augmentedManual;

import com.android.augmentedManual.simple.ARManualViewActivity;
import com.metaio.sdk.jni.IMetaioSDKAndroid;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ManualViewerActivity extends Activity {

	String mManualName;

	// ------------------------------------------------------------------------
	static {     
		IMetaioSDKAndroid.loadNativeLibs();
	} 
		
    @Override
    // ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("DEBUG", "ManualViewerActivity::onCreate");
        setContentView(R.layout.manual_viewer_fragment);
        Intent launchingIntent = getIntent();
        String manualName = launchingIntent.getData().toString();
        ManualViewerFragment viewer = (ManualViewerFragment) getFragmentManager()
                .findFragmentById(R.id.manualView_fragment);
        viewer.updateUi(manualName);
        this.mManualName = manualName;
    }

    @Override
    // ------------------------------------------------------------------------
	public boolean onCreateOptionsMenu(Menu menu) {
    	ActionBar actionBar = getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	actionBar.setDisplayShowTitleEnabled(false);
        getMenuInflater().inflate(R.menu.activity_manual_viewer, menu);
        return true;
    }
    
	// ------------------------------------------------------------------------
	public void onButtonStartClick(View view) {
		Log.v("DEBUG", "ManualViewerActivity::onButtonStartClick start");
		if (view.getId() != R.id.manualViewerStartButton) {
			Log.v("DEBUG", "Start activity come from a wrong button");
			return;
		}
		
		Log.v("DEBUG", "ManualViewerActivity::onButtonStartClick popup message : " + this.mManualName);
		// Pop up a message
		Toast msgT = Toast.makeText(this, 
									this.mManualName + " is starting ...",
									Toast.LENGTH_SHORT);
    	msgT.show();
    	
    	// Start the activity
    	final Intent newActivity;
    	Log.v("DEBUG", "ManualViewerActivity::onButtonStartClick intent activity");
    	newActivity = new Intent(this, ARManualViewActivity.class);
    	Log.v("DEBUG", "ManualViewerActivity::onButtonStartClick set data on " + newActivity.toString());
    	newActivity.setData(Uri.parse(this.mManualName));
    	Log.v("DEBUG", "ManualViewerActivity::onButtonStartClick start new Activity");
    	startActivity(newActivity);
    	Log.v("DEBUG", "ManualViewerActivity::onButtonStartClick end");
	}
    
    @Override
	// ------------------------------------------------------------------------
	public boolean onOptionsItemSelected(MenuItem item)
	{
        switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, ManualListActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
	            				Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
	            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
	}
}
