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
	
	//TODO test this code
	public double distanceFromLocationInMeters(Location otherLocation) {
	    double pk = (180/3.14169);

	    double a1 = this.lat / pk;
	    double a2 = this.lon / pk;
	    double b1 = otherLocation.getLat() / pk;
	    double b2 = otherLocation.getLon() / pk;

	    double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
	    double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
	    double t3 = Math.sin(a1) * Math.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);

	    return 6366000 * tt;
	}

}
