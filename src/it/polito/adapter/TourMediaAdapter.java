package it.polito.adapter;

import it.polito.bikingtour.R;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class TourMediaAdapter extends ArrayAdapter<Bitmap> {
	private Context context;
	private ArrayList<Bitmap> objects;
	
	public TourMediaAdapter(Context context, ArrayList<Bitmap> objects) {
		super(context, R.layout.list_tour_media, objects);
		this.objects = objects;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(rowView == null)
			rowView = inflater.inflate(R.layout.list_tour_media, parent, false);
		ImageView image = (ImageView) rowView.findViewById(R.id.tour_media);
		image.setImageBitmap(objects.get(position));
		
		return rowView;
	}

}
