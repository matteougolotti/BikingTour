package it.polito.bikingtour;

import it.polito.model.Tour;
import it.polito.model.ToursContainer;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView 
						implements SurfaceHolder.Callback,
						Camera.PictureCallback {
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private Context context;
	private Tour currentTour;
	
	
	public CameraView(Context context) {
		super(context);
		this.currentTour = ToursContainer.newInstance(context).getCurrentTour();
		this.context = context;
		camera = null;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		//TODO this method is deprecated, but maybe we need it (???!!!)
		//surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(holder.getSurface() == null || camera == null)
			return;
		try{
			camera.stopPreview();
			camera.startPreview();
		}catch(Exception e){
			Log.d("CameraView.surfaceChanged", e.getMessage());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		try{
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		}catch(Exception e){
			Log.d("CameraView.surfaceCreated", e.getMessage());
			closeCamera();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		closeCamera();
	}

	private void closeCamera(){
		if(camera != null){
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public  boolean capture() { 
		   if (camera != null) {
		      camera.takePicture(null, null, this);
		      return true; 
		   } else {
		      return false;
		   }
		}

	
	@Override
	public void onPictureTaken(byte[] data, Camera c) {
		currentTour.addPicture(data,  context);
	}
	
}
