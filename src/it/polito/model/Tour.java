package it.polito.model;

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
	private ArrayList<String> notes;
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
			
			this.notes = new ArrayList<String>();
			JSONArray jnotes = jobject.getJSONArray("notes");
			for(int i=0; i<jnotes.length(); i++){
				notes.add(jnotes.getString(i));
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
		JSONArray jVideos = new JSONArray("videos");
		for(String video : videos){
			jVideos.put(video);
		}
		jTour.accumulate("videos",  jVideos);
		JSONArray jPictures = new JSONArray("pictures");
		for(String picture : pictures){
			jPictures.put(picture);
		}
		jTour.accumulate("pictures", jPictures);
		JSONArray jNotes = new JSONArray("notes");
		for(String note : notes){
			jNotes.put(note);
		}
		jTour.accumulate("notes", jNotes);
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
	
	public ArrayList<String> getNotes(){
		return this.notes;
	}
	
	public void setRoute(Route route){
		this.route = route;
	}
	
	public void setTourDuration(long tourDurationInMillis){
		this.tourDurationInMillis = tourDurationInMillis;
	}
	
	public void setVideos(ArrayList<String> videos){
		this.videos = videos;
	}
	
	public void setPictures(ArrayList<String> pictures){
		this.pictures = pictures;
	}
	
	public void setNotes(ArrayList<String> notes){
		this.notes = notes;
	}
	
}
