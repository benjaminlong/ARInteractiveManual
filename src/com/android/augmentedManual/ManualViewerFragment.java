package com.android.augmentedManual;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.android.augmentedManual.utility.ManualXMLParser;

import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ManualViewerFragment extends Fragment{
	
	// Variable about the view
	private View 			rootView = null;
	private TextView 		Title;
	
	private LinearLayout	Layout;

	private TextView 		DescriptionContent;
	private ImageView		Image;
		
	private ImageView 		Icon;
	private ImageView 		NoManualIcon;
	
	private Button			StartButton;
	
	private ManualXMLParser	XmlParser = null;
	
	@Override
	// ------------------------------------------------------------------------
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.XmlParser = new ManualXMLParser();
    }
	
	@Override
	// ------------------------------------------------------------------------
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		// Around part ...
		this.rootView = inflater.inflate(
				R.layout.activity_manual_viewer, container, false);
		this.Title = (TextView)rootView.findViewById(
				R.id.manualViewerTitleTextView);
		this.Icon = (ImageView)rootView.findViewById(
				R.id.manualViewerManualIconImageView);
		this.NoManualIcon = (ImageView)rootView.findViewById(
				R.id.manualViewerNoManualImageView);
		this.StartButton = (Button)rootView.findViewById(
				R.id.manualViewerStartButton);
		// Description ...
		this.DescriptionContent = (TextView)rootView.findViewById(
				R.id.manualViewerDescriptionContentTextView);
		// Manual Image ...
		this.Image = (ImageView)rootView.findViewById(
				R.id.manualViewerImagesImage);
		// Layout ...
		this.Layout = (LinearLayout)rootView.findViewById(
				R.id.manualViewerLayout);
		
		// Shadows ...
		
		
		this.setVisibilitiesData(false);
		return this.rootView;
	}
	
	// ------------------------------------------------------------------------
	public void updateUi(String manualName) {
		Log.v("DEBUG", "Update UI, manualName : " + manualName);
		if (manualName.isEmpty()) {
			// Do Nothing
			return;
		}
		
		try {
			String path = manualName + "/" + manualName + ".xml";
			boolean result = this.XmlParser.setXMLManual(
					getActivity().getAssets().open(path));
			
			if (!result) {
				Log.v("DEBUG", "no xml set ! " + path);
				return;
			}
			
			Map<String, String> infos = this.XmlParser.getManualInfo();
			Log.v("DEBUG", infos.toString());
			
			if( !infos.containsKey("manualname") || 
				!infos.containsKey("manualicon") ||
			    !infos.containsKey("factoryname") || 
			    !infos.containsKey("factoryicon") ||
			    !infos.containsKey("description")) {
				Log.v("DEBUG", "Some of the important information are missing");
				return;
			}
			
		    if (this.rootView != null) {
		    	Log.v("DEBUG", "rootView != null");
		    	// Title
				this.Title.setText(infos.get("manualname"));
				// Logo
				Bitmap logo = this.getBitmapFromAsset(
						manualName + "/" + infos.get("manualicon"));
		        if (logo == null) {
		        	Log.v("DEBUG", "logo is null");
		        	Drawable drawable =
		        			this.getResources().getDrawable(R.drawable.no_icon);
		        	this.Icon.setImageDrawable(drawable);
		        }
		        else {
		        	logo = Bitmap.createScaledBitmap(logo, 72, 72, true);
		        	this.Icon.setImageBitmap(logo);
		        }
		        
				// Description
				this.DescriptionContent.setText(infos.get("description"));
				// Manual Image
				Bitmap image = this.getBitmapFromAsset(
						manualName + "/" + infos.get("manualimage"));
				if (image == null) {
					Log.v("DEBUG", "image is null");
		        	Drawable drawable =
		        			this.getResources().getDrawable(R.drawable.step_default);
		        	this.Image.setImageDrawable(drawable);
		        }
		        else {
		        	this.Image.setImageBitmap(image);
		        }
				
				this.setVisibilitiesData(true);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// TODO factorize the function with ListCatalogueAdapter !
	// ------------------------------------------------------------------------
	private Bitmap getBitmapFromAsset(String strName)
    {
    	Bitmap bitmap = null;
    	try {
    		AssetManager assetManager = this.getActivity().getAssets();
    		InputStream inputStream;
    		inputStream = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

        return bitmap;
    }
	
	// ------------------------------------------------------------------------
	private void setVisibilitiesData(boolean Data) {
		// Layout ...
		this.Layout.setVisibility(Data ? View.VISIBLE : View.GONE);
		
		// Around part
		this.Icon.setVisibility(Data ? View.VISIBLE : View.GONE);
		this.Title.setVisibility(Data ? View.VISIBLE : View.GONE);
		this.StartButton.setVisibility(Data ? View.VISIBLE : View.GONE);
		this.NoManualIcon.setVisibility(Data ? View.GONE : View.VISIBLE);
	}
}
