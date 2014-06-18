package it.polito.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RoutesContainer extends JSONDataContainer {
	private static RoutesContainer container;
	private ArrayList<Route> routes;
	private String fileName = "routes.json";
	
	private RoutesContainer(){
		try{
			String jsonString = readJsonFromFile(fileName);
			JSONObject JsonData = new JSONObject(jsonString);
			JSONArray JsonRoutes = JsonData.getJSONArray("routes");
			this.routes = new ArrayList<Route>();
			for(int i=0; i<JsonRoutes.length(); i++){
				JSONObject JsonRoute = JsonRoutes.getJSONObject(i);
				Route r = new Route(JsonRoute);
				this.routes.add(r);
			}
		}catch(JSONException e){
			e.printStackTrace();
			Log.d("RoutesContainer", e.getMessage());
			System.exit(1);
		}
	}
	
	public static RoutesContainer newInstance(Context context){
		if(RoutesContainer.container == null){
			RoutesContainer.context = context;
			RoutesContainer.container = new RoutesContainer();
		}
		return RoutesContainer.container;
	}
	
	//TODO call this method in the onPause() method of the parent Activity
	public void save(){
		try{
			JSONObject jobject = new JSONObject();
			JSONArray jarray = new JSONArray("routes");
			for(Route r : routes){
				jarray.put(r.serializeToJson());
			}
			jobject.accumulate("routes", jarray);
			writeJsonToFile(jobject.toString(), fileName);
		}catch(JSONException e){
			Log.d("RoutesContainer", e.getMessage());
			System.exit(1);
		}
	}
	
	public void addRoute(Route r){
		this.routes.add(r);
	}
	
	public void removeRoute(int index){
		this.routes.remove(index);
	}
	
	public Route getRoute(int index){
		return this.routes.get(index);
	}
	
}
