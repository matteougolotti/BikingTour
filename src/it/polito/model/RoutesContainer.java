package it.polito.model;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RoutesContainer extends JSONDataContainer {
	private static RoutesContainer container;
	private HashMap<String, Route> routes;
	
	private RoutesContainer(){
		try{
			this.fileName = "routes.json";
			String jsonString = readJsonFromFile();
			JSONObject JsonData = new JSONObject(jsonString);
			JSONArray JsonRoutes = JsonData.getJSONArray("routes");
			this.routes = new HashMap<String, Route>();
			for(int i=0; i<JsonRoutes.length(); i++){
				JSONObject JsonRoute = JsonRoutes.getJSONObject(i);
				Route r = new Route(JsonRoute);
				this.routes.put(r.getName(),  r);
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
		//TODO saves routes to JSON file. Should be called when any change in data occurs.
	}
	
	/***
	 * TODO Add methods to add or delete routes.
	 * 		Each method should automatically call the save() method,
	 * 		to immediately save any change on persistent storage.
	 */
}
