package it.polito.bikingtour;

import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class RouteFragment extends Fragment {

	private View rootView;
	private TabHost tabHost;
	private TabSpec specMap, specInfo;
	private ListView listPlaces;
	private TextView difficulty;
	private Route route;
	private RoutesContainer routesContainer;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);
	    routesContainer = RoutesContainer.newInstance(getActivity());
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		route = routesContainer.getRoute(savedInstanceState.getLong("route_id"));
        rootView = inflater.inflate(R.layout.fragment_route, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.tabhostinfo);
        tabHost.setup();
        
        specMap = tabHost.newTabSpec("Map");
        specMap.setContent(R.id.tabmap);
        specMap.setIndicator("Map");
        
        specInfo = tabHost.newTabSpec("Info");
        specInfo.setContent(R.id.tabinfo);
        specInfo.setIndicator("Info");
        
        tabHost.addTab(specMap);
        tabHost.addTab(specInfo);
        
        listPlaces = (ListView) rootView.findViewById(R.id.listviewPlaces);
        
        difficulty = (TextView) rootView.findViewById(R.id.textdifficulty);
        
        ImageView mapImage = (ImageView) rootView.findViewById(R.id.route_mapimage);
        mapImage.setImageBitmap(route.getMapImage(getActivity()));
        
        
        
        return rootView;
    }
	
}
