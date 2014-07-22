package it.polito.bikingtour;

import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PictureDetailsFragment extends Fragment{
	private View rootView;
	private ToursContainer toursContainer;
	private Button deleteButton, shareButton;
	private int pictureIndex;
	private int tourIndex;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);
	    this.toursContainer = ToursContainer.newInstance(getActivity());
	    this.tourIndex = this.getArguments().getInt("tourIndex");
	    this.pictureIndex = this.getArguments().getInt("pictureIndex");
	    toursContainer.setCurrentTour(tourIndex);
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_picture_details, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.picture_details_image);
        
        Bitmap pictureImage = toursContainer.getCurrentTour().getPicturesImages().get(pictureIndex);
        if(pictureImage != null)
        	image.setImageBitmap(pictureImage);
        
        deleteButton = (Button) rootView.findViewById(R.id.picture_details_delete);
        deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				toursContainer.removePictureFromCurrentTour(pictureIndex);
				Bundle bundle = new Bundle();
				bundle.putInt("tourId", tourIndex);
				Fragment newFragment = new TourFragment();
				newFragment.setArguments(bundle);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.frame_container, newFragment);
				transaction.commit();
			}
        });
        
        return rootView;
    }
}
