package com.android.augmentedManual;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ManualViewerActivity extends Activity {

	String mManualName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("DEBUG", "ManualViewerActivity::onCreate");
        setContentView(R.layout.manual_viewer_fragment);
        Intent launchingIntent = getIntent();
        String manualName = launchingIntent.getData().toString();
        ManualViewerFragment viewer = (ManualViewerFragment) getFragmentManager()
                .findFragmentById(R.id.manualView_fragment);
        viewer.updateUi(manualName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	ActionBar actionBar = getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.activity_manual_viewer, menu);
        return true;
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
