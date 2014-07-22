package it.polito.model;

import android.util.FloatMath;

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
	
	//TODO test this code
	public double distanceFromLocationInMeters(Location otherLocation) {
	    float pk = (float) (180/3.14169);

	    float a1 = (float) this.lat / pk;
	    float a2 = (float) this.lon / pk;
	    float b1 = (float) otherLocation.getLat() / pk;
	    float b2 = (float) otherLocation.getLon() / pk;

	    float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
	    float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
	    float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);

	    return 6366000*tt;
	}

}
