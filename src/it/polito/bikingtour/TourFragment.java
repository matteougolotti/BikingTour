package it.polito.bikingtour;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.polito.bikingtour.R;
import it.polito.adapter.TourMediaAdapter;
import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private ToursContainer toursContainer;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);   
	    this.toursContainer = ToursContainer.newInstance(getActivity());
	    this.tour = toursContainer.getTour(this.getArguments().getInt("tourId"));
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
        
        ListView picturesListView = (ListView) rootView.findViewById(R.id.tour_pictures_listview);
        ArrayList<Bitmap> pictures = new ArrayList<Bitmap>();
        for(String picture : tour.getPictures()){
        	Bitmap newPicture = getBitmapByName(picture);
        	if(newPicture != null)
        		pictures.add(newPicture);
        }
        if(pictures.size() > 0){
        	picturesListView.setAdapter(new TourMediaAdapter(getActivity(), pictures));
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
        }else{
        	TextView videosText = new TextView(getActivity());
        	videosText.setText(getActivity().getString(R.string.no_videos));
        	LinearLayout tourTabVideos = (LinearLayout) rootView.findViewById(R.id.tour_tab_videos);
        	tourTabVideos.addView(videosText);
        }
        
        return rootView;
    }
	
	private Bitmap getBitmapByName(String fileName){
        InputStream is = null;
        Bitmap bitmap = null;
        int scaled_size = getScaledSize();
        try{
        	is = getActivity().openFileInput(fileName);
        	bitmap = BitmapFactory.decodeStream(is);
            bitmap = Bitmap.createScaledBitmap(bitmap,  (int)(scaled_size), (int)(scaled_size), false);
        }catch(IOException e){
            Log.d("TourFragment.getBitmapByName", e.getMessage());
        }
        
        return bitmap;
    }

    private int getScaledSize(){
    	DisplayMetrics metrics = new DisplayMetrics();
	    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi == DisplayMetrics.DENSITY_LOW)
            return (int)96;
        else if(metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM)
            return (int)192;
        else if(metrics.densityDpi == DisplayMetrics.DENSITY_HIGH)
            return (int)288;
        else if(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
            return (int)384;
        else if(metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH)
            return (int)576;
        return 0;
    }
	
    private Bitmap getVideoPreviewByName(String fileName){
    	Bitmap videoPreview = null;
    	try{
    		MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
    		File file = new File(fileName);
    		if(file.exists()){
    			mRetriever.setDataSource(fileName);
    			videoPreview = mRetriever.getFrameAtTime((long)1000);
    		}
    	}catch(Exception e){
    		Log.d("TourFragment.getVideoPreviewByName", e.getMessage());
    	}
    	
    	return videoPreview;
    }
    
}
