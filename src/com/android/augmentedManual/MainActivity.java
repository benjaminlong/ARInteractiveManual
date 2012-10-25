package com.android.augmentedManual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TableRow;
import android.content.Intent;

import com.android.augmentedManual.simple.ARManualViewActivity;
import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.ndk.IUnifeyeMobileAndroid;

//------------------------------------------------------------------------
public class MainActivity extends Activity implements View.OnClickListener {

	// ------------------------------------------------------------------------
	static {     
		IUnifeyeMobileAndroid.loadNativeLibs();
	} 
	
	String[][] repertoire = new String[][]{
            {"Manual : Table Ikea FUSTA", "More Information ..."},
            {"Manual : Chaise Ikea HARISTO", "More Information ..."}};
	
	
	AssetsExtracter 				mTask;
	View 							mProgress;
	ListView 						mCatalogueView;
	List<HashMap<String, String>> 	mCatalogueMap;

	Boolean			 				beta = true;
	Button 							downloadButton = null;
	TableRow 						manual = null;
	
    @Override
 // ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        this.mCatalogueView = (ListView) findViewById(R.id.catalogueListView);
        this.mCatalogueMap = this.getCatalogueList();

        ListAdapter adapter = 
        		new SimpleAdapter(this,
        						  this.mCatalogueMap,
								  android.R.layout.simple_list_item_2,
								  new String[] {"Title", "Info"},
								  new int[] {android.R.id.text1, android.R.id.text2});
        this.mCatalogueView.setAdapter(adapter);
        this.mCatalogueView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, 
									View view, 
									int position, 
									long id) {
		    	startNewActivity(position);	
			}
        });
        
        // extract all the assets
//     	mTask = new AssetsExtracter();
//     	mTask.execute(0);
        extractAssets();

        downloadButton = (Button)this.findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(this);
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
        return true;
    }
    
	// ------------------------------------------------------------------------
	public void onClick(View view) {
    	String message = "";
    	switch (view.getId()) {
    		case R.id.downloadButton :
    			message = "Version Beta, no manual available !";
    			beta = true;
    			break;
    	}
    	
    	Toast msgT = Toast.makeText(this, message, Toast.LENGTH_SHORT);
    	msgT.show();
    }
    
	// ------------------------------------------------------------------------
	public boolean startNewActivity(int tmp) {
    	final Intent newActivity;
    	newActivity = new Intent(MainActivity.this, ARManualViewActivity.class);
    	Bundle newBundle = new Bundle();
    	newBundle.putString("manualName", this.mCatalogueMap.get(tmp).get("Title"));
    	newActivity.putExtras(newBundle);
    	startActivity(newActivity);
    	return true;
    }
    
    /**
	 * Extract all the assets.
	 * <p> If application has lots of assets, this should not be called
	 * on main thread
	 * @return true on success
	 */
	// ------------------------------------------------------------------------
	public boolean extractAssets()
	{
		try
		{
			AssetsManager.extractAllAssets(this, true);
			return true;
		}
		catch (Exception e)
		{
			UnifeyeDebug.log(Log.ERROR, "Error extracting assets: "+e.getMessage());
			return false;
		}
	}
	
	// ------------------------------------------------------------------------
	public List<String> recoverManualFromAsset() {
		List<String> manuals = new ArrayList<String>();
		// TODO
		
		
		return manuals;
	}
	
	// ------------------------------------------------------------------------
	public List<HashMap<String, String>> getCatalogueList() {
		List<HashMap<String, String>> catalogue = 
				new ArrayList<HashMap<String, String>>();
			
//		List<String> manuals = this.recoverManualFromAsset();
		List<String> manuals = new ArrayList<String>();
		manuals.add("Manual_Test");
		manuals.add("Manual_Table_Ikea_BANARA");

		HashMap<String, String> manualMap;
		for (int i = 0; i < manuals.size(); i++) {
			manualMap = new HashMap<String, String>();
			manualMap.put("Title", manuals.get(i));
			manualMap.put("Info", "More information ...");
			catalogue.add(manualMap);
		}
		
		return catalogue;
	}
	
	// ------------------------------------------------------------------------
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
    {

    	@Override
    	// --------------------------------------------------------------------
    	protected void onPreExecute() 
    	{
    		mProgress.setVisibility(View.VISIBLE);
    	}
    	
    	@Override
    	// --------------------------------------------------------------------
    	protected Boolean doInBackground(Integer... params) 
    	{
    		try 
    		{
    			AssetsManager.extractAllAssets(getApplicationContext(), true);
    		} 
    		catch (IOException e) 
    		{
    			UnifeyeDebug.printStackTrace(Log.ERROR, e);
    			return false;
    		}
    		
    		return true;
    	}
    	
    	@Override
    	// --------------------------------------------------------------------
    	protected void onPostExecute(Boolean result) 
    	{
    		mProgress.setVisibility(View.GONE);
    		
    		if (result)
    		{
    			//mWebView.loadUrl("http://192.168.10.190/WebWrapper/index.html");
    			//mWebView.loadUrl("file:///android_asset/WebWrapper/index.html");
    			UnifeyeDebug.log(Log.ERROR, "Extracting assets from web");
    		}
    		else
    		{
    			UnifeyeDebug.log(Log.ERROR, "Error extracting assets, closing the application...");
    			finish();
    		}
        }
    	
    }
}
