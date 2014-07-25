package it.polito.bikingtour;

import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class RouteFragment extends Fragment {

	private View rootView;
	private TabHost tabHost;
	private TabSpec specMap, specInfo;
	private TextView origin, destination, length, difficulty;
	private Route route;
	private RoutesContainer routesContainer;
	private Button startButton, deleteButton, exitButton;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);   
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		routesContainer = RoutesContainer.newInstance(getActivity());
	    route = routesContainer.getRoute(this.getArguments().getLong("routeId"));
        rootView = inflater.inflate(R.layout.fragment_route, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.route_tabhost);
        tabHost.setup();
        
        specMap = tabHost.newTabSpec("Map");
        specMap.setContent(R.id.route_tabmap);
        specMap.setIndicator("Map");
        
        specInfo = tabHost.newTabSpec("Info");
        specInfo.setContent(R.id.route_tabinfo);
        specInfo.setIndicator("Info");
        
        tabHost.addTab(specMap);
        tabHost.addTab(specInfo);
        
        difficulty = (TextView) rootView.findViewById(R.id.route_textdifficulty);
        difficulty.append(route.getDifficulty());
        
        origin = (TextView) rootView.findViewById(R.id.route_textorigin);
        origin.append(route.getOrigin().getName());
        
        destination = (TextView) rootView.findViewById(R.id.route_textdestination);
        destination.append(route.getDestination().getName());
        
        //length = (TextView) rootView.findViewById(R.id.route_textlength);
        //length.append(String.valueOf(route.getLengthInMeters()) + "m");
        
        ImageView mapImage = (ImageView) rootView.findViewById(R.id.route_mapimage);
        Bitmap mapBitmap = route.getMapImage(getActivity());
        if(mapBitmap != null)
        	mapImage.setImageBitmap(route.getMapImage(getActivity()));
        
        startButton = (Button) rootView.findViewById(R.id.route_buttonStart);
        startButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ToursContainer toursContainer = ToursContainer.newInstance(getActivity());
				if(toursContainer.getCurrentTour() != null)
					toursContainer.stopCurrentTour();
				toursContainer.CreateNewTour(route);
				Fragment newFragment = new NavigationFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
        });
        
        deleteButton = (Button) rootView.findViewById(R.id.route_buttonDelete);
        deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				routesContainer.removeRoute(route.getId());
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new HomeFragment()).commit();
			}
        });
        
        exitButton = (Button) rootView.findViewById(R.id.route_buttonExit);
        exitButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new HomeFragment()).commit();
			}
        });
        
        return rootView;
    }
	
}
