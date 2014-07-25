package it.polito.bikingtour;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import it.polito.bikingtour.R;
import it.polito.adapter.TourMediaAdapter;
import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class TourFragment extends Fragment{
	private View rootView;
	private TabHost tabHost;
	private TabSpec specPictures, specVideos, specInfo;
	private Tour tour;
	private int tourIndex;
	private ToursContainer toursContainer;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);   
	    this.toursContainer = ToursContainer.newInstance(getActivity());
	    this.tourIndex = this.getArguments().getInt("tourId");
	    this.tour = toursContainer.getTour(this.tourIndex);
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}
	
	@SuppressLint("SimpleDateFormat")
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
        
        ListView picturesListView = (ListView) rootView.findViewById(R.id.tour_pictures_listview);
        ArrayList<Bitmap> pictures = tour.getPicturesImages();
        if(pictures.size() > 0){
        	picturesListView.setAdapter(new TourMediaAdapter(getActivity(), pictures));
        	picturesListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Bundle bundle = new Bundle();
					bundle.putInt("tourIndex", tourIndex);
					bundle.putInt("pictureIndex", position);
					Fragment newFragment = new PictureDetailsFragment();
					newFragment.setArguments(bundle);
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.frame_container, newFragment);
					transaction.addToBackStack(null);
					transaction.commit();	
				}
        	});
        }else{
        	TextView picturesText = new TextView(getActivity());
        	picturesText.setText(getActivity().getString(R.string.no_pictures));
        	LinearLayout tourTabPictures = (LinearLayout) rootView.findViewById(R.id.tour_tab_pictures);
        	tourTabPictures.addView(picturesText);
        }
        
        ListView videosListView = (ListView) rootView.findViewById(R.id.tour_videos_listview);
        ArrayList<Bitmap> videos = new ArrayList<Bitmap>();
        for(String video : tour.getVideos()){
        	Bitmap newVideo = getVideoPreviewByName(video);
        	if(newVideo != null)
        		videos.add(newVideo);
        }
        if(videos.size() > 0){
        	videosListView.setAdapter(new TourMediaAdapter(getActivity(), videos));
        	videosListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Bundle bundle = new Bundle();
					bundle.putInt("tourIndex", tourIndex);
					bundle.putInt("videoIndex", position);
					Fragment newFragment = new VideoDetailsFragment();
					newFragment.setArguments(bundle);
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.frame_container, newFragment);
					transaction.addToBackStack(null);
					transaction.commit();	
				}
        	});
        }else{
        	TextView videosText = new TextView(getActivity());
        	videosText.setText(getActivity().getString(R.string.no_videos));
        	LinearLayout tourTabVideos = (LinearLayout) rootView.findViewById(R.id.tour_tab_videos);
        	tourTabVideos.addView(videosText);
        }
        
        Button deleteButton = (Button) rootView.findViewById(R.id.tour_delete_button);
        deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				toursContainer.removeTour(tourIndex);	
				FragmentManager fragmentManager = getFragmentManager();
				TourReviewFragment fragment = new TourReviewFragment();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}
        });
        
        TextView origin = (TextView) rootView.findViewById(R.id.tour_origin);
        origin.setText("Origin: " + toursContainer.getTour(tourIndex).getRoute().getOrigin().getName());
        
        TextView destination = (TextView) rootView.findViewById(R.id.tour_destination);
        destination.setText("Destination: " + toursContainer.getTour(tourIndex).getRoute().getDestination().getName());
        
        //TextView length = (TextView) rootView.findViewById(R.id.tour_length);
        //length.setText("Length of the tour: " + toursContainer.getTour(tourIndex).getRoute().getDistance());
        
        TextView date = (TextView) rootView.findViewById(R.id.tour_date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
        date.setText("Date of the tour: " + formatter.format(new Date(toursContainer.getTour(tourIndex).getTourDate())));
        
        long millis = toursContainer.getTour(tourIndex).getTourDuration();
        String durationString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        TextView duration = (TextView) rootView.findViewById(R.id.tour_duration);
        duration.setText("Duration of the tour: " + durationString);
        
        return rootView;
    }
	
    private Bitmap getVideoPreviewByName(String fileName){
    	Bitmap videoPreview = null;
    	//String path = getActivity().getFilesDir().getAbsolutePath();
    	File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    	String path = sdCard.getAbsolutePath();
    	
    	try{
    		MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
    		File file = new File(path + "/" + fileName);
    		if(file.exists()){
    			mRetriever.setDataSource(path + "/" + fileName);
    			videoPreview = mRetriever.getFrameAtTime((long)1000);
    		}
    	}catch(Exception e){
    		Log.d("TourFragment.getVideoPreviewByName", e.getMessage());
    	}
    	
    	return videoPreview;
    }
    
}
