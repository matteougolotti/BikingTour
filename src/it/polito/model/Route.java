package it.polito.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 
 * @author Matteo
 * 
 * This class contains all the data of a single route.
 * It contains and manages data used for route planning,
 * like origin, destination, way-points and points of interest.
 *
 */

public class Route {
	private String name;
	private long id;
	private Location origin, destination;
	private ArrayList<Location> wayPoints;
	private String difficulty;
	private int lengthInMeters; // It's better use the distance of the tour in km, because probably will be large tours. I made another constructor, with the basic informations
	private String distance; 
	private Context context;
	
	//This constructor should not be used.
	protected Route(){
	}
	
	public Route(Location origin,
			Location destination,
			ArrayList<Location> wayPoints,
			Bitmap mapImage,
			String difficulty,
			int lengthInMeters,
			Context context){
		
		this.context = context;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HHmm");
		Date date = new Date();
        String currentDateandTime = sdf.format(date);
		this.id = date.getTime();
		this.name = "Route of " + currentDateandTime;
		this.origin = origin;
		this.destination = destination;
		this.wayPoints = wayPoints;
		this.difficulty = difficulty;
		this.lengthInMeters = lengthInMeters;
		
		String file_name = new String(String.valueOf(id) + ".png");
	    File file = new File (file_name);
	    if (file.exists ()) file.delete (); 
	    try {
	           FileOutputStream out = context.openFileOutput(file_name, Context.MODE_PRIVATE);
	           mapImage.compress(Bitmap.CompressFormat.PNG, 90, out);
	           out.flush();
	           out.close();

	    } catch (Exception e) {
	           Log.d("Route.Route", e.getMessage());
	    }

	}
	
	public Route(Location origin,
			Location destination,
			ArrayList<Location> wayPoints,
			String difficulty,
			String distance,
			Context context){
		
		this.context = context;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HHmm");
		Date date = new Date();
        String currentDateandTime = sdf.format(date);
		this.id = date.getTime();
		this.name = "Route of " + currentDateandTime;
		this.origin = origin;
		this.destination = destination;
		this.wayPoints = wayPoints;
		this.difficulty = difficulty;
		this.distance = distance;
	}
	
	//Constructor used to load the object from a JSON object
	public Route(JSONObject jobject){
		try{
			this.name = jobject.getString("name");
			this.id = jobject.getLong("id");
			
			this.origin = new Location(jobject.getString("originName"));
			origin.setLat(jobject.getDouble("srcLat"));
			origin.setLon(jobject.getDouble("srcLon"));
			
			this.destination = new Location(jobject.getString("destinationName"));
			destination.setLat(jobject.getDouble("dstLat"));
			destination.setLon(jobject.getDouble("dstLon"));
			
			difficulty = jobject.getString("difficulty");
			lengthInMeters = jobject.getInt("lengthInMeters");
			
			JSONArray JsonWayPoints = jobject.getJSONArray("wayPoints");
			this.wayPoints = new ArrayList<Location>();
			for(int i=0; i<JsonWayPoints.length(); i++){
				JSONObject JsonWayPoint = JsonWayPoints.getJSONObject(i);
				Location wayPoint = new Location(
						JsonWayPoint.getDouble("lat"),
						JsonWayPoint.getDouble("lon"));
				this.wayPoints.add(wayPoint);	
			}
		}catch(JSONException e){
			Log.d("Route", "Error parsing json. " + e.getMessage());
		}
	}
	
	public JSONObject serializeToJson() throws JSONException {
		JSONObject jRoute = new JSONObject();
		jRoute.put("name", name);
		jRoute.put("id",  id);
		jRoute.put("originName", origin.getName());
		jRoute.put("srcLat", origin.getLat());
		jRoute.put("srcLon",  origin.getLon());
		jRoute.put("destinationName",  destination.getName());
		jRoute.put("dstLat",  destination.getLat());
		jRoute.put("dstLon",  destination.getLon());
		jRoute.put("difficulty", difficulty);
		jRoute.put("lengthInMeters", lengthInMeters);
		JSONArray jWayPoints = new JSONArray();
		for(Location wayPoint : wayPoints){
			JSONObject jWayPoint = new JSONObject();
			jWayPoint.put("lat", wayPoint.getLat());
			jWayPoint.put("lon",  wayPoint.getLon());
			jWayPoints.put(jWayPoint);
		}
		jRoute.accumulate("wayPoints",  jWayPoints);
		return jRoute;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setOrigin(Location origin){
		this.origin = origin;
	}
	
	public void setDestination(Location destination){
		this.destination = destination;
	}
	
	public void setDifficulty(String difficulty){
		this.difficulty = difficulty;
	}
	
	public void setLengthInMeters(int lengthInMeters){
		this.lengthInMeters = lengthInMeters;
	}
	
	public void setWayPoints(ArrayList<Location> wayPoints){
		this.wayPoints = wayPoints;
	}
	
	public void setMapImage(Bitmap mapImage){
		String file_name = new String(String.valueOf(id) + ".png");
	    File file = new File (file_name);
	    if (file.exists ()) file.delete (); 
	    try {
	           FileOutputStream out = context.openFileOutput(file_name, Context.MODE_PRIVATE);
	           mapImage.compress(Bitmap.CompressFormat.PNG, 90, out);
	           out.flush();
	           out.close();

	    } catch (Exception e) {
	           Log.d("Route.Route", e.getMessage());
	    }
	}
	
	public String getName(){
		return this.name;
	}
	
	public Location getOrigin(){
		return this.origin;
	}
	
	public Location getDestination(){
		return this.destination;
	}
	
	public String getDifficulty(){
		return this.difficulty;
	}
	
	public int getLengthInMeters(){
		return this.lengthInMeters;
	}
	
	public ArrayList<Location> getWayPoints(){
		return this.wayPoints;
	}
	
	public long getId(){
		return this.id;
	}
	
	public Bitmap getMapImage(Context context){
		String file_name = new String(String.valueOf(id) + ".png");
		InputStream is = null;
		try{
			is = context.openFileInput(file_name);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		}catch(Exception e){
			Log.e("Route.getMapImage", e.getMessage());
		}
		
		return null;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	
}
