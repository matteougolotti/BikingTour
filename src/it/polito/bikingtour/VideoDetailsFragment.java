package it.polito.bikingtour;

import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoDetailsFragment extends Fragment {
	private View rootView;
	private ToursContainer toursContainer;
	private Button deleteButton, shareButton;
	private int videoIndex;
	private int tourIndex;
	private MediaController mediaController;
	private Uri video;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);
	    this.mediaController = new MediaController(getActivity());
	    this.toursContainer = ToursContainer.newInstance(getActivity());
	    this.tourIndex = this.getArguments().getInt("tourIndex");
	    this.videoIndex = this.getArguments().getInt("videoIndex");
	    toursContainer.setCurrentTour(tourIndex);
	    String videoName = toursContainer.getCurrentTour().getVideos().get(videoIndex);
	    String path = getActivity().getFilesDir().getAbsolutePath();
	    
	    this.video = Uri.parse(path + "/" + videoName);
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_video_details, container, false);
        VideoView videoView = (VideoView) rootView.findViewById(R.id.video_details_videoview);
        videoView.setMediaController(this.mediaController);
        videoView.setVideoURI(this.video);
        
        deleteButton = (Button) rootView.findViewById(R.id.video_details_delete);
        deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//TODO write code to delete the video
				Bundle bundle = new Bundle();
				bundle.putInt("tourId", tourIndex);
				Fragment newFragment = new TourFragment();
				newFragment.setArguments(bundle);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container, newFragment);
				transaction.commit();
			}
        });
        
        shareButton = (Button) rootView.findViewById(R.id.video_details_share);
        shareButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		//TODO add code to manage video sharing
        	}
        });
        
        return rootView;
    }
}
