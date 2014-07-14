package it.polito.bikingtour;

import it.polito.adapter.TourArrayAdapter;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TourReviewFragment extends Fragment {
	private ToursContainer tours;
	
	public TourReviewFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        
        tours = ToursContainer.newInstance(getActivity());
        ListView myTours = (ListView) rootView.findViewById(R.id.myToursListView);
        myTours.setAdapter(new TourArrayAdapter(getActivity(), tours.getTours()));
        
        return rootView;
    }
}
