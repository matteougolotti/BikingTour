package it.polito.bikingtour;

import java.util.ArrayList;

import it.polito.adapter.RouteArrayAdapter;
import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeFragment extends Fragment {
	private RoutesContainer routes;
	private View rootView;
	
	public HomeFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
<<<<<<< HEAD
        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
=======
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
>>>>>>> origin/master
        
        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
        routes = RoutesContainer.newInstance(getActivity());
        ListView myRoutes = (ListView) rootView.findViewById(R.id.myRoutesListView);
        ArrayList<Route> routesArray = new ArrayList<Route>();
        routesArray.addAll(routes.getRoutes());
        myRoutes.setAdapter(new RouteArrayAdapter(getActivity(), routesArray));
        
	}
	
}
