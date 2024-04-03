package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Point;

public class StatelessDrone extends Drone{
	
	Random r;

	public StatelessDrone(double latitude, double longnitude, int seed) {
		super(latitude, longnitude);
		this.r = new Random(seed);;
	}

	public Direction calNextDirection(List<Station> stations) {
		List<Station> reachableStations = super.stationsInRange(stations);
		if (reachableStations.isEmpty()) {
			return RandDirection(reachableStations);
		}
		else {
			return highestStation(reachableStations);
		}
		
	}

	public Direction highestStation(List<Station> reachableStations) {
		Direction highestDir = null;
		Station highestStation = null;
		double highestCoins = Double.NEGATIVE_INFINITY;
		for (Direction d : super.directions) {
			Position p = super.position.nextPosition(d);
			if (p.inPlayArea()) {
				for (Station s : reachableStations) {
					if (super.calDis(p.latitude, p.longitude, s) <= 0.00025) {
						if (highestCoins < s.getCoins()) {
							boolean avoids = true;
//							for (Station rs : reachableStations) {
//								if (super.calDis(p.latitude, p.longitude, s) <= 0.00025 && rs.getCoins() < 0 && rs != s) {
//									avoids = false;
//								}
//							}
							if (avoids) {
								highestDir = d;
								highestStation = s;
							}
						}
					}
				}
			}
		}
		
		if (highestStation == null) {
			return RandDirection(reachableStations);
		}
		if (highestStation.getCoins() <= 0) {
			return RandDirection(reachableStations);
		}
		else {
			super.updateStation(highestStation);
			return highestDir;
		}
	}
	
	public Direction RandDirection(List<Station> reachableStations) {
		if (reachableStations.isEmpty()) {
			List<Direction> dirs = new ArrayList<Direction>(Arrays.asList(super.directions));
			while (true) {
				Direction d = dirs.get(r.nextInt(dirs.size()));
				if (super.position.nextPosition(d).inPlayArea()) {
					return d;
				}
				else {
					dirs.remove(d);
				}
			}
		}
		else {
			Direction[] temp = super.directions;
			List<Direction> dirs = new ArrayList<Direction>(Arrays.asList(temp));
			boolean avoids = false;
			Direction d = null;
			while (!dirs.isEmpty() && avoids == false) {
				d = dirs.get(r.nextInt(dirs.size()));
				dirs.remove(d);
				avoids = true;
				Position p = super.position.nextPosition(d);
				if (p.inPlayArea()) {
					for (Station s : reachableStations) {
						if (super.calDis(p.latitude, p.longitude, s) <= 0.00025 && s.getCoins() >= 0) {
							avoids = false;
						}
					}
				}
				else {avoids = false;}
			}
			
			if (avoids == true) {
				return d;
			}
			
			else {
				Direction highestDir = null;
				Station highestStation = null;
				double highestCoins = Double.NEGATIVE_INFINITY;
				for (Direction dir : super.directions) {
					Position p = super.position.nextPosition(d);
					if (p.inPlayArea()) {
						for (Station s : reachableStations) {
							if (super.calDis(p.latitude, p.longitude, s) <= 0.00025) {
								if (highestCoins < s.getCoins()) {
									highestDir = dir;
									highestStation = s;
								}
							}
						}
					}
				}
				super.updateStation(highestStation);
				return highestDir;
				
			}
		}
	}

}
