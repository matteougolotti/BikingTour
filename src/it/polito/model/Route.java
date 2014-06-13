package it.polito.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/***
 * 
 * @author Matteo
 * 
 * This class contains all the data of a single route.
 * It contains and manages both data used for route planning,
 * like origin and destination coordinates, way-points, etc and
 * data gathered during the tour, like pictures, videos and notes file paths.
 *
 */

public class Route {
	//Data used for route planning
	private String name;
	private Location origin, destination;
	private ArrayList<Location> wayPoints;
	private ArrayList<Location> pointsOfInterest;
	
	//Data used for route sharing
	private ArrayList<String> pictures;
	private ArrayList<String> videos;
	private ArrayList<String> notes;
	
	//Default constructor used to create a new route
	public Route(){
	}
	
	//Constructor used to load the object from a JSON object
	public Route(JSONObject jobject){
		try{
			this.name = jobject.getString("name");
			
			this.origin = new Location(jobject.getString("originName"));
			origin.setLat(jobject.getDouble("srcLat"));
			origin.setLon(jobject.getDouble("srcLon"));
			
			this.destination = new Location(jobject.getString("destinationName"));
			destination.setLat(jobject.getDouble("dstLat"));
			destination.setLon(jobject.getDouble("dstLon"));
			
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
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setOrigin(Location origin){
		this.origin = origin;
	}
	
	public void setDestination(Location destination){
		this.destination = destination;
	}
	
	public void setWayPoints(ArrayList<Location> wayPoints){
		this.wayPoints = wayPoints;
	}
	
	public void setPointsOfInterest(ArrayList<Location> pointsOfInterest){
		this.pointsOfInterest = pointsOfInterest;
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
	
	public ArrayList<Location> getWayPoints(){
		return this.wayPoints;
	}
	
	public ArrayList<Location> getPointsOfInterest(){
		return this.pointsOfInterest;
	}
}
