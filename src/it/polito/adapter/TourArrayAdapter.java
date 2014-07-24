package it.polito.adapter;

import it.polito.bikingtour.R;
import it.polito.model.Tour;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(rowView == null)
			rowView = inflater.inflate(R.layout.tour_list_layout, parent, false);
		TextView routeName = (TextView) rowView.findViewById(R.id.tour_name);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HHmm");
		Date date = new Date();
		date.setTime(values.get(position).getTourDate());
        String currentDateAndTime = sdf.format(date);
		routeName.setText("Tour of " + currentDateAndTime);
		
		return rowView;
	}
	
}
