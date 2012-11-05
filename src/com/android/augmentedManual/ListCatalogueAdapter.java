package com.android.augmentedManual;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
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
    public ImageLoader imageLoader;
    
	public ListCatalogueAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_row, null);
 
        TextView title = (TextView)view.findViewById(R.id.manualTitle); // title
        TextView artist = (TextView)view.findViewById(R.id.manualInfo); // artist name
        ImageView thumb_image=(ImageView)view.findViewById(R.id.manualIcon); // thumb image
 
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
 
        // Setting all values in listview
        title.setText(song.get(MainActivity.KEY_TITLE));
        artist.setText(song.get(MainActivity.KEY_ARTIST));
        imageLoader.DisplayImage(song.get(MainActivity.KEY_THUMB_URL), thumb_image);
        return view;
	}
	
}
