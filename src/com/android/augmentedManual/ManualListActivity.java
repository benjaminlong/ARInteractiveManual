package com.android.augmentedManual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TableRow;

import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.ndk.IUnifeyeMobileAndroid;

//------------------------------------------------------------------------
public class ManualListActivity extends Activity implements
		ManualListFragment.manualSelectedListener {

	// ------------------------------------------------------------------------
	static {     
		IUnifeyeMobileAndroid.loadNativeLibs();
	} 
	
	// XML node keys
    static final String KEY_MANUAL = "manual"; // parent node
    static final String KEY_ID = "manual_id";
    static final String KEY_TITLE = "manual_title";
    static final String KEY_ARTIST = "manual_info";
    static final String KEY_THUMB_URL = "manual_thumb_url";
	
	AssetsExtracter 				mTask;
	View 							mProgress;
	ListView 						mCatalogueView;
	ListCatalogueAdapter			mAdapter;
	ArrayList<HashMap<String, String>> mCatalogueMap = 
			new ArrayList<HashMap<String, String>>();

	Boolean			 				beta = true;
	Button 							downloadButton = null;
	TableRow 						manual = null;
	boolean							First = true;
	
    @Override
 // ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UnifeyeDebug.enableLogging(true);
        
        // extract all the assets
//        extractAssets();
        
        setContentView(R.layout.manual_list_fragment);

//        this.mCatalogueMap = this.getCatalogueList();
//
//        // Getting adapter by passing xml data ArrayList
//        this.mAdapter= new ListCatalogueAdapter(this, this.mCatalogueMap);
//        this.setListAdapter(this.mAdapter);
//        this.mCatalogueView = this.getListView();
//        this.mCatalogueView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, 
//									View view, 
//									int position, 
//									long id) {
//		    	startNewActivity(position);	
//			}
//        });
        
        // extract all the assets
//     	  mTask = new AssetsExtracter();
//     	  mTask.execute(0);
//        extractAssets();
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
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_buttonbar_bg));
        getMenuInflater().inflate(R.menu.main_activity, menu);
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
	public void onButtonDownloadClick(View view) {
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
	public void onManualSelected(String manualName) {
//		Intent newActivity = new Intent(getApplicationContext(),
//	            ManualViewerActivity.class);
//		newActivity.setData(Uri.parse(manualName));
//	    startActivity(newActivity);
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
	}
    
//	// ------------------------------------------------------------------------
//	public boolean startNewActivity(int tmp) {
//    	final Intent newActivity;
//    	newActivity = new Intent(getApplicationContext(), ManualViewerActivity.class);
////    	Bundle newBundle = new Bundle();
////    	newBundle.putString("manualName", this.mCatalogueMap.get(tmp).get(KEY_TITLE));
////    	newActivity.putExtras(newBundle);
//    	newActivity.setData(Uri.parse(this.mCatalogueMap.get(tmp).get(KEY_TITLE)));
//    	startActivity(newActivity);
//    	return true;
//    }
    
    /**
	 * Extract all the assets.
	 * <p> If application has lots of assets, this should not be called
	 * on main thread
	 * @return true on success
	 */
//	// ------------------------------------------------------------------------
//	public boolean extractAssets()
//	{
//		try
//		{
//			AssetsManager.extractAllAssets(this, true);
//			return true;
//		}
//		catch (Exception e)
//		{
//			UnifeyeDebug.log(Log.ERROR, "Error extracting assets: "+e.getMessage());
//			return false;
//		}
//	}
	
//	// ------------------------------------------------------------------------
//	public List<String> recoverManualFromAsset() {
//		List<String> manuals = new ArrayList<String>();
//		// TODO
//		
//		
//		return manuals;
//	}
	
//	// ------------------------------------------------------------------------
//	public ArrayList<HashMap<String, String>> getCatalogueList() {
//		ArrayList<HashMap<String, String>> catalogue = 
//				new ArrayList<HashMap<String, String>>();
//			
////		List<String> manuals = this.recoverManualFromAsset();
//		ArrayList<String> manuals = new ArrayList<String>();
//		manuals.add("Manual_Test");
//		manuals.add("Manual_Table_Ikea_BANARA");
//
////		NodeList nl = doc.getElementsByTagName(KEY_MANUAL);
//		for (int i = 0; i < manuals.size(); i++) {
//			
//			// creating new HashMap
//			HashMap<String, String> map = new HashMap<String, String>();
////            Element e = (Element) nl.item(i);
//			// adding each child node to HashMap key = value
////            map.put(KEY_ID, parser.getValue(e, KEY_ID));
//			map.put(KEY_TITLE, manuals.get(i));
//			map.put(KEY_ARTIST, "More Information ...");
////            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));
//			// adding HashList to ArrayList
//			catalogue.add(map);
//		}
//		
//		return catalogue;
//	}
	
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
