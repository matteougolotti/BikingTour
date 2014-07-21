package it.polito.bikingtour;

import it.polito.model.Route;
import it.polito.model.RoutesContainer;
import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class TourFragment extends Fragment{
	private View rootView;
	private TabHost tabHost;
	private TabSpec specPictures, specVideos, specInfo;
	private Tour tour;
	private ToursContainer toursContainer;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);   
	    this.toursContainer = ToursContainer.newInstance(getActivity());
	    //TODO get tour id from fragment arguments
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tour, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.tour_tabhost);
        tabHost.setup();
        
        specPictures = tabHost.newTabSpec("Pics");
        specPictures.setContent(R.id.tour_tab_pictures);
        specPictures.setIndicator("Pics");
        
        specInfo = tabHost.newTabSpec("Info");
        specInfo.setContent(R.id.tour_tab_info);
        specInfo.setIndicator("Info");
        
        specVideos = tabHost.newTabSpec("Vids");
        specVideos.setContent(R.id.tour_tab_videos);
        specVideos.setIndicator("Videos");
        
        tabHost.addTab(specPictures);
        tabHost.addTab(specVideos);
        tabHost.addTab(specInfo);
        
        //TODO complete fragment view setup
        
        return rootView;
    }
}
