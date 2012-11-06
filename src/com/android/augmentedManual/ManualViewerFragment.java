package com.android.augmentedManual;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ManualViewerFragment extends Fragment{
	
	private View rootView = null;
	
	@Override
	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	// ------------------------------------------------------------------------
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
//		Intent launchingIntent = getActivity().getIntent();
//		String manualName = launchingIntent.getData().toString();
		this.rootView = inflater.inflate(R.layout.activity_manual_viewer, container, false);
//		TextView title = (TextView)rootView.findViewById(R.id.manualViewerTitle);
//		title.setText(manualName);
	    return this.rootView;
	}
	
	// ------------------------------------------------------------------------
	public void updateUi(String manualName) {
		Log.v("DEBUG", "Update UI, manualName : " + manualName);
	    if (this.rootView != null) {
	    	Log.v("DEBUG", "rootView != null");
	    	TextView title = (TextView)rootView.findViewById(R.id.manualViewerTitle);
			title.setText(manualName);
	    }
	}
}
