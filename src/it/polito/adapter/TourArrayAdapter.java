package it.polito.adapter;

import it.polito.bikingtour.R;
import it.polito.model.Tour;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TourArrayAdapter extends ArrayAdapter<Tour>{
	private final Context context;
	private final ArrayList<Tour> values;
	
	public TourArrayAdapter(Context context, ArrayList<Tour> values) {
		super(context, R.layout.tour_list_layout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.tour_list_layout, parent, false);
		TextView tourName = (TextView) rowView.findViewById(R.id.tour_name);
		tourName.setText(values.get(position).getRoute().getName());
		
		return rowView;
	}
	
}