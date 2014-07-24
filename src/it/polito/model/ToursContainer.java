package it.polito.model;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ToursContainer extends JSONDataContainer {
	private static ToursContainer container;
	private ArrayList<Tour> tours;
	private String fileName = "tours.json";
	private Tour currentTour = null;
	
	private ToursContainer(){
		try{
			String jsonString = readJsonFromFile(fileName);
			JSONObject JsonData = new JSONObject(jsonString);
			JSONArray JsonTours = JsonData.getJSONArray("tours");
			this.tours = new ArrayList<Tour>();
			for(int i=0; i<JsonTours.length(); i++){
				JSONObject JsonTour = JsonTours.getJSONObject(i);
				Tour t = new Tour(JsonTour, context);
				this.tours.add(t);
			}
		}catch(JSONException e){
			e.printStackTrace();
			Log.d("ToursContainer", e.getMessage());
			System.exit(1);
		}
	}
	
	public static ToursContainer newInstance(Context context){
		if(ToursContainer.container == null){
			ToursContainer.context = context;
			ToursContainer.container = new ToursContainer();
		}
		return ToursContainer.container;
	}
	
	public void CreateNewTour(Route route){
		Tour tour = new Tour(route, context);
		this.addTour(tour);
		this.save();
		this.currentTour = tour;
	}
	
	public void stopCurrentTour(){
		if(currentTour != null){
			Date date = new Date();
			currentTour.setTourDuration(date.getTime() - currentTour.getTourDate());
			currentTour = null;
			save();
		}
	}
	
	public void save(){
		try{
			JSONObject jobject = new JSONObject();
			JSONArray jarray = new JSONArray();
			for(Tour t : tours){
				jarray.put(t.serializeToJson());
			}
			jobject.accumulate("tours", jarray);
			writeJsonToFile(jobject.toString(), fileName);
		}catch(JSONException e){
			Log.d("ToursContainer", e.getMessage());
			System.exit(1);
		}
	}
	
	public void addTour(Tour t){
		this.tours.add(t);
		this.save();
	}
	
	public void removeTour(int index){
		Tour tour = tours.get(index);
		for(int i = 0; i<tour.getPictures().size(); i++){
			tour.removePicture(i);
		}
		for(int i=0; i<tour.getVideos().size(); i++){
			tour.removeVideo(i);
		}
		this.tours.remove(index);
		this.save();
	}
	
	public Tour getTour(int index){
		return this.tours.get(index);
	}
	
	public ArrayList<Tour> getTours(){
		return this.tours;
	}
	
	public Tour getCurrentTour(){
		return this.currentTour;
	}
	
	public void setCurrentTour(int index){
		if(index >= tours.size() || index < 0)
			index = 0;
		this.currentTour = tours.get(index);
	}
	
	public void addPictureToCurrentTour(byte[] data){
		if(this.currentTour != null){
			this.currentTour.addPicture(data,  context);
			this.save();
		}
	}
	
	public void removePictureFromCurrentTour(int index){
		if(currentTour != null){
			currentTour.removePicture(index);
			save();
		}
	}
	
	public void addVideoToCurrentTour(String videoName){
		if(this.currentTour != null){
			this.currentTour.addVideo(videoName);
			this.save();
		}
	}
	
	public void removeVideoFromCurrentTour(int index){
		if(currentTour != null){
			currentTour.removeVideo(index);
			save();
		}
	}
	
}
