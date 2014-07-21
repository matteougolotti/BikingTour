package it.polito.bikingtour;

import java.io.IOException;

import it.polito.model.Tour;
import it.polito.model.ToursContainer;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRecorderView extends SurfaceView 
							implements SurfaceHolder.Callback {
	private MediaRecorder recorder;
	private Camera camera;
	private int cameraId = 0;
	private SurfaceHolder surfaceHolder;
	private boolean isRecording = false;
	private Tour currentTour;
	private String videoName;
	
	public VideoRecorderView(Context context) {
		super(context);
		init(context);
	}
	
	public VideoRecorderView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		init(context);
	}

	private void init(Context context){
		recorder = new MediaRecorder();
	    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	    this.currentTour = ToursContainer.newInstance(context).getCurrentTour();
	    this.videoName = currentTour.getNewVideoName();
	    recorder.setOutputFile(this.videoName);
		
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
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
			Log.d("VideoRecorderView.surfaceChanged", e.getMessage());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(cameraId);
		try{
			recorder.setPreviewDisplay(surfaceHolder.getSurface());
			recorder.setCamera(camera);
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		}catch(Exception e){
			Log.d("VideoRecorderView.surfaceCreated", e.getMessage());
			closeRecorder();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		closeRecorder();
	}

	private void closeRecorder(){
		if(recorder != null){
			if(isRecording)
				recorder.stop();
			else
				camera.stopPreview();
			camera.release();
			recorder.release();
		}
		currentTour.addVideo(videoName);
	}
	
	public void startRecording(){
		if(!isRecording){
			camera.stopPreview();
			camera.unlock();
			recorder.start();
		}
		isRecording = true;
	}
	
	public void stopRecording(){
		if(isRecording){
			recorder.stop();
			try{
				camera.reconnect();
			}catch(IOException e){
				Log.d("VideoRecorderView.stopRecording", e.getMessage());
			}
			camera.lock();
			camera.startPreview();
		}
		isRecording = false;
		
	}
	
	public boolean isRecording(){
		return this.isRecording;
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
