package com.android.augmentedManual;

import java.io.IOException;

import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class SplashScreen extends Activity {

private static final int SPLASH_DISPLAY_TIME = 3500; /* 3 seconds */

public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

//    extractAssets();
    AssetsExtracter mTask = new AssetsExtracter();
	mTask.execute(0);
    
    new Handler().postDelayed(new Runnable() {

        public void run() {

            Intent mainIntent = new Intent(SplashScreen.this,
                    ManualListActivity.class);
            SplashScreen.this.startActivity(mainIntent);

            SplashScreen.this.finish();
            overridePendingTransition(R.anim.mainfadein,
                    R.anim.splashfadeout);
        	}
    	}, SPLASH_DISPLAY_TIME);
    
	}

	// ------------------------------------------------------------------------
	public boolean extractAssets()
	{
		try {
			AssetsManager.extractAllAssets(this, true);
			return true;
		}
		catch (Exception e) {
			UnifeyeDebug.log(Log.ERROR, "Error extracting assets: "+e.getMessage());
			return false;
		}
	}
	
	// ------------------------------------------------------------------------
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
    {

    	@Override
    	// --------------------------------------------------------------------
    	protected void onPreExecute() {
    	
    	}
    	
    	@Override
    	// --------------------------------------------------------------------
    	protected Boolean doInBackground(Integer... params) {
    		try {
    			AssetsManager.extractAllAssets(getApplicationContext(), true);
    		} 
    		catch (IOException e) {
    			UnifeyeDebug.printStackTrace(Log.ERROR, e);
    			return false;
    		}
    		return true;
    	}
    	
    	@Override
    	// --------------------------------------------------------------------
    	protected void onPostExecute(Boolean result) {
    		if (result) {
    			//mWebView.loadUrl("http://192.168.10.190/WebWrapper/index.html");
    			//mWebView.loadUrl("file:///android_asset/WebWrapper/index.html");
    			Log.v("DEBUG", "result extrqct asset OK");
    			UnifeyeDebug.log(Log.ERROR, "Extracting assets from web");
    		}
    		else {
    			Log.v("DEBUG", "result extrqct qsset false");
    			UnifeyeDebug.log(Log.ERROR, "Error extracting assets, closing the application...");
    			finish();
    		}
    	}
    }
}