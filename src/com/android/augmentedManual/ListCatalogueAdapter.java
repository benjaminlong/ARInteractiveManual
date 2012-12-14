package com.android.augmentedManual;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListCatalogueAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    
	// ------------------------------------------------------------------------
    public ListCatalogueAdapter(
    		Activity act, ArrayList<HashMap<String, String>> d) {
        activity = act;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(
        		Context.LAYOUT_INFLATER_SERVICE);
    }

    // ------------------------------------------------------------------------
	public int getCount() {
		return data.size();
	}

	// ------------------------------------------------------------------------
	public Object getItem(int position) {
		return position;
	}

	// ------------------------------------------------------------------------
	public long getItemId(int position) {
		return position;
	}

	// ------------------------------------------------------------------------
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_row, null);
 
        // title
        TextView title = (TextView)view.findViewById(R.id.manualTitle);
        // info
        TextView info = (TextView)view.findViewById(R.id.manualInfo);
        // thumb image
        ImageView thumb_image=(ImageView)view.findViewById(R.id.manualIcon); 
 
        HashMap<String, String> manual = new HashMap<String, String>();
        manual = data.get(position);
 
        // Setting all values in listview
        // Title list
        title.setText(manual.get(ManualListFragment.KEY_TITLE).replace("_", " "));
        
        // Info List
        info.setText(manual.get(ManualListFragment.KEY_INFO));
        
        // Icon list
        Bitmap logo = getBitmapFromAsset(
        		manual.get(ManualListFragment.KEY_THUMB_URL));
        // if icon not found, we load a default icon from the resources
        if (logo == null) {
        	Drawable drawable =
        			this.activity.getResources().getDrawable(R.drawable.no_icon);
        	thumb_image.setImageDrawable(drawable);
        }
        else {
        	thumb_image.setImageBitmap(logo);
        }
 
        return view;
	}
	
	
	// ------------------------------------------------------------------------
	private Bitmap getBitmapFromAsset(String strName)
    {
    	Bitmap bitmap = null;
    	try {
    		AssetManager assetManager = this.activity.getAssets();
    		InputStream inputStream;
    		inputStream = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

        return bitmap;
    }
	
}
