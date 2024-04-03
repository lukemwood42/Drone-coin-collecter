package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Drone {

	public final Direction[] directions = {Direction.N, Direction.NNE, Direction.NE, Direction.ENE, Direction.E, Direction.ESE, Direction.SE, 
														Direction.SSE, Direction.S, Direction.SSW, Direction.SW, Direction.WSW, Direction.W, Direction.WNW, Direction.NW, Direction.NNW};
	Position position;
	int moves = 0;
	double power = 250;
	double coins = 0;
	int seed;
	
	public Drone(double latitude, double longitude) {
		this.position = new Position(latitude, longitude);
	}
	
	public double getPower() {
		return power;
	}

	public int getMoves() {
		return moves;
	}

	protected double calDis(double latitude, double longitude, Station s) {
		double latDis = Math.pow((latitude - s.getLatitude()), 2);
		double longDis = Math.pow((longitude - s.getLongitude()), 2);
		return Math.sqrt(longDis + latDis);
	}

	public void makeMove(Position nextPosition) {
		power -= 1.25;
		moves += 1;
		position = nextPosition;
	}

	public void updateStation(Station highestStation) {
		this.coins += highestStation.getCoins();
		highestStation.coins = 0;
		if (this.power + highestStation.getPower() <= 0) {
			this.power = 0;
			highestStation.power = this.power + highestStation.getPower();
		}
		else {
			power += highestStation.getPower();
			highestStation.power = 0;
		}
	}

	public List<Station> stationsInRange(List<Station> stations) {
		List<Station> reachableStations = new ArrayList<>();
		for (Station s : stations) {
			if (calDis(position.latitude, position.longitude, s) <= 0.00025 + 0.0003) {
				reachableStations.add(s);
			}
		}
		return reachableStations;
	}
	

		
//		System.out.println("lat - " + newPos.latitude + ", long - " + newPos.longitude + ", direction - " + nextDirection.toString() + ", new Lat - " + position.latitude + ", new Long - " + position.longitude);
//		System.out.println(coins + "         " + power);	
	
	
}
