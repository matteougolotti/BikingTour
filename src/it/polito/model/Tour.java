package it.polito.model;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * 
 * @author Matteo
 *	
 *	This class contains information on a single completed tour.
 *	Specifically it contains informations on the route followed 
 *	during the tour, tour date, tour duration, plus data gathered 
 *	in the tour, like pictures, videos, notes, etc.
 *	This class is the base component of the Tour Review functionality.
 *
 */

public class Tour {
	private Route route;
	private RoutesContainer routesContainer;
	private ArrayList<String> videos;
	private ArrayList<String> pictures;
	private long tourDateInMillis, tourDurationInMillis;
	
	public Tour(Route route, Context context){
		Date date = new Date();
		this.tourDateInMillis = date.getTime();
		this.route = route;
		routesContainer = RoutesContainer.newInstance(context);
	}
	
	public Tour(JSONObject jobject){
		try{
			this.route = routesContainer.getRoute(jobject.getLong("route"));
			
			this.tourDateInMillis = jobject.getLong("date");
			this.tourDurationInMillis = jobject.getLong("duration");
			
			this.videos = new ArrayList<String>();
			JSONArray jvideos = jobject.getJSONArray("videos");
			for(int i=0; i<jvideos.length(); i++){
				videos.add(jvideos.getString(i));
			}
			
			this.pictures = new ArrayList<String>();
			JSONArray jpictures = jobject.getJSONArray("pictures");
			for(int i=0; i<jpictures.length(); i++){
				pictures.add(jpictures.getString(i));
			}
		}catch(JSONException e){
			e.printStackTrace();
			Log.d("Tour", e.getMessage());
			System.exit(1);
		}
	}
	
	public JSONObject serializeToJson() throws JSONException{
		JSONObject jTour = new JSONObject();
		jTour.put("route", route.getId());
		jTour.put("date", tourDateInMillis);
		jTour.put("duration", tourDurationInMillis);
		JSONArray jVideos = new JSONArray();
		for(String video : videos){
			jVideos.put(video);
		}
		jTour.accumulate("videos",  jVideos);
		JSONArray jPictures = new JSONArray();
		for(String picture : pictures){
			jPictures.put(picture);
		}
		jTour.accumulate("pictures", jPictures);
		
		return jTour;
	}
	
	public Route getRoute(){
		return this.route;
	}
	
	public long getTourDate(){
		return this.tourDateInMillis;
	}
	
	public long getTourDuration(){
		return this.tourDurationInMillis;
	}
	
	public ArrayList<String> getVideos(){
		return this.videos;
	}
	
	public ArrayList<String> getPictures(){
		return this.pictures;
	}
	
	public void setRoute(Route route){
		this.route = route;
	}
	
	public void setTourDuration(long tourDurationInMillis){
		this.tourDurationInMillis = tourDurationInMillis;
	}
	
	public void addVideo(String videoName){
		if(this.videos == null)
			this.videos = new ArrayList<String>();
		this.videos.add(videoName);
	}
	
	public String getNewVideoName(){
		String tour = String.valueOf(this.tourDateInMillis);
		String number = String.valueOf(videos.size());
		String videoName = "tour" + tour + "vid" + number + ".mp4";
		return videoName;
	}
	
	public void addPicture(byte[] data, Context context){
		if(this.pictures == null)
			this.pictures = new ArrayList<String>();
		String picNumber = String.valueOf(this.pictures.size());
		String tourId = String.valueOf(this.tourDateInMillis);
		String picName = "tour" + tourId + "pic" + picNumber + ".png";
		FileOutputStream fos;
		try{
			fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
			fos.write(data);
			fos.close();
			this.pictures.add(picName);
		}catch(Exception e){
			Log.d("Tour.addPicture", e.getMessage());
		}
	}
	
}
