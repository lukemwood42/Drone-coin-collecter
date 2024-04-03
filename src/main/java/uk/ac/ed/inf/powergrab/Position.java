package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	double r = 0.0003;
	
	public Position(double latitude, double longitude) { 
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) { 
		double rad = direction.getRad(); //gets the direction in rads
		double newLat = this.latitude + r * Math.cos(rad); 
		double newLong = this.longitude + r * Math.sin(rad);
		return new Position(newLat, newLong);
	}
	
	public boolean inPlayArea() { 
		//checks if Position is in area
		if (this.latitude < 55.946233 && this.latitude > 55.942617) {
			if (this.longitude < -3.184319 && this.longitude > -3.192473) {
				return true;
			}
		}
		return false;
	}
}
