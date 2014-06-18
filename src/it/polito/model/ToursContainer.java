package it.polito.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ToursContainer extends JSONDataContainer {
	private static ToursContainer container;
	private ArrayList<Tour> tours;
	private String fileName = "tours.json";
	
	private ToursContainer(){
		try{
			String jsonString = readJsonFromFile(fileName);
			JSONObject JsonData = new JSONObject(jsonString);
			JSONArray JsonTours = JsonData.getJSONArray("tours");
			this.tours = new ArrayList<Tour>();
			for(int i=0; i<JsonTours.length(); i++){
				JSONObject JsonTour = JsonTours.getJSONObject(i);
				Tour t = new Tour(JsonTour);
				this.tours.add(t);
			}
		}catch(JSONException e){
			e.printStackTrace();
			Log.d("ToursContainer", e.getMessage());
			System.exit(1);
		}
	}
	
	public ToursContainer newInstance(Context context){
		if(ToursContainer.container == null){
			ToursContainer.context = context;
			ToursContainer.container = new ToursContainer();
		}
		return ToursContainer.container;
	}
	
	public void save(){
		try{
			JSONObject jobject = new JSONObject();
			JSONArray jarray = new JSONArray("tours");
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
	}
	
	public void removeTour(int index){
		this.tours.remove(index);
	}
	
	public Tour getTour(int index){
		return this.tours.get(index);
	}
	
}
