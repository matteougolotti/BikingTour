package it.polito.bikingtour;

import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class RouteFragment extends Fragment implements OnClickListener {

	private View rootView;
	private TabHost tabHost;
	private TabSpec specMap, specInfo;
	private TextView origin, destination, length, difficulty;
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
        specInfo.setContent(R.id.route_tabinfo);
        specInfo.setIndicator("Info");
        
        tabHost.addTab(specMap);
        tabHost.addTab(specInfo);
        
        difficulty = (TextView) rootView.findViewById(R.id.textdifficulty);
        difficulty.append(route.getDifficulty());
        
        origin = (TextView) rootView.findViewById(R.id.route_textorigin);
        origin.append(route.getOrigin().getName());
        
        destination = (TextView) rootView.findViewById(R.id.route_textdestination);
        destination.append(route.getDestination().getName());
        
        length = (TextView) rootView.findViewById(R.id.route_textlength);
        length.append(String.valueOf(route.getLengthInMeters()) + "m");
        
        ImageView mapImage = (ImageView) rootView.findViewById(R.id.route_mapimage);
        mapImage.setImageBitmap(route.getMapImage(getActivity()));
        
        return rootView;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonStart:
			start();
		case R.id.buttonDelete:
			delete();
		case R.id.buttonExit:
			exit();
		}

	}
	
	private void start(){
		
	}
		
	private void delete(){
		FragmentManager fragmentManager = getFragmentManager();
		routesContainer.removeRoute(route.getId());
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, new HomeFragment()).commit();
	}
	
	private void exit(){
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, new HomeFragment()).commit();
	}
	
}
