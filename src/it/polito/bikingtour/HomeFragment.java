package it.polito.bikingtour;

import it.polito.adapter.RouteArrayAdapter;
import it.polito.adapter.TourArrayAdapter;
import it.polito.model.RoutesContainer;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class HomeFragment extends Fragment {
	private TabHost tabHost;
	private TabSpec spec1, spec2;
	private RoutesContainer routes;
	private ToursContainer tours;
	private View rootView;
	
	public HomeFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
        tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
        tabHost.setup();
        
        spec1 = tabHost.newTabSpec("TAB 1");
        spec1.setContent(R.id.myRoutesListView);
        spec1.setIndicator("My Routes");
        
        spec2 = tabHost.newTabSpec("TAB 2");
        spec2.setContent(R.id.oldRoutesListView);
        spec2.setIndicator("Old Routes");
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        
        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
        routes = RoutesContainer.newInstance(getActivity());
        ListView myRoutes = (ListView) rootView.findViewById(R.id.myRoutesListView);
        myRoutes.setAdapter(new RouteArrayAdapter(getActivity(), routes.getRoutes()));
        
        tours = ToursContainer.newInstance(getActivity());
        ListView myTours = (ListView) rootView.findViewById(R.id.oldRoutesListView);
        myTours.setAdapter(new TourArrayAdapter(getActivity(), tours.getTours()));
	}
	
}
