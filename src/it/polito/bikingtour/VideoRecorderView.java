package it.polito.bikingtour;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRecorderView extends SurfaceView 
							implements SurfaceHolder.Callback {
	private MediaRecorder recorder;
	private SurfaceHolder surfaceHolder;
	private boolean isRecording = false;
	
	public VideoRecorderView(Context context) {
		super(context);
		
		recorder = new MediaRecorder();
		recorder = new MediaRecorder();
	    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	    //TODO set output file name based on current route
	    recorder.setOutputFile("fileName.mp4");
		
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//TODO method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try{
			recorder.setPreviewDisplay(surfaceHolder.getSurface());
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
			recorder.release();
		}
	}
	
	public void startRecording(){
		if(!isRecording)
			recorder.start();
		isRecording = true;
	}
	
	public void stopRecording(){
		if(isRecording)
			recorder.stop();
		isRecording = false;
	}
	
	public boolean isRecording(){
		return this.isRecording;
	}
	
}