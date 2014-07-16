package it.polito.model;

public class Location {
	private double lat;
	private double lon;
	private double distance;
	private String name;
	private String urlImage;
	private String address;
	
	public Location(String name){
		this.name = name;
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
	
	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
