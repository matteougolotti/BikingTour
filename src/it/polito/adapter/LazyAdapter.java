package it.polito.adapter;

import it.polito.bikingtour.R;
import it.polito.model.Location;
import it.polito.utils.ImageLoader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
	private Activity activity;
    private ArrayList<Location> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<Location> listData) {
        activity = a;
        data=listData;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.list_places_support, null);

        TextView title = (TextView) vi.findViewById(R.id.title); // title
        TextView distance = (TextView) vi.findViewById(R.id.distance); // distance 
        ImageView thumb_image=(ImageView) vi.findViewById(R.id.thumbImage); // thumb image
        
        Location location = data.get(position);
        
        // Setting all values in listview
        title.setText(location.getName());
        distance.setText("Distance: " + String.valueOf(location.getDistance()));
        imageLoader.DisplayImage(location.getUrlImage(), thumb_image);
        return vi;
    }
}
