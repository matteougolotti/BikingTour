package it.polito.bikingtour;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.hardware.Camera;

public class PictureFragment extends Fragment {
	CameraView cameraView;
	Camera camera;
	
	@Override
	public View onCreateView(LayoutInflater i, ViewGroup vg,
			Bundle savedInstanceState) {
		
		View v = i.inflate(R.layout.fragment_picture, vg, false);
	    cameraView = (CameraView) v.findViewById(R.id.picture_camera_preview);
	    camera = null;
	    ImageButton takePictureButton = (ImageButton) v.findViewById(R.id.take_picture_button);
	    takePictureButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				cameraView.capture();	
			}
	    });
	    ImageButton exitButton = (ImageButton) v.findViewById(R.id.close_fragment_picture);
	    exitButton.setOnClickListener(new OnClickListener(){
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
