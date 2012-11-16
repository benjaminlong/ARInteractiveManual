package com.android.augmentedManual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ManualListFragment extends ListFragment {
	
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	static final String KEY_MANUAL 		= "manual"; // parent node
    static final String KEY_ID 			= "manual_id";
    static final String KEY_TITLE 		= "manual_title";
    static final String KEY_INFO 		= "manual_info";
    static final String KEY_THUMB_URL 	= "manual_thumb_url";
	
	ListCatalogueAdapter				mAdapter;
	ArrayList<HashMap<String, String>> 	mCatalogueMap = 
			new ArrayList<HashMap<String, String>>();
	
	private manualSelectedListener 		mSelectedListener;
	// ------------------------------------------------------------------------
	public interface manualSelectedListener {
	    public void onManualSelected(String manualName);
	}
	
	@Override
	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	
	 @Override
	 public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
	 }
	
	@Override
	// ------------------------------------------------------------------------
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		this.mCatalogueMap = this.getCatalogueList();
        this.mAdapter= new ListCatalogueAdapter(this.getActivity(),
        										this.mCatalogueMap);
        this.setListAdapter(this.mAdapter);
        this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
	
	@Override
	// ------------------------------------------------------------------------
	public void onListItemClick(ListView list, View view, int position, long id) {
		String manualName = this.mCatalogueMap.get(position).get(KEY_TITLE);
		this.mActivatedPosition = position;
		mSelectedListener.onManualSelected(manualName);
	}
	
    @Override
    // ------------------------------------------------------------------------
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }
    
	// ------------------------------------------------------------------------
	public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
	
	// ------------------------------------------------------------------------
	private List<String> recoverManualFromAsset() {
		// TODO Factorize the function
		List<String> manuals = new ArrayList<String>();

		try {
			AssetManager assetManager = getActivity().getAssets();
			String[] rootList = assetManager.list("");
			// Recover data at the asset root
			for (int i = 0 ; i < rootList.length ; i++) {
				// if Start by "Manual" we might have a manual folder
				if (rootList[i].startsWith("Manual")) {
					// We check that the folder contain the xml
					String[] subList = assetManager.list(rootList[i]);
					for (int j = 0 ; j < subList.length ; j++) {
						// If yes, we add it to the manual list
						if (subList[j].equals(rootList[i] + ".xml")) {
							manuals.add(rootList[i]);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return manuals;
	}
	
	// ------------------------------------------------------------------------
	private ArrayList<HashMap<String, String>> getCatalogueList() {
		ArrayList<HashMap<String, String>> catalogue = 
				new ArrayList<HashMap<String, String>>();
			
		List<String> manuals = this.recoverManualFromAsset();
		Log.v("DEBUG", "manuals found : " + manuals.size() + manuals.toString());

//		NodeList nl = doc.getElementsByTagName(KEY_MANUAL);
		for (int i = 0; i < manuals.size(); i++) {
			
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
//            Element e = (Element) nl.item(i);
			// adding each child node to HashMap key = value
//            map.put(KEY_ID, parser.getValue(e, KEY_ID));
			map.put(KEY_TITLE, manuals.get(i));
			map.put(KEY_INFO, "More Information ...");
			map.put(KEY_THUMB_URL, manuals.get(i) + "/" + manuals.get(i) + ".png");
			// adding HashList to ArrayList
			catalogue.add(map);
		}
		
		return catalogue;
	}
}
