package it.polito.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
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
	private ArrayList<Location> pointsOfInterest;
	private String difficulty;
	private int lengthInMeters;
	
	//This constructor should not be used.
	protected Route(){
	}
	
	//Default constructor used to create a new route
	public Route(String name, 
			Location origin,
			Location destination,
			ArrayList<Location> wayPoints,
			ArrayList<Location> pointsOfInterest,
			Bitmap mapImage,
			String difficulty,
			int lengthInMeters,
			Context context){
		
		this.id = Calendar.getInstance().getTimeInMillis();
		this.name = name;
		this.origin = origin;
		this.destination = destination;
		this.wayPoints = wayPoints;
		this.difficulty = difficulty;
		this.lengthInMeters = lengthInMeters;
		this.pointsOfInterest = pointsOfInterest;
		
		String file_name = new String(String.valueOf(id) + ".png");
	    File file = new File (file_name);
	    if (file.exists ()) file.delete (); 
	    try {
	           FileOutputStream out = new FileOutputStream(file);
	           mapImage.compress(Bitmap.CompressFormat.PNG, 90, out);
	           out.flush();
	           out.close();

	    } catch (Exception e) {
	           Log.d("Route.Route", e.getMessage());
	    }

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
			
			JSONArray JsonPointsOfInterest = jobject.getJSONArray("pointsOfInterest");
			this.pointsOfInterest = new ArrayList<Location>();
			for(int i=0; i<JsonPointsOfInterest.length(); i++){
				JSONObject JsonPointOfInterest = JsonPointsOfInterest.getJSONObject(i);
				Location pointOfInterest = new Location(
						JsonPointOfInterest.getDouble("lat"),
						JsonPointOfInterest.getDouble("lon"));
				this.pointsOfInterest.add(pointOfInterest);
			}
		}catch(JSONException e){
			e.printStackTrace();
			Log.d("Route", "Error parsing json. " + e.getMessage());
			System.exit(1);
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
		JSONArray jWayPoints = new JSONArray("wayPoints");
		for(Location wayPoint : wayPoints){
			JSONObject jWayPoint = new JSONObject();
			jWayPoint.put("lat", wayPoint.getLat());
			jWayPoint.put("lon",  wayPoint.getLon());
			jWayPoints.put(jWayPoint);
		}
		jRoute.accumulate("wayPoints",  jWayPoints);
		JSONArray jPointsOfInterest = new JSONArray("pointsOfInterest");
		for(Location point : pointsOfInterest){
			JSONObject jPoint = new JSONObject();
			jPoint.put("lat", point.getLat());
			jPoint.put("lon",  point.getLon());
			jPointsOfInterest.put(jPoint);
		}
		jRoute.accumulate("pointsOfInterest", jPointsOfInterest);
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
	
	public void setPointsOfInterest(ArrayList<Location> pointsOfInterest){
		this.pointsOfInterest = pointsOfInterest;
	}
	
	public void setMapImage(Bitmap mapImage){
		String file_name = new String(String.valueOf(id) + ".png");
	    File file = new File (file_name);
	    if (file.exists ()) file.delete (); 
	    try {
	           FileOutputStream out = new FileOutputStream(file);
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
	
	public ArrayList<Location> getPointsOfInterest(){
		return this.pointsOfInterest;
	}
	
	public long getId(){
		return this.id;
	}
	
	public Bitmap getMapImage(Context context){
		String file_name = new String(String.valueOf(id) + ".png");
    	AssetManager assetManager = context.getAssets();
		InputStream is = null;
		try{
			is = assetManager.open(file_name);
		}catch(IOException e){
			Log.e("RoomEditorActivity", "Exception caught: " + e.getMessage());
		}
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		
		return bitmap;
	}
	
}
