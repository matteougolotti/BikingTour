package it.polito.model;

public class Location {
	private double lat;
	private double lon;
	private String name;
	
	public Location(String name){
		this.name = name;
		//TODO if possible automatically get the coordinates.
	}
	
	public Location(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}
	
	public void setLat(double lat){
		this.lat = lat;
	}
	
	public void setLon(double lon){
		this.lon = lon;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public double getLat(){
		return this.lat;
	}
	
	public double getLon(){
		return this.lon;
	}
	
	public String getName(){
		return this.name;
	}
}
