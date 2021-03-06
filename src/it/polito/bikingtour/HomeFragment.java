package it.polito.bikingtour;

import java.util.ArrayList;

import it.polito.adapter.RouteArrayAdapter;
import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HomeFragment extends Fragment {
	private RoutesContainer routes;
	private View rootView;
	private ListView myRoutes;
	private ArrayList<Route> routesArray;
	
	public HomeFragment() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        myRoutes = (ListView) rootView.findViewById(R.id.myRoutesListView);
        routesArray = new ArrayList<Route>();
        
        routes = RoutesContainer.newInstance(getActivity());
        routesArray.addAll(routes.getRoutes());
		
		if (!routesArray.isEmpty()) {
			myRoutes.setAdapter(new RouteArrayAdapter(getActivity(), routesArray));
	        myRoutes.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					ArrayList<Route> routesArray = new ArrayList<Route>();
			        routesArray.addAll(routes.getRoutes());
			        long routeId = routesArray.get(position).getId();
			        Bundle bundle = new Bundle();
					bundle.putLong("routeId", routeId);
					Fragment newFragment = new RouteFragment();
					newFragment.setArguments(bundle);
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.frame_container, newFragment);
					transaction.addToBackStack(null);
					transaction.commit();
				}
	        	
	        });
		}
        return rootView;
    }
	
	public void addRouteOnListView(Route route) {
		routesArray.add(route);
	}
	
}
