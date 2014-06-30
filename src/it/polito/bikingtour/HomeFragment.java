package it.polito.bikingtour;

import it.polito.adapter.RouteArrayAdapter;
import it.polito.model.RoutesContainer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class HomeFragment extends Fragment {
	public HomeFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
        TabHost tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
        tabHost.setup();
        
        TabSpec spec1 = tabHost.newTabSpec("TAB 1");
        spec1.setContent(R.id.myRoutesListView);
        spec1.setIndicator("My Routes");
        
        TabSpec spec2 = tabHost.newTabSpec("TAB 2");
        spec2.setContent(R.id.oldRoutesListView);
        spec2.setIndicator("Old Routes");
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        
        RoutesContainer routes = RoutesContainer.newInstance(getActivity());
        ListView myRoutes = (ListView) rootView.findViewById(R.id.myRoutesListView);
        myRoutes.setAdapter(new RouteArrayAdapter(getActivity(), routes.getRoutes()));
        
        return rootView;
    }
}
