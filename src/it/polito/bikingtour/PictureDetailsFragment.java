package it.polito.bikingtour;

import java.io.ByteArrayOutputStream;

import it.polito.model.ToursContainer;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
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
	private Bitmap pictureImage;
	
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
        
        pictureImage = toursContainer.getCurrentTour().getPicturesImages().get(pictureIndex);
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
        
        shareButton = (Button) rootView.findViewById(R.id.picture_details_share);
        shareButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		Uri imageUri = getImageUri(getActivity(), pictureImage);
        		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
        	    sharingIntent.setType("image/jpeg");
        	    sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
        	    startActivity(Intent.createChooser(sharingIntent, "Share via"));
        	}
        });
        
        return rootView;
    }
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	} 
}
