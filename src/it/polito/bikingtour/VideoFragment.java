package it.polito.bikingtour;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class VideoFragment extends Fragment{
	private VideoRecorderView recorderView;
	
	@Override
	public View onCreateView(LayoutInflater i, ViewGroup vg,
			Bundle savedInstanceState) {
		
		View v = i.inflate(R.layout.fragment_video, vg, false);
	    recorderView = (VideoRecorderView) v.findViewById(R.id.video_camera_preview);
	    ImageButton recordVideoButton = (ImageButton) v.findViewById(R.id.record_video_button);
	    recordVideoButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.record_video_button){
					if(recorderView.isRecording()){
						recorderView.startRecording();
					}else{
						recorderView.stopRecording();
					}
				}
			}
	    });
	    
	    ImageButton closeVideoFragmentButton = (ImageButton) v.findViewById(R.id.close_fragment_video);
	    closeVideoFragmentButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Fragment newFragment = new NavigationFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
	    });
	    
	    return v;
	}

}
