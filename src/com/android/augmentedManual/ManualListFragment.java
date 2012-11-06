package com.android.augmentedManual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ManualListFragment extends ListFragment {
	
	static final String KEY_MANUAL = "manual"; // parent node
    static final String KEY_ID = "manual_id";
    static final String KEY_TITLE = "manual_title";
    static final String KEY_ARTIST = "manual_info";
    static final String KEY_THUMB_URL = "manual_thumb_url";
	
	ListCatalogueAdapter				mAdapter;
	ListView 							mCatalogueView;
	ArrayList<HashMap<String, String>> 	mCatalogueMap = 
			new ArrayList<HashMap<String, String>>();
	
	private manualSelectedListener mSelectedListener;
	
	@Override
	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    this.mCatalogueMap = this.getCatalogueList();
	    // Getting adapter by passing xml data ArrayList
        this.mAdapter= new ListCatalogueAdapter(this.getActivity(),
        										this.mCatalogueMap);
        this.setListAdapter(this.mAdapter);
	}
	
	@Override
	// ------------------------------------------------------------------------
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mSelectedListener = (manualSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement manualSelectedListener");
		}
	}
	
	// ------------------------------------------------------------------------
	public interface manualSelectedListener {
	    public void onManualSelected(String manualName);
	}
	
	@Override
	// ------------------------------------------------------------------------
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.v("DEBUG", "ManualListFragment::onListItemClick");
//	    this.startNewActivity(position);
		String manualName = this.mCatalogueMap.get(position).get(KEY_TITLE);
		mSelectedListener.onManualSelected(manualName);
	}
	
	// ------------------------------------------------------------------------
	public boolean startNewActivity(int position) {
    	final Intent newActivity;
    	newActivity = new Intent(getActivity().getApplicationContext(),
    							 ManualViewerActivity.class);
    	newActivity.setData(Uri.parse(this.mCatalogueMap.get(position).get(KEY_TITLE)));
    	startActivity(newActivity);
    	return true;
    }
	
	// ------------------------------------------------------------------------
	public List<String> recoverManualFromAsset() {
		List<String> manuals = new ArrayList<String>();
		// TODO
		
		
		return manuals;
	}
	
	// ------------------------------------------------------------------------
	public ArrayList<HashMap<String, String>> getCatalogueList() {
		ArrayList<HashMap<String, String>> catalogue = 
				new ArrayList<HashMap<String, String>>();
			
//		List<String> manuals = this.recoverManualFromAsset();
		ArrayList<String> manuals = new ArrayList<String>();
		manuals.add("Manual_Test");
		manuals.add("Manual_Table_Ikea_BANARA");

//		NodeList nl = doc.getElementsByTagName(KEY_MANUAL);
		for (int i = 0; i < manuals.size(); i++) {
			
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
//            Element e = (Element) nl.item(i);
			// adding each child node to HashMap key = value
//            map.put(KEY_ID, parser.getValue(e, KEY_ID));
			map.put(KEY_TITLE, manuals.get(i));
			map.put(KEY_ARTIST, "More Information ...");
//            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));
			// adding HashList to ArrayList
			catalogue.add(map);
		}
		
		return catalogue;
	}
}
