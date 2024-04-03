package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Point;

public class Station {
	
	double coins;
	double power;
	double longitude;
	double latitude;
	
	public Station(double coins, double power, Point p) {
		this.coins = coins;
		this.power = power;
		this.longitude = p.longitude();
		this.latitude = p.latitude();
	}
	
	public double getCoins() {
		return coins;
	}
	
	public double getPower() {
		return power;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String toString() {
		return "coins - " + coins + ", power - " + power + ", point - [" + latitude + "," + longitude + "]";
	}
}
