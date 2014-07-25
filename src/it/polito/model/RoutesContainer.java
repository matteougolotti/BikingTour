package it.polito.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class RoutesContainer extends JSONDataContainer {
	private static RoutesContainer container;
	private HashMap<String, Route> routes;
	private String fileName = "routes.json";

	private RoutesContainer(){
		try{
			String jsonString = readJsonFromFile(fileName);
			JSONObject JsonData = new JSONObject(jsonString);
			JSONArray JsonRoutes = JsonData.getJSONArray("routes");
			this.routes = new HashMap<String, Route>();
			for(int i=0; i<JsonRoutes.length(); i++){
				JSONObject JsonRoute = JsonRoutes.getJSONObject(i);
				Route r = new Route(JsonRoute);
				this.routes.put(String.valueOf(r.getId()), r);
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

	public void CreateNewRoute(Location origin,
			Location destination,
			ArrayList<Location> wayPoints,
			Bitmap mapImage,
			String difficulty,
			int lengthInMeters){
		Route route = new Route(origin,
							destination,
							wayPoints,
							mapImage,
							difficulty,
							lengthInMeters,
							RoutesContainer.context);
		this.addRoute(route);
	}

	public void save(){
		try{
			JSONObject jobject = new JSONObject();
			JSONArray jarray = new JSONArray();
			for(Route r : routes.values()){
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
		this.routes.put(String.valueOf(r.getId()), r);
		this.save();
	}

	public void removeRoute(long id){
		File file = new File(String.valueOf(id) + ".png");
		if(file.exists())
		  file.delete();
		this.routes.remove(String.valueOf(id));
		this.save();
	}

	public Route getRoute(long id){
		return this.routes.get(String.valueOf(id));
	}

	public Collection<Route> getRoutes(){
		return this.routes.values();
	}

}