package it.polito.bikingtour;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatisticsFragment extends Fragment {
	private View rootView;
	private TextView numberOfTours, totalDistanceCovered, 
			bikingTime, averageSpeed, longestTour;
	private ToursContainer toursContainer;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.toursContainer = ToursContainer.newInstance(getActivity());
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
		
		numberOfTours = (TextView) rootView.findViewById(R.id.statistics_number_of_tours);
		String toursNumber = " " + Integer.toString(toursContainer.getTours().size());
		numberOfTours.append(toursNumber);
		
		totalDistanceCovered = (TextView) rootView.findViewById(R.id.statistics_total_distance_covered);
		Tour t;
		double distanceCovered = 0.0;
		int longestTourId = 0;
		
		for(int i=0; i<toursContainer.getTours().size(); i++){
			t = toursContainer.getTour(i);
			distanceCovered += t.getTourLength();
			if(t.getTourLength() > toursContainer.getTour(longestTourId).getTourLength())
				longestTourId = i;
		}
		totalDistanceCovered.append(" " + String.valueOf(distanceCovered));
		
		bikingTime = (TextView) rootView.findViewById(R.id.statistics_biking_time);
		long time = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		for(int i=0; i<toursContainer.getTours().size(); i++){
			t = toursContainer.getTour(i);
			time += t.getTourDuration();
		}
		date.setTime(time);
		String totalTime = sdf.format(date);
		bikingTime.append(" " + totalTime);
		
		averageSpeed = (TextView) rootView.findViewById(R.id.statistics_average_speed);
		double averageSpeedValue = distanceCovered / time;
		averageSpeed.append(" " + String.valueOf(averageSpeedValue));
		
		longestTour = (TextView) rootView.findViewById(R.id.statistics_longest_tour);
		longestTour.append(" " + String.valueOf(toursContainer.getTour(longestTourId).getTourLength()));
		
		return rootView;
	}
	
}
