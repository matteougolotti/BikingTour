package it.polito.bikingtour;

import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
	private int pictureIndex = 0;
	
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);
	    this.toursContainer = ToursContainer.newInstance(getActivity());
	    int tourIndex = this.getArguments().getInt("tourIndex");
	    this.pictureIndex = this.getArguments().getInt("pictureId");
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
        image.setImageBitmap(toursContainer.getCurrentTour().getPicturesImages().get(pictureIndex));
        
        deleteButton = (Button) rootView.findViewById(R.id.picture_details_delete);
        
        return rootView;
    }
}
