package it.polito.adapter;

import java.util.ArrayList;

import it.polito.bikingtour.R;
import it.polito.model.Route;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RouteArrayAdapter extends ArrayAdapter<Route>{
	private final Context context;
	private final ArrayList<Route> values;
	
	public RouteArrayAdapter(Context context, ArrayList<Route> values) {
		super(context, R.layout.route_list_layout, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(rowView == null)
			rowView = inflater.inflate(R.layout.route_list_layout, parent, false);
		TextView routeName = (TextView) rowView.findViewById(R.id.route_name);
		routeName.setText(values.get(position).getName());
		
		return rowView;
	}
	
}
