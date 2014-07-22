package it.polito.bikingtour;

import it.polito.adapter.TourArrayAdapter;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
        myTours.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Bundle bundle = new Bundle();
				bundle.putInt("tourId", position);
				Fragment newFragment = new TourFragment();
				newFragment.setArguments(bundle);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
        });
        
        return rootView;
    }
}
