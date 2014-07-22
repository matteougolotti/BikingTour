package it.polito.bikingtour;

import it.polito.model.ToursContainer;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView 
						implements SurfaceHolder.Callback,
						Camera.PictureCallback {
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private ToursContainer toursContainer;
	private int cameraId = 0;
	
	public CameraView(Context context) {
		super(context);
		init(context);
	}
	
	public CameraView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		init(context);
	}

	private void init(Context context){
		this.toursContainer = ToursContainer.newInstance(context);
		camera = null;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		//surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(holder.getSurface() == null || camera == null)
			return;
		try{
			camera.stopPreview();
			this.setCameraDisplayOrientation();
			camera.startPreview();
		}catch(Exception e){
			Log.d("CameraView.surfaceChanged", e.getMessage());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(cameraId);
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

	public void takePicture() { 
		   camera.takePicture(null, null, this);
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera c) {
		toursContainer.addPictureToCurrentTour(data);
		camera.startPreview();
	}
	
	private void setCameraDisplayOrientation(){
	    CameraInfo info = new CameraInfo();
	    Camera.getCameraInfo(cameraId, info);
	    int rotation = info.orientation;
	    int degrees = 0;
	    switch (rotation)
	    {
	    case Surface.ROTATION_0:
	        degrees = 0;
	        break;
	    case Surface.ROTATION_90:
	        degrees = 90;
	        break;
	    case Surface.ROTATION_180:
	        degrees = 180;
	        break;
	    case Surface.ROTATION_270:
	        degrees = 270;
	        break;
	    }

	    int result;
	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
	        result = (info.orientation + degrees) % 360;
	        result = (360 - result) % 360;
	    }
	    else{
	        result = (info.orientation - degrees + 360) % 360;
	    }
	    camera.setDisplayOrientation(result);
	}
	
}
