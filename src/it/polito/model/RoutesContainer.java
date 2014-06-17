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
	
	private RoutesContainer(){
		try{
			this.fileName = "routes.json";
			String jsonString = readJsonFromFile();
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
			Log.d("RoutesContainer", "Error parsing json data. " + e.getMessage());
			System.exit(1);
		}
	}
	
	public RoutesContainer newInstance(Context context){
		if(RoutesContainer.container == null){
			this.context = context;
			RoutesContainer.container = new RoutesContainer();
		}
		return RoutesContainer.container;
	}
	
	private void save(){
		try{
			JSONObject jobject = new JSONObject();
			JSONArray jarray = new JSONArray("routes");
			for(Route r : routes){
				jarray.put(r.serializeToJson());
			}
			jobject.accumulate("routes", jarray);
			writeJsonToFile(jobject.toString());
		}catch(JSONException e){
			Log.d("RoutesContainer", "Error saving to Json file. " + e.getMessage());
			System.exit(1);
		}
	}
	
	/***
	 * TODO Add methods to add or delete routes.
	 * 		Each method should automatically call the save() method,
	 * 		to immediately save any change on persistent storage.
	 */
}
