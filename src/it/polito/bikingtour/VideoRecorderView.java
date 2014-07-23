package it.polito.bikingtour;

import java.io.File;
import java.io.IOException;

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
	private String videoName;
	private ToursContainer toursContainer;
	private Context context;
	
	public VideoRecorderView(Context context) {
		super(context);
		init(context);
	}
	
	public VideoRecorderView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		init(context);
	}

	private void init(Context context){
		//recorder = new MediaRecorder();
	    //recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    //recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	    this.toursContainer = ToursContainer.newInstance(context);
		this.context = context;
	    
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
			//recorder.setPreviewDisplay(surfaceHolder.getSurface());
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		}catch(Exception e){
			Log.d("VideoRecorderView.surfaceCreated", e.getMessage());
			closeRecorder();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		closeRecorder();
	}

	private void closeRecorder(){
		if(recorder != null){
			if(isRecording){
				recorder.stop();
				recorder.reset();
			}
			else{
				camera.stopPreview();
			}
			camera.release();
			recorder.release();
		}
	}
	
	public void startRecording(){
		if(!isRecording){
			try{
				camera.stopPreview();
				camera.unlock();
		        
		        String path = context.getFilesDir().getAbsolutePath();
		        this.videoName = toursContainer.getCurrentTour().getNewVideoName();
		         
		        File file = new File(path, videoName);
		         
		        recorder = new MediaRecorder(); 

		        recorder.setCamera(camera);    
		        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);     
		        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		        recorder.setPreviewDisplay(surfaceHolder.getSurface());
		        recorder.setOutputFile(path + "/" + videoName);
		        recorder.prepare();
		        recorder.start();

			}catch(IOException e){
				Log.d("VideoRecorderView.startRecording", e.getMessage());
			}
		}
		isRecording = true;
	}
	
	public void stopRecording(){
		if(isRecording){
			recorder.stop();
			recorder.release();
			//camera.release();
			toursContainer.addVideoToCurrentTour(videoName);
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
